package org.apache.lucene.search;

import junit.framework.TestCase;

import java.util.Random;
import java.util.BitSet;
import java.util.Set;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

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
 * @author yonik
 * @version $Id$
 */
public class TestScorerPerf extends TestCase {
  Random r = new Random(0);
  boolean validate = true;  // set to false when doing performance testing

  BitSet[] sets;
  IndexSearcher s;

  public void createDummySearcher() throws Exception {
      // Create a dummy index with nothing in it.
    // This could possibly fail if Lucene starts checking for docid ranges...
    RAMDirectory rd = new RAMDirectory();
    IndexWriter iw = new IndexWriter(rd,new WhitespaceAnalyzer(), true);
    iw.close();
    s = new IndexSearcher(rd);
  }

  public void createRandomTerms(int nDocs, int nTerms, double power, Directory dir) throws Exception {
    int[] freq = new int[nTerms];
    for (int i=0; i<nTerms; i++) {
      int f = (nTerms+1)-i;  // make first terms less frequent
      freq[i] = (int)Math.ceil(Math.pow(f,power));
    }

    IndexWriter iw = new IndexWriter(dir,new WhitespaceAnalyzer(), true);
    iw.setMaxBufferedDocs(123);
    for (int i=0; i<nDocs; i++) {
      Document d = new Document();
      for (int j=0; j<nTerms; j++) {
        if (r.nextInt(freq[j]) == 0) {
          d.add(new Field("f", Character.toString((char)j), Field.Store.NO, Field.Index.UN_TOKENIZED));
        }
      }
      iw.addDocument(d);
    }
    iw.close();
  }


  public BitSet randBitSet(int sz, int numBitsToSet) {
    BitSet set = new BitSet(sz);
    for (int i=0; i<numBitsToSet; i++) {
      set.set(r.nextInt(sz));
    }
    return set;
  }

  public BitSet[] randBitSets(int numSets, int setSize) {
    BitSet[] sets = new BitSet[numSets];
    for (int i=0; i<sets.length; i++) {
      sets[i] = randBitSet(setSize, r.nextInt(setSize));
    }
    return sets;
  }

  public static class BitSetFilter extends Filter {
    public BitSet set;
    public BitSetFilter(BitSet set) {
      this.set = set;
    }
    public BitSet bits(IndexReader reader) throws IOException {
      return set;
    }
  }

  public static class CountingHitCollector extends HitCollector {
    int count=0;
    int sum=0;

    public void collect(int doc, float score) {
      count++;
      sum += doc;  // use it to avoid any possibility of being optimized away
    }

    public int getCount() { return count; }
    public int getSum() { return sum; }
  }


  public static class MatchingHitCollector extends CountingHitCollector {
    BitSet answer;
    int pos=-1;
    public MatchingHitCollector(BitSet answer) {
      this.answer = answer;
    }

    public void collect(int doc, float score) {
      pos = answer.nextSetBit(pos+1);
      if (pos != doc) {
        throw new RuntimeException("Expected doc " + pos + " but got " + doc);
      }
      super.collect(doc,score);
    }
  }


  BitSet addClause(BooleanQuery bq, BitSet result) {
    BitSet rnd = sets[r.nextInt(sets.length)];
    Query q = new ConstantScoreQuery(new BitSetFilter(rnd));
    bq.add(q, BooleanClause.Occur.MUST);
    if (validate) {
      if (result==null) result = (BitSet)rnd.clone();
      else result.and(rnd);
    }
    return result;
  }


  public int doConjunctions(int iter, int maxClauses) throws IOException {
    int ret=0;

    for (int i=0; i<iter; i++) {
      int nClauses = r.nextInt(maxClauses-1)+2; // min 2 clauses
      BooleanQuery bq = new BooleanQuery();
      BitSet result=null;
      for (int j=0; j<nClauses; j++) {
        result = addClause(bq,result);
      }

      CountingHitCollector hc = validate ? new MatchingHitCollector(result)
                                         : new CountingHitCollector();
      s.search(bq, hc);
      ret += hc.getSum();
      if (validate) assertEquals(result.cardinality(), hc.getCount());
      // System.out.println(hc.getCount());
    }
    
    return ret;
  }

  public int doNestedConjunctions(int iter, int maxOuterClauses, int maxClauses) throws IOException {
    int ret=0;

    for (int i=0; i<iter; i++) {
      int oClauses = r.nextInt(maxOuterClauses-1)+2;
      BooleanQuery oq = new BooleanQuery();
      BitSet result=null;

      for (int o=0; o<oClauses; o++) {

      int nClauses = r.nextInt(maxClauses-1)+2; // min 2 clauses
      BooleanQuery bq = new BooleanQuery();
      for (int j=0; j<nClauses; j++) {
        result = addClause(bq,result);
      }

      oq.add(bq, BooleanClause.Occur.MUST);
      } // outer


      CountingHitCollector hc = validate ? new MatchingHitCollector(result)
                                         : new CountingHitCollector();
      s.search(oq, hc);
      ret += hc.getSum();
      if (validate) assertEquals(result.cardinality(), hc.getCount());
      // System.out.println(hc.getCount());
    }

    return ret;
  }

  
  public int doTermConjunctions(IndexSearcher s,
                                int termsInIndex,
                                int maxClauses,
                                int iter
  ) throws IOException {
    int ret=0;

    for (int i=0; i<iter; i++) {
      int nClauses = r.nextInt(maxClauses-1)+2; // min 2 clauses
      BooleanQuery bq = new BooleanQuery();
      BitSet terms = new BitSet(termsInIndex);
      for (int j=0; j<nClauses; j++) {
        int tnum;
        // don't pick same clause twice
        do {tnum = r.nextInt(termsInIndex);} while (terms.get(tnum));
        Query tq = new TermQuery(new Term("f",Character.toString((char)tnum)));
        bq.add(tq, BooleanClause.Occur.MUST);
      }

      CountingHitCollector hc = new CountingHitCollector();
      s.search(bq, hc);
      ret += hc.getSum();
    }

    return ret;
  }


