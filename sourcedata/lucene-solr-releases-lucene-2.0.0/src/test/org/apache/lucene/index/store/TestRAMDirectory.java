package org.apache.lucene.index.store;

/**
 * Copyright 2005 The Apache Software Foundation
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

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.English;

/**
 * JUnit testcase to test RAMDirectory. RAMDirectory itself is used in many testcases,
 * but not one of them uses an different constructor other than the default constructor.
 * 
 * @author Bernhard Messer
 * 
 * @version $Id: RAMDirectory.java 150537 2004-09-28 22:45:26 +0200 (Di, 28 Sep 2004) cutting $
 */
public class TestRAMDirectory extends TestCase {
  
  private File indexDir = null;
  
  // add enough document so that the index will be larger than RAMDirectory.READ_BUFFER_SIZE
  private final int docsToAdd = 500;
  
  // setup the index
  public void setUp () throws IOException {
    String tempDir = System.getProperty("java.io.tmpdir");
    if (tempDir == null)
      throw new IOException("java.io.tmpdir undefined, cannot run test");
    indexDir = new File(tempDir, "RAMDirIndex");
    
    IndexWriter writer  = new IndexWriter(indexDir, new WhitespaceAnalyzer(), true);
    // add some documents
    Document doc = null;
    for (int i = 0; i < docsToAdd; i++) {
      doc = new Document();
      doc.add(new Field("content", English.intToEnglish(i).trim(), Field.Store.YES, Field.Index.UN_TOKENIZED));
      writer.addDocument(doc);
    }
    assertEquals(docsToAdd, writer.docCount());
    writer.optimize();
    writer.close();
  }
  
  public void testRAMDirectory () throws IOException {
    
    Directory dir = FSDirectory.getDirectory(indexDir, false);
    RAMDirectory ramDir = new RAMDirectory(dir);
    
    // close the underlaying directory and delete the index
    dir.close();
    
    // open reader to test document count
    IndexReader reader = IndexReader.open(ramDir);
    assertEquals(docsToAdd, reader.numDocs());
    
    // open search zo check if all doc's are there
    IndexSearcher searcher = new IndexSearcher(reader);
    
    // search for all documents
    for (int i = 0; i < docsToAdd; i++) {
      Document doc = searcher.doc(i);
      assertTrue(doc.getField("content") != null);
    }

    // cleanup
    reader.close();
    searcher.close();
  }

  public void testRAMDirectoryFile () throws IOException {
    
    RAMDirectory ramDir = new RAMDirectory(indexDir);
    
    // open reader to test document count
    IndexReader reader = IndexReader.open(ramDir);
    assertEquals(docsToAdd, reader.numDocs());
    
    // open search zo check if all doc's are there
    IndexSearcher searcher = new IndexSearcher(reader);
    
    // search for all documents
    for (int i = 0; i < docsToAdd; i++) {
      Document doc = searcher.doc(i);
      assertTrue(doc.getField("content") != null);
    }

    // cleanup
    reader.close();
    searcher.close();
  }
  
  public void testRAMDirectoryString () throws IOException {
    
    RAMDirectory ramDir = new RAMDirectory(indexDir.getCanonicalPath());
    
    // open reader to test document count
    IndexReader reader = IndexReader.open(ramDir);
    assertEquals(docsToAdd, reader.numDocs());
    
    // open search zo check if all doc's are there
    IndexSearcher searcher = new IndexSearcher(reader);
    
    // search for all documents
    for (int i = 0; i < docsToAdd; i++) {
      Document doc = searcher.doc(i);
      assertTrue(doc.getField("content") != null);
    }

    // cleanup
    reader.close();
    searcher.close();
  }

  public void tearDown() {
    // cleanup 
    if (indexDir != null && indexDir.exists()) {
      rmDir (indexDir);
    }
  }
  
  private void rmDir(File dir) {
    File[] files = dir.listFiles();
    for (int i = 0; i < files.length; i++) {
      files[i].delete();
    }
    dir.delete();
  }
}
