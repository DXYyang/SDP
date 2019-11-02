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



package org.apache.poi.hwpf.usermodel;


import org.apache.poi.util.LittleEndian;

import org.apache.poi.hwpf.HWPFDocument;

import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.usermodel.Section;

import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.model.CHPX;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.model.SEPX;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.ListTables;

import org.apache.poi.hwpf.sprm.CharacterSprmCompressor;
import org.apache.poi.hwpf.sprm.ParagraphSprmCompressor;
import org.apache.poi.hwpf.sprm.SprmBuffer;


import java.util.List;
import java.util.NoSuchElementException;
import java.lang.ref.WeakReference;

/**
 * This class is the central class of the HWPF object model. All properties
 * that apply to a range of characters in a Word document extend this class.
 *
 * It is possible to insert text and/or properties at the beginning or end of a
 * range.
 *
 * Ranges are only valid if there hasn't been an insert in a prior Range since
 * the Range's creation. Once an element (text, paragraph, etc.) has been
 * inserted into a Range, subsequent Ranges become unstable.
 *
 * @author Ryan Ackley
 */
public class Range
{

  public static final int TYPE_PARAGRAPH = 0;
  public static final int TYPE_CHARACTER= 1;
  public static final int TYPE_SECTION = 2;
  public static final int TYPE_TEXT = 3;
  public static final int TYPE_LISTENTRY = 4;
  public static final int TYPE_TABLE = 5;
  public static final int TYPE_UNDEFINED = 6;

  /** Needed so inserts and deletes will ripple up through containing Ranges */
  private WeakReference _parent;

  /** The starting character offset of this range.*/
  protected int _start;

  /** The ending character offset of this range.*/
  protected int _end;

  /** The document this range blongs to.*/
  protected HWPFDocument _doc;

  /** Have we loaded the section indexes yet*/
  boolean _sectionRangeFound;

  /** All sections that belong to the document this Range belongs to.*/
  protected List _sections;

  /** The start index in the sections list for this Range*/
  protected int _sectionStart;

  /** The end index in the sections list for this Range.*/
  protected int _sectionEnd;

  /** Have we loaded the paragraph indexes yet.*/
  protected boolean _parRangeFound;

  /** All paragraphs that belong to the document this Range belongs to.*/
  protected List _paragraphs;

  /** The start index in the paragraphs list for this Range*/
  protected int _parStart;

  /** The end index in the paragraphs list for this Range.*/
  protected int _parEnd;

  /** Have we loaded the characterRun indexes yet.*/
  protected boolean _charRangeFound;

  /** All CharacterRuns that belong to the document this Range belongs to.*/
  protected List _characters;

  /** The start index in the characterRuns list for this Range*/
  protected int _charStart;

  /** The end index in the characterRuns list for this Range. */
  protected int _charEnd;

  /** Have we loaded the Text indexes yet*/
  protected boolean _textRangeFound;

  /** All text pieces that belong to the document this Range belongs to.*/
  protected List _text;

  /** The start index in the text list for this Range.*/
  protected int _textStart;

  /** The end index in the text list for this Range.*/
  protected int _textEnd;

//  protected Range()
//  {
//
//  }

  /**
   * Used to construct a Range from a document. This is generally used to
   * create a Range that spans the whole document.
   *
   * @param start Starting character offset of the range.
   * @param end Ending character offset of the range.
   * @param doc The HWPFDocument the range is based on.
   */
  public Range(int start, int end, HWPFDocument doc)
  {
    _start = start;
    _end = end;
    _doc = doc;
    _sections = _doc.getSectionTable().getSections();
    _paragraphs = _doc.getParagraphTable().getParagraphs();
    _characters = _doc.getCharacterTable().getTextRuns();
    _text = _doc.getTextTable().getTextPieces();
    _parent = new WeakReference(null);
  }


  /**
   * Used to create Ranges that are children of other Ranges.
   *
   * @param start Starting character offset of the range.
   * @param end Ending character offset of the range.
   * @param parent The parent this range belongs to.
   */
  protected Range(int start, int end, Range parent)
  {
    _start = start;
    _end = end;
    _doc = parent._doc;
    _sections = parent._sections;
    _paragraphs = parent._paragraphs;
    _characters = parent._characters;
    _text = parent._text;
    _parent = new WeakReference(parent);
  }

