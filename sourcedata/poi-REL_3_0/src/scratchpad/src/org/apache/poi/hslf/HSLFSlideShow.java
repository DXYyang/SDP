
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
        


package org.apache.poi.hslf;

import java.util.*;
import java.io.*;

import org.apache.poi.POIDocument;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.MutablePropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.DocumentSummaryInformation;

import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.exceptions.EncryptedPowerPointFileException;
import org.apache.poi.hslf.record.*;
import org.apache.poi.hslf.usermodel.PictureData;

/**
 * This class contains the main functionality for the Powerpoint file 
 * "reader". It is only a very basic class for now
 *
 * @author Nick Burch
 */

public class HSLFSlideShow extends POIDocument
{
	private InputStream istream;

	// Holds metadata on where things are in our document
	private CurrentUserAtom currentUser;

	// Low level contents of the file
	private byte[] _docstream;

	// Low level contents
	private Record[] _records;

	// Raw Pictures contained in the pictures stream
	private PictureData[] _pictures;
	
	/**
	 * Returns the underlying POIFSFileSystem for the document
	 *  that is open.
	 */
	protected POIFSFileSystem getPOIFSFileSystem() {
		return filesystem;
	}

	/**
	 * Constructs a Powerpoint document from fileName. Parses the document 
	 * and places all the important stuff into data structures.
	 *
	 * @param fileName The name of the file to read.
	 * @throws IOException if there is a problem while parsing the document.
	 */
	public HSLFSlideShow(String fileName) throws IOException
	{
		this(new FileInputStream(fileName));
	}
  
	/**
	 * Constructs a Powerpoint document from an input stream. Parses the 
	 * document and places all the important stuff into data structures.
	 *
	 * @param inputStream the source of the data
	 * @throws IOException if there is a problem while parsing the document.
	 */
	public HSLFSlideShow(InputStream inputStream) throws IOException
	{
		//do Ole stuff
		this(new POIFSFileSystem(inputStream));
		istream = inputStream;
	}

	/**
	 * Constructs a Powerpoint document from a POIFS Filesystem. Parses the 
	 * document and places all the important stuff into data structures.
	 *
	 * @param filesystem the POIFS FileSystem to read from
	 * @throws IOException if there is a problem while parsing the document.
	 */
	public HSLFSlideShow(POIFSFileSystem filesystem) throws IOException
	{
		this.filesystem = filesystem;
		
		// First up, grab the "Current User" stream
		// We need this before we can detect Encrypted Documents
		readCurrentUserStream();
		
		// Next up, grab the data that makes up the 
		//  PowerPoint stream
		readPowerPointStream();
		
		// Check to see if we have an encrypted document,
		//  bailing out if we do
		boolean encrypted = EncryptedSlideShow.checkIfEncrypted(this);
		if(encrypted) {
			throw new EncryptedPowerPointFileException("Encrypted PowerPoint files are not supported");
		}

		// Now, build records based on the PowerPoint stream
		buildRecords();

		// Look for Property Streams:
		readProperties();
		
		// Look for any other streams
		readOtherStreams();

		// Look for Picture Streams:
		readPictures();
	}

	/**
	 * Constructs a new, empty, Powerpoint document.
	 */
	public HSLFSlideShow() throws IOException 
	{
		this(HSLFSlideShow.class.getResourceAsStream("/org/apache/poi/hslf/data/empty.ppt"));
	}

	/**
	 * Shuts things down. Closes underlying streams etc
	 *
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		if(istream != null) {
			istream.close();
		}
		filesystem = null;
	}


	/**
	 * Extracts the main PowerPoint document stream from the 
	 *  POI file, ready to be passed 
	 *
	 * @throws IOException
	 */
	private void readPowerPointStream() throws IOException
	{
		// Get the main document stream
		DocumentEntry docProps =
			(DocumentEntry)filesystem.getRoot().getEntry("PowerPoint Document");

		// Grab the document stream
		_docstream = new byte[docProps.getSize()];
		filesystem.createDocumentInputStream("PowerPoint Document").read(_docstream);
	}
	
