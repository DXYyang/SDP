package org.apache.lucene.document;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.util.Parameter;

import java.io.Reader;
import java.io.Serializable;

/**
  A field is a section of a Document.  Each field has two parts, a name and a
  value.  Values may be free text, provided as a String or as a Reader, or they
  may be atomic keywords, which are not further processed.  Such keywords may
  be used to represent dates, urls, etc.  Fields are optionally stored in the
  index, so that they may be returned with hits on the document.
  */

public final class Field implements Serializable {
  private String name = "body";
  
  // the one and only data object for all different kind of field values
  private Object fieldsData = null;
  
  private boolean storeTermVector = false;
  private boolean storeOffsetWithTermVector = false; 
  private boolean storePositionWithTermVector = false;
  private boolean omitNorms = false;
  private boolean isStored = false;
  private boolean isIndexed = true;
  private boolean isTokenized = true;
  private boolean isBinary = false;
  private boolean isCompressed = false;
  
  private float boost = 1.0f;
  
  /** Specifies whether and how a field should be stored. */
  public static final class Store extends Parameter implements Serializable {

    private Store(String name) {
      super(name);
    }

    /** Store the original field value in the index in a compressed form. This is
     * useful for long documents and for binary valued fields.
     */
    public static final Store COMPRESS = new Store("COMPRESS");

    /** Store the original field value in the index. This is useful for short texts
     * like a document's title which should be displayed with the results. The
     * value is stored in its original form, i.e. no analyzer is used before it is
     * stored.
     */
    public static final Store YES = new Store("YES");

    /** Do not store the field value in the index. */
    public static final Store NO = new Store("NO");
  }

  /** Specifies whether and how a field should be indexed. */
  public static final class Index extends Parameter implements Serializable {

    private Index(String name) {
      super(name);
    }

    /** Do not index the field value. This field can thus not be searched,
     * but one can still access its contents provided it is
     * {@link Field.Store stored}. */
    public static final Index NO = new Index("NO");

    /** Index the field's value so it can be searched. An Analyzer will be used
     * to tokenize and possibly further normalize the text before its
     * terms will be stored in the index. This is useful for common text.
     */
    public static final Index TOKENIZED = new Index("TOKENIZED");

    /** Index the field's value without using an Analyzer, so it can be searched.
     * As no analyzer is used the value will be stored as a single term. This is
     * useful for unique Ids like product numbers.
     */
    public static final Index UN_TOKENIZED = new Index("UN_TOKENIZED");

    /** Index the field's value without an Analyzer, and disable
     * the storing of norms.  No norms means that index-time boosting
     * and field length normalization will be disabled.  The benefit is
     * less memory usage as norms take up one byte per indexed field
     * for every document in the index.
     */
    public static final Index NO_NORMS = new Index("NO_NORMS");

  }

  /** Specifies whether and how a field should have term vectors. */
  public static final class TermVector  extends Parameter implements Serializable {
    
    private TermVector(String name) {
      super(name);
    }
    
    /** Do not store term vectors. 
     */
    public static final TermVector NO = new TermVector("NO");
    
    /** Store the term vectors of each document. A term vector is a list
     * of the document's terms and their number of occurences in that document. */
    public static final TermVector YES = new TermVector("YES");
    
    /**
     * Store the term vector + token position information
     * 
     * @see #YES
     */ 
    public static final TermVector WITH_POSITIONS = new TermVector("WITH_POSITIONS");
    
    /**
     * Store the term vector + Token offset information
     * 
     * @see #YES
     */ 
    public static final TermVector WITH_OFFSETS = new TermVector("WITH_OFFSETS");
    
    /**
     * Store the term vector + Token position and offset information
     * 
     * @see #YES
     * @see #WITH_POSITIONS
     * @see #WITH_OFFSETS
     */ 
    public static final TermVector WITH_POSITIONS_OFFSETS = new TermVector("WITH_POSITIONS_OFFSETS");
  }
  