  /**
   * Constructor used to build a Range from indexes in one of its internal
   * lists.
   *
   * @param startIdx The starting index in the list.
   * @param endIdx The ending index in the list.
   * @param idxType The list type.
   * @param parent The parent Range this range belongs to.
   */
  protected Range(int startIdx, int endIdx, int idxType, Range parent)
  {
    _doc = parent._doc;
    _sections = parent._sections;
    _paragraphs = parent._paragraphs;
    _characters = parent._characters;
    _text = parent._text;
    _parent = new WeakReference(parent);

    switch (idxType)
    {
      case TYPE_PARAGRAPH:
        _parStart = parent._parStart + startIdx;
        _parEnd = parent._parStart + endIdx;
        _start = ((PropertyNode)_paragraphs.get(_parStart)).getStart();
        _end = ((PropertyNode)_paragraphs.get(_parEnd)).getEnd();
        _parRangeFound = true;
        break;
      case TYPE_CHARACTER:
        _charStart = parent._charStart + startIdx;
        _charEnd = parent._charStart + endIdx;
        _start = ((PropertyNode)_characters.get(_charStart)).getStart();
        _end = ((PropertyNode)_characters.get(_charEnd)).getEnd();
        _charRangeFound = true;
        break;
     case TYPE_SECTION:
        _sectionStart = parent._sectionStart + startIdx;
        _sectionEnd = parent._sectionStart + endIdx;
        _start = ((PropertyNode)_sections.get(_sectionStart)).getStart();
        _end = ((PropertyNode)_sections.get(_sectionEnd)).getEnd();
        _sectionRangeFound = true;
        break;
     case TYPE_TEXT:
        _textStart = parent._textStart + startIdx;
        _textEnd = parent._textStart + endIdx;
        _start = ((PropertyNode)_text.get(_textStart)).getStart();
        _end = ((PropertyNode)_text.get(_textEnd)).getEnd();
        _textRangeFound = true;
        break;
    }
  }

  /**
   * Gets the text that this Range contains.
   *
   * @return The text for this range.
   */
  public String text()
  {
    initText();

    StringBuffer sb = new StringBuffer();

    for (int x = _textStart; x < _textEnd; x++)
    {
      TextPiece piece = (TextPiece)_text.get(x);
      int start = _start > piece.getStart() ? _start - piece.getStart() : 0;
      int end = _end <= piece.getEnd() ? _end - piece.getStart() : piece.getEnd() - piece.getStart();

      if(piece.usesUnicode()) // convert the byte pointers to char pointers
      {
        start/=2;
        end/=2;
      }
      sb.append(piece.getStringBuffer().substring(start, end));
    }
    return sb.toString();
  }

  /**
   * Used to get the number of sections in a range. If this range is smaller
   * than a section, it will return 1 for its containing section.
   *
   * @return The number of sections in this range.
   */
  public int numSections()
  {
    initSections();
    return _sectionEnd - _sectionStart;
  }

  /**
   * Used to get the number of paragraphs in a range. If this range is smaller
   * than a paragraph, it will return 1 for its containing paragraph.
   *
   * @return The number of paragraphs in this range.
   */

  public int numParagraphs()
  {
    initParagraphs();
    return _parEnd - _parStart;
  }

  /**
   *
   * @return The number of characterRuns in this range.
   */

  public int numCharacterRuns()
  {
    initCharacterRuns();
    return _charEnd - _charStart;
  }

  /**
   * Inserts text into the front of this range.
   *
   * @param text The text to insert
   * @return The character run that text was inserted into.
   */
  public CharacterRun insertBefore(String text)
    //throws UnsupportedEncodingException
  {
    initAll();

    TextPiece tp = (TextPiece)_text.get(_textStart);
    StringBuffer sb = (StringBuffer)tp.getStringBuffer();

    // Since this is the first item in our list, it is safe to assume that
    // _start >= tp.getStart()
    int insertIndex = _start - tp.getStart();
    sb.insert(insertIndex, text);
    int adjustedLength = _doc.getTextTable().adjustForInsert(_textStart, text.length());
    _doc.getCharacterTable().adjustForInsert(_charStart, adjustedLength);
    _doc.getParagraphTable().adjustForInsert(_parStart, adjustedLength);
    _doc.getSectionTable().adjustForInsert(_sectionStart, adjustedLength);
    adjustForInsert(text.length());

    return getCharacterRun(0);
  }

