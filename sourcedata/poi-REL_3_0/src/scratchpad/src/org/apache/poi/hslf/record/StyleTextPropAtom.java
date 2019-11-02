
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
        

package org.apache.poi.hslf.record;

import org.apache.poi.hslf.model.textproperties.AlignmentTextProp;
import org.apache.poi.hslf.model.textproperties.BitMaskTextProp;
import org.apache.poi.hslf.model.textproperties.CharFlagsTextProp;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * A StyleTextPropAtom (type 4001). Holds basic character properties 
 *  (bold, italic, underline, font size etc) and paragraph properties
 *  (alignment, line spacing etc) for the block of text (TextBytesAtom
 *  or TextCharsAtom) that this record follows.
 * You will find two lists within this class.
 *  1 - Paragraph style list (paragraphStyles)
 *  2 - Character style list (charStyles)
 * Both are lists of TextPropCollections. These define how many characters
 *  the style applies to, and what style elements make up the style (another
 *  list, this time of TextProps). Each TextProp has a value, which somehow
 *  encapsulates a property of the style
 *
 * @author Nick Burch
 * @author Yegor Kozlov
 */

public class StyleTextPropAtom extends RecordAtom
{
	private byte[] _header;
	private static long _type = 4001l;
	private byte[] reserved;

	private byte[] rawContents; // Holds the contents between write-outs

	/** 
	 * Only set to true once setParentTextSize(int) is called.
	 * Until then, no stylings will have been decoded
	 */
	private boolean initialised = false;

	/** 
	 * The list of all the different paragraph stylings we code for.
	 * Each entry is a TextPropCollection, which tells you how many
	 *  Characters the paragraph covers, and also contains the TextProps
	 *  that actually define the styling of the paragraph.
	 */
	private LinkedList paragraphStyles;
	public LinkedList getParagraphStyles() { return paragraphStyles; }
	/**
	 * Updates the link list of TextPropCollections which make up the
	 *  paragraph stylings
	 */
	public void setParagraphStyles(LinkedList ps) { paragraphStyles = ps; }
	/** 
	 * The list of all the different character stylings we code for.
	 * Each entry is a TextPropCollection, which tells you how many
	 *  Characters the character styling covers, and also contains the 
	 *  TextProps that actually define the styling of the characters.
	 */
	private LinkedList charStyles;
	public LinkedList getCharacterStyles() { return charStyles; }
	/**
	 * Updates the link list of TextPropCollections which make up the
	 *  character stylings
	 */
	public void setCharacterStyles(LinkedList cs) { charStyles = cs; }

	/** All the different kinds of paragraph properties we might handle */
	public static TextProp[] paragraphTextPropTypes = new TextProp[] {
				new BitMaskTextProp(2,  0xF, "paragraph_flags", new String[] {
					"bullet", "bullet.hardfont", 
					"bullet.hardcolor", "bullet.hardsize"}
				),
                new TextProp(2, 0x80, "bullet.char"),
				new TextProp(2, 0x10, "bullet.font"),
				new TextProp(4, 0x20, "bullet.color"),
				new TextProp(2, 0x40, "bullet.size"),
                new AlignmentTextProp(),
                new TextProp(2, 0x400, "bullet.offset"),
				new TextProp(2, 0x200, "para_unknown_2"),
                new TextProp(2, 0x100, "text.offset"),
				new TextProp(2, 0x1000, "linespacing"),
				new TextProp(2, 0x2000, "spacebefore"),
				new TextProp(2, 0x4000, "spaceafter"),
				new TextProp(2, 0x8000, "para_unknown_4"),
				new TextProp(2, 0x10000, "para_unknown_5"),
				new TextProp(2, 0xE0000, "para_unknown_6"),
				new TextProp(2, 0x200000, "para_unknown_7")
	};
	/** All the different kinds of character properties we might handle */
	public static TextProp[] characterTextPropTypes = new TextProp[] {
				new CharFlagsTextProp(),
				new TextProp(2, 0x10000, "font.index"),
                new TextProp(2, 0x200000, "asian_or_complex"),
                new TextProp(2, 0x400000, "char_unknown_2"),
                new TextProp(2, 0x800000, "symbol"),
				new TextProp(2, 0x20000, "font.size"),
				new TextProp(4, 0x40000, "font.color"),
				new TextProp(2, 0x80000, "offset"),
				new TextProp(2, 0x100000, "char_unknown_1"),
				new TextProp(2, 0x1000000, "char_unknown_3"),
				new TextProp(2, 0x2000000, "char_unknown_4"),
				new TextProp(2, 0x4000000, "char_unknown_5"),
				new TextProp(2, 0x8000000, "char_unknown_6"),
				new TextProp(2, 0x10000000, "char_unknown_7"),
				new TextProp(2, 0x20000000, "char_unknown_8"),
				new TextProp(2, 0x40000000, "char_unknown_9"),
				new TextProp(2, 0x80000000, "char_unknown_10"),
	};