  /** Sets the boost factor hits on this field.  This value will be
   * multiplied into the score of all hits on this this field of this
   * document.
   *
   * <p>The boost is multiplied by {@link Document#getBoost()} of the document
   * containing this field.  If a document has multiple fields with the same
   * name, all such values are multiplied together.  This product is then
   * multipled by the value {@link Similarity#lengthNorm(String,int)}, and
   * rounded by {@link Similarity#encodeNorm(float)} before it is stored in the
   * index.  One should attempt to ensure that this product does not overflow
   * the range of that encoding.
   *
   * @see Document#setBoost(float)
   * @see Similarity#lengthNorm(String, int)
   * @see Similarity#encodeNorm(float)
   */
  public void setBoost(float boost) {
    this.boost = boost;
  }

  /** Returns the boost factor for hits for this field.
   *
   * <p>The default value is 1.0.
   *
   * <p>Note: this value is not stored directly with the document in the index.
   * Documents returned from {@link IndexReader#document(int)} and
   * {@link Hits#doc(int)} may thus not have the same value present as when
   * this field was indexed.
   *
   * @see #setBoost(float)
   */
  public float getBoost() {
    return boost;
  }
  /** Returns the name of the field as an interned string.
   * For example "date", "title", "body", ...
   */
  public String name()    { return name; }

  /** The value of the field as a String, or null.  If null, the Reader value
   * or binary value is used.  Exactly one of stringValue(), readerValue(), and
   * binaryValue() must be set. */
  public String stringValue()   { return fieldsData instanceof String ? (String)fieldsData : null; }
  
  /** The value of the field as a Reader, or null.  If null, the String value
   * or binary value is  used.  Exactly one of stringValue(), readerValue(),
   * and binaryValue() must be set. */
  public Reader readerValue()   { return fieldsData instanceof Reader ? (Reader)fieldsData : null; }
  
  /** The value of the field in Binary, or null.  If null, the Reader or
   * String value is used.  Exactly one of stringValue(), readerValue() and
   * binaryValue() must be set. */
  public byte[] binaryValue()   { return fieldsData instanceof byte[] ? (byte[])fieldsData : null; }
  
  /**
   * Create a field by specifying its name, value and how it will
   * be saved in the index. Term vectors will not be stored in the index.
   * 
   * @param name The name of the field
   * @param value The string to process
   * @param store Whether <code>value</code> should be stored in the index
   * @param index Whether the field should be indexed, and if so, if it should
   *  be tokenized before indexing 
   * @throws NullPointerException if name or value is <code>null</code>
   * @throws IllegalArgumentException if the field is neither stored nor indexed 
   */
  public Field(String name, String value, Store store, Index index) {
    this(name, value, store, index, TermVector.NO);
  }
  