  /**
   * Inserts text onto the end of this range
   *
   * @param text The text to insert
   * @return The character run the text was inserted into.
   */
  public CharacterRun insertAfter(String text)
  {
    initAll();

    int listIndex = _textEnd - 1;
    TextPiece tp = (TextPiece)_text.get(listIndex);
    StringBuffer sb = (StringBuffer)tp.getStringBuffer();

    int insertIndex = _end - tp.getStart();

    if (tp.getStringBuffer().charAt(_end - 1) == '\r' && text.charAt(0) != '\u0007')
    {
      insertIndex--;
    }
    sb.insert(insertIndex, text);
    int adjustedLength = _doc.getTextTable().adjustForInsert(listIndex, text.length());
    _doc.getCharacterTable().adjustForInsert(_charEnd - 1, adjustedLength);
    _doc.getParagraphTable().adjustForInsert(_parEnd - 1, adjustedLength);
    _doc.getSectionTable().adjustForInsert(_sectionEnd - 1, adjustedLength);
    adjustForInsert(text.length());

    return getCharacterRun(numCharacterRuns() - 1);

  }

  /**
   * Inserts text into the front of this range and it gives that text the
   * CharacterProperties specified in props.
   *
   * @param text The text to insert.
   * @param props The CharacterProperties to give the text.
   * @return A new CharacterRun that has the given text and properties and is n
   *         ow a part of the document.
   */
  public CharacterRun insertBefore(String text, CharacterProperties props)
    //throws UnsupportedEncodingException
  {
    initAll();
    PAPX papx = (PAPX)_paragraphs.get(_parStart);
    short istd = papx.getIstd();

    StyleSheet ss = _doc.getStyleSheet();
    CharacterProperties baseStyle = ss.getCharacterStyle(istd);
    byte[] grpprl = CharacterSprmCompressor.compressCharacterProperty(props, baseStyle);
    SprmBuffer buf = new SprmBuffer(grpprl);
    _doc.getCharacterTable().insert(_charStart, _start, buf);

    return insertBefore(text);
  }

  /**
   * Inserts text onto the end of this range and gives that text the
   * CharacterProperties specified in props.
   *
   * @param text The text to insert.
   * @param props The CharacterProperties to give the text.
   * @return A new CharacterRun that has the given text and properties and is n
   *         ow a part of the document.
   */
  public CharacterRun insertAfter(String text, CharacterProperties props)
    //throws UnsupportedEncodingException
  {
    initAll();
    PAPX papx = (PAPX)_paragraphs.get(_parEnd - 1);
    short istd = papx.getIstd();

    StyleSheet ss = _doc.getStyleSheet();
    CharacterProperties baseStyle = ss.getCharacterStyle(istd);
    byte[] grpprl = CharacterSprmCompressor.compressCharacterProperty(props, baseStyle);
    SprmBuffer buf = new SprmBuffer(grpprl);
    _doc.getCharacterTable().insert(_charEnd, _end, buf);
    _charEnd++;
    return insertAfter(text);
  }

  /**
   * Inserts and empty paragraph into the front of this range.
   *
   * @param props The properties that the new paragraph will have.
   * @param styleIndex The index into the stylesheet for the new paragraph.
   * @return The newly inserted paragraph.
   */
  public Paragraph insertBefore(ParagraphProperties props, int styleIndex)
    //throws UnsupportedEncodingException
  {
   return this.insertBefore(props, styleIndex, "\r");
  }