	/* *************** record code follows ********************** */

	/** 
	 * For the Text Style Properties (StyleTextProp) Atom
	 */
	protected StyleTextPropAtom(byte[] source, int start, int len) {
		// Sanity Checking - we're always at least 8+10 bytes long
		if(len < 18) {
			len = 18;
			if(source.length - start < 18) {
				throw new RuntimeException("Not enough data to form a StyleTextPropAtom (min size 18 bytes long) - found " + (source.length - start));
			}
		}

		// Get the header
		_header = new byte[8];
		System.arraycopy(source,start,_header,0,8);

		// Save the contents of the atom, until we're asked to go and
		//  decode them (via a call to setParentTextSize(int)
		rawContents = new byte[len-8];
		System.arraycopy(source,start+8,rawContents,0,rawContents.length);
		reserved = new byte[0];

		// Set empty linked lists, ready for when they call setParentTextSize
		paragraphStyles = new LinkedList();
		charStyles = new LinkedList();
	}


	/** 
	 * A new set of text style properties for some text without any.
	 */
	public StyleTextPropAtom(int parentTextSize) {
		_header = new byte[8];
		rawContents = new byte[0];
		reserved = new byte[0];

		// Set our type
		LittleEndian.putInt(_header,2,(short)_type);
		// Our initial size is 10
		LittleEndian.putInt(_header,4,10);

		// Set empty paragraph and character styles
		paragraphStyles = new LinkedList();
		charStyles = new LinkedList();

		TextPropCollection defaultParagraphTextProps = 
			new TextPropCollection(parentTextSize, (short)0);
		paragraphStyles.add(defaultParagraphTextProps);

		TextPropCollection defaultCharacterTextProps = 
			new TextPropCollection(parentTextSize);
		charStyles.add(defaultCharacterTextProps);

		// Set us as now initialised
		initialised = true;
	}


	/**
	 * We are of type 4001
	 */
	public long getRecordType() { return _type; }


	/**
	 * Write the contents of the record back, so it can be written
	 *  to disk
	 */
	public void writeOut(OutputStream out) throws IOException {
		// First thing to do is update the raw bytes of the contents, based
		//  on the properties
		updateRawContents();

		// Now ensure that the header size is correct
		int newSize = rawContents.length + reserved.length;
		LittleEndian.putInt(_header,4,newSize);

		// Write out the (new) header
		out.write(_header);

		// Write out the styles
		out.write(rawContents);

		// Write out any extra bits
		out.write(reserved);
	}


