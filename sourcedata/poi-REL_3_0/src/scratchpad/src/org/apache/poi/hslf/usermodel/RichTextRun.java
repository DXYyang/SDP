
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
        


package org.apache.poi.hslf.usermodel;

import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.model.Sheet;
import org.apache.poi.hslf.model.SlideMaster;
import org.apache.poi.hslf.model.textproperties.CharFlagsTextProp;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.ColorSchemeAtom;

import java.awt.*;

/**
 * Represents a run of text, all with the same style
 * 
 * TODO: get access to the font/character properties
 *
 * @author Nick Burch
 */

public class RichTextRun
{
	/** The TextRun we belong to */
	private TextRun parentRun;
	/** The SlideShow we belong to */
	private SlideShow slideShow;
	
	/** Where in the parent TextRun we start from */
	private int startPos;
	
	/** How long a string (in the parent TextRun) we represent */
	private int length;
	
    private String _fontname;
    /**
	 * Our paragraph and character style.
	 * Note - we may share these styles with other RichTextRuns
	 */
	private TextPropCollection paragraphStyle;
	private TextPropCollection characterStyle;
	private boolean sharingParagraphStyle;
	private boolean sharingCharacterStyle;
	
	/**
	 * Create a new wrapper around a (currently not)
	 *  rich text string
	 * @param parent
	 * @param startAt
	 * @param len
	 */
	public RichTextRun(TextRun parent, int startAt, int len) {
		this(parent, startAt, len, null, null, false, false);
	}
	/**
	 * Create a new wrapper around a rich text string
	 * @param parent The parent TextRun
	 * @param startAt The start position of this run
	 * @param len The length of this run
	 * @param pStyle The paragraph style property collection
	 * @param cStyle The character style property collection
	 * @param pShared The paragraph styles are shared with other runs
	 * @param cShared The character styles are shared with other runs
	 */
	public RichTextRun(TextRun parent, int startAt, int len, 
	TextPropCollection pStyle,  TextPropCollection cStyle,
	boolean pShared, boolean cShared) {
		parentRun = parent;
		startPos = startAt;
		length = len;
		paragraphStyle = pStyle;
		characterStyle = cStyle;
		sharingParagraphStyle = pShared;
		sharingCharacterStyle = cShared;
	}

	/**
	 * Supply (normally default) textprops, and if they're shared, 
	 *  when a run gets them 
	 */
	public void supplyTextProps(TextPropCollection pStyle,  TextPropCollection cStyle, boolean pShared, boolean cShared) {
		if(paragraphStyle != null || characterStyle != null) {
			throw new IllegalStateException("Can't call supplyTextProps if run already has some");
		}
		paragraphStyle = pStyle;
		characterStyle = cStyle;
		sharingParagraphStyle = pShared;
		sharingCharacterStyle = cShared;
	}
	/**
	 * Supply the SlideShow we belong to
	 */
	public void supplySlideShow(SlideShow ss) {
		slideShow = ss;
        if (_fontname != null) {
            setFontName(_fontname);
            _fontname = null;
        }
	}
	
	/**
	 * Get the length of the text
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Fetch the text, in output suitable form
	 */
	public String getText() {
		return parentRun.getText().substring(startPos, startPos+length);
	}
	/**
	 * Fetch the text, in raw storage form
	 */
	public String getRawText() {
		return parentRun.getRawText().substring(startPos, startPos+length);
	}
	
	/**
	 * Change the text
	 */
	public void setText(String text) {
		length = text.length();
		parentRun.changeTextInRichTextRun(this,text);
	}
	
	/**
	 * Tells the RichTextRun its new position in the parent TextRun
	 * @param startAt
	 */
	public void updateStartPosition(int startAt) {
		startPos = startAt;
	}
	
	
	// --------------- Internal helpers on rich text properties -------
	
	/**
	 * Fetch the value of the given flag in the CharFlagsTextProp.
	 * Returns false if the CharFlagsTextProp isn't present, since the
	 *  text property won't be set if there's no CharFlagsTextProp.
	 */
	private boolean isCharFlagsTextPropVal(int index) {
        CharFlagsTextProp cftp = null;
        if (characterStyle != null){
            cftp = (CharFlagsTextProp)characterStyle.findByName("char_flags");
        }
        if (cftp == null){
            Sheet sheet = parentRun.getSheet();
            int txtype = parentRun.getRunType();
            SlideMaster master = (SlideMaster)sheet.getMasterSheet();
            cftp = (CharFlagsTextProp)master.getStyleAttribute(txtype, getIndentLevel(), "char_flags", true);
        }

		return cftp == null ? false : cftp.getSubValue(index);
	}

