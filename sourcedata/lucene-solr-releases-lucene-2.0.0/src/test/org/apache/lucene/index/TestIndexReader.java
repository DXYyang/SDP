package org.apache.lucene.index;

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


import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.Collection;
import java.io.IOException;
import java.io.File;

public class TestIndexReader extends TestCase
{
    /** Main for running test case by itself. */
    public static void main(String args[]) {
        TestRunner.run (new TestSuite(TestIndexReader.class));
//        TestRunner.run (new TestIndexReader("testBasicDelete"));
//        TestRunner.run (new TestIndexReader("testDeleteReaderWriterConflict"));
//        TestRunner.run (new TestIndexReader("testDeleteReaderReaderConflict"));
//        TestRunner.run (new TestIndexReader("testFilesOpenClose"));
    }

    public TestIndexReader(String name) {
        super(name);
    }

    public void testIsCurrent() throws Exception
    {
      RAMDirectory d = new RAMDirectory();
      IndexWriter writer = new IndexWriter(d, new StandardAnalyzer(), true);
      addDocumentWithFields(writer);
      writer.close();
      // set up reader:
      IndexReader reader = IndexReader.open(d);
      assertTrue(reader.isCurrent());
      // modify index by adding another document:
      writer = new IndexWriter(d, new StandardAnalyzer(), false);
      addDocumentWithFields(writer);
      writer.close();
      assertFalse(reader.isCurrent());
      // re-create index:
      writer = new IndexWriter(d, new StandardAnalyzer(), true);
      addDocumentWithFields(writer);
      writer.close();
      assertFalse(reader.isCurrent());
      reader.close();
    }

    /**
     * Tests the IndexReader.getFieldNames implementation
     * @throws Exception on error
     */
    public void testGetFieldNames() throws Exception
    {
        RAMDirectory d = new RAMDirectory();
        // set up writer
        IndexWriter writer = new IndexWriter(d, new StandardAnalyzer(), true);
        addDocumentWithFields(writer);
        writer.close();
        // set up reader
        IndexReader reader = IndexReader.open(d);
        Collection fieldNames = reader.getFieldNames(IndexReader.FieldOption.ALL);
        assertTrue(fieldNames.contains("keyword"));
        assertTrue(fieldNames.contains("text"));
        assertTrue(fieldNames.contains("unindexed"));
        assertTrue(fieldNames.contains("unstored"));
        // add more documents
        writer = new IndexWriter(d, new StandardAnalyzer(), false);
        // want to get some more segments here
        for (int i = 0; i < 5*writer.getMergeFactor(); i++)
        {
            addDocumentWithFields(writer);
        }
        // new fields are in some different segments (we hope)
        for (int i = 0; i < 5*writer.getMergeFactor(); i++)
        {
            addDocumentWithDifferentFields(writer);
        }
        // new termvector fields
        for (int i = 0; i < 5*writer.getMergeFactor(); i++)
        {
          addDocumentWithTermVectorFields(writer);
        }
        
        writer.close();
        // verify fields again
        reader = IndexReader.open(d);
        fieldNames = reader.getFieldNames(IndexReader.FieldOption.ALL);
        assertEquals(13, fieldNames.size());    // the following fields
        assertTrue(fieldNames.contains("keyword"));
        assertTrue(fieldNames.contains("text"));
        assertTrue(fieldNames.contains("unindexed"));
        assertTrue(fieldNames.contains("unstored"));
        assertTrue(fieldNames.contains("keyword2"));
        assertTrue(fieldNames.contains("text2"));
        assertTrue(fieldNames.contains("unindexed2"));
        assertTrue(fieldNames.contains("unstored2"));
        assertTrue(fieldNames.contains("tvnot"));
        assertTrue(fieldNames.contains("termvector"));
        assertTrue(fieldNames.contains("tvposition"));
        assertTrue(fieldNames.contains("tvoffset"));
        assertTrue(fieldNames.contains("tvpositionoffset"));
        
        // verify that only indexed fields were returned
        fieldNames = reader.getFieldNames(IndexReader.FieldOption.INDEXED);
        assertEquals(11, fieldNames.size());    // 6 original + the 5 termvector fields 
        assertTrue(fieldNames.contains("keyword"));
        assertTrue(fieldNames.contains("text"));
        assertTrue(fieldNames.contains("unstored"));
        assertTrue(fieldNames.contains("keyword2"));
        assertTrue(fieldNames.contains("text2"));
        assertTrue(fieldNames.contains("unstored2"));
        assertTrue(fieldNames.contains("tvnot"));
        assertTrue(fieldNames.contains("termvector"));
        assertTrue(fieldNames.contains("tvposition"));
        assertTrue(fieldNames.contains("tvoffset"));
        assertTrue(fieldNames.contains("tvpositionoffset"));
        
        // verify that only unindexed fields were returned
        fieldNames = reader.getFieldNames(IndexReader.FieldOption.UNINDEXED);
        assertEquals(2, fieldNames.size());    // the following fields
        assertTrue(fieldNames.contains("unindexed"));
        assertTrue(fieldNames.contains("unindexed2"));
                
        // verify index term vector fields  
        fieldNames = reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR);
        assertEquals(1, fieldNames.size());    // 1 field has term vector only
        assertTrue(fieldNames.contains("termvector"));
        
