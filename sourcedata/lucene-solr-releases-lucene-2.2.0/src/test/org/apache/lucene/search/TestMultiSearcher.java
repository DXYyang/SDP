package org.apache.lucene.search;

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
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SetBasedFieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.MockRAMDirectory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests {@link MultiSearcher} class.
 *
 * @version $Id$
 */
public class TestMultiSearcher extends TestCase
{
    public TestMultiSearcher(String name)
    {
        super(name);
    }

	/**
	 * ReturnS a new instance of the concrete MultiSearcher class
	 * used in this test.
	 */
	protected MultiSearcher getMultiSearcherInstance(Searcher[] searchers) throws IOException {
		return new MultiSearcher(searchers);
	}

    public void testEmptyIndex()
        throws Exception
    {
        // creating two directories for indices
        Directory indexStoreA = new MockRAMDirectory();
        Directory indexStoreB = new MockRAMDirectory();

        // creating a document to store
        Document lDoc = new Document();
        lDoc.add(new Field("fulltext", "Once upon a time.....", Field.Store.YES, Field.Index.TOKENIZED));
        lDoc.add(new Field("id", "doc1", Field.Store.YES, Field.Index.UN_TOKENIZED));
        lDoc.add(new Field("handle", "1", Field.Store.YES, Field.Index.UN_TOKENIZED));

        // creating a document to store
        Document lDoc2 = new Document();
        lDoc2.add(new Field("fulltext", "in a galaxy far far away.....",
            Field.Store.YES, Field.Index.TOKENIZED));
        lDoc2.add(new Field("id", "doc2", Field.Store.YES, Field.Index.UN_TOKENIZED));
        lDoc2.add(new Field("handle", "1", Field.Store.YES, Field.Index.UN_TOKENIZED));

        // creating a document to store
        Document lDoc3 = new Document();
        lDoc3.add(new Field("fulltext", "a bizarre bug manifested itself....",
            Field.Store.YES, Field.Index.TOKENIZED));
        lDoc3.add(new Field("id", "doc3", Field.Store.YES, Field.Index.UN_TOKENIZED));
        lDoc3.add(new Field("handle", "1", Field.Store.YES, Field.Index.UN_TOKENIZED));

        // creating an index writer for the first index
        IndexWriter writerA = new IndexWriter(indexStoreA, new StandardAnalyzer(), true);
        // creating an index writer for the second index, but writing nothing
        IndexWriter writerB = new IndexWriter(indexStoreB, new StandardAnalyzer(), true);

        //--------------------------------------------------------------------
        // scenario 1
        //--------------------------------------------------------------------

        // writing the documents to the first index
        writerA.addDocument(lDoc);
        writerA.addDocument(lDoc2);
        writerA.addDocument(lDoc3);
        writerA.optimize();
        writerA.close();

        // closing the second index
        writerB.close();

        // creating the query
        QueryParser parser = new QueryParser("fulltext", new StandardAnalyzer());
        Query query = parser.parse("handle:1");

        // building the searchables
        Searcher[] searchers = new Searcher[2];
        // VITAL STEP:adding the searcher for the empty index first, before the searcher for the populated index
        searchers[0] = new IndexSearcher(indexStoreB);
        searchers[1] = new IndexSearcher(indexStoreA);
        // creating the multiSearcher
        Searcher mSearcher = getMultiSearcherInstance(searchers);
        // performing the search
        Hits hits = mSearcher.search(query);

        assertEquals(3, hits.length());

        // iterating over the hit documents
        for (int i = 0; i < hits.length(); i++) {
            Document d = hits.doc(i);
        }
        mSearcher.close();


        //--------------------------------------------------------------------
        // scenario 2
        //--------------------------------------------------------------------

        // adding one document to the empty index
        writerB = new IndexWriter(indexStoreB, new StandardAnalyzer(), false);
        writerB.addDocument(lDoc);
        writerB.optimize();
        writerB.close();

        // building the searchables
        Searcher[] searchers2 = new Searcher[2];
        // VITAL STEP:adding the searcher for the empty index first, before the searcher for the populated index
        searchers2[0] = new IndexSearcher(indexStoreB);
        searchers2[1] = new IndexSearcher(indexStoreA);
        // creating the mulitSearcher
        MultiSearcher mSearcher2 = getMultiSearcherInstance(searchers2);
        // performing the same search
        Hits hits2 = mSearcher2.search(query);

        assertEquals(4, hits2.length());

        // iterating over the hit documents
        for (int i = 0; i < hits2.length(); i++) {
            // no exception should happen at this point
            Document d = hits2.doc(i);
        }

        // test the subSearcher() method:
        Query subSearcherQuery = parser.parse("id:doc1");
        hits2 = mSearcher2.search(subSearcherQuery);
        assertEquals(2, hits2.length());
        assertEquals(0, mSearcher2.subSearcher(hits2.id(0)));   // hit from searchers2[0]
        assertEquals(1, mSearcher2.subSearcher(hits2.id(1)));   // hit from searchers2[1]
        subSearcherQuery = parser.parse("id:doc2");
        hits2 = mSearcher2.search(subSearcherQuery);
        assertEquals(1, hits2.length());
        assertEquals(1, mSearcher2.subSearcher(hits2.id(0)));   // hit from searchers2[1]
        mSearcher2.close();

        //--------------------------------------------------------------------
        // scenario 3
        //--------------------------------------------------------------------

        // deleting the document just added, this will cause a different exception to take place
        Term term = new Term("id", "doc1");
        IndexReader readerB = IndexReader.open(indexStoreB);
        readerB.deleteDocuments(term);
        readerB.close();

        // optimizing the index with the writer
        writerB = new IndexWriter(indexStoreB, new StandardAnalyzer(), false);
        writerB.optimize();
        writerB.close();

        // building the searchables
        Searcher[] searchers3 = new Searcher[2];

        searchers3[0] = new IndexSearcher(indexStoreB);
        searchers3[1] = new IndexSearcher(indexStoreA);
        // creating the mulitSearcher
        Searcher mSearcher3 = getMultiSearcherInstance(searchers3);
        // performing the same search
        Hits hits3 = mSearcher3.search(query);

        assertEquals(3, hits3.length());

        // iterating over the hit documents
        for (int i = 0; i < hits3.length(); i++) {
            Document d = hits3.doc(i);
        }
        mSearcher3.close();
        indexStoreA.close();
        indexStoreB.close();
    }
    
