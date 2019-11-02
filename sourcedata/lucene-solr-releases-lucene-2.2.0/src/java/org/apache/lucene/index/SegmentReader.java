package org.apache.lucene.index;

/**
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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.util.BitVector;

import java.io.IOException;
import java.util.*;

/**
 * @version $Id$
 */
class SegmentReader extends IndexReader {
  private String segment;
  private SegmentInfo si;

  FieldInfos fieldInfos;
  private FieldsReader fieldsReader;

  TermInfosReader tis;
  TermVectorsReader termVectorsReaderOrig = null;
  ThreadLocal termVectorsLocal = new ThreadLocal();

  BitVector deletedDocs = null;
  private boolean deletedDocsDirty = false;
  private boolean normsDirty = false;
  private boolean undeleteAll = false;

  private boolean rollbackDeletedDocsDirty = false;
  private boolean rollbackNormsDirty = false;
  private boolean rollbackUndeleteAll = false;

  IndexInput freqStream;
  IndexInput proxStream;

  // optionally used for the .nrm file shared by multiple norms
  private IndexInput singleNormStream;

  // Compound File Reader when based on a compound file segment
  CompoundFileReader cfsReader = null;

  private class Norm {
    public Norm(IndexInput in, int number, long normSeek)
    {
      this.in = in;
      this.number = number;
      this.normSeek = normSeek;
    }

    private IndexInput in;
    private byte[] bytes;
    private boolean dirty;
    private int number;
    private long normSeek;
    private boolean rollbackDirty;

    private void reWrite(SegmentInfo si) throws IOException {
      // NOTE: norms are re-written in regular directory, not cfs
      si.advanceNormGen(this.number);
      IndexOutput out = directory().createOutput(si.getNormFileName(this.number));
      try {
        out.writeBytes(bytes, maxDoc());
      } finally {
        out.close();
      }
      this.dirty = false;
    }

    /** Closes the underlying IndexInput for this norm.
     * It is still valid to access all other norm properties after close is called.
     * @throws IOException
     */
    public void close() throws IOException {
      if (in != null && in != singleNormStream) {
        in.close();
      }
      in = null;
    }
  }

  private Hashtable norms = new Hashtable();

  /** The class which implements SegmentReader. */
  private static Class IMPL;
  static {
    try {
      String name =
        System.getProperty("org.apache.lucene.SegmentReader.class",
                           SegmentReader.class.getName());
      IMPL = Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("cannot load SegmentReader class: " + e, e);
    } catch (SecurityException se) {
      try {
        IMPL = Class.forName(SegmentReader.class.getName());
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("cannot load default SegmentReader class: " + e, e);
      }
    }
  }

  protected SegmentReader() { super(null); }

  /**
   * @throws CorruptIndexException if the index is corrupt
   * @throws IOException if there is a low-level IO error
   */
  public static SegmentReader get(SegmentInfo si) throws CorruptIndexException, IOException {
    return get(si.dir, si, null, false, false, BufferedIndexInput.BUFFER_SIZE);
  }

  /**
   * @throws CorruptIndexException if the index is corrupt
   * @throws IOException if there is a low-level IO error
   */
  public static SegmentReader get(SegmentInfo si, int readBufferSize) throws CorruptIndexException, IOException {
    return get(si.dir, si, null, false, false, readBufferSize);
  }

  /**
   * @throws CorruptIndexException if the index is corrupt
   * @throws IOException if there is a low-level IO error
   */
  public static SegmentReader get(SegmentInfos sis, SegmentInfo si,
                                  boolean closeDir) throws CorruptIndexException, IOException {
    return get(si.dir, si, sis, closeDir, true, BufferedIndexInput.BUFFER_SIZE);
  }

