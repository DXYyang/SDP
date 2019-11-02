package org.apache.lucene.search.spans;

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

import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Similarity;

class SpanWeight implements Weight {
  private Similarity similarity;
  private float value;
  private float idf;
  private float queryNorm;
  private float queryWeight;

  private Set terms;
  private SpanQuery query;

  public SpanWeight(SpanQuery query, Searcher searcher)
    throws IOException {
    this.similarity = query.getSimilarity(searcher);
    this.query = query;
    terms=new HashSet();
    query.extractTerms(terms);

    idf = this.query.getSimilarity(searcher).idf(terms, searcher);
  }

  public Query getQuery() { return query; }
  public float getValue() { return value; }

  public float sumOfSquaredWeights() throws IOException {
    queryWeight = idf * query.getBoost();         // compute query weight
    return queryWeight * queryWeight;             // square it
  }

  public void normalize(float queryNorm) {
    this.queryNorm = queryNorm;
    queryWeight *= queryNorm;                     // normalize query weight
    value = queryWeight * idf;                    // idf for document
  }

  public Scorer scorer(IndexReader reader) throws IOException {
    return new SpanScorer(query.getSpans(reader), this,
                          similarity,
                          reader.norms(query.getField()));
  }

  public Explanation explain(IndexReader reader, int doc)
    throws IOException {

    Explanation result = new Explanation();
    result.setDescription("weight("+getQuery()+" in "+doc+"), product of:");
    String field = ((SpanQuery)getQuery()).getField();

    StringBuffer docFreqs = new StringBuffer();
    Iterator i = terms.iterator();
    while (i.hasNext()) {
      Term term = (Term)i.next();
      docFreqs.append(term.text());
      docFreqs.append("=");
      docFreqs.append(reader.docFreq(term));

      if (i.hasNext()) {
        docFreqs.append(" ");
      }
    }

    Explanation idfExpl =
      new Explanation(idf, "idf(" + field + ": " + docFreqs + ")");

    // explain query weight
    Explanation queryExpl = new Explanation();
    queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");

    Explanation boostExpl = new Explanation(getQuery().getBoost(), "boost");
    if (getQuery().getBoost() != 1.0f)
      queryExpl.addDetail(boostExpl);
    queryExpl.addDetail(idfExpl);

    Explanation queryNormExpl = new Explanation(queryNorm,"queryNorm");
    queryExpl.addDetail(queryNormExpl);

    queryExpl.setValue(boostExpl.getValue() *
                       idfExpl.getValue() *
                       queryNormExpl.getValue());

    result.addDetail(queryExpl);

    // explain field weight
    Explanation fieldExpl = new Explanation();
    fieldExpl.setDescription("fieldWeight("+field+":"+query.toString(field)+
                             " in "+doc+"), product of:");

    Explanation tfExpl = scorer(reader).explain(doc);
    fieldExpl.addDetail(tfExpl);
    fieldExpl.addDetail(idfExpl);

    Explanation fieldNormExpl = new Explanation();
    byte[] fieldNorms = reader.norms(field);
    float fieldNorm =
      fieldNorms!=null ? Similarity.decodeNorm(fieldNorms[doc]) : 0.0f;
    fieldNormExpl.setValue(fieldNorm);
    fieldNormExpl.setDescription("fieldNorm(field="+field+", doc="+doc+")");
    fieldExpl.addDetail(fieldNormExpl);

    fieldExpl.setValue(tfExpl.getValue() *
                       idfExpl.getValue() *
                       fieldNormExpl.getValue());

    result.addDetail(fieldExpl);

    // combine them
    result.setValue(queryExpl.getValue() * fieldExpl.getValue());

    if (queryExpl.getValue() == 1.0f)
      return fieldExpl;

    return result;
  }
}
