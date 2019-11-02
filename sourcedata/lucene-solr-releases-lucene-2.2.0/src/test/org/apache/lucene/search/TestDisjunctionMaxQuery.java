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
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.text.DecimalFormat;
import java.io.IOException;

/**
 * Test of the DisjunctionMaxQuery.
 *
 */
public class TestDisjunctionMaxQuery extends TestCase{

    /** threshold for comparing floats */
    public static final float SCORE_COMP_THRESH = 0.0000f;

    /**
     * Similarity to eliminate tf, idf and lengthNorm effects to
     * isolate test case.
     *
     * <p>
     * same as TestRankingSimilarity in TestRanking.zip from
     * http://issues.apache.org/jira/browse/LUCENE-323
     * </p>
     * @author Williams
     */
    private static class TestSimilarity extends DefaultSimilarity {

        public TestSimilarity() {
        }
        public float tf(float freq) {
            if (freq > 0.0f) return 1.0f;
            else return 0.0f;
        }
        public float lengthNorm(String fieldName, int numTerms) {
            return 1.0f;
        }
        public float idf(int docFreq, int numDocs) {
            return 1.0f;
        }
    }

    public Similarity sim = new TestSimilarity();
    public Directory index;
    public IndexReader r;
    public IndexSearcher s;

    public void setUp() throws Exception {

        index = new RAMDirectory();
        IndexWriter writer = new IndexWriter(index,
                                             new WhitespaceAnalyzer(),
                                             true);
        writer.setSimilarity(sim);

        // hed is the most important field, dek is secondary

        // d1 is an "ok" match for:  albino elephant
        {
            Document d1 = new Document();
            d1.add(new Field("id", "d1", Field.Store.YES, Field.Index.UN_TOKENIZED));//Field.Keyword("id", "d1"));
            d1.add(new Field("hed", "elephant", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("hed", "elephant"));
            d1.add(new Field("dek", "elephant", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("dek", "elephant"));
            writer.addDocument(d1);
        }

        // d2 is a "good" match for:  albino elephant
        {
            Document d2 = new Document();
            d2.add(new Field("id", "d2", Field.Store.YES, Field.Index.UN_TOKENIZED));//Field.Keyword("id", "d2"));
            d2.add(new Field("hed", "elephant", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("hed", "elephant"));
            d2.add(new Field("dek", "albino", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("dek", "albino"));
            d2.add(new Field("dek", "elephant", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("dek", "elephant"));
            writer.addDocument(d2);
        }

        // d3 is a "better" match for:  albino elephant
        {
            Document d3 = new Document();
            d3.add(new Field("id", "d3", Field.Store.YES, Field.Index.UN_TOKENIZED));//Field.Keyword("id", "d3"));
            d3.add(new Field("hed", "albino", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("hed", "albino"));
            d3.add(new Field("hed", "elephant", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("hed", "elephant"));
            writer.addDocument(d3);
        }

        // d4 is the "best" match for:  albino elephant
        {
            Document d4 = new Document();
            d4.add(new Field("id", "d4", Field.Store.YES, Field.Index.UN_TOKENIZED));//Field.Keyword("id", "d4"));
            d4.add(new Field("hed", "albino", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("hed", "albino"));
            d4.add(new Field("hed", "elephant", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("hed", "elephant"));
            d4.add(new Field("dek", "albino", Field.Store.YES, Field.Index.TOKENIZED));//Field.Text("dek", "albino"));
            writer.addDocument(d4);
        }

        writer.close();

        r = IndexReader.open(index);
        s = new IndexSearcher(r);
        s.setSimilarity(sim);
    }

  public void testSkipToFirsttimeMiss() throws IOException {
    final DisjunctionMaxQuery dq = new DisjunctionMaxQuery(0.0f);
    dq.add(tq("id","d1"));
    dq.add(tq("dek","DOES_NOT_EXIST"));

    QueryUtils.check(dq,s);

    final Weight dw = dq.weight(s);
    final Scorer ds = dw.scorer(r);
    final boolean skipOk = ds.skipTo(3);
    if (skipOk) {
      fail("firsttime skipTo found a match? ... " + 
            r.document(ds.doc()).get("id"));
    }
  }

  public void testSkipToFirsttimeHit() throws IOException {
    final DisjunctionMaxQuery dq = new DisjunctionMaxQuery(0.0f);
    dq.add(tq("dek","albino"));
    dq.add(tq("dek","DOES_NOT_EXIST"));

    QueryUtils.check(dq,s);

    final Weight dw = dq.weight(s);
    final Scorer ds = dw.scorer(r);
    assertTrue("firsttime skipTo found no match", ds.skipTo(3));
    assertEquals("found wrong docid", "d4", r.document(ds.doc()).get("id"));
  }



    public void testSimpleEqualScores1() throws Exception {

        DisjunctionMaxQuery q = new DisjunctionMaxQuery(0.0f);
        q.add(tq("hed","albino"));
        q.add(tq("hed","elephant"));
        QueryUtils.check(q,s);

        Hits h = s.search(q);

        try {
            assertEquals("all docs should match " + q.toString(),
                         4, h.length());

            float score = h.score(0);
            for (int i = 1; i < h.length(); i++) {
                assertEquals("score #" + i + " is not the same",
                             score, h.score(i), SCORE_COMP_THRESH);
            }
        } catch (Error e) {
            printHits("testSimpleEqualScores1",h);
            throw e;
        }


    }

    public void testSimpleEqualScores2() throws Exception {

        DisjunctionMaxQuery q = new DisjunctionMaxQuery(0.0f);
        q.add(tq("dek","albino"));
        q.add(tq("dek","elephant"));
        QueryUtils.check(q,s);


        Hits h = s.search(q);

        try {
            assertEquals("3 docs should match " + q.toString(),
                         3, h.length());
            float score = h.score(0);
            for (int i = 1; i < h.length(); i++) {
                assertEquals("score #" + i + " is not the same",
                             score, h.score(i), SCORE_COMP_THRESH);
            }
        } catch (Error e) {
            printHits("testSimpleEqualScores2",h);
            throw e;
        }

    }

    public void testSimpleEqualScores3() throws Exception {

        DisjunctionMaxQuery q = new DisjunctionMaxQuery(0.0f);
        q.add(tq("hed","albino"));
        q.add(tq("hed","elephant"));
        q.add(tq("dek","albino"));
        q.add(tq("dek","elephant"));
        QueryUtils.check(q,s);


        Hits h = s.search(q);

        try {
            assertEquals("all docs should match " + q.toString(),
                         4, h.length());
            float score = h.score(0);
            for (int i = 1; i < h.length(); i++) {
                assertEquals("score #" + i + " is not the same",
                             score, h.score(i), SCORE_COMP_THRESH);
            }
        } catch (Error e) {
            printHits("testSimpleEqualScores3",h);
            throw e;
        }

    }

    public void testSimpleTiebreaker() throws Exception {

        DisjunctionMaxQuery q = new DisjunctionMaxQuery(0.01f);
        q.add(tq("dek","albino"));
        q.add(tq("dek","elephant"));
        QueryUtils.check(q,s);


        Hits h = s.search(q);

        try {
            assertEquals("3 docs should match " + q.toString(),
                         3, h.length());
            assertEquals("wrong first",  "d2", h.doc(0).get("id"));
            float score0 = h.score(0);
            float score1 = h.score(1);
            float score2 = h.score(2);
            assertTrue("d2 does not have better score then others: " +
                       score0 + " >? " + score1,
                       score0 > score1);
            assertEquals("d4 and d1 don't have equal scores",
                         score1, score2, SCORE_COMP_THRESH);
        } catch (Error e) {
            printHits("testSimpleTiebreaker",h);
            throw e;
        }
    }

    public void testBooleanRequiredEqualScores() throws Exception {

        BooleanQuery q = new BooleanQuery();
        {
            DisjunctionMaxQuery q1 = new DisjunctionMaxQuery(0.0f);
            q1.add(tq("hed","albino"));
            q1.add(tq("dek","albino"));
            q.add(q1,BooleanClause.Occur.MUST);//true,false);
            QueryUtils.check(q1,s);

        }
        {
            DisjunctionMaxQuery q2 = new DisjunctionMaxQuery(0.0f);
            q2.add(tq("hed","elephant"));
            q2.add(tq("dek","elephant"));
            q.add(q2, BooleanClause.Occur.MUST);//true,false);
           QueryUtils.check(q2,s);
        }

        QueryUtils.check(q,s);

        Hits h = s.search(q);

        try {
            assertEquals("3 docs should match " + q.toString(),
                         3, h.length());
            float score = h.score(0);
            for (int i = 1; i < h.length(); i++) {
                assertEquals("score #" + i + " is not the same",
                             score, h.score(i), SCORE_COMP_THRESH);
            }
        } catch (Error e) {
            printHits("testBooleanRequiredEqualScores1",h);
            throw e;
        }
    }


    public void testBooleanOptionalNoTiebreaker() throws Exception {

        BooleanQuery q = new BooleanQuery();
        {
            DisjunctionMaxQuery q1 = new DisjunctionMaxQuery(0.0f);
            q1.add(tq("hed","albino"));
            q1.add(tq("dek","albino"));
            q.add(q1, BooleanClause.Occur.SHOULD);//false,false);
        }
        {
            DisjunctionMaxQuery q2 = new DisjunctionMaxQuery(0.0f);
            q2.add(tq("hed","elephant"));
            q2.add(tq("dek","elephant"));
            q.add(q2, BooleanClause.Occur.SHOULD);//false,false);
        }
        QueryUtils.check(q,s);


        Hits h = s.search(q);

        try {
            assertEquals("4 docs should match " + q.toString(),
                         4, h.length());
            float score = h.score(0);
            for (int i = 1; i < h.length()-1; i++) { /* note: -1 */
                assertEquals("score #" + i + " is not the same",
                             score, h.score(i), SCORE_COMP_THRESH);
            }
            assertEquals("wrong last", "d1", h.doc(h.length()-1).get("id"));
            float score1 = h.score(h.length()-1);
            assertTrue("d1 does not have worse score then others: " +
                       score + " >? " + score1,
                       score > score1);
        } catch (Error e) {
            printHits("testBooleanOptionalNoTiebreaker",h);
            throw e;
        }
    }


    public void testBooleanOptionalWithTiebreaker() throws Exception {

        BooleanQuery q = new BooleanQuery();
        {
            DisjunctionMaxQuery q1 = new DisjunctionMaxQuery(0.01f);
            q1.add(tq("hed","albino"));
            q1.add(tq("dek","albino"));
            q.add(q1, BooleanClause.Occur.SHOULD);//false,false);
        }
        {
            DisjunctionMaxQuery q2 = new DisjunctionMaxQuery(0.01f);
            q2.add(tq("hed","elephant"));
            q2.add(tq("dek","elephant"));
            q.add(q2, BooleanClause.Occur.SHOULD);//false,false);
        }
        QueryUtils.check(q,s);


        Hits h = s.search(q);

        try {

            assertEquals("4 docs should match " + q.toString(),
                         4, h.length());

            float score0 = h.score(0);
            float score1 = h.score(1);
            float score2 = h.score(2);
            float score3 = h.score(3);

            String doc0 = h.doc(0).get("id");
            String doc1 = h.doc(1).get("id");
            String doc2 = h.doc(2).get("id");
            String doc3 = h.doc(3).get("id");

            assertTrue("doc0 should be d2 or d4: " + doc0,
                       doc0.equals("d2") || doc0.equals("d4"));
            assertTrue("doc1 should be d2 or d4: " + doc0,
                       doc1.equals("d2") || doc1.equals("d4"));
            assertEquals("score0 and score1 should match",
                         score0, score1, SCORE_COMP_THRESH);
            assertEquals("wrong third", "d3", doc2);
            assertTrue("d3 does not have worse score then d2 and d4: " +
                       score1 + " >? " + score2,
                       score1 > score2);

            assertEquals("wrong fourth", "d1", doc3);
            assertTrue("d1 does not have worse score then d3: " +
                       score2 + " >? " + score3,
                       score2 > score3);

        } catch (Error e) {
            printHits("testBooleanOptionalWithTiebreaker",h);
            throw e;
        }

    }


    public void testBooleanOptionalWithTiebreakerAndBoost() throws Exception {

        BooleanQuery q = new BooleanQuery();
        {
            DisjunctionMaxQuery q1 = new DisjunctionMaxQuery(0.01f);
            q1.add(tq("hed","albino", 1.5f));
            q1.add(tq("dek","albino"));
            q.add(q1, BooleanClause.Occur.SHOULD);//false,false);
        }
        {
            DisjunctionMaxQuery q2 = new DisjunctionMaxQuery(0.01f);
            q2.add(tq("hed","elephant", 1.5f));
            q2.add(tq("dek","elephant"));
            q.add(q2, BooleanClause.Occur.SHOULD);//false,false);
        }
        QueryUtils.check(q,s);


        Hits h = s.search(q);

        try {

            assertEquals("4 docs should match " + q.toString(),
                         4, h.length());

            float score0 = h.score(0);
            float score1 = h.score(1);
            float score2 = h.score(2);
            float score3 = h.score(3);

            String doc0 = h.doc(0).get("id");
            String doc1 = h.doc(1).get("id");
            String doc2 = h.doc(2).get("id");
            String doc3 = h.doc(3).get("id");

            assertEquals("doc0 should be d4: ", "d4", doc0);
            assertEquals("doc1 should be d3: ", "d3", doc1);
            assertEquals("doc2 should be d2: ", "d2", doc2);
            assertEquals("doc3 should be d1: ", "d1", doc3);

            assertTrue("d4 does not have a better score then d3: " +
                       score0 + " >? " + score1,
                       score0 > score1);
            assertTrue("d3 does not have a better score then d2: " +
                       score1 + " >? " + score2,
                       score1 > score2);
            assertTrue("d3 does not have a better score then d1: " +
                       score2 + " >? " + score3,
                       score2 > score3);

        } catch (Error e) {
            printHits("testBooleanOptionalWithTiebreakerAndBoost",h);
            throw e;
        }
    }







    /** macro */
    protected Query tq(String f, String t) {
        return new TermQuery(new Term(f, t));
    }
    /** macro */
    protected Query tq(String f, String t, float b) {
        Query q = tq(f,t);
        q.setBoost(b);
        return q;
    }


    protected void printHits(String test, Hits h) throws Exception {

        System.err.println("------- " + test + " -------");

        DecimalFormat f = new DecimalFormat("0.000000000");

        for (int i = 0; i < h.length(); i++) {
            Document d = h.doc(i);
            float score = h.score(i);
            System.err.println("#" + i + ": " + f.format(score) + " - " +
                               d.get("id"));
        }
    }

}