	/**
	 * Builds the list of records, based on the contents  
	 *  of the PowerPoint stream
	 */
	private void buildRecords()
	{
		// The format of records in a powerpoint file are:
		//   <little endian 2 byte "info">
		//   <little endian 2 byte "type">
		//   <little endian 4 byte "length">
		// If it has a zero length, following it will be another record
		//		<xx xx yy yy 00 00 00 00> <xx xx yy yy zz zz zz zz>
		// If it has a length, depending on its type it may have children or data
		// If it has children, these will follow straight away
		//		<xx xx yy yy zz zz zz zz <xx xx yy yy zz zz zz zz>>
		// If it has data, this will come straigh after, and run for the length
		//      <xx xx yy yy zz zz zz zz dd dd dd dd dd dd dd>
		// All lengths given exclude the 8 byte record header
		// (Data records are known as Atoms)
	
		// Document should start with:
		//   0F 00 E8 03 ## ## ## ##
	    //     (type 1000 = document, info 00 0f is normal, rest is document length)
		//   01 00 E9 03 28 00 00 00
		//     (type 1001 = document atom, info 00 01 normal, 28 bytes long)
		//   80 16 00 00 E0 10 00 00 xx xx xx xx xx xx xx xx
		//   05 00 00 00 0A 00 00 00 xx xx xx
		//     (the contents of the document atom, not sure what it means yet)
		//   (records then follow)
	
		// When parsing a document, look to see if you know about that type
		//  of the current record. If you know it's a type that has children, 
		//  process the record's data area looking for more records
		// If you know about the type and it doesn't have children, either do
		//  something with the data (eg TextRun) or skip over it
		// If you don't know about the type, play safe and skip over it (using
		//  its length to know where the next record will start)
		//
		// For now, this work is handled by Record.findChildRecords
	
		_records = Record.findChildRecords(_docstream,0,_docstream.length);
	}

	/**
	 * Find the "Current User" stream, and load it 
	 */
	private void readCurrentUserStream() {
		try {
			currentUser = new CurrentUserAtom(filesystem);
		} catch(IOException ie) {
			System.err.println("Error finding Current User Atom:\n" + ie);
			currentUser = new CurrentUserAtom();
		}
	}
	
	/**
	 * Find any other streams from the filesystem, and load them 
	 */
	private void readOtherStreams() {
		// Currently, there aren't any
	}

	/**
	 * Find and read in pictures contained in this presentation
	 */
	private void readPictures() throws IOException {
		byte[] pictstream;

		try {
			DocumentEntry entry = (DocumentEntry)filesystem.getRoot().getEntry("Pictures");
			pictstream = new byte[entry.getSize()];
			DocumentInputStream is = filesystem.createDocumentInputStream("Pictures");
			is.read(pictstream);
		} catch (FileNotFoundException e){
			// Silently catch exceptions if the presentation doesn't 
			//  contain pictures - will use a null set instead
			return;
		}

        List p = new ArrayList();
        int pos = 0;

		// An empty picture record (length 0) will take up 8 bytes
        while (pos <= (pictstream.length-8)) {
            int offset = pos;

            // Image signature
            int signature = LittleEndian.getUShort(pictstream, pos);
            pos += LittleEndian.SHORT_SIZE;
            // Image type + 0xF018
            int type = LittleEndian.getUShort(pictstream, pos);
            pos += LittleEndian.SHORT_SIZE;
            // Image size (excluding the 8 byte header)
            int imgsize = LittleEndian.getInt(pictstream, pos);
            pos += LittleEndian.INT_SIZE;

			// The image size must be 0 or greater
			// (0 is allowed, but odd, since we do wind on by the header each
			//  time, so we won't get stuck)
			if(imgsize < 0) {
				throw new CorruptPowerPointFileException("The file contains a picture, at position " + p.size() + ", which has a negatively sized data length, so we can't trust any of the picture data");
			}

			// If they type (including the bonus 0xF018) is 0, skip it
			if(type == 0) {
				System.err.println("Problem reading picture: Invalid image type 0, on picture with length " + imgsize + ".\nYou document will probably become corrupted if you save it!");
				System.err.println(pos);
			} else {
	            // Copy the data, ready to pass to PictureData
	            byte[] imgdata = new byte[imgsize];
	            if(imgsize > 0) {
	            	System.arraycopy(pictstream, pos, imgdata, 0, imgdata.length);
	            }
	            
				// Build the PictureData object from the data
				try {
					PictureData pict = PictureData.create(type - 0xF018);
					pict.setRawData(imgdata);
					pict.setOffset(offset);
					p.add(pict);
				} catch(IllegalArgumentException e) {
					System.err.println("Problem reading picture: " + e + "\nYou document will probably become corrupted if you save it!");
				}
			}
            
            pos += imgsize;
        }

		_pictures = (PictureData[])p.toArray(new PictureData[p.size()]);
	}