    private static Document createDocument(String contents1, String contents2) {
        Document document=new Document();
        
        document.add(new Field("contents", contents1, Field.Store.YES, Field.Index.UN_TOKENIZED));
      document.add(new Field("other", "other contents", Field.Store.YES, Field.Index.UN_TOKENIZED));
        if (contents2!=null) {
            document.add(new Field("contents", contents2, Field.Store.YES, Field.Index.UN_TOKENIZED));
        }
        
        return document;
    }
    
    private static void initIndex(Directory directory, int nDocs, boolean create, String contents2) throws IOException {
        IndexWriter indexWriter=null;
        
        try {
            indexWriter=new IndexWriter(directory, new KeywordAnalyzer(), create);
            
            for (int i=0; i<nDocs; i++) {
                indexWriter.addDocument(createDocument("doc" + i, contents2));
            }
        } finally {
            if (indexWriter!=null) {
                indexWriter.close();
            }
        }
    }

  public void testFieldSelector() throws Exception {
    RAMDirectory ramDirectory1, ramDirectory2;
    IndexSearcher indexSearcher1, indexSearcher2;

    ramDirectory1 = new RAMDirectory();
    ramDirectory2 = new RAMDirectory();
    Query query = new TermQuery(new Term("contents", "doc0"));

    // Now put the documents in a different index
    initIndex(ramDirectory1, 10, true, null); // documents with a single token "doc0", "doc1", etc...
    initIndex(ramDirectory2, 10, true, "x"); // documents with two tokens "doc0" and "x", "doc1" and x, etc...

    indexSearcher1 = new IndexSearcher(ramDirectory1);
    indexSearcher2 = new IndexSearcher(ramDirectory2);

    MultiSearcher searcher = getMultiSearcherInstance(new Searcher[]{indexSearcher1, indexSearcher2});
    assertTrue("searcher is null and it shouldn't be", searcher != null);
    Hits hits = searcher.search(query);
    assertTrue("hits is null and it shouldn't be", hits != null);
    assertTrue(hits.length() + " does not equal: " + 2, hits.length() == 2);
    Document document = searcher.doc(hits.id(0));
    assertTrue("document is null and it shouldn't be", document != null);
    assertTrue("document.getFields() Size: " + document.getFields().size() + " is not: " + 2, document.getFields().size() == 2);
    //Should be one document from each directory
    //they both have two fields, contents and other
    Set ftl = new HashSet();
    ftl.add("other");
    SetBasedFieldSelector fs = new SetBasedFieldSelector(ftl, Collections.EMPTY_SET);
    document = searcher.doc(hits.id(0), fs);
    assertTrue("document is null and it shouldn't be", document != null);
    assertTrue("document.getFields() Size: " + document.getFields().size() + " is not: " + 1, document.getFields().size() == 1);
    String value = document.get("contents");
    assertTrue("value is not null and it should be", value == null);
    value = document.get("other");
    assertTrue("value is null and it shouldn't be", value != null);
    ftl.clear();
    ftl.add("contents");
    fs = new SetBasedFieldSelector(ftl, Collections.EMPTY_SET);
    document = searcher.doc(hits.id(1), fs);
    value = document.get("contents");
    assertTrue("value is null and it shouldn't be", value != null);    
    value = document.get("other");
    assertTrue("value is not null and it should be", value == null);
  }

