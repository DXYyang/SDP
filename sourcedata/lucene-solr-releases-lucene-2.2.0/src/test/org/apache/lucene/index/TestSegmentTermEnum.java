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

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * @author goller
 */
public class TestSegmentTermEnum extends TestCase
{
  Directory dir = new RAMDirectory();

  public void testTermEnum() throws IOException
  {
    IndexWriter writer = null;

    writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);

    // add 100 documents with term : aaa
    // add 100 documents with terms: aaa bbb
    // Therefore, term 'aaa' has document frequency of 200 and term 'bbb' 100
    for (int i = 0; i < 100; i++) {
      addDoc(writer, "aaa");
      addDoc(writer, "aaa bbb");
    }

    writer.close();

    // verify document frequency of terms in an unoptimized index
    verifyDocFreq();

    // merge segments by optimizing the index
    writer = new IndexWriter(dir, new WhitespaceAnalyzer(), false);
    writer.optimize();
    writer.close();

    // verify document frequency of terms in an optimized index
    verifyDocFreq();
  }

  private void verifyDocFreq()
      throws IOException
  {
      IndexReader reader = IndexReader.open(dir);
      TermEnum termEnum = null;

    // create enumeration of all terms
    termEnum = reader.terms();
    // go to the first term (aaa)
    termEnum.next();
    // assert that term is 'aaa'
    assertEquals("aaa", termEnum.term().text());
    assertEquals(200, termEnum.docFreq());
    // go to the second term (bbb)
    termEnum.next();
    // assert that term is 'bbb'
    assertEquals("bbb", termEnum.term().text());
    assertEquals(100, termEnum.docFreq());

    termEnum.close();


    // create enumeration of terms after term 'aaa', including 'aaa'
    termEnum = reader.terms(new Term("content", "aaa"));
    // assert that term is 'aaa'
    assertEquals("aaa", termEnum.term().text());
    assertEquals(200, termEnum.docFreq());
    // go to term 'bbb'
    termEnum.next();
    // assert that term is 'bbb'
    assertEquals("bbb", termEnum.term().text());
    assertEquals(100, termEnum.docFreq());

    termEnum.close();
  }

  private void addDoc(IndexWriter writer, String value) throws IOException
  {
    Document doc = new Document();
    doc.add(new Field("content", value, Field.Store.NO, Field.Index.TOKENIZED));
    writer.addDocument(doc);
  }
}