  /**
   * Writes out the slideshow file the is represented by an instance of
   *  this class
   * @param out The OutputStream to write to.
   *  @throws IOException If there is an unexpected IOException from the passed
   *            in OutputStream
   */
   public void write(OutputStream out) throws IOException {
	// Get a new Filesystem to write into
	POIFSFileSystem outFS = new POIFSFileSystem();

	// Write out the Property Streams
	writeProperties(outFS);


	// For position dependent records, hold where they were and now are
	// As we go along, update, and hand over, to any Position Dependent
	//  records we happen across
	Hashtable oldToNewPositions = new Hashtable();
	
	// First pass - figure out where all the position dependent
	//   records are going to end up, in the new scheme
	// (Annoyingly, some powerpoing files have PersistPtrHolders
	//  that reference slides after the PersistPtrHolder)
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	for(int i=0; i<_records.length; i++) {
		if(_records[i] instanceof PositionDependentRecord) {
			PositionDependentRecord pdr = (PositionDependentRecord)_records[i];
			int oldPos = pdr.getLastOnDiskOffset();
			int newPos = baos.size();
			pdr.setLastOnDiskOffset(newPos);
			oldToNewPositions.put(new Integer(oldPos),new Integer(newPos));
			//System.out.println(oldPos + " -> " + newPos);
		}
		
		// Dummy write out, so the position winds on properly
		_records[i].writeOut(baos);
	}

	// No go back through, actually writing ourselves out
	baos.reset();
	for(int i=0; i<_records.length; i++) {
		// For now, we're only handling PositionDependentRecord's that
		//  happen at the top level.
		// In future, we'll need the handle them everywhere, but that's
		//  a bit trickier
		if(_records[i] instanceof PositionDependentRecord) {
			// We've already figured out their new location, and
			//  told them that
			// Tell them of the positions of the other records though
			PositionDependentRecord pdr = (PositionDependentRecord)_records[i];
			pdr.updateOtherRecordReferences(oldToNewPositions);
		}

		// Whatever happens, write out that record tree
		_records[i].writeOut(baos);
	}
	// Update our cached copy of the bytes that make up the PPT stream
	_docstream = baos.toByteArray();

	// Write the PPT stream into the POIFS layer
	ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	outFS.createDocument(bais,"PowerPoint Document");


	// Update and write out the Current User atom
	int oldLastUserEditAtomPos = (int)currentUser.getCurrentEditOffset();
	Integer newLastUserEditAtomPos = (Integer)oldToNewPositions.get(new Integer(oldLastUserEditAtomPos));
	if(newLastUserEditAtomPos == null) {
		throw new RuntimeException("Couldn't find the new location of the UserEditAtom that used to be at " + oldLastUserEditAtomPos);
	}
	currentUser.setCurrentEditOffset(newLastUserEditAtomPos.intValue());
	currentUser.writeToFS(outFS);

	
	// Write any pictures, into another stream
	if (_pictures != null) {
		ByteArrayOutputStream pict = new ByteArrayOutputStream();
		for (int i = 0; i < _pictures.length; i++ ) {
			_pictures[i].write(pict);
		}
		outFS.createDocument(
				new ByteArrayInputStream(pict.toByteArray()), "Pictures"
		);
	}

	// Send the POIFSFileSystem object out to the underlying stream
	outFS.writeFilesystem(out);
   }


	/* ******************* adding methods follow ********************* */

	/**
	 * Adds a new root level record, at the end, but before the last
	 *  PersistPtrIncrementalBlock.
	 */
	public synchronized int appendRootLevelRecord(Record newRecord) {
		int addedAt = -1;
		Record[] r = new Record[_records.length+1];
		boolean added = false;
		for(int i=(_records.length-1); i>=0; i--) {
			if(added) {
				// Just copy over
				r[i] = _records[i];
			} else {
				r[(i+1)] = _records[i];
				if(_records[i] instanceof PersistPtrHolder) {
					r[i] = newRecord;
					added = true;
					addedAt = i;
				}
			}
		}
		_records = r;
		return addedAt;
	}
	
	/**
	 *  Add a new picture to this presentation.
	 */
	public void addPicture(PictureData img) {
		// Copy over the existing pictures, into an array one bigger
		PictureData[] lst;
		if(_pictures == null) {
			lst = new PictureData[1];
		} else {
			lst = new PictureData[(_pictures.length+1)];
			System.arraycopy(_pictures,0,lst,0,_pictures.length);
		}
		// Add in the new image
		lst[lst.length - 1] = img;
		_pictures = lst;
	}

	/* ******************* fetching methods follow ********************* */


	/**
	 * Returns an array of all the records found in the slideshow
	 */
	public Record[] getRecords() { return _records; }

	/**
	 * Returns an array of the bytes of the file. Only correct after a
	 *  call to open or write - at all other times might be wrong!
	 */
	public byte[] getUnderlyingBytes() { return _docstream; }

	/**
	 * Fetch the Current User Atom of the document
	 */
	public CurrentUserAtom getCurrentUserAtom() { return currentUser; }

	/**
	 *  Return array of pictures contained in this presentation
	 *
	 *  @return array with the read pictures or <code>null</code> if the
	 *  presentation doesn't contain pictures.
	 */
	public PictureData[] getPictures() {
		return _pictures;
	}
}