  /**
   * Inserts a paragraph into the front of this range. The paragraph will
   * contain one character run that has the default properties for the
   * paragraph's style.
   *
   * It is necessary for the text to end with the character '\r'
   *
   * @param props The paragraph's properties.
   * @param styleIndex The index of the paragraph's style in the style sheet.
   * @param text The text to insert.
   * @return A newly inserted paragraph.
   */
  protected Paragraph insertBefore(ParagraphProperties props, int styleIndex, String text)
    //throws UnsupportedEncodingException
  {
    initAll();
    StyleSheet ss = _doc.getStyleSheet();
    ParagraphProperties baseStyle = ss.getParagraphStyle(styleIndex);
    CharacterProperties baseChp = ss.getCharacterStyle(styleIndex);

    byte[] grpprl = ParagraphSprmCompressor.compressParagraphProperty(props, baseStyle);
    byte[] withIndex = new byte[grpprl.length + LittleEndian.SHORT_SIZE];
    LittleEndian.putShort(withIndex, (short)styleIndex);
    System.arraycopy(grpprl, 0, withIndex, LittleEndian.SHORT_SIZE, grpprl.length);
    SprmBuffer buf = new SprmBuffer(withIndex);

    _doc.getParagraphTable().insert(_parStart, _start, buf);
    insertBefore(text, baseChp);
    return getParagraph(0);
  }

  /**
   * Inserts and empty paragraph into the end of this range.
   *
   * @param props The properties that the new paragraph will have.
   * @param styleIndex The index into the stylesheet for the new paragraph.
   * @return The newly inserted paragraph.
   */

  public Paragraph insertAfter(ParagraphProperties props, int styleIndex)
    //throws UnsupportedEncodingException
  {
    return this.insertAfter(props, styleIndex, "\r");
  }

  /**
  * Inserts a paragraph into the end of this range. The paragraph will
  * contain one character run that has the default properties for the
  * paragraph's style.
  *
  * It is necessary for the text to end with the character '\r'
  *
  * @param props The paragraph's properties.
  * @param styleIndex The index of the paragraph's style in the style sheet.
  * @param text The text to insert.
  * @return A newly inserted paragraph.
  */
  protected Paragraph insertAfter(ParagraphProperties props, int styleIndex, String text)
    //throws UnsupportedEncodingException
  {
    initAll();
    StyleSheet ss = _doc.getStyleSheet();
    ParagraphProperties baseStyle = ss.getParagraphStyle(styleIndex);
    CharacterProperties baseChp = ss.getCharacterStyle(styleIndex);

    byte[] grpprl = ParagraphSprmCompressor.compressParagraphProperty(props, baseStyle);
    byte[] withIndex = new byte[grpprl.length + LittleEndian.SHORT_SIZE];
    LittleEndian.putShort(withIndex, (short)styleIndex);
    System.arraycopy(grpprl, 0, withIndex, LittleEndian.SHORT_SIZE, grpprl.length);
    SprmBuffer buf = new SprmBuffer(withIndex);

    _doc.getParagraphTable().insert(_parEnd, _end, buf);
    _parEnd++;
    insertAfter(text, baseChp);
    return getParagraph(numParagraphs() - 1);
  }

  public void delete()
  {
    initAll();

    int numSections = _sections.size();
    int numRuns = _characters.size();
    int numParagraphs = _paragraphs.size();

    for (int x = _charStart; x < numRuns; x++)
    {
      CHPX chpx = (CHPX)_characters.get(x);
      chpx.adjustForDelete(_start, _end - _start);
    }

    for (int x = _parStart; x < numParagraphs; x++)
    {
      PAPX papx = (PAPX)_paragraphs.get(x);
      papx.adjustForDelete(_start, _end - _start);
    }

    for (int x = _sectionStart; x < numSections; x++)
    {
      SEPX sepx = (SEPX)_sections.get(x);
      sepx.adjustForDelete(_start, _end - _start);
    }
  }