  /* uncomment this when the highest score is always normalized to 1.0, even when it was < 1.0
 public void testNormalization1() throws IOException {
     testNormalization(1, "Using 1 document per index:");
 }
  */
    
    public void testNormalization10() throws IOException {
        testNormalization(10, "Using 10 documents per index:");
    }
    
    private void testNormalization(int nDocs, String message) throws IOException {
        Query query=new TermQuery(new Term("contents", "doc0"));
        
        RAMDirectory ramDirectory1;
        IndexSearcher indexSearcher1;
        Hits hits;
        
        ramDirectory1=new MockRAMDirectory();
        
        // First put the documents in the same index
        initIndex(ramDirectory1, nDocs, true, null); // documents with a single token "doc0", "doc1", etc...
        initIndex(ramDirectory1, nDocs, false, "x"); // documents with two tokens "doc0" and "x", "doc1" and x, etc...
        
        indexSearcher1=new IndexSearcher(ramDirectory1);
        
        hits=indexSearcher1.search(query);
        
        assertEquals(message, 2, hits.length());
        
        assertEquals(message, 1, hits.score(0), 1e-6); // hits.score(0) is 0.594535 if only a single document is in first index
        
        // Store the scores for use later
        float[] scores={ hits.score(0), hits.score(1) };
        
        assertTrue(message, scores[0] > scores[1]);
        
        indexSearcher1.close();
        ramDirectory1.close();
        hits=null;
        
        
        
        RAMDirectory ramDirectory2;
        IndexSearcher indexSearcher2;
        
        ramDirectory1=new MockRAMDirectory();
        ramDirectory2=new MockRAMDirectory();
        
        // Now put the documents in a different index
        initIndex(ramDirectory1, nDocs, true, null); // documents with a single token "doc0", "doc1", etc...
        initIndex(ramDirectory2, nDocs, true, "x"); // documents with two tokens "doc0" and "x", "doc1" and x, etc...
        
        indexSearcher1=new IndexSearcher(ramDirectory1);
        indexSearcher2=new IndexSearcher(ramDirectory2);
        
        Searcher searcher=getMultiSearcherInstance(new Searcher[] { indexSearcher1, indexSearcher2 });
        
        hits=searcher.search(query);
        
        assertEquals(message, 2, hits.length());
        
        // The scores should be the same (within reason)
        assertEquals(message, scores[0], hits.score(0), 1e-6); // This will a document from ramDirectory1
        assertEquals(message, scores[1], hits.score(1), 1e-6); // This will a document from ramDirectory2
        
        
        
        // Adding a Sort.RELEVANCE object should not change anything
        hits=searcher.search(query, Sort.RELEVANCE);
        
        assertEquals(message, 2, hits.length());
        
        assertEquals(message, scores[0], hits.score(0), 1e-6); // This will a document from ramDirectory1
        assertEquals(message, scores[1], hits.score(1), 1e-6); // This will a document from ramDirectory2
        
        searcher.close();
        
        ramDirectory1.close();
        ramDirectory2.close();
    }
    
    /**
     * test that custom similarity is in effect when using MultiSearcher (LUCENE-789).
     * @throws IOException 
     */
    public void testCustomSimilarity () throws IOException {
        RAMDirectory dir = new RAMDirectory();
        initIndex(dir, 10, true, "x"); // documents with two tokens "doc0" and "x", "doc1" and x, etc...
        IndexSearcher srchr = new IndexSearcher(dir);
        MultiSearcher msrchr = getMultiSearcherInstance(new Searcher[]{srchr});
        
        Similarity customSimilarity = new DefaultSimilarity() {
            // overide all
            public float idf(int docFreq, int numDocs) { return 100.0f; }
            public float coord(int overlap, int maxOverlap) { return 1.0f; }
            public float lengthNorm(String fieldName, int numTokens) { return 1.0f; }
            public float queryNorm(float sumOfSquaredWeights) { return 1.0f; }
            public float sloppyFreq(int distance) { return 1.0f; }
            public float tf(float freq) { return 1.0f; }
        };
        
        srchr.setSimilarity(customSimilarity);
        msrchr.setSimilarity(customSimilarity);
  
        Query query=new TermQuery(new Term("contents", "doc0"));
  
        // Get a score from IndexSearcher
        TopDocs topDocs = srchr.search(query, null, 1);
        float score1 = topDocs.getMaxScore();
        
        // Get the score from MultiSearcher
        topDocs = msrchr.search(query, null, 1);
        float scoreN = topDocs.getMaxScore();
        
        // The scores from the IndexSearcher and Multisearcher should be the same
        // if the same similarity is used.
        assertEquals("MultiSearcher score must be equal to single esrcher score!", score1, scoreN, 1e-6);
    }
}