  /**
   * @throws CorruptIndexException if the index is corrupt
   * @throws IOException if there is a low-level IO error
   */
  public static SegmentReader get(Directory dir, SegmentInfo si,
                                  SegmentInfos sis,
                                  boolean closeDir, boolean ownDir,
                                  int readBufferSize)
    throws CorruptIndexException, IOException {
    SegmentReader instance;
    try {
      instance = (SegmentReader)IMPL.newInstance();
    } catch (Exception e) {
      throw new RuntimeException("cannot load SegmentReader class: " + e, e);
    }
    instance.init(dir, sis, closeDir, ownDir);
    instance.initialize(si, readBufferSize);
    return instance;
  }

  private void initialize(SegmentInfo si, int readBufferSize) throws CorruptIndexException, IOException {
    segment = si.name;
    this.si = si;

    boolean success = false;

    try {
      // Use compound file directory for some files, if it exists
      Directory cfsDir = directory();
      if (si.getUseCompoundFile()) {
        cfsReader = new CompoundFileReader(directory(), segment + ".cfs", readBufferSize);
        cfsDir = cfsReader;
      }

      // No compound file exists - use the multi-file format
      fieldInfos = new FieldInfos(cfsDir, segment + ".fnm");
      fieldsReader = new FieldsReader(cfsDir, segment, fieldInfos, readBufferSize);

      // Verify two sources of "maxDoc" agree:
      if (fieldsReader.size() != si.docCount) {
        throw new CorruptIndexException("doc counts differ for segment " + si.name + ": fieldsReader shows " + fieldsReader.size() + " but segmentInfo shows " + si.docCount);
      }

      tis = new TermInfosReader(cfsDir, segment, fieldInfos, readBufferSize);
      
      // NOTE: the bitvector is stored using the regular directory, not cfs
      if (hasDeletions(si)) {
        deletedDocs = new BitVector(directory(), si.getDelFileName());

        // Verify # deletes does not exceed maxDoc for this segment:
        if (deletedDocs.count() > maxDoc()) {
          throw new CorruptIndexException("number of deletes (" + deletedDocs.count() + ") exceeds max doc (" + maxDoc() + ") for segment " + si.name);
        }
      }

      // make sure that all index files have been read or are kept open
      // so that if an index update removes them we'll still have them
      freqStream = cfsDir.openInput(segment + ".frq", readBufferSize);
      proxStream = cfsDir.openInput(segment + ".prx", readBufferSize);
      openNorms(cfsDir, readBufferSize);

      if (fieldInfos.hasVectors()) { // open term vector files only as needed
        termVectorsReaderOrig = new TermVectorsReader(cfsDir, segment, fieldInfos, readBufferSize);
      }
      success = true;
    } finally {

      // With lock-less commits, it's entirely possible (and
      // fine) to hit a FileNotFound exception above.  In
      // this case, we want to explicitly close any subset
      // of things that were opened so that we don't have to
      // wait for a GC to do so.
      if (!success) {
        doClose();
      }
    }
  }

  protected void doCommit() throws IOException {
    if (deletedDocsDirty) {               // re-write deleted
      si.advanceDelGen();

      // We can write directly to the actual name (vs to a
      // .tmp & renaming it) because the file is not live
      // until segments file is written:
      deletedDocs.write(directory(), si.getDelFileName());
    }
    if (undeleteAll && si.hasDeletions()) {
      si.clearDelGen();
    }
    if (normsDirty) {               // re-write norms
      si.setNumFields(fieldInfos.size());
      Enumeration values = norms.elements();
      while (values.hasMoreElements()) {
        Norm norm = (Norm) values.nextElement();
        if (norm.dirty) {
          norm.reWrite(si);
        }
      }
    }
    deletedDocsDirty = false;
    normsDirty = false;
    undeleteAll = false;
  }

  protected void doClose() throws IOException {
    if (fieldsReader != null) {
      fieldsReader.close();
    }
    if (tis != null) {
      tis.close();
    }

    if (freqStream != null)
      freqStream.close();
    if (proxStream != null)
      proxStream.close();

    closeNorms();

    if (termVectorsReaderOrig != null)
      termVectorsReaderOrig.close();

    if (cfsReader != null)
      cfsReader.close();
  }