  /**
   * Inserts a simple table into the beginning of this range. The number of
   * columns is determined by the TableProperties passed into this function.
   *
   * @param props The table properties for the table.
   * @param rows The number of rows.
   * @return The empty Table that is now part of the document.
   */
  public Table insertBefore(TableProperties props, int rows)
  {
    ParagraphProperties parProps = new ParagraphProperties();
    parProps.setFInTable((byte)1);
    parProps.setTableLevel((byte)1);

    int columns = props.getItcMac();
    for (int x = 0; x < rows; x++)
    {
      Paragraph cell = this.insertBefore(parProps, StyleSheet.NIL_STYLE);
      cell.insertAfter(String.valueOf('\u0007'));
      for(int y = 1; y < columns; y++)
      {
        cell = cell.insertAfter(parProps, StyleSheet.NIL_STYLE);
        cell.insertAfter(String.valueOf('\u0007'));
      }
      cell = cell.insertAfter(parProps, StyleSheet.NIL_STYLE, String.valueOf('\u0007'));
      cell.setTableRowEnd(props);
    }
    return new Table(_start, _start + (rows * (columns + 1)), this, 1);
  }

  /**
   * Inserts a list into the beginning of this range.
   *
   * @param props The properties of the list entry. All list entries are
   *        paragraphs.
   * @param listID The id of the list that contains the properties.
   * @param level The indentation level of the list.
   * @param styleIndex The base style's index in the stylesheet.
   * @return The empty ListEntry that is now part of the document.
   */
  public ListEntry insertBefore(ParagraphProperties props, int listID, int level, int styleIndex)
  {
    ListTables lt = _doc.getListTables();
    if (lt.getLevel(listID, level) == null)
    {
      throw new NoSuchElementException("The specified list and level do not exist");
    }

    int ilfo = lt.getOverrideIndexFromListID(listID);
    props.setIlfo(ilfo);
    props.setIlvl((byte)level);

    return (ListEntry)insertBefore(props, styleIndex);
  }

  /**
   * Inserts a list into the beginning of this range.
   *
   * @param props The properties of the list entry. All list entries are
   *        paragraphs.
   * @param listID The id of the list that contains the properties.
   * @param level The indentation level of the list.
   * @param styleIndex The base style's index in the stylesheet.
   * @return The empty ListEntry that is now part of the document.
   */
  public ListEntry insertAfter(ParagraphProperties props, int listID, int level, int styleIndex)
  {
    ListTables lt = _doc.getListTables();
    if (lt.getLevel(listID, level) == null)
    {
      throw new NoSuchElementException("The specified list and level do not exist");
    }
    int ilfo = lt.getOverrideIndexFromListID(listID);
    props.setIlfo(ilfo);
    props.setIlvl((byte)level);

    return (ListEntry)insertAfter(props, styleIndex);
  }


  /**
   * Gets the character run at index. The index is relative to this range.
   *
   * @param index The index of the character run to get.
   * @return The character run at the specified index in this range.
   */
  public CharacterRun getCharacterRun(int index)
  {
    initCharacterRuns();
    CHPX chpx = (CHPX)_characters.get(index + _charStart);

    int[] point = findRange(_paragraphs, _parStart, Math.max(chpx.getStart(), _start),
                              chpx.getEnd());
    PAPX papx = (PAPX)_paragraphs.get(point[0]);
    short istd = papx.getIstd();

    CharacterRun chp = new CharacterRun(chpx, _doc.getStyleSheet(), istd, this);

    return chp;
  }

  /**
   * Gets the section at index. The index is relative to this range.
   *
   * @param index The index of the section to get.
   * @return The section at the specified index in this range.
   */
  public Section getSection(int index)
  {
    initSections();
    SEPX sepx = (SEPX)_sections.get(index + _sectionStart);
    Section sep = new Section(sepx, this);
    return sep;
  }

  /**
   * Gets the paragraph at index. The index is relative to this range.
   *
   * @param index The index of the paragraph to get.
   * @return The paragraph at the specified index in this range.
   */

  public Paragraph getParagraph(int index)
  {
    initParagraphs();
    PAPX papx = (PAPX)_paragraphs.get(index + _parStart);

    ParagraphProperties props = papx.getParagraphProperties(_doc.getStyleSheet());
    Paragraph pap = null;
    if (props.getIlfo() > 0)
    {
      pap = new ListEntry(papx, this, _doc.getListTables());
    }
    else
    {
      pap = new Paragraph(papx, this);
    }

    return pap;
  }

  /**
   * This method is used to determine the type. Handy for switch statements
   * compared to the instanceof operator.
   *
   * @return A TYPE constant.
   */
  public int type()
  {
    return TYPE_UNDEFINED;
  }

