package org.apache.lucene.document;

import junit.framework.TestCase;

import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Hits;

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

/**
 * Tests {@link Document} class.
 *
 * @author Otis Gospodnetic
 * @version $Id$
 */
public class TestDocument extends TestCase
{

  String binaryVal = "this text will be stored as a byte array in the index";
  String binaryVal2 = "this text will be also stored as a byte array in the index";
  
  public void testBinaryField()
    throws Exception
  {
    Document doc = new Document();
    Fieldable stringFld = new Field("string", binaryVal, Field.Store.YES, Field.Index.NO);
    Fieldable binaryFld = new Field("binary", binaryVal.getBytes(), Field.Store.YES);
    Fieldable binaryFld2 = new Field("binary", binaryVal2.getBytes(), Field.Store.YES);
    
    doc.add(stringFld);
    doc.add(binaryFld);
    
    assertEquals(2, doc.fields.size());
    
    assertTrue(binaryFld.isBinary());
    assertTrue(binaryFld.isStored());
    assertFalse(binaryFld.isIndexed());
    assertFalse(binaryFld.isTokenized());
    
    String binaryTest = new String(doc.getBinaryValue("binary"));
    assertTrue(binaryTest.equals(binaryVal));
    
    String stringTest = doc.get("string");
    assertTrue(binaryTest.equals(stringTest));
    
    doc.add(binaryFld2);
    
    assertEquals(3, doc.fields.size());
    
    byte[][] binaryTests = doc.getBinaryValues("binary");
        
    assertEquals(2, binaryTests.length);
    
    binaryTest = new String(binaryTests[0]);
    String binaryTest2 = new String(binaryTests[1]);
    
    assertFalse(binaryTest.equals(binaryTest2));
    
    assertTrue(binaryTest.equals(binaryVal));
    assertTrue(binaryTest2.equals(binaryVal2));
    
    doc.removeField("string");
    assertEquals(2, doc.fields.size());
    
    doc.removeFields("binary");
    assertEquals(0, doc.fields.size());
  }
  
  /**
   * Tests {@link Document#removeField(String)} method for a brand new Document
   * that has not been indexed yet.
   *
   * @throws Exception on error
   */
  public void testRemoveForNewDocument() throws Exception
  {
    Document doc = makeDocumentWithFields();
    assertEquals(8, doc.fields.size());
    doc.removeFields("keyword");
    assertEquals(6, doc.fields.size());
    doc.removeFields("doesnotexists");      // removing non-existing fields is siltenlty ignored
    doc.removeFields("keyword");		// removing a field more than once
    assertEquals(6, doc.fields.size());
    doc.removeField("text");
    assertEquals(5, doc.fields.size());
    doc.removeField("text");
    assertEquals(4, doc.fields.size());
    doc.removeField("text");
    assertEquals(4, doc.fields.size());
    doc.removeField("doesnotexists");       // removing non-existing fields is siltenlty ignored
    assertEquals(4, doc.fields.size());
    doc.removeFields("unindexed");
    assertEquals(2, doc.fields.size());
    doc.removeFields("unstored");
    assertEquals(0, doc.fields.size());
    doc.removeFields("doesnotexists");	// removing non-existing fields is siltenlty ignored
    assertEquals(0, doc.fields.size());
  }

  public void testConstructorExceptions()
  {
    new Field("name", "value", Field.Store.YES, Field.Index.NO);  // okay
    new Field("name", "value", Field.Store.NO, Field.Index.UN_TOKENIZED);  // okay
    try {
      new Field("name", "value", Field.Store.NO, Field.Index.NO);
      fail();
    } catch(IllegalArgumentException e) {
      // expected exception
    }
    new Field("name", "value", Field.Store.YES, Field.Index.NO, Field.TermVector.NO); // okay
    try {
      new Field("name", "value", Field.Store.YES, Field.Index.NO, Field.TermVector.YES);
      fail();
    } catch(IllegalArgumentException e) {
      // expected exception
    }
  }
  
    /**
     * Tests {@link Document#getValues(String)} method for a brand new Document
     * that has not been indexed yet.
     *
     * @throws Exception on error
     */
    public void testGetValuesForNewDocument() throws Exception
    {
        doAssert(makeDocumentWithFields(), false);
    }

    /**
     * Tests {@link Document#getValues(String)} method for a Document retrieved from
     * an index.
     *
     * @throws Exception on error
     */
    public void testGetValuesForIndexedDocument() throws Exception
    {
        RAMDirectory dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(), true);
        writer.addDocument(makeDocumentWithFields());
        writer.close();

        Searcher searcher = new IndexSearcher(dir);

	// search for something that does exists
	Query query = new TermQuery(new Term("keyword", "test1"));

	// ensure that queries return expected results without DateFilter first
        Hits hits = searcher.search(query);
	assertEquals(1, hits.length());

         doAssert(hits.doc(0), true);
         searcher.close();
    }

    private Document makeDocumentWithFields()
    {
        Document doc = new Document();
        doc.add(new Field(  "keyword",   "test1", Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(  "keyword",   "test2", Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(     "text",      "test1", Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field(     "text",      "test2", Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("unindexed", "test1", Field.Store.YES, Field.Index.NO));
        doc.add(new Field("unindexed", "test2", Field.Store.YES, Field.Index.NO));
        doc.add(new Field( "unstored",  "test1", Field.Store.NO, Field.Index.TOKENIZED));
        doc.add(new Field( "unstored",  "test2", Field.Store.NO, Field.Index.TOKENIZED));
        return doc;
    }

    private void doAssert(Document doc, boolean fromIndex)
    {
        String[] keywordFieldValues   = doc.getValues("keyword");
        String[] textFieldValues      = doc.getValues("text");
        String[] unindexedFieldValues = doc.getValues("unindexed");
        String[] unstoredFieldValues  = doc.getValues("unstored");

        assertTrue(keywordFieldValues.length   == 2);
        assertTrue(textFieldValues.length      == 2);
        assertTrue(unindexedFieldValues.length == 2);
        // this test cannot work for documents retrieved from the index
        // since unstored fields will obviously not be returned
        if (! fromIndex)
        {
            assertTrue(unstoredFieldValues.length  == 2);
        }

        assertTrue(keywordFieldValues[0].equals("test1"));
        assertTrue(keywordFieldValues[1].equals("test2"));
        assertTrue(textFieldValues[0].equals("test1"));
        assertTrue(textFieldValues[1].equals("test2"));
        assertTrue(unindexedFieldValues[0].equals("test1"));
        assertTrue(unindexedFieldValues[1].equals("test2"));
        // this test cannot work for documents retrieved from the index
        // since unstored fields will obviously not be returned
        if (! fromIndex)
        {
            assertTrue(unstoredFieldValues[0].equals("test1"));
            assertTrue(unstoredFieldValues[1].equals("test2"));
        }
    }
}
