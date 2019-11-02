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
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.util.LittleEndian;

/**
 * This class represents the data of a link in the document. 
 * @author Nick Burch
 */
public class ExHyperlink extends RecordContainer {
	private byte[] _header;
	private static long _type = 4055;
	
	// Links to our more interesting children
	private ExHyperlinkAtom linkAtom;
	private CString linkDetailsA;
	private CString linkDetailsB;
	
	/** 
	 * Returns the ExHyperlinkAtom of this link
	 */ 
	public ExHyperlinkAtom getExHyperlinkAtom() { return linkAtom; }
	
	/**
	 * Returns the URL of the link.
	 * TODO: Figure out which of detailsA or detailsB is the
	 *  one that always holds it 
	 */
	public String getLinkURL() {
		return linkDetailsA.getText();
	}
	
	/**
	 * Sets the URL of the link
	 * TODO: Figure out if we should always set both
	 */
	public void setLinkURL(String url) {
		linkDetailsA.setText(url);

		// linkDetailsB isn't present in all PPT versions
		if(linkDetailsB != null) {
			linkDetailsB.setText(url);
		}
	}
	
	/**
	 * Get the link details (field A)
	 */
	public String _getDetailsA() {
		return linkDetailsA.getText();
	}
	/**
	 * Get the link details (field B)
	 */
	public String _getDetailsB() {
		return linkDetailsB.getText();
	}

	/** 
	 * Set things up, and find our more interesting children
	 */
	protected ExHyperlink(byte[] source, int start, int len) {
		// Grab the header
		_header = new byte[8];
		System.arraycopy(source,start,_header,0,8);

		// Find our children
		_children = Record.findChildRecords(source,start+8,len-8);
		findInterestingChildren();
	}

	/**
	 * Go through our child records, picking out the ones that are
	 *  interesting, and saving those for use by the easy helper
	 *  methods.
	 */	
	private void findInterestingChildren() {
		// We need to have 2 children, ideally 3, and sometimes have more
		if(_children.length < 2) {
			throw new IllegalStateException("We need at least two child records, but we only had " + _children.length);
		}

		// First child should be the ExHyperlinkAtom
		if(_children[0] instanceof ExHyperlinkAtom) {
			linkAtom = (ExHyperlinkAtom)_children[0];
		} else {
			throw new IllegalStateException("First child record wasn't a ExHyperlinkAtom, was of type " + _children[0].getRecordType());
		}
		
		// Second child should be the first link details
		if(_children[1] instanceof CString) {
			linkDetailsA = (CString)_children[1];
		} else {
			throw new IllegalStateException("Second child record wasn't a CString, was of type " + _children[1].getRecordType());
		}

		// Third child, if it exists, should be the second link details
		if(_children.length >= 3) {
			if(_children[2] instanceof CString) {
				linkDetailsB = (CString)_children[2];
			} else {
				throw new IllegalStateException("Third child record wasn't a CString, was of type " + _children[2].getRecordType());
			}
		} else {
			// Should be fine to not have one
		}
	}
	
	/**
	 * Create a new ExHyperlink, with blank fields
	 */
	public ExHyperlink() {
		_header = new byte[8];
		_children = new Record[3];
		
		// Setup our header block
		_header[0] = 0x0f; // We are a container record
		LittleEndian.putShort(_header, 2, (short)_type);
		
		// Setup our child records
		CString csa = new CString();
		CString csb = new CString();
		csa.setCount(0x00);
		csb.setCount(0x10);
		_children[0] = new ExHyperlinkAtom();
		_children[1] = csa;
		_children[2] = csb;
		findInterestingChildren();
	}

	/**
	 * We are of type 4055
	 */
	public long getRecordType() { return _type; }

	/**
	 * Write the contents of the record back, so it can be written
	 *  to disk
	 */
	public void writeOut(OutputStream out) throws IOException {
		writeOut(_header[0],_header[1],_type,_children,out);
	}
}