	/**
	 * Tell us how much text the parent TextCharsAtom or TextBytesAtom
	 *  contains, so we can go ahead and initialise ourselves.
	 */
	public void setParentTextSize(int size) {
		int pos = 0;
		int textHandled = 0;

		// While we have text in need of paragraph stylings, go ahead and
		// grok the contents as paragraph formatting data
        int prsize = size;
		while(pos < rawContents.length && textHandled < prsize) {
			// First up, fetch the number of characters this applies to
			int textLen = LittleEndian.getInt(rawContents,pos);
			textHandled += textLen;
			pos += 4;

			// Fetch the 2 byte value that is safe to ignore as 0
			short paraIgn = LittleEndian.getShort(rawContents,pos);
			pos += 2;

			// Grab the 4 byte value that tells us what properties follow
			int paraFlags = LittleEndian.getInt(rawContents,pos);
			pos += 4;

			// Now make sense of those properties
			TextPropCollection thisCollection = new TextPropCollection(textLen, paraIgn);
			int plSize = thisCollection.buildTextPropList(
					paraFlags, paragraphTextPropTypes, rawContents, pos);
			pos += plSize;

			// Save this properties set
			paragraphStyles.add(thisCollection);

            // Handle extra 1 paragraph styles at the end
            if(pos < rawContents.length && textHandled == size) {
                prsize++;
            }

		}
        if (rawContents.length > 0 && textHandled != (size+1)){
            logger.log(POILogger.WARN, "Problem reading paragraph style runs: textHandled = " + textHandled + ", text.size+1 = " + (size+1));
        }

		// Now do the character stylings
		textHandled = 0;
        int chsize = size;
		while(pos < rawContents.length && textHandled < chsize) {
			// First up, fetch the number of characters this applies to
			int textLen = LittleEndian.getInt(rawContents,pos);
			textHandled += textLen;
			pos += 4;

			// There is no 2 byte value
			short no_val = -1;

			// Grab the 4 byte value that tells us what properties follow
			int charFlags = LittleEndian.getInt(rawContents,pos);
			pos += 4;

			// Now make sense of those properties
			// (Assuming we actually have some)
			TextPropCollection thisCollection = new TextPropCollection(textLen, no_val);
			int chSize = thisCollection.buildTextPropList(
					charFlags, characterTextPropTypes, rawContents, pos);
			pos += chSize;

			// Save this properties set
			charStyles.add(thisCollection);
			
			// Handle extra 1 char styles at the end
			if(pos < rawContents.length && textHandled == size) {
				chsize++;
			}
		}
        if (rawContents.length > 0 && textHandled != (size+1)){
            logger.log(POILogger.WARN, "Problem reading character style runs: textHandled = " + textHandled + ", text.size+1 = " + (size+1));
        }

		// Handle anything left over
		if(pos < rawContents.length) {
			reserved = new byte[rawContents.length-pos];
			System.arraycopy(rawContents,pos,reserved,0,reserved.length);
		}

		initialised = true;
	}


	/**
	 * Updates the cache of the raw contents. Serialised the styles out.
	 */
	private void updateRawContents() throws IOException {
		if(!initialised) {
			// We haven't groked the styles since creation, so just stick
			// with what we found
			return;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// First up, we need to serialise the paragraph properties
		for(int i=0; i<paragraphStyles.size(); i++) {
			TextPropCollection tpc = (TextPropCollection)paragraphStyles.get(i);
			tpc.writeOut(baos);
		}

		// Now, we do the character ones
		for(int i=0; i<charStyles.size(); i++) {
			TextPropCollection tpc = (TextPropCollection)charStyles.get(i);
			tpc.writeOut(baos);
		}

		rawContents	= baos.toByteArray();
	}
	
	/**
	 * Create a new Paragraph TextPropCollection, and add it to the list
	 * @param charactersCovered The number of characters this TextPropCollection will cover
	 * @return the new TextPropCollection, which will then be in the list
	 */
	public TextPropCollection addParagraphTextPropCollection(int charactersCovered) {
		TextPropCollection tpc = new TextPropCollection(charactersCovered, (short)0);
		paragraphStyles.add(tpc);
		return tpc;
	}
	/**
	 * Create a new Character TextPropCollection, and add it to the list
	 * @param charactersCovered The number of characters this TextPropCollection will cover
	 * @return the new TextPropCollection, which will then be in the list
	 */
	public TextPropCollection addCharacterTextPropCollection(int charactersCovered) {
		TextPropCollection tpc = new TextPropCollection(charactersCovered);
		charStyles.add(tpc);
		return tpc;
	}


/* ************************************************************************ */


	/**
     * Dump the record content into <code>StringBuffer</code>
     *
     * @return the string representation of the record data
     */
    public String toString(){
        StringBuffer out = new StringBuffer();
        out.append("Paragraph properties\n");
        for (Iterator it1 = getParagraphStyles().iterator(); it1.hasNext();) {
            TextPropCollection pr = (TextPropCollection)it1.next();
            out.append("  chars covered: " + pr.getCharactersCovered() + "\n");
            for (Iterator it2 = pr.getTextPropList().iterator(); it2.hasNext(); ) {
                TextProp p = (TextProp)it2.next();
                out.append("    " + p.getName() + " = " + p.getValue() + "\n");
            }
        }

        out.append("Character properties\n");
        for (Iterator it1 = getCharacterStyles().iterator(); it1.hasNext();) {
            TextPropCollection pr = (TextPropCollection)it1.next();
            out.append("  chars covered: " + pr.getCharactersCovered() + "\n");
            for (Iterator it2 = pr.getTextPropList().iterator(); it2.hasNext(); ) {
                TextProp p = (TextProp)it2.next();
                out.append("    " + p.getName() + " = " + p.getValue() + "\n");
            }
        }

        return out.toString();
    }
}