  static boolean hasDeletions(SegmentInfo si) throws IOException {
    // Don't call ensureOpen() here (it could affect performance)
    return si.hasDeletions();
  }

  public boolean hasDeletions() {
    // Don't call ensureOpen() here (it could affect performance)
    return deletedDocs != null;
  }

  static boolean usesCompoundFile(SegmentInfo si) throws IOException {
    return si.getUseCompoundFile();
  }

  static boolean hasSeparateNorms(SegmentInfo si) throws IOException {
    return si.hasSeparateNorms();
  }

  protected void doDelete(int docNum) {
    if (deletedDocs == null)
      deletedDocs = new BitVector(maxDoc());
    deletedDocsDirty = true;
    undeleteAll = false;
    deletedDocs.set(docNum);
  }

  protected void doUndeleteAll() {
      deletedDocs = null;
      deletedDocsDirty = false;
      undeleteAll = true;
  }

  Vector files() throws IOException {
    return new Vector(si.files());
  }

  public TermEnum terms() {
    ensureOpen();
    return tis.terms();
  }

  public TermEnum terms(Term t) throws IOException {
    ensureOpen();
    return tis.terms(t);
  }

  /**
   * @throws CorruptIndexException if the index is corrupt
   * @throws IOException if there is a low-level IO error
   */
  public synchronized Document document(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
    ensureOpen();
    if (isDeleted(n))
      throw new IllegalArgumentException
              ("attempt to access a deleted document");
    return fieldsReader.doc(n, fieldSelector);
  }

  public synchronized boolean isDeleted(int n) {
    return (deletedDocs != null && deletedDocs.get(n));
  }

  public TermDocs termDocs() throws IOException {
    ensureOpen();
    return new SegmentTermDocs(this);
  }

  public TermPositions termPositions() throws IOException {
    ensureOpen();
    return new SegmentTermPositions(this);
  }

  public int docFreq(Term t) throws IOException {
    ensureOpen();
    TermInfo ti = tis.get(t);
    if (ti != null)
      return ti.docFreq;
    else
      return 0;
  }

  public int numDocs() {
    // Don't call ensureOpen() here (it could affect performance)
    int n = maxDoc();
    if (deletedDocs != null)
      n -= deletedDocs.count();
    return n;
  }

  public int maxDoc() {
    // Don't call ensureOpen() here (it could affect performance)
    return si.docCount;
  }

  /**
   * @see IndexReader#getFieldNames(IndexReader.FieldOption fldOption)
   */
  public Collection getFieldNames(IndexReader.FieldOption fieldOption) {
    ensureOpen();

    Set fieldSet = new HashSet();
    for (int i = 0; i < fieldInfos.size(); i++) {
      FieldInfo fi = fieldInfos.fieldInfo(i);
      if (fieldOption == IndexReader.FieldOption.ALL) {
        fieldSet.add(fi.name);
      }
      else if (!fi.isIndexed && fieldOption == IndexReader.FieldOption.UNINDEXED) {
        fieldSet.add(fi.name);
      }
      else if (fi.storePayloads && fieldOption == IndexReader.FieldOption.STORES_PAYLOADS) {
        fieldSet.add(fi.name);
      }
      else if (fi.isIndexed && fieldOption == IndexReader.FieldOption.INDEXED) {
        fieldSet.add(fi.name);
      }
      else if (fi.isIndexed && fi.storeTermVector == false && fieldOption == IndexReader.FieldOption.INDEXED_NO_TERMVECTOR) {
        fieldSet.add(fi.name);
      }
      else if (fi.storeTermVector == true &&
               fi.storePositionWithTermVector == false &&
               fi.storeOffsetWithTermVector == false &&
               fieldOption == IndexReader.FieldOption.TERMVECTOR) {
        fieldSet.add(fi.name);
      }
      else if (fi.isIndexed && fi.storeTermVector && fieldOption == IndexReader.FieldOption.INDEXED_WITH_TERMVECTOR) {
        fieldSet.add(fi.name);
      }
      else if (fi.storePositionWithTermVector && fi.storeOffsetWithTermVector == false && fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_POSITION) {
        fieldSet.add(fi.name);
      }
      else if (fi.storeOffsetWithTermVector && fi.storePositionWithTermVector == false && fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_OFFSET) {
        fieldSet.add(fi.name);
      }
      else if ((fi.storeOffsetWithTermVector && fi.storePositionWithTermVector) &&
                fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_POSITION_OFFSET) {
        fieldSet.add(fi.name);
      }
    }
    return fieldSet;
  }


