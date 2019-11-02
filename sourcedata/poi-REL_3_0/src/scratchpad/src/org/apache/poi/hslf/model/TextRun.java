
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
        


package org.apache.poi.hslf.model;

import java.util.LinkedList;
import java.util.Vector;

import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.*;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.util.StringUtil;

/**
 * This class represents a run of text in a powerpoint document. That
 *  run could be text on a sheet, or text in a note.
 *  It is only a very basic class for now
 *
 * @author Nick Burch
 */

public class TextRun
{
	// Note: These fields are protected to help with unit testing
	//   Other classes shouldn't really go playing with them!
	protected TextHeaderAtom _headerAtom;
	protected TextBytesAtom  _byteAtom;
	protected TextCharsAtom  _charAtom;
	protected StyleTextPropAtom _styleAtom;
	protected boolean _isUnicode;
	protected RichTextRun[] _rtRuns;
	private SlideShow slideShow;
    private Sheet sheet;

	/**
	* Constructs a Text Run from a Unicode text block
	*
	* @param tha the TextHeaderAtom that defines what's what
	* @param tca the TextCharsAtom containing the text
	* @param sta the StyleTextPropAtom which defines the character stylings
	*/
	public TextRun(TextHeaderAtom tha, TextCharsAtom tca, StyleTextPropAtom sta) {
		this(tha,null,tca,sta);
	}

	/**
	* Constructs a Text Run from a Ascii text block
	*
	* @param tha the TextHeaderAtom that defines what's what
	* @param tba the TextBytesAtom containing the text
	* @param sta the StyleTextPropAtom which defines the character stylings
	*/
	public TextRun(TextHeaderAtom tha, TextBytesAtom tba, StyleTextPropAtom sta) {
		this(tha,tba,null,sta);
	}
	
	/**
	 * Internal constructor and initializer
	 */
	private TextRun(TextHeaderAtom tha, TextBytesAtom tba, TextCharsAtom tca, StyleTextPropAtom sta) {
		_headerAtom = tha;
		_styleAtom = sta;
		if(tba != null) {
			_byteAtom = tba;
			_isUnicode = false;
		} else {
			_charAtom = tca;
			_isUnicode = true;
		}
		String runRawText = getText();
		
		// Figure out the rich text runs
		LinkedList pStyles = new LinkedList();
		LinkedList cStyles = new LinkedList();
		if(_styleAtom != null) {
			// Get the style atom to grok itself
			_styleAtom.setParentTextSize(runRawText.length());
			pStyles = _styleAtom.getParagraphStyles();
			cStyles = _styleAtom.getCharacterStyles();
		}

		// Handle case of no current style, with a default
		if(pStyles.size() == 0 || cStyles.size() == 0) { 
			_rtRuns = new RichTextRun[1];
			_rtRuns[0] = new RichTextRun(this, 0, runRawText.length());
		} else {
			// Build up Rich Text Runs, one for each 
			//  character/paragraph style pair
			Vector rtrs = new Vector();

			int pos = 0;
			
			int curP = 0;
			int curC = 0;
			int pLenRemain = -1;
			int cLenRemain = -1;
			
			// Build one for each run with the same style
			while(pos <= runRawText.length() && curP < pStyles.size() && curC < cStyles.size()) {
				// Get the Props to use
				TextPropCollection pProps = (TextPropCollection)pStyles.get(curP);
				TextPropCollection cProps = (TextPropCollection)cStyles.get(curC);
				
				int pLen = pProps.getCharactersCovered();
				int cLen = cProps.getCharactersCovered();
				
				// Handle new pass
				boolean freshSet = false;
				if(pLenRemain == -1 && cLenRemain == -1) { freshSet = true; }
				if(pLenRemain == -1) { pLenRemain = pLen; }
				if(cLenRemain == -1) { cLenRemain = cLen; }
				
				// So we know how to build the eventual run
				int runLen = -1;
				boolean pShared = false;
				boolean cShared = false;
				
				// Same size, new styles - neither shared
				if(pLen == cLen && freshSet) {
					runLen = cLen;
					pShared = false;
					cShared = false;
					curP++;
					curC++;
					pLenRemain = -1;
					cLenRemain = -1;
				} else {
					// Some sharing
					
					// See if we are already in a shared block
					if(pLenRemain < pLen) {
						// Existing shared p block
						pShared = true;
						
						// Do we end with the c block, or either side of it?
						if(pLenRemain == cLenRemain) {
							// We end at the same time
							cShared = false;
							runLen = pLenRemain;
							curP++;
							curC++;
							pLenRemain = -1;
							cLenRemain = -1;
						} else if(pLenRemain < cLenRemain) {
							// We end before the c block
							cShared = true;
							runLen = pLenRemain;
							curP++;
							cLenRemain -= pLenRemain;
							pLenRemain = -1;
						} else {
							// We end after the c block
							cShared = false;
							runLen = cLenRemain;
							curC++;
							pLenRemain -= cLenRemain;
							cLenRemain = -1;
						}
					} else if(cLenRemain < cLen) {
						// Existing shared c block
						cShared = true;
						
						// Do we end with the p block, or either side of it?
						if(pLenRemain == cLenRemain) {
							// We end at the same time
							pShared = false;
							runLen = cLenRemain;
							curP++;
							curC++;
							pLenRemain = -1;
							cLenRemain = -1;
						} else if(cLenRemain < pLenRemain) {
							// We end before the p block
							pShared = true;
							runLen = cLenRemain;
							curC++;
							pLenRemain -= cLenRemain;
							cLenRemain = -1;
						} else {
							// We end after the p block
							pShared = false;
							runLen = pLenRemain;
							curP++;
							cLenRemain -= pLenRemain;
							pLenRemain = -1;
						}
					} else {
						// Start of a shared block
						if(pLenRemain < cLenRemain) {
							// Shared c block
							pShared = false;
							cShared = true;
							runLen = pLenRemain;
							curP++;
							cLenRemain -= pLenRemain;
							pLenRemain = -1;
						} else {
							// Shared p block
							pShared = true;
							cShared = false;
							runLen = cLenRemain;
							curC++;
							pLenRemain -= cLenRemain;
							cLenRemain = -1;
						}
					}
				}
				
				// Wind on
				int prevPos = pos;
				pos += runLen;
				// Adjust for end-of-run extra 1 length
				if(pos > runRawText.length()) {
					runLen--; 
				}
				
				// Save
				RichTextRun rtr = new RichTextRun(this, prevPos, runLen, pProps, cProps, pShared, cShared);
				rtrs.add(rtr);
			}
			
			// Build the array
			_rtRuns = new RichTextRun[rtrs.size()];
			rtrs.copyInto(_rtRuns);
		}
	}
	
	
	// Update methods follow