	/**
	 * Set the value of the given flag in the CharFlagsTextProp, adding
	 *  it if required. 
	 */
	private void setCharFlagsTextPropVal(int index, boolean value) {
		// Ensure we have the StyleTextProp atom we're going to need
		if(characterStyle == null) {
			parentRun.ensureStyleAtomPresent();
			// characterStyle will now be defined
		}
		
		CharFlagsTextProp cftp = (CharFlagsTextProp)
			fetchOrAddTextProp(characterStyle, "char_flags");
		cftp.setSubValue(value,index);
	}
	
	/**
	 * Returns the named TextProp, either by fetching it (if it exists) or adding it
	 *  (if it didn't)
	 * @param textPropCol The TextPropCollection to fetch from / add into
	 * @param textPropName The name of the TextProp to fetch/add
	 */
	private TextProp fetchOrAddTextProp(TextPropCollection textPropCol, String textPropName) {
		// Fetch / Add the TextProp
		TextProp tp = textPropCol.findByName(textPropName);
		if(tp == null) {
			tp = textPropCol.addWithName(textPropName);
		}
		return tp;
	}
	
	/**
	 * Fetch the value of the given Character related TextProp. 
	 * Returns -1 if that TextProp isn't present. 
	 * If the TextProp isn't present, the value from the appropriate 
	 *  Master Sheet will apply.
	 */
	private int getCharTextPropVal(String propName) {
        TextProp prop = null;
        if (characterStyle != null){
            prop = characterStyle.findByName(propName);
        }

        if (prop == null){
            Sheet sheet = parentRun.getSheet();
            int txtype = parentRun.getRunType();
            SlideMaster master = (SlideMaster)sheet.getMasterSheet();
            prop = master.getStyleAttribute(txtype, getIndentLevel(), propName, true);
        }
		return prop == null ? -1 : prop.getValue();
	}
	/**
	 * Fetch the value of the given Paragraph related TextProp.
	 * Returns -1 if that TextProp isn't present.
	 * If the TextProp isn't present, the value from the appropriate
	 *  Master Sheet will apply.
	 */
	private int getParaTextPropVal(String propName) {
        TextProp prop = null;
        if (paragraphStyle != null){
            prop = paragraphStyle.findByName(propName);
        }
        if (prop == null){
            Sheet sheet = parentRun.getSheet();
            int txtype = parentRun.getRunType();
            SlideMaster master = (SlideMaster)sheet.getMasterSheet();
            prop = master.getStyleAttribute(txtype, getIndentLevel(), propName, false);
        }

		return prop == null ? -1 : prop.getValue();
	}
	
	/**
	 * Sets the value of the given Character TextProp, add if required
	 * @param propName The name of the Character TextProp
	 * @param val The value to set for the TextProp
	 */
	private void setParaTextPropVal(String propName, int val) {
		// Ensure we have the StyleTextProp atom we're going to need
		if(paragraphStyle == null) {
			parentRun.ensureStyleAtomPresent();
			// paragraphStyle will now be defined
		}
		
		TextProp tp = fetchOrAddTextProp(paragraphStyle, propName);
		tp.setValue(val);
	}
	/**
	 * Sets the value of the given Paragraph TextProp, add if required
	 * @param propName The name of the Paragraph TextProp
	 * @param val The value to set for the TextProp
	 */
	private void setCharTextPropVal(String propName, int val) {
		// Ensure we have the StyleTextProp atom we're going to need
		if(characterStyle == null) {
			parentRun.ensureStyleAtomPresent();
			// characterStyle will now be defined
		}
		
		TextProp tp = fetchOrAddTextProp(characterStyle, propName);
		tp.setValue(val);
	}
	
	
	// --------------- Friendly getters / setters on rich text properties -------
	
	public boolean isBold() {
		return isCharFlagsTextPropVal(CharFlagsTextProp.BOLD_IDX);
	}
	public void setBold(boolean bold) {
		setCharFlagsTextPropVal(CharFlagsTextProp.BOLD_IDX, bold);
	}
	