  /**
   * Create a field by specifying its name, value and how it will
   * be saved in the index.
   * 
   * @param name The name of the field
   * @param value The string to process
   * @param store Whether <code>value</code> should be stored in the index
   * @param index Whether the field should be indexed, and if so, if it should
   *  be tokenized before indexing 
   * @param termVector Whether term vector should be stored
   * @throws NullPointerException if name or value is <code>null</code>
   * @throws IllegalArgumentException in any of the following situations:
   * <ul> 
   *  <li>the field is neither stored nor indexed</li> 
   *  <li>the field is not indexed but termVector is <code>TermVector.YES</code></li>
   * </ul> 
   */ 
  public Field(String name, String value, Store store, Index index, TermVector termVector) {
    if (name == null)
      throw new NullPointerException("name cannot be null");
    if (value == null)
      throw new NullPointerException("value cannot be null");
    if (name.length() == 0 && value.length() == 0)
      throw new IllegalArgumentException("name and value cannot both be empty");
    if (index == Index.NO && store == Store.NO)
      throw new IllegalArgumentException("it doesn't make sense to have a field that "
         + "is neither indexed nor stored");
    if (index == Index.NO && termVector != TermVector.NO)
      throw new IllegalArgumentException("cannot store term vector information "
         + "for a field that is not indexed");
          
    this.name = name.intern();        // field names are interned
    this.fieldsData = value;

    if (store == Store.YES){
      this.isStored = true;
      this.isCompressed = false;
    }
    else if (store == Store.COMPRESS) {
      this.isStored = true;
      this.isCompressed = true;
    }
    else if (store == Store.NO){
      this.isStored = false;
      this.isCompressed = false;
    }
    else
      throw new IllegalArgumentException("unknown store parameter " + store);
   
    if (index == Index.NO) {
      this.isIndexed = false;
      this.isTokenized = false;
    } else if (index == Index.TOKENIZED) {
      this.isIndexed = true;
      this.isTokenized = true;
    } else if (index == Index.UN_TOKENIZED) {
      this.isIndexed = true;
      this.isTokenized = false;
    } else if (index == Index.NO_NORMS) {
      this.isIndexed = true;
      this.isTokenized = false;
      this.omitNorms = true;
    } else {
      throw new IllegalArgumentException("unknown index parameter " + index);
    }
    
    this.isBinary = false;

    setStoreTermVector(termVector);
  }

  /**
   * Create a tokenized and indexed field that is not stored. Term vectors will
   * not be stored.
   * 
   * @param name The name of the field
   * @param reader The reader with the content
   * @throws NullPointerException if name or reader is <code>null</code>
   */
  public Field(String name, Reader reader) {
    this(name, reader, TermVector.NO);
  }

  /**
   * Create a tokenized and indexed field that is not stored, optionally with 
   * storing term vectors.
   * 
   * @param name The name of the field
   * @param reader The reader with the content
   * @param termVector Whether term vector should be stored
   * @throws NullPointerException if name or reader is <code>null</code>
   */ 
  public Field(String name, Reader reader, TermVector termVector) {
    if (name == null)
      throw new NullPointerException("name cannot be null");
    if (reader == null)
      throw new NullPointerException("reader cannot be null");
    
    this.name = name.intern();        // field names are interned
    this.fieldsData = reader;
    
    this.isStored = false;
    this.isCompressed = false;
    
    this.isIndexed = true;
    this.isTokenized = true;
    
    this.isBinary = false;
    
    setStoreTermVector(termVector);
  }


  
  /**
   * Create a stored field with binary value. Optionally the value may be compressed.
   * 
   * @param name The name of the field
   * @param value The binary value
   * @param store How <code>value</code> should be stored (compressed or not)
   * @throws IllegalArgumentException if store is <code>Store.NO</code> 
   */
  public Field(String name, byte[] value, Store store) {
    if (name == null)
      throw new IllegalArgumentException("name cannot be null");
    if (value == null)
      throw new IllegalArgumentException("value cannot be null");
    
    this.name = name.intern();
    this.fieldsData = value;
    
    if (store == Store.YES){
      this.isStored = true;
      this.isCompressed = false;
    }
    else if (store == Store.COMPRESS) {
      this.isStored = true;
      this.isCompressed = true;
    }
    else if (store == Store.NO)
      throw new IllegalArgumentException("binary values can't be unstored");
    else
      throw new IllegalArgumentException("unknown store parameter " + store);
    
    this.isIndexed   = false;
    this.isTokenized = false;
    
    this.isBinary    = true;
    
    setStoreTermVector(TermVector.NO);
  }
  