	/**
	 * Saves the given string to the records. Doesn't touch the stylings. 
	 */
	private void storeText(String s) {
		// Remove a single trailing \n, as there is an implicit one at the
		//  end of every record
		if(s.endsWith("\n")) {
			s = s.substring(0, s.length()-1);
		}
		
		// Store in the appropriate record
		if(_isUnicode) {
			// The atom can safely convert to unicode
			_charAtom.setText(s);
		} else {
			// Will it fit in a 8 bit atom?
			boolean hasMultibyte = StringUtil.hasMultibyte(s);
			if(! hasMultibyte) {
				// Fine to go into 8 bit atom
				byte[] text = new byte[s.length()];
				StringUtil.putCompressedUnicode(s,text,0);
				_byteAtom.setText(text);
			} else {
				// Need to swap a TextBytesAtom for a TextCharsAtom
				
				// Build the new TextCharsAtom
				_charAtom = new TextCharsAtom();
				_charAtom.setText(s);
				
				// Use the TextHeaderAtom to do the swap on the parent
				RecordContainer parent = _headerAtom.getParentRecord();
				Record[] cr = parent.getChildRecords();
				for(int i=0; i<cr.length; i++) {
					// Look for TextBytesAtom
					if(cr[i].equals(_byteAtom)) {
						// Found it, so replace, then all done
						cr[i] = _charAtom;
						break;
					}
				}
				
				// Flag the change
				_byteAtom = null;
				_isUnicode = true;
			}
		}
	}
	
	/**
	 * Handles an update to the text stored in one of the Rich Text Runs
	 * @param run
	 * @param s
	 */
	public synchronized void changeTextInRichTextRun(RichTextRun run, String s) {
		// Figure out which run it is
		int runID = -1;
		for(int i=0; i<_rtRuns.length; i++) {
			if(run.equals(_rtRuns[i])) {
				runID = i;
			}
		}
		if(runID == -1) {
			throw new IllegalArgumentException("Supplied RichTextRun wasn't from this TextRun");
		}
		
		// Ensure a StyleTextPropAtom is present, adding if required
		ensureStyleAtomPresent();
		
		// Update the text length for its Paragraph and Character stylings
		// If it's shared:
		//   * calculate the new length based on the run's old text
		//   * this should leave in any +1's for the end of block if needed
		// If it isn't shared:
		//   * reset the length, to the new string's length
		//   * add on +1 if the last block
		// The last run needs its stylings to be 1 longer than the raw
		//  text is. This is to define the stylings that any new text
		//  that is added will inherit
		TextPropCollection pCol = run._getRawParagraphStyle();
		TextPropCollection cCol = run._getRawCharacterStyle();
		int newSize = s.length();
		if(runID == _rtRuns.length-1) {
			newSize++;
		}
		
		if(run._isParagraphStyleShared()) {
			pCol.updateTextSize( pCol.getCharactersCovered() - run.getLength() + s.length() );
		} else {
			pCol.updateTextSize(newSize);
		}
		if(run._isCharacterStyleShared()) {
			cCol.updateTextSize( cCol.getCharactersCovered() - run.getLength() + s.length() );
		} else {
			cCol.updateTextSize(newSize);
		}
		
		// Build up the new text
		// As we go through, update the start position for all subsequent runs
		// The building relies on the old text still being present
		StringBuffer newText = new StringBuffer();
		for(int i=0; i<_rtRuns.length; i++) {
			int newStartPos = newText.length();
			
			// Build up the new text
			if(i != runID) {
				// Not the affected run, so keep old text
				newText.append(_rtRuns[i].getRawText());
			} else {
				// Affected run, so use new text
				newText.append(s);
			}
			
			// Do we need to update the start position of this run?
			// (Need to get the text before we update the start pos)
			if(i <= runID) {
				// Change is after this, so don't need to change start position
			} else {
				// Change has occured, so update start position
				_rtRuns[i].updateStartPosition(newStartPos);
			}
		}
		
		// Now we can save the new text
		storeText(newText.toString());
	}