	public boolean isItalic() {
		return isCharFlagsTextPropVal(CharFlagsTextProp.ITALIC_IDX);
	}
	public void setItalic(boolean italic) {
		setCharFlagsTextPropVal(CharFlagsTextProp.ITALIC_IDX, italic);
	}
	
	public boolean isUnderlined() {
		return isCharFlagsTextPropVal(CharFlagsTextProp.UNDERLINE_IDX);
	}
	public void setUnderlined(boolean underlined) {
		setCharFlagsTextPropVal(CharFlagsTextProp.UNDERLINE_IDX, underlined);
	}
	
	public int getFontSize() {
		return getCharTextPropVal("font.size");
	}
	public void setFontSize(int fontSize) {
		setCharTextPropVal("font.size", fontSize);
	}
	
	public void setFontName(String fontName) {
        if (slideShow == null) {
            //we can't set font since slideshow is not assigned yet
            _fontname = fontName;
        } else {
    		// Get the index for this font (adding if needed)
	    	int fontIdx = slideShow.getFontCollection().addFont(fontName);
		    setCharTextPropVal("font.index", fontIdx);
        }
	}
	public String getFontName() {
        if (slideShow == null) {
            return _fontname;
        } else {
            int fontIdx = getCharTextPropVal("font.index");
            if(fontIdx == -1) { return null; }
            return slideShow.getFontCollection().getFontWithId(fontIdx);
        }
	}
	
	/**
	 * @return font color as RGB value
	 * @see java.awt.Color
	 */
	public Color getFontColor() {
        int rgb = getCharTextPropVal("font.color");
        if (rgb >= 0x8000000) {
            int idx = rgb % 0x8000000;
            ColorSchemeAtom ca = parentRun.getSheet().getColorScheme();
            if(idx >= 0 && idx <= 7) rgb = ca.getColor(idx);
        }

        Color tmp = new Color(rgb, true);
        return new Color(tmp.getBlue(), tmp.getGreen(), tmp.getRed());
	}

	/**
	 * Sets color of the text, as a int bgr.
	 * (PowerPoint stores as BlueGreenRed, not the more
	 *  usual RedGreenBlue) 
	 * @see java.awt.Color
	 */
	public void setFontColor(int bgr) {
		setCharTextPropVal("font.color", bgr);
	}
	
    /**
     * Sets color of the text, as a java.awt.Color
     */
    public void setFontColor(Color color) {
        // In PowerPont RGB bytes are swapped, as BGR
        int rgb = new Color(color.getBlue(), color.getGreen(), color.getRed(), 254).getRGB();
        setFontColor(rgb);
    }

    /**
     * Sets the type of horizontal alignment for the text.
     * One of the <code>Align*</code> constants defined in the <code>TextBox</code> class.
     *
     * @param align - the type of alignment
     */
    public void setAlignment(int align) {
        setParaTextPropVal("alignment", align);
    }
    /**
     * Returns the type of horizontal alignment for the text.
     * One of the <code>Align*</code> constants defined in the <code>TextBox</class> class.
     *
     * @return the type of alignment
     */
    public int getAlignment() {
        return getParaTextPropVal("alignment");
    }

    /**
     *
     * @return indentation level
     */
    public int getIndentLevel() {
        return paragraphStyle == null ? 0 : paragraphStyle.getReservedField();
    }
	
	// --------------- Internal HSLF methods, not intended for end-user use! -------
	
	/**
	 * Internal Use Only - get the underlying paragraph style collection.
	 * For normal use, use the friendly setters and getters 
	 */
	public TextPropCollection _getRawParagraphStyle() { return paragraphStyle; }
	/**
	 * Internal Use Only - get the underlying character style collection.
	 * For normal use, use the friendly setters and getters 
	 */
	public TextPropCollection _getRawCharacterStyle() { return characterStyle; }
	/**
	 * Internal Use Only - are the Paragraph styles shared?
	 */
	public boolean _isParagraphStyleShared() { return sharingParagraphStyle; }
	/**
	 * Internal Use Only - are the Character styles shared?
	 */
	public boolean _isCharacterStyleShared() { return sharingCharacterStyle; }
}
