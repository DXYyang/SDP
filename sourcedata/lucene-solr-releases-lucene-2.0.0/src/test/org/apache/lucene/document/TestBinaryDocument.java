package org.apache.lucene.document;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.RAMDirectory;

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

/**
 * Tests {@link Document} class.
 *
 * @author Bernhard Messer
 * @version $Id$
 */
public class TestBinaryDocument extends TestCase
{

  String binaryValStored = "this text will be stored as a byte array in the index";
  String binaryValCompressed = "this text will be also stored and compressed as a byte array in the index";
  
  public void testBinaryFieldInIndex()
    throws Exception
  {
    Field binaryFldStored = new Field("binaryStored", binaryValStored.getBytes(), Field.Store.YES);
    Field binaryFldCompressed = new Field("binaryCompressed", binaryValCompressed.getBytes(), Field.Store.COMPRESS);
    Field stringFldStored = new Field("stringStored", binaryValStored, Field.Store.YES, Field.Index.NO, Field.TermVector.NO);
    Field stringFldCompressed = new Field("stringCompressed", binaryValCompressed, Field.Store.COMPRESS, Field.Index.NO, Field.TermVector.NO);
    
    try {
      // binary fields with store off are not allowed
      new Field("fail", binaryValCompressed.getBytes(), Field.Store.NO);
      fail();
    }
    catch (IllegalArgumentException iae) {
      ;
    }
    
    Document doc = new Document();
    
    doc.add(binaryFldStored);
    doc.add(binaryFldCompressed);
    
    doc.add(stringFldStored);
    doc.add(stringFldCompressed);
    
    /** test for field count */
    assertEquals(4, doc.fields.size());
    
    /** add the doc to a ram index */
    RAMDirectory dir = new RAMDirectory();
    IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(), true);
    writer.addDocument(doc);
    writer.close();
    
    /** open a reader and fetch the document */ 
    IndexReader reader = IndexReader.open(dir);
    Document docFromReader = reader.document(0);
    assertTrue(docFromReader != null);
    
    /** fetch the binary stored field and compare it's content with the original one */
    String binaryFldStoredTest = new String(docFromReader.getBinaryValue("binaryStored"));
    assertTrue(binaryFldStoredTest.equals(binaryValStored));
    
    /** fetch the binary compressed field and compare it's content with the original one */
    String binaryFldCompressedTest = new String(docFromReader.getBinaryValue("binaryCompressed"));
    assertTrue(binaryFldCompressedTest.equals(binaryValCompressed));
    
    /** fetch the string field and compare it's content with the original one */
    String stringFldStoredTest = new String(docFromReader.get("stringStored"));
    assertTrue(stringFldStoredTest.equals(binaryValStored));
    
    /** fetch the compressed string field and compare it's content with the original one */
    String stringFldCompressedTest = new String(docFromReader.get("stringCompressed"));
    assertTrue(stringFldCompressedTest.equals(binaryValCompressed));
    
    /** delete the document from index */
    reader.deleteDocument(0);
    assertEquals(0, reader.numDocs());
    
    reader.close();
    
  }
  
}