  private void setStoreTermVector(TermVector termVector) {
    if (termVector == TermVector.NO) {
      this.storeTermVector = false;
      this.storePositionWithTermVector = false;
      this.storeOffsetWithTermVector = false;
    } 
    else if (termVector == TermVector.YES) {
      this.storeTermVector = true;
      this.storePositionWithTermVector = false;
      this.storeOffsetWithTermVector = false;
    }
    else if (termVector == TermVector.WITH_POSITIONS) {
      this.storeTermVector = true;
      this.storePositionWithTermVector = true;
      this.storeOffsetWithTermVector = false;
    } 
    else if (termVector == TermVector.WITH_OFFSETS) {
      this.storeTermVector = true;
      this.storePositionWithTermVector = false;
      this.storeOffsetWithTermVector = true;
    } 
    else if (termVector == TermVector.WITH_POSITIONS_OFFSETS) {
      this.storeTermVector = true;
      this.storePositionWithTermVector = true;
      this.storeOffsetWithTermVector = true;
    } 
    else {
      throw new IllegalArgumentException("unknown termVector parameter " + termVector);
    }
  }
  
  /** True iff the value of the field is to be stored in the index for return
    with search hits.  It is an error for this to be true if a field is
    Reader-valued. */
  public final boolean  isStored()  { return isStored; }

  /** True iff the value of the field is to be indexed, so that it may be
    searched on. */
  public final boolean  isIndexed()   { return isIndexed; }

  /** True iff the value of the field should be tokenized as text prior to
    indexing.  Un-tokenized fields are indexed as a single word and may not be
    Reader-valued. */
  public final boolean  isTokenized()   { return isTokenized; }
  
  /** True if the value of the field is stored and compressed within the index */
  public final boolean  isCompressed()   { return isCompressed; }

  /** True iff the term or terms used to index this field are stored as a term
   *  vector, available from {@link IndexReader#getTermFreqVector(int,String)}.
   *  These methods do not provide access to the original content of the field,
   *  only to terms used to index it. If the original content must be
   *  preserved, use the <code>stored</code> attribute instead.
   *
   * @see IndexReader#getTermFreqVector(int, String)
   */
  public final boolean isTermVectorStored() { return storeTermVector; }
  
  /**
   * True iff terms are stored as term vector together with their offsets 
   * (start and end positon in source text).
   */
  public boolean isStoreOffsetWithTermVector(){ 
    return storeOffsetWithTermVector; 
  } 
  
  /**
   * True iff terms are stored as term vector together with their token positions.
   */
  public boolean isStorePositionWithTermVector(){ 
    return storePositionWithTermVector; 
  }
      
  /** True iff the value of the filed is stored as binary */
  public final boolean  isBinary()      { return isBinary; }
  
  /** True if norms are omitted for this indexed field */
  public boolean getOmitNorms() { return omitNorms; }

  /** Expert:
   *
   * If set, omit normalization factors associated with this indexed field.
   * This effectively disables indexing boosts and length normalization for this field.
   */
  public void setOmitNorms(boolean omitNorms) { this.omitNorms=omitNorms; }
  
  /** Prints a Field for human consumption. */
  public final String toString() {
    StringBuffer result = new StringBuffer();
    if (isStored) {
      result.append("stored");
      if (isCompressed)
        result.append("/compressed");
      else
        result.append("/uncompressed");
    }
    if (isIndexed) {
      if (result.length() > 0)
        result.append(",");
      result.append("indexed");
    }
    if (isTokenized) {
      if (result.length() > 0)
        result.append(",");
      result.append("tokenized");
    }
    if (storeTermVector) {
      if (result.length() > 0)
        result.append(",");
      result.append("termVector");
    }
    if (storeOffsetWithTermVector) { 
      if (result.length() > 0) 
        result.append(","); 
      result.append("termVectorOffsets"); 
    } 
    if (storePositionWithTermVector) { 
      if (result.length() > 0) 
        result.append(","); 
      result.append("termVectorPosition"); 
    } 
    if (isBinary) {
      if (result.length() > 0)
        result.append(",");
      result.append("binary");
    }
    if (omitNorms) {
      result.append(",omitNorms");
    }
    result.append('<');
    result.append(name);
    result.append(':');
    
    if (fieldsData != null) {
      result.append(fieldsData);
    }
    
    result.append('>');
    return result.toString();
  }

}