	/**
	 * Changes the text, and sets it all to have the same styling
	 *  as the the first character has. 
	 * If you care about styling, do setText on a RichTextRun instead 
	 */
	public synchronized void setText(String s) {
		// Save the new text to the atoms
		storeText(s);
		RichTextRun fst = _rtRuns[0];

		// Finally, zap and re-do the RichTextRuns
		for(int i=0; i<_rtRuns.length; i++) { _rtRuns[i] = null; }
		_rtRuns = new RichTextRun[1];
        _rtRuns[0] = fst;

		// Now handle record stylings:
		// If there isn't styling
		//  no change, stays with no styling
		// If there is styling:
		//  everthing gets the same style that the first block has
		if(_styleAtom != null) {
			LinkedList pStyles = _styleAtom.getParagraphStyles();
			while(pStyles.size() > 1) { pStyles.removeLast(); }
			
			LinkedList cStyles = _styleAtom.getCharacterStyles();
			while(cStyles.size() > 1) { cStyles.removeLast(); }
			
			_rtRuns[0].setText(s);
		} else {
			// Recreate rich text run with no styling
			_rtRuns[0] = new RichTextRun(this,0,s.length());
		}
	}

	/**
	 * Ensure a StyleTextPropAtom is present for this run, 
	 *  by adding if required. Normally for internal TextRun use.
	 */
	public synchronized void ensureStyleAtomPresent() {
		if(_styleAtom != null) {
			// All there
			return;
		}
		
		// Create a new one at the right size
		_styleAtom = new StyleTextPropAtom(getRawText().length() + 1);
		
		// Use the TextHeader atom to get at the parent
		RecordContainer runAtomsParent = _headerAtom.getParentRecord();
		
		// Add the new StyleTextPropAtom after the TextCharsAtom / TextBytesAtom
		Record addAfter = _byteAtom;
		if(_byteAtom == null) { addAfter = _charAtom; }
		runAtomsParent.addChildAfter(_styleAtom, addAfter);
		
		// Feed this to our sole rich text run
		if(_rtRuns.length != 1) {
			throw new IllegalStateException("Needed to add StyleTextPropAtom when had many rich text runs");
		}
		// These are the only styles for now 
		_rtRuns[0].supplyTextProps(
				(TextPropCollection)_styleAtom.getParagraphStyles().get(0),
				(TextPropCollection)_styleAtom.getCharacterStyles().get(0),
				false,
				false
		);
	}

	// Accesser methods follow

	/**
	 * Returns the text content of the run, which has been made safe
	 * for printing and other use.
	 */
	public String getText() {
		String rawText = getRawText();

		// PowerPoint seems to store files with \r as the line break
		// The messes things up on everything but a Mac, so translate
		//  them to \n
		String text = rawText.replace('\r','\n');
		return text;
	}

	/**
	* Returns the raw text content of the run. This hasn't had any
	*  changes applied to it, and so is probably unlikely to print
	*  out nicely.
	*/
	public String getRawText() {
		if(_isUnicode) {
			return _charAtom.getText();
		} else {
			return _byteAtom.getText();
		}
	}
	
	/**
	 * Fetch the rich text runs (runs of text with the same styling) that
	 *  are contained within this block of text
	 * @return
	 */
	public RichTextRun[] getRichTextRuns() {
		return 	_rtRuns;
	}
	
	/**
	* Returns the type of the text, from the TextHeaderAtom.
	* Possible values can be seen from TextHeaderAtom
	* @see org.apache.poi.hslf.record.TextHeaderAtom
	*/
	public int getRunType() { 
		return _headerAtom.getTextType();
	}

	/**
	* Changes the type of the text. Values should be taken
	*  from TextHeaderAtom. No checking is done to ensure you
	*  set this to a valid value!
	* @see org.apache.poi.hslf.record.TextHeaderAtom
	*/
	public void setRunType(int type) {
		_headerAtom.setTextType(type);
	}
	
	/**
	 * Supply the SlideShow we belong to.
	 * Also passes it on to our child RichTextRuns
	 */
	public void supplySlideShow(SlideShow ss) {
		slideShow = ss;
		if(_rtRuns != null) {
			for(int i=0; i<_rtRuns.length; i++) {
				_rtRuns[i].supplySlideShow(slideShow);
			}
		}
	}

    public void setSheet(Sheet sheet){
        this.sheet = sheet;
    }

    public Sheet getSheet(){
        return this.sheet;        
    }
}