  public synchronized boolean hasNorms(String field) {
    ensureOpen();
    return norms.containsKey(field);
  }

  static byte[] createFakeNorms(int size) {
    byte[] ones = new byte[size];
    Arrays.fill(ones, DefaultSimilarity.encodeNorm(1.0f));
    return ones;
  }

  private byte[] ones;
  private byte[] fakeNorms() {
    if (ones==null) ones=createFakeNorms(maxDoc());
    return ones;
  }

  // can return null if norms aren't stored
  protected synchronized byte[] getNorms(String field) throws IOException {
    Norm norm = (Norm) norms.get(field);
    if (norm == null) return null;  // not indexed, or norms not stored
    if (norm.bytes == null) {                     // value not yet read
      byte[] bytes = new byte[maxDoc()];
      norms(field, bytes, 0);
      norm.bytes = bytes;                         // cache it
      // it's OK to close the underlying IndexInput as we have cached the
      // norms and will never read them again.
      norm.close();
    }
    return norm.bytes;
  }

  // returns fake norms if norms aren't available
  public synchronized byte[] norms(String field) throws IOException {
    ensureOpen();
    byte[] bytes = getNorms(field);
    if (bytes==null) bytes=fakeNorms();
    return bytes;
  }

  protected void doSetNorm(int doc, String field, byte value)
          throws IOException {
    Norm norm = (Norm) norms.get(field);
    if (norm == null)                             // not an indexed field
      return;

    norm.dirty = true;                            // mark it dirty
    normsDirty = true;

    norms(field)[doc] = value;                    // set the value
  }

  /** Read norms into a pre-allocated array. */
  public synchronized void norms(String field, byte[] bytes, int offset)
    throws IOException {

    ensureOpen();
    Norm norm = (Norm) norms.get(field);
    if (norm == null) {
      System.arraycopy(fakeNorms(), 0, bytes, offset, maxDoc());
      return;
    }

    if (norm.bytes != null) {                     // can copy from cache
      System.arraycopy(norm.bytes, 0, bytes, offset, maxDoc());
      return;
    }

    // Read from disk.  norm.in may be shared across  multiple norms and
    // should only be used in a synchronized context.
    norm.in.seek(norm.normSeek);
    norm.in.readBytes(bytes, offset, maxDoc());
  }


  private void openNorms(Directory cfsDir, int readBufferSize) throws IOException {
    long nextNormSeek = SegmentMerger.NORMS_HEADER.length; //skip header (header unused for now)
    int maxDoc = maxDoc();
    for (int i = 0; i < fieldInfos.size(); i++) {
      FieldInfo fi = fieldInfos.fieldInfo(i);
      if (fi.isIndexed && !fi.omitNorms) {
        Directory d = directory();
        String fileName = si.getNormFileName(fi.number);
        if (!si.hasSeparateNorms(fi.number)) {
          d = cfsDir;
        }
        
        // singleNormFile means multiple norms share this file
        boolean singleNormFile = fileName.endsWith("." + IndexFileNames.NORMS_EXTENSION);
        IndexInput normInput = null;
        long normSeek;

        if (singleNormFile) {
          normSeek = nextNormSeek;
          if (singleNormStream==null) {
            singleNormStream = d.openInput(fileName, readBufferSize);
          }
          // All norms in the .nrm file can share a single IndexInput since
          // they are only used in a synchronized context.
          // If this were to change in the future, a clone could be done here.
          normInput = singleNormStream;
        } else {
          normSeek = 0;
          normInput = d.openInput(fileName);
        }

        norms.put(fi.name, new Norm(normInput, fi.number, normSeek));
        nextNormSeek += maxDoc; // increment also if some norms are separate
      }
    }
  }

