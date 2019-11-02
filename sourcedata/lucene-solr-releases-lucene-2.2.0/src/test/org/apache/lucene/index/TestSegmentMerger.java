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

import junit.framework.TestCase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.Collection;

public class TestSegmentMerger extends TestCase {
  //The variables for the new merged segment
  private Directory mergedDir = new RAMDirectory();
  private String mergedSegment = "test";
  //First segment to be merged
  private Directory merge1Dir = new RAMDirectory();
  private Document doc1 = new Document();
  private String merge1Segment = "test-1";
  private SegmentReader reader1 = null;
  //Second Segment to be merged
  private Directory merge2Dir = new RAMDirectory();
  private Document doc2 = new Document();
  private String merge2Segment = "test-2";
  private SegmentReader reader2 = null;
  

  public TestSegmentMerger(String s) {
    super(s);
  }

  protected void setUp() throws IOException {
    DocHelper.setupDoc(doc1);
    DocHelper.writeDoc(merge1Dir, merge1Segment, doc1);
    DocHelper.setupDoc(doc2);
    DocHelper.writeDoc(merge2Dir, merge2Segment, doc2);
    reader1 = SegmentReader.get(new SegmentInfo(merge1Segment, 1, merge1Dir));
    reader2 = SegmentReader.get(new SegmentInfo(merge2Segment, 1, merge2Dir));
  }

  public void test() {
    assertTrue(mergedDir != null);
    assertTrue(merge1Dir != null);
    assertTrue(merge2Dir != null);
    assertTrue(reader1 != null);
    assertTrue(reader2 != null);
  }
  
  public void testMerge() throws IOException {                             
    SegmentMerger merger = new SegmentMerger(mergedDir, mergedSegment);
    merger.add(reader1);
    merger.add(reader2);
    int docsMerged = merger.merge();
    merger.closeReaders();
    assertTrue(docsMerged == 2);
    //Should be able to open a new SegmentReader against the new directory
    SegmentReader mergedReader = SegmentReader.get(new SegmentInfo(mergedSegment, docsMerged, mergedDir, false, true));
    assertTrue(mergedReader != null);
    assertTrue(mergedReader.numDocs() == 2);
    Document newDoc1 = mergedReader.document(0);
    assertTrue(newDoc1 != null);
    //There are 2 unstored fields on the document
    assertTrue(DocHelper.numFields(newDoc1) == DocHelper.numFields(doc1) - DocHelper.unstored.size());
    Document newDoc2 = mergedReader.document(1);
    assertTrue(newDoc2 != null);
    assertTrue(DocHelper.numFields(newDoc2) == DocHelper.numFields(doc2) - DocHelper.unstored.size());
    
    TermDocs termDocs = mergedReader.termDocs(new Term(DocHelper.TEXT_FIELD_2_KEY, "field"));
    assertTrue(termDocs != null);
    assertTrue(termDocs.next() == true);
    
    Collection stored = mergedReader.getFieldNames(IndexReader.FieldOption.INDEXED_WITH_TERMVECTOR);
    assertTrue(stored != null);
    //System.out.println("stored size: " + stored.size());
    assertTrue("We do not have 4 fields that were indexed with term vector",stored.size() == 4);
    
    TermFreqVector vector = mergedReader.getTermFreqVector(0, DocHelper.TEXT_FIELD_2_KEY);
    assertTrue(vector != null);
    String [] terms = vector.getTerms();
    assertTrue(terms != null);
    //System.out.println("Terms size: " + terms.length);
    assertTrue(terms.length == 3);
    int [] freqs = vector.getTermFrequencies();
    assertTrue(freqs != null);
    //System.out.println("Freqs size: " + freqs.length);
    assertTrue(vector instanceof TermPositionVector == true);
    
    for (int i = 0; i < terms.length; i++) {
      String term = terms[i];
      int freq = freqs[i];
      //System.out.println("Term: " + term + " Freq: " + freq);
      assertTrue(DocHelper.FIELD_2_TEXT.indexOf(term) != -1);
      assertTrue(DocHelper.FIELD_2_FREQS[i] == freq);
    }

    TestSegmentReader.checkNorms(mergedReader);
  }    
}