  public int doNestedTermConjunctions(IndexSearcher s,
                                int termsInIndex,
                                int maxOuterClauses,
                                int maxClauses,
                                int iter
  ) throws IOException {
    int ret=0;

    for (int i=0; i<iter; i++) {
      int oClauses = r.nextInt(maxOuterClauses-1)+2;
      BooleanQuery oq = new BooleanQuery();
      for (int o=0; o<oClauses; o++) {

      int nClauses = r.nextInt(maxClauses-1)+2; // min 2 clauses
      BooleanQuery bq = new BooleanQuery();
      BitSet terms = new BitSet(termsInIndex);
      for (int j=0; j<nClauses; j++) {
        int tnum;
        // don't pick same clause twice
        do {tnum = r.nextInt(termsInIndex);} while (terms.get(tnum));
        Query tq = new TermQuery(new Term("f",Character.toString((char)tnum)));
        bq.add(tq, BooleanClause.Occur.MUST);
      } // inner

      oq.add(bq, BooleanClause.Occur.MUST);
      } // outer


      CountingHitCollector hc = new CountingHitCollector();
      s.search(oq, hc);
      ret += hc.getSum();
    }

    return ret;
  }


    public int doSloppyPhrase(IndexSearcher s,
                                int termsInIndex,
                                int maxClauses,
                                int iter
  ) throws IOException {
    int ret=0;

    for (int i=0; i<iter; i++) {
      int nClauses = r.nextInt(maxClauses-1)+2; // min 2 clauses
      PhraseQuery q = new PhraseQuery();
      for (int j=0; j<nClauses; j++) {
        int tnum = r.nextInt(termsInIndex);
        q.add(new Term("f",Character.toString((char)tnum)), j);
      }
      q.setSlop(termsInIndex);  // this could be random too

      CountingHitCollector hc = new CountingHitCollector();
      s.search(q, hc);
      ret += hc.getSum();
    }

    return ret;
  }


  public void testConjunctions() throws Exception {
    // test many small sets... the bugs will be found on boundary conditions
    createDummySearcher();
    validate=true;
    sets=randBitSets(1000,10);
    doConjunctions(10000,5);
    doNestedConjunctions(10000,3,3);
    s.close();
  }

  /***
  int bigIter=6;
  public void testConjunctionPerf() throws Exception {
    createDummySearcher();
    validate=false;
    sets=randBitSets(32,1000000);
    for (int i=0; i<bigIter; i++) {
      long start = System.currentTimeMillis();
      doConjunctions(500,6);
      long end = System.currentTimeMillis();
      System.out.println("milliseconds="+(end-start));
    }
    s.close();
  }

  public void testNestedConjunctionPerf() throws Exception {
    createDummySearcher();
    validate=false;
    sets=randBitSets(32,1000000);
    for (int i=0; i<bigIter; i++) {
      long start = System.currentTimeMillis();
      doNestedConjunctions(500,3,3);
      long end = System.currentTimeMillis();
      System.out.println("milliseconds="+(end-start));
    }
    s.close();
  }

  public void testConjunctionTerms() throws Exception {
    validate=false;
    RAMDirectory dir = new RAMDirectory();
    System.out.println("Creating index");
    createRandomTerms(100000,25,2, dir);
    s = new IndexSearcher(dir);
    System.out.println("Starting performance test");
    for (int i=0; i<bigIter; i++) {
      long start = System.currentTimeMillis();
      doTermConjunctions(s,25,5,10000);
      long end = System.currentTimeMillis();
      System.out.println("milliseconds="+(end-start));
    }
    s.close();
  }

  public void testNestedConjunctionTerms() throws Exception {
    validate=false;    
    RAMDirectory dir = new RAMDirectory();
    System.out.println("Creating index");
    createRandomTerms(100000,25,2, dir);
    s = new IndexSearcher(dir);
    System.out.println("Starting performance test");
    for (int i=0; i<bigIter; i++) {
      long start = System.currentTimeMillis();
      doNestedTermConjunctions(s,25,5,5,1000);
      long end = System.currentTimeMillis();
      System.out.println("milliseconds="+(end-start));
    }
    s.close();
  }


  public void testSloppyPhrasePerf() throws Exception {
    validate=false;    
    RAMDirectory dir = new RAMDirectory();
    System.out.println("Creating index");
    createRandomTerms(100000,25,2,dir);
    s = new IndexSearcher(dir);
    System.out.println("Starting performance test");
    for (int i=0; i<bigIter; i++) {
      long start = System.currentTimeMillis();
      doSloppyPhrase(s,25,2,1000);
      long end = System.currentTimeMillis();
      System.out.println("milliseconds="+(end-start));
    }
    s.close();

  }
   ***/

}
