/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.poi.hwpf.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Class to extract the text from a Word Document.
 * 
 * You should use either getParagraphText() or getText() unless
 *  you have a strong reason otherwise.
 *
 * @author Nick Burch (nick at torchbox dot com)
 */
public class WordExtractor {
	private POIFSFileSystem fs;
	private HWPFDocument doc;
	
	/**
	 * Create a new Word Extractor
	 * @param is InputStream containing the word file
	 */
	public WordExtractor(InputStream is) throws IOException {
		this( HWPFDocument.verifyAndBuildPOIFS(is) );
	}

	/**
	 * Create a new Word Extractor
	 * @param fs POIFSFileSystem containing the word file
	 */
	public WordExtractor(POIFSFileSystem fs) throws IOException {
		this(new HWPFDocument(fs));
		this.fs = fs;
	}
	
	/**
	 * Create a new Word Extractor
	 * @param doc The HWPFDocument to extract from
	 */
	public WordExtractor(HWPFDocument doc) throws IOException {
		this.doc = doc;
	}

	/**
	 * Command line extractor, so people will stop moaning that
	 *  they can't just run this.
	 */
	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			System.err.println("Use:");
			System.err.println("   java org.apache.poi.hwpf.extractor.WordExtractor <filename>");
			System.exit(1);
		}

		// Process the first argument as a file
		FileInputStream fin = new FileInputStream(args[0]);
		WordExtractor extractor = new WordExtractor(fin);
		System.out.println(extractor.getText());
	}
	
	/**
	 * Get the text from the word file, as an array with one String
	 *  per paragraph
	 */
	public String[] getParagraphText() {
		String[] ret;
		
		// Extract using the model code
		try {
	    	Range r = doc.getRange();

			ret = new String[r.numParagraphs()];
			for(int i=0; i<ret.length; i++) {
				Paragraph p = r.getParagraph(i);
				ret[i] = p.text();
				
				// Fix the line ending
				if(ret[i].endsWith("\r")) {
					ret[i] = ret[i] + "\n";
				}
			}
		} catch(Exception e) {
			// Something's up with turning the text pieces into paragraphs
			// Fall back to ripping out the text pieces
			ret = new String[1];
			ret[0] = getTextFromPieces();
		}
		
		return ret;
	}
	
	/**
	 * Grab the text out of the text pieces. Might also include various
	 *  bits of crud, but will work in cases where the text piece -> paragraph
	 *  mapping is broken. Fast too.
	 */
	public String getTextFromPieces() {
    	StringBuffer textBuf = new StringBuffer();
    	
    	Iterator textPieces = doc.getTextTable().getTextPieces().iterator();
    	while (textPieces.hasNext()) {
    		TextPiece piece = (TextPiece) textPieces.next();

    		String encoding = "Cp1252";
    		if (piece.usesUnicode()) {
    			encoding = "UTF-16LE";
    		}
    		try {
    			String text = new String(piece.getRawBytes(), encoding);
    			textBuf.append(text);
    		} catch(UnsupportedEncodingException e) {
    			throw new InternalError("Standard Encoding " + encoding + " not found, JVM broken");
    		}
    	}
    	
    	String text = textBuf.toString();
    	
    	// Fix line endings (Note - won't get all of them
    	text = text.replaceAll("\r\r\r", "\r\n\r\n\r\n");
    	text = text.replaceAll("\r\r", "\r\n\r\n");
    	
    	if(text.endsWith("\r")) {
    		text += "\n";
    	}
    	
    	return text;
	}
	
	/**
	 * Grab the text, based on the paragraphs. Shouldn't include any crud,
	 *  but slightly slower than getTextFromPieces().
	 */
	public String getText() {
		StringBuffer ret = new StringBuffer();
		String[] text = getParagraphText();
		for(int i=0; i<text.length; i++) {
			ret.append(text[i]);
		}
		return ret.toString();
	}
}