  /**
   * Gets the table that starts with paragraph. In a Word file, a table consists
   * of a group of paragraphs with certain flags set.
   *
   * @param paragraph The paragraph that is the first paragraph in the table.
   * @return The table that starts with paragraph
   */
  public Table getTable(Paragraph paragraph)
  {
    if (!paragraph.isInTable())
    {
      throw new IllegalArgumentException("This paragraph doesn't belong to a table");
    }

    Range r = (Range)paragraph;
    if (r._parent.get() != this)
    {
      throw new IllegalArgumentException("This paragraph is not a child of this range");
    }

    r.initAll();
    int tableEnd = r._parEnd;

    if (r._parStart != 0 && getParagraph(r._parStart - 1).isInTable()
        && getParagraph(r._parStart - 1)._sectionEnd >= r._sectionStart)
    {
      throw new IllegalArgumentException("This paragraph is not the first one in the table");
    }

    int limit = _paragraphs.size();
    for (; tableEnd < limit; tableEnd++)
    {
       if (!getParagraph(tableEnd).isInTable())
      {
        break;
      }
    }

    initAll();
    if (tableEnd > _parEnd)
    {
      throw new ArrayIndexOutOfBoundsException("The table's bounds fall outside of this Range");
    }
    return new Table(r._parStart, tableEnd, r._doc.getRange(), paragraph.getTableLevel());
  }

  /**
   * loads all of the list indexes.
   */
  protected void initAll()
  {
    initText();
    initCharacterRuns();
    initParagraphs();
    initSections();
  }

  /**
   * inits the paragraph list indexes.
   */
  private void initParagraphs()
  {
    if (!_parRangeFound)
    {
      int[] point = findRange(_paragraphs, _parStart, _start, _end);
      _parStart = point[0];
      _parEnd = point[1];
      _parRangeFound = true;
    }
  }

  /**
   * inits the character run list indexes.
   */
  private void initCharacterRuns()
  {
    if (!_charRangeFound)
    {
      int[] point = findRange(_characters, _charStart, _start, _end);
      _charStart = point[0];
      _charEnd = point[1];
      _charRangeFound = true;
    }
  }

  /**
   * inits the text piece list indexes.
   */
  private void initText()
  {
    if (!_textRangeFound)
    {
      int[] point = findRange(_text, _textStart, _start, _end);
      _textStart = point[0];
      _textEnd = point[1];
      _textRangeFound = true;
    }
  }

  /**
   * inits the section list indexes.
   */
  private void initSections()
  {
    if (!_sectionRangeFound)
    {
      int[] point = findRange(_sections, _sectionStart, _start, _end);
      _sectionStart = point[0];
      _sectionEnd = point[1];
      _sectionRangeFound = true;
    }
  }

  /**
   * Used to find the list indexes of a particular property.
   *
   * @param rpl A list of property nodes.
   * @param min A hint on where to start looking.
   * @param start The starting character offset.
   * @param end The ending character offset.
   * @return An int array of length 2. The first int is the start index and the
   *         second int is the end index.
   */
  private int[] findRange(List rpl, int min, int start, int end)
  {
    int x = min;
    PropertyNode node = (PropertyNode)rpl.get(x);
    while(node.getEnd() <= start && x < rpl.size()-1)
    {
      x++;
      node = (PropertyNode)rpl.get(x);
    }

    int y = x;
    node = (PropertyNode)rpl.get(y);
    while(node.getEnd() < end && y < rpl.size()-1)
    {
      y++;
      node = (PropertyNode)rpl.get(y);
    }
    return new int[]{x, y + 1};
  }

  /**
   * resets the list indexes.
   */
  private void reset()
  {
    _textRangeFound = false;
    _charRangeFound = false;
    _parRangeFound = false;
    _sectionRangeFound = false;
  }

  /**
   * adjust this range after an insert happens.
   * @param length the length to adjust for
   */
  private void adjustForInsert(int length)
  {
    _end += length;
    reset();
    Range parent = (Range)_parent.get();
    if (parent != null)
    {
      parent.adjustForInsert(length);
    }
  }

}