        fieldNames = reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_POSITION);
        assertEquals(1, fieldNames.size());    // 4 fields are indexed with term vectors
        assertTrue(fieldNames.contains("tvposition"));
        
        fieldNames = reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_OFFSET);
        assertEquals(1, fieldNames.size());    // 4 fields are indexed with term vectors
        assertTrue(fieldNames.contains("tvoffset"));
                
        fieldNames = reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_POSITION_OFFSET);
        assertEquals(1, fieldNames.size());    // 4 fields are indexed with term vectors
        assertTrue(fieldNames.contains("tvpositionoffset"));
        
    }


    private void assertTermDocsCount(String msg,
                                     IndexReader reader,
                                     Term term,
                                     int expected)
    throws IOException
    {
        TermDocs tdocs = null;

        try {
            tdocs = reader.termDocs(term);
            assertNotNull(msg + ", null TermDocs", tdocs);
            int count = 0;
            while(tdocs.next()) {
                count++;
            }
            assertEquals(msg + ", count mismatch", expected, count);

        } finally {
            if (tdocs != null)
                tdocs.close();
        }

    }



    public void testBasicDelete() throws IOException
    {
        Directory dir = new RAMDirectory();

        IndexWriter writer = null;
        IndexReader reader = null;
        Term searchTerm = new Term("content", "aaa");

        //  add 100 documents with term : aaa
        writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
        for (int i = 0; i < 100; i++)
        {
            addDoc(writer, searchTerm.text());
        }
        writer.close();

        // OPEN READER AT THIS POINT - this should fix the view of the
        // index at the point of having 100 "aaa" documents and 0 "bbb"
        reader = IndexReader.open(dir);
        assertEquals("first docFreq", 100, reader.docFreq(searchTerm));
        assertTermDocsCount("first reader", reader, searchTerm, 100);

        // DELETE DOCUMENTS CONTAINING TERM: aaa
        int deleted = 0;
        reader = IndexReader.open(dir);
        deleted = reader.deleteDocuments(searchTerm);
        assertEquals("deleted count", 100, deleted);
        assertEquals("deleted docFreq", 100, reader.docFreq(searchTerm));
        assertTermDocsCount("deleted termDocs", reader, searchTerm, 0);
        reader.close();

        // CREATE A NEW READER and re-test
        reader = IndexReader.open(dir);
        assertEquals("deleted docFreq", 100, reader.docFreq(searchTerm));
        assertTermDocsCount("deleted termDocs", reader, searchTerm, 0);
        reader.close();
    }


    public void testDeleteReaderWriterConflictUnoptimized() throws IOException{
      deleteReaderWriterConflict(false);
    }
    
    public void testDeleteReaderWriterConflictOptimized() throws IOException{
        deleteReaderWriterConflict(true);
    }

    private void deleteReaderWriterConflict(boolean optimize) throws IOException
    {
        //Directory dir = new RAMDirectory();
        Directory dir = getDirectory(true);

        Term searchTerm = new Term("content", "aaa");
        Term searchTerm2 = new Term("content", "bbb");

        //  add 100 documents with term : aaa
        IndexWriter writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
        for (int i = 0; i < 100; i++)
        {
            addDoc(writer, searchTerm.text());
        }
        writer.close();

        // OPEN READER AT THIS POINT - this should fix the view of the
        // index at the point of having 100 "aaa" documents and 0 "bbb"
        IndexReader reader = IndexReader.open(dir);
        assertEquals("first docFreq", 100, reader.docFreq(searchTerm));
        assertEquals("first docFreq", 0, reader.docFreq(searchTerm2));
        assertTermDocsCount("first reader", reader, searchTerm, 100);
        assertTermDocsCount("first reader", reader, searchTerm2, 0);

        // add 100 documents with term : bbb
        writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), false);
        for (int i = 0; i < 100; i++)
        {
            addDoc(writer, searchTerm2.text());
        }

        // REQUEST OPTIMIZATION
        // This causes a new segment to become current for all subsequent
        // searchers. Because of this, deletions made via a previously open
        // reader, which would be applied to that reader's segment, are lost
        // for subsequent searchers/readers
        if(optimize)
          writer.optimize();
        writer.close();

        // The reader should not see the new data
        assertEquals("first docFreq", 100, reader.docFreq(searchTerm));
        assertEquals("first docFreq", 0, reader.docFreq(searchTerm2));
        assertTermDocsCount("first reader", reader, searchTerm, 100);
        assertTermDocsCount("first reader", reader, searchTerm2, 0);


        // DELETE DOCUMENTS CONTAINING TERM: aaa
        // NOTE: the reader was created when only "aaa" documents were in
        int deleted = 0;
        try {
            deleted = reader.deleteDocuments(searchTerm);
            fail("Delete allowed on an index reader with stale segment information");
        } catch (IOException e) {
            /* success */
        }

        // Re-open index reader and try again. This time it should see
        // the new data.
        reader.close();
        reader = IndexReader.open(dir);
        assertEquals("first docFreq", 100, reader.docFreq(searchTerm));
        assertEquals("first docFreq", 100, reader.docFreq(searchTerm2));
        assertTermDocsCount("first reader", reader, searchTerm, 100);
        assertTermDocsCount("first reader", reader, searchTerm2, 100);

        deleted = reader.deleteDocuments(searchTerm);
        assertEquals("deleted count", 100, deleted);
        assertEquals("deleted docFreq", 100, reader.docFreq(searchTerm));
        assertEquals("deleted docFreq", 100, reader.docFreq(searchTerm2));
        assertTermDocsCount("deleted termDocs", reader, searchTerm, 0);
        assertTermDocsCount("deleted termDocs", reader, searchTerm2, 100);
        reader.close();

        // CREATE A NEW READER and re-test
        reader = IndexReader.open(dir);
        assertEquals("deleted docFreq", 100, reader.docFreq(searchTerm));
        assertEquals("deleted docFreq", 100, reader.docFreq(searchTerm2));
        assertTermDocsCount("deleted termDocs", reader, searchTerm, 0);
        assertTermDocsCount("deleted termDocs", reader, searchTerm2, 100);
        reader.close();
    }

  private Directory getDirectory(boolean create) throws IOException {
    return FSDirectory.getDirectory(new File(System.getProperty("tempDir"), "testIndex"), create);
  }

  public void testFilesOpenClose() throws IOException
    {
        // Create initial data set
        Directory dir = getDirectory(true);
        IndexWriter writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
        addDoc(writer, "test");
        writer.close();
        dir.close();

        // Try to erase the data - this ensures that the writer closed all files
        dir = getDirectory(true);

        // Now create the data set again, just as before
        writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
        addDoc(writer, "test");
        writer.close();
        dir.close();

        // Now open existing directory and test that reader closes all files
        dir = getDirectory(false);
        IndexReader reader1 = IndexReader.open(dir);
        reader1.close();
        dir.close();

        // The following will fail if reader did not close all files
        dir = getDirectory(true);
    }

    public void testLastModified() throws IOException {
      assertFalse(IndexReader.indexExists("there_is_no_such_index"));
      Directory dir = new RAMDirectory();
      assertFalse(IndexReader.indexExists(dir));
      IndexWriter writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
      addDocumentWithFields(writer);
      assertTrue(IndexReader.isLocked(dir));		// writer open, so dir is locked
      writer.close();
      assertTrue(IndexReader.indexExists(dir));
      IndexReader reader = IndexReader.open(dir);
      assertFalse(IndexReader.isLocked(dir));		// reader only, no lock
      long version = IndexReader.lastModified(dir);
      reader.close();
      // modify index and check version has been incremented:
      writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
      addDocumentWithFields(writer);
      writer.close();
      reader = IndexReader.open(dir);
      assertTrue(version < IndexReader.getCurrentVersion(dir));
      reader.close();
    }

    public void testLock() throws IOException {
      Directory dir = new RAMDirectory();
      IndexWriter writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
      addDocumentWithFields(writer);
      writer.close();
      writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), false);
      IndexReader reader = IndexReader.open(dir);
      try {
        reader.deleteDocument(0);
        fail("expected lock");
      } catch(IOException e) {
        // expected exception
      }
      IndexReader.unlock(dir);		// this should not be done in the real world! 
      reader.deleteDocument(0);
      reader.close();
      writer.close();
    }

    public void testUndeleteAll() throws IOException {
      Directory dir = new RAMDirectory();
      IndexWriter writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
      addDocumentWithFields(writer);
      addDocumentWithFields(writer);
      writer.close();
      IndexReader reader = IndexReader.open(dir);
      reader.deleteDocument(0);
      reader.deleteDocument(1);
      reader.undeleteAll();
      reader.close();
      reader = IndexReader.open(dir);
      assertEquals(2, reader.numDocs());	// nothing has really been deleted thanks to undeleteAll()
      reader.close();
    }

    public void testDeleteReaderReaderConflictUnoptimized() throws IOException{
      deleteReaderReaderConflict(false);
    }
    
    public void testDeleteReaderReaderConflictOptimized() throws IOException{
      deleteReaderReaderConflict(true);
    }
    
    private void deleteReaderReaderConflict(boolean optimize) throws IOException
    {
        Directory dir = getDirectory(true);

        Term searchTerm1 = new Term("content", "aaa");
        Term searchTerm2 = new Term("content", "bbb");
        Term searchTerm3 = new Term("content", "ccc");

        //  add 100 documents with term : aaa
        //  add 100 documents with term : bbb
        //  add 100 documents with term : ccc
        IndexWriter writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
        for (int i = 0; i < 100; i++)
        {
            addDoc(writer, searchTerm1.text());
            addDoc(writer, searchTerm2.text());
            addDoc(writer, searchTerm3.text());
        }
        if(optimize)
          writer.optimize();
        writer.close();

        // OPEN TWO READERS
        // Both readers get segment info as exists at this time
        IndexReader reader1 = IndexReader.open(dir);
        assertEquals("first opened", 100, reader1.docFreq(searchTerm1));
        assertEquals("first opened", 100, reader1.docFreq(searchTerm2));
        assertEquals("first opened", 100, reader1.docFreq(searchTerm3));
        assertTermDocsCount("first opened", reader1, searchTerm1, 100);
        assertTermDocsCount("first opened", reader1, searchTerm2, 100);
        assertTermDocsCount("first opened", reader1, searchTerm3, 100);

        IndexReader reader2 = IndexReader.open(dir);
        assertEquals("first opened", 100, reader2.docFreq(searchTerm1));
        assertEquals("first opened", 100, reader2.docFreq(searchTerm2));
        assertEquals("first opened", 100, reader2.docFreq(searchTerm3));
        assertTermDocsCount("first opened", reader2, searchTerm1, 100);
        assertTermDocsCount("first opened", reader2, searchTerm2, 100);
        assertTermDocsCount("first opened", reader2, searchTerm3, 100);

        // DELETE DOCS FROM READER 2 and CLOSE IT
        // delete documents containing term: aaa
        // when the reader is closed, the segment info is updated and
        // the first reader is now stale
        reader2.deleteDocuments(searchTerm1);
        assertEquals("after delete 1", 100, reader2.docFreq(searchTerm1));
        assertEquals("after delete 1", 100, reader2.docFreq(searchTerm2));
        assertEquals("after delete 1", 100, reader2.docFreq(searchTerm3));
        assertTermDocsCount("after delete 1", reader2, searchTerm1, 0);
        assertTermDocsCount("after delete 1", reader2, searchTerm2, 100);
        assertTermDocsCount("after delete 1", reader2, searchTerm3, 100);
        reader2.close();

        // Make sure reader 1 is unchanged since it was open earlier
        assertEquals("after delete 1", 100, reader1.docFreq(searchTerm1));
        assertEquals("after delete 1", 100, reader1.docFreq(searchTerm2));
        assertEquals("after delete 1", 100, reader1.docFreq(searchTerm3));
        assertTermDocsCount("after delete 1", reader1, searchTerm1, 100);
        assertTermDocsCount("after delete 1", reader1, searchTerm2, 100);
        assertTermDocsCount("after delete 1", reader1, searchTerm3, 100);


        // ATTEMPT TO DELETE FROM STALE READER
        // delete documents containing term: bbb
        try {
            reader1.deleteDocuments(searchTerm2);
            fail("Delete allowed from a stale index reader");
        } catch (IOException e) {
            /* success */
        }

        // RECREATE READER AND TRY AGAIN
        reader1.close();
        reader1 = IndexReader.open(dir);
        assertEquals("reopened", 100, reader1.docFreq(searchTerm1));
        assertEquals("reopened", 100, reader1.docFreq(searchTerm2));
        assertEquals("reopened", 100, reader1.docFreq(searchTerm3));
        assertTermDocsCount("reopened", reader1, searchTerm1, 0);
        assertTermDocsCount("reopened", reader1, searchTerm2, 100);
        assertTermDocsCount("reopened", reader1, searchTerm3, 100);

        reader1.deleteDocuments(searchTerm2);
        assertEquals("deleted 2", 100, reader1.docFreq(searchTerm1));
        assertEquals("deleted 2", 100, reader1.docFreq(searchTerm2));
        assertEquals("deleted 2", 100, reader1.docFreq(searchTerm3));
        assertTermDocsCount("deleted 2", reader1, searchTerm1, 0);
        assertTermDocsCount("deleted 2", reader1, searchTerm2, 0);
        assertTermDocsCount("deleted 2", reader1, searchTerm3, 100);
        reader1.close();

        // Open another reader to confirm that everything is deleted
        reader2 = IndexReader.open(dir);
        assertEquals("reopened 2", 100, reader2.docFreq(searchTerm1));
        assertEquals("reopened 2", 100, reader2.docFreq(searchTerm2));
        assertEquals("reopened 2", 100, reader2.docFreq(searchTerm3));
        assertTermDocsCount("reopened 2", reader2, searchTerm1, 0);
        assertTermDocsCount("reopened 2", reader2, searchTerm2, 0);
        assertTermDocsCount("reopened 2", reader2, searchTerm3, 100);
        reader2.close();

        dir.close();
    }


    private void addDocumentWithFields(IndexWriter writer) throws IOException
    {
        Document doc = new Document();
        doc.add(new Field("keyword","test1", Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("text","test1", Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("unindexed","test1", Field.Store.YES, Field.Index.NO));
        doc.add(new Field("unstored","test1", Field.Store.NO, Field.Index.TOKENIZED));
        writer.addDocument(doc);
    }

    private void addDocumentWithDifferentFields(IndexWriter writer) throws IOException
    {
        Document doc = new Document();
        doc.add(new Field("keyword2","test1", Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("text2","test1", Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("unindexed2","test1", Field.Store.YES, Field.Index.NO));
        doc.add(new Field("unstored2","test1", Field.Store.NO, Field.Index.TOKENIZED));
        writer.addDocument(doc);
    }

    private void addDocumentWithTermVectorFields(IndexWriter writer) throws IOException
    {
        Document doc = new Document();
        doc.add(new Field("tvnot","tvnot", Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.NO));
        doc.add(new Field("termvector","termvector", Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.YES));
        doc.add(new Field("tvoffset","tvoffset", Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.WITH_OFFSETS));
        doc.add(new Field("tvposition","tvposition", Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("tvpositionoffset","tvpositionoffset", Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        
        writer.addDocument(doc);
    }
    
    private void addDoc(IndexWriter writer, String value) throws IOException
    {
        Document doc = new Document();
        doc.add(new Field("content", value, Field.Store.NO, Field.Index.TOKENIZED));
        writer.addDocument(doc);
    }
}