  private void closeNorms() throws IOException {
    synchronized (norms) {
      Enumeration enumerator = norms.elements();
      while (enumerator.hasMoreElements()) {
        Norm norm = (Norm) enumerator.nextElement();
        norm.close();
      }
      if (singleNormStream != null) {
        singleNormStream.close();
        singleNormStream = null;
      }
    }
  }
  
  /**
   * Create a clone from the initial TermVectorsReader and store it in the ThreadLocal.
   * @return TermVectorsReader
   */
  private TermVectorsReader getTermVectorsReader() {
    TermVectorsReader tvReader = (TermVectorsReader)termVectorsLocal.get();
    if (tvReader == null) {
      tvReader = (TermVectorsReader)termVectorsReaderOrig.clone();
      termVectorsLocal.set(tvReader);
    }
    return tvReader;
  }
  
  /** Return a term frequency vector for the specified document and field. The
   *  vector returned contains term numbers and frequencies for all terms in
   *  the specified field of this document, if the field had storeTermVector
   *  flag set.  If the flag was not set, the method returns null.
   * @throws IOException
   */
  public TermFreqVector getTermFreqVector(int docNumber, String field) throws IOException {
    // Check if this field is invalid or has no stored term vector
    ensureOpen();
    FieldInfo fi = fieldInfos.fieldInfo(field);
    if (fi == null || !fi.storeTermVector || termVectorsReaderOrig == null) 
      return null;
    
    TermVectorsReader termVectorsReader = getTermVectorsReader();
    if (termVectorsReader == null)
      return null;
    
    return termVectorsReader.get(docNumber, field);
  }


  /** Return an array of term frequency vectors for the specified document.
   *  The array contains a vector for each vectorized field in the document.
   *  Each vector vector contains term numbers and frequencies for all terms
   *  in a given vectorized field.
   *  If no such fields existed, the method returns null.
   * @throws IOException
   */
  public TermFreqVector[] getTermFreqVectors(int docNumber) throws IOException {
    ensureOpen();
    if (termVectorsReaderOrig == null)
      return null;
    
    TermVectorsReader termVectorsReader = getTermVectorsReader();
    if (termVectorsReader == null)
      return null;
    
    return termVectorsReader.get(docNumber);
  }
  
  /** Returns the field infos of this segment */
  FieldInfos fieldInfos() {
    return fieldInfos;
  }
  
  /**
   * Return the name of the segment this reader is reading.
   */
  String getSegmentName() {
    return segment;
  }

  void setSegmentInfo(SegmentInfo info) {
    si = info;
  }

  void startCommit() {
    super.startCommit();
    rollbackDeletedDocsDirty = deletedDocsDirty;
    rollbackNormsDirty = normsDirty;
    rollbackUndeleteAll = undeleteAll;
    Enumeration values = norms.elements();
    while (values.hasMoreElements()) {
      Norm norm = (Norm) values.nextElement();
      norm.rollbackDirty = norm.dirty;
    }
  }

  void rollbackCommit() {
    super.rollbackCommit();
    deletedDocsDirty = rollbackDeletedDocsDirty;
    normsDirty = rollbackNormsDirty;
    undeleteAll = rollbackUndeleteAll;
    Enumeration values = norms.elements();
    while (values.hasMoreElements()) {
      Norm norm = (Norm) values.nextElement();
      norm.dirty = norm.rollbackDirty;
    }
  }
}
