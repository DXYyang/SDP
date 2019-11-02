package org.apache.lucene.search;

/**
 * Copyright 2004-2005 The Apache Software Foundation
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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.ToStringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/** A Query that matches documents matching boolean combinations of other
  * queries, e.g. {@link TermQuery}s, {@link PhraseQuery}s or other
  * BooleanQuerys.
  */
public class BooleanQuery extends Query {

  /**

   */
  private static int maxClauseCount = 1024;

  /** Thrown when an attempt is made to add more than {@link
   * #getMaxClauseCount()} clauses. This typically happens if
   * a PrefixQuery, FuzzyQuery, WildcardQuery, or RangeQuery 
   * is expanded to many terms during search. 
   */
  public static class TooManyClauses extends RuntimeException {}

  /** Return the maximum number of clauses permitted, 1024 by default.
   * Attempts to add more than the permitted number of clauses cause {@link
   * TooManyClauses} to be thrown.
   * @see #setMaxClauseCount(int)
   */
  public static int getMaxClauseCount() { return maxClauseCount; }

  /** Set the maximum number of clauses permitted per BooleanQuery.
   * Default value is 1024.
   * <p>TermQuery clauses are generated from for example prefix queries and
   * fuzzy queries. Each TermQuery needs some buffer space during search,
   * so this parameter indirectly controls the maximum buffer requirements for
   * query search.
   * <p>When this parameter becomes a bottleneck for a Query one can use a
   * Filter. For example instead of a {@link RangeQuery} one can use a
   * {@link RangeFilter}.
   * <p>Normally the buffers are allocated by the JVM. When using for example
   * {@link org.apache.lucene.store.MMapDirectory} the buffering is left to
   * the operating system.
   */
  public static void setMaxClauseCount(int maxClauseCount) {
    if (maxClauseCount < 1)
      throw new IllegalArgumentException("maxClauseCount must be >= 1");
    BooleanQuery.maxClauseCount = maxClauseCount;
  }

  private Vector clauses = new Vector();
  private boolean disableCoord;

  /** Constructs an empty boolean query. */
  public BooleanQuery() {}

  /** Constructs an empty boolean query.
   *
   * {@link Similarity#coord(int,int)} may be disabled in scoring, as
   * appropriate. For example, this score factor does not make sense for most
   * automatically generated queries, like {@link WildcardQuery} and {@link
   * FuzzyQuery}.
   *
   * @param disableCoord disables {@link Similarity#coord(int,int)} in scoring.
   */
  public BooleanQuery(boolean disableCoord) {
    this.disableCoord = disableCoord;
  }

  /** Returns true iff {@link Similarity#coord(int,int)} is disabled in
   * scoring for this query instance.
   * @see #BooleanQuery(boolean)
   */
  public boolean isCoordDisabled() { return disableCoord; }

  // Implement coord disabling.
  // Inherit javadoc.
  public Similarity getSimilarity(Searcher searcher) {
    Similarity result = super.getSimilarity(searcher);
    if (disableCoord) {                           // disable coord as requested
      result = new SimilarityDelegator(result) {
          public float coord(int overlap, int maxOverlap) {
            return 1.0f;
          }
        };
    }
    return result;
  }

  /**
   * Specifies a minimum number of the optional BooleanClauses
   * which must be satisifed.
   *
   * <p>
   * By default no optional clauses are neccessary for a match
   * (unless there are no required clauses).  If this method is used,
   * then the specified numebr of clauses is required.
   * </p>
   * <p>
   * Use of this method is totally independant of specifying that
   * any specific clauses are required (or prohibited).  This number will
   * only be compared against the number of matching optional clauses.
   * </p>
   * <p>
   * EXPERT NOTE: Using this method will force the use of BooleanWeight2,
   * regardless of wether setUseScorer14(true) has been called.
   * </p>
   *
   * @param min the number of optional clauses that must match
   * @see #setUseScorer14
   */
  public void setMinimumNumberShouldMatch(int min) {
    this.minNrShouldMatch = min;
  }
  protected int minNrShouldMatch = 0;

  /**
   * Gets the minimum number of the optional BooleanClauses
   * which must be satisifed.
   */
  public int getMinimumNumberShouldMatch() {
    return minNrShouldMatch;
  }

  /** Adds a clause to a boolean query.
   *
   * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number
   * @see #getMaxClauseCount()
   */
  public void add(Query query, BooleanClause.Occur occur) {
    add(new BooleanClause(query, occur));
  }

  /** Adds a clause to a boolean query.
   * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number
   * @see #getMaxClauseCount()
   */
  public void add(BooleanClause clause) {
    if (clauses.size() >= maxClauseCount)
      throw new TooManyClauses();

    clauses.addElement(clause);
  }

  /** Returns the set of clauses in this query. */
  public BooleanClause[] getClauses() {
    return (BooleanClause[])clauses.toArray(new BooleanClause[0]);
  }

  private class BooleanWeight implements Weight {
    protected Similarity similarity;
    protected Vector weights = new Vector();

    public BooleanWeight(Searcher searcher)
      throws IOException {
      this.similarity = getSimilarity(searcher);
      for (int i = 0 ; i < clauses.size(); i++) {
        BooleanClause c = (BooleanClause)clauses.elementAt(i);
        weights.add(c.getQuery().createWeight(searcher));
      }
    }

    public Query getQuery() { return BooleanQuery.this; }
    public float getValue() { return getBoost(); }

    public float sumOfSquaredWeights() throws IOException {
      float sum = 0.0f;
      for (int i = 0 ; i < weights.size(); i++) {
        BooleanClause c = (BooleanClause)clauses.elementAt(i);
        Weight w = (Weight)weights.elementAt(i);
        if (!c.isProhibited())
          sum += w.sumOfSquaredWeights();         // sum sub weights
      }

      sum *= getBoost() * getBoost();             // boost each sub-weight

      return sum ;
    }


    public void normalize(float norm) {
      norm *= getBoost();                         // incorporate boost
      for (int i = 0 ; i < weights.size(); i++) {
        BooleanClause c = (BooleanClause)clauses.elementAt(i);
        Weight w = (Weight)weights.elementAt(i);
        if (!c.isProhibited())
          w.normalize(norm);
      }
    }

    /** @return A good old 1.4 Scorer */
    public Scorer scorer(IndexReader reader) throws IOException {
      // First see if the (faster) ConjunctionScorer will work.  This can be
      // used when all clauses are required.  Also, at this point a
      // BooleanScorer cannot be embedded in a ConjunctionScorer, as the hits
      // from a BooleanScorer are not always sorted by document number (sigh)
      // and hence BooleanScorer cannot implement skipTo() correctly, which is
      // required by ConjunctionScorer.
      boolean allRequired = true;
      boolean noneBoolean = true;
      for (int i = 0 ; i < weights.size(); i++) {
        BooleanClause c = (BooleanClause)clauses.elementAt(i);
        if (!c.isRequired())
          allRequired = false;
        if (c.getQuery() instanceof BooleanQuery)
          noneBoolean = false;
      }

      if (allRequired && noneBoolean) {           // ConjunctionScorer is okay
        ConjunctionScorer result =
          new ConjunctionScorer(similarity);
        for (int i = 0 ; i < weights.size(); i++) {
          Weight w = (Weight)weights.elementAt(i);
          Scorer subScorer = w.scorer(reader);
          if (subScorer == null)
            return null;
          result.add(subScorer);
        }
        return result;
      }

      // Use good-old BooleanScorer instead.
      BooleanScorer result = new BooleanScorer(similarity);

      for (int i = 0 ; i < weights.size(); i++) {
        BooleanClause c = (BooleanClause)clauses.elementAt(i);
        Weight w = (Weight)weights.elementAt(i);
        Scorer subScorer = w.scorer(reader);
        if (subScorer != null)
          result.add(subScorer, c.isRequired(), c.isProhibited());
        else if (c.isRequired())
          return null;
      }

      return result;
    }

    public Explanation explain(IndexReader reader, int doc)
      throws IOException {
      Explanation sumExpl = new Explanation();
      sumExpl.setDescription("sum of:");
      int coord = 0;
      int maxCoord = 0;
      float sum = 0.0f;
      for (int i = 0 ; i < weights.size(); i++) {
        BooleanClause c = (BooleanClause)clauses.elementAt(i);
        Weight w = (Weight)weights.elementAt(i);
        Explanation e = w.explain(reader, doc);
        if (!c.isProhibited()) maxCoord++;
        if (e.getValue() > 0) {
          if (!c.isProhibited()) {
            sumExpl.addDetail(e);
            sum += e.getValue();
            coord++;
          } else {
            return new Explanation(0.0f, "match prohibited");
          }
        } else if (c.isRequired()) {
          return new Explanation(0.0f, "match required");
        }
      }
      sumExpl.setValue(sum);

      if (coord == 1)                               // only one clause matched
        sumExpl = sumExpl.getDetails()[0];          // eliminate wrapper

      float coordFactor = similarity.coord(coord, maxCoord);
      if (coordFactor == 1.0f)                      // coord is no-op
        return sumExpl;                             // eliminate wrapper
      else {
        Explanation result = new Explanation();
        result.setDescription("product of:");
        result.addDetail(sumExpl);
        result.addDetail(new Explanation(coordFactor,
                                         "coord("+coord+"/"+maxCoord+")"));
        result.setValue(sum*coordFactor);
        return result;
      }
    }
  }

  private class BooleanWeight2 extends BooleanWeight {
    /* Merge into BooleanWeight in case the 1.4 BooleanScorer is dropped */
    public BooleanWeight2(Searcher searcher)
      throws IOException {
        super(searcher);
    }

    /** @return An alternative Scorer that uses and provides skipTo(),
     *          and scores documents in document number order.
     */
    public Scorer scorer(IndexReader reader) throws IOException {
      BooleanScorer2 result = new BooleanScorer2(similarity,
                                                 minNrShouldMatch);

      for (int i = 0 ; i < weights.size(); i++) {
        BooleanClause c = (BooleanClause)clauses.elementAt(i);
        Weight w = (Weight)weights.elementAt(i);
        Scorer subScorer = w.scorer(reader);
        if (subScorer != null)
          result.add(subScorer, c.isRequired(), c.isProhibited());
        else if (c.isRequired())
          return null;
      }

      return result;
    }
  }

  /** Indicates whether to use good old 1.4 BooleanScorer. */
  private static boolean useScorer14 = false;

  public static void setUseScorer14(boolean use14) {
    useScorer14 = use14;
  }

  public static boolean getUseScorer14() {
    return useScorer14;
  }

  protected Weight createWeight(Searcher searcher) throws IOException {

    if (0 < minNrShouldMatch) {
      // :TODO: should we throw an exception if getUseScorer14 ?
      return new BooleanWeight2(searcher);
    }

    return getUseScorer14() ? (Weight) new BooleanWeight(searcher)
                            : (Weight) new BooleanWeight2(searcher);
  }

  public Query rewrite(IndexReader reader) throws IOException {
    if (clauses.size() == 1) {                    // optimize 1-clause queries
      BooleanClause c = (BooleanClause)clauses.elementAt(0);
      if (!c.isProhibited()) {			  // just return clause

        Query query = c.getQuery().rewrite(reader);    // rewrite first

        if (getBoost() != 1.0f) {                 // incorporate boost
          if (query == c.getQuery())                   // if rewrite was no-op
            query = (Query)query.clone();         // then clone before boost
          query.setBoost(getBoost() * query.getBoost());
        }

        return query;
      }
    }

    BooleanQuery clone = null;                    // recursively rewrite
    for (int i = 0 ; i < clauses.size(); i++) {
      BooleanClause c = (BooleanClause)clauses.elementAt(i);
      Query query = c.getQuery().rewrite(reader);
      if (query != c.getQuery()) {                     // clause rewrote: must clone
        if (clone == null)
          clone = (BooleanQuery)this.clone();
        clone.clauses.setElementAt
          (new BooleanClause(query, c.getOccur()), i);
      }
    }
    if (clone != null) {
      return clone;                               // some clauses rewrote
    } else
      return this;                                // no clauses rewrote
  }

  // inherit javadoc
  public void extractTerms(Set terms) {
      for (Iterator i = clauses.iterator(); i.hasNext();) {
          BooleanClause clause = (BooleanClause) i.next();
          clause.getQuery().extractTerms(terms);
        }
  }

  public Object clone() {
    BooleanQuery clone = (BooleanQuery)super.clone();
    clone.clauses = (Vector)this.clauses.clone();
    return clone;
  }

  /** Prints a user-readable version of this query. */
  public String toString(String field) {
    StringBuffer buffer = new StringBuffer();
    boolean needParens=(getBoost() != 1.0) || (getMinimumNumberShouldMatch()>0) ;
    if (needParens) {
      buffer.append("(");
    }

    for (int i = 0 ; i < clauses.size(); i++) {
      BooleanClause c = (BooleanClause)clauses.elementAt(i);
      if (c.isProhibited())
        buffer.append("-");
      else if (c.isRequired())
        buffer.append("+");

      Query subQuery = c.getQuery();
      if (subQuery instanceof BooleanQuery) {	  // wrap sub-bools in parens
        buffer.append("(");
        buffer.append(c.getQuery().toString(field));
        buffer.append(")");
      } else
        buffer.append(c.getQuery().toString(field));

      if (i != clauses.size()-1)
        buffer.append(" ");
    }

    if (needParens) {
      buffer.append(")");
    }

    if (getMinimumNumberShouldMatch()>0) {
      buffer.append('~');
      buffer.append(getMinimumNumberShouldMatch());
    }

    if (getBoost() != 1.0f)
    {
      buffer.append(ToStringUtils.boost(getBoost()));
    }

    return buffer.toString();
  }

  /** Returns true iff <code>o</code> is equal to this. */
  public boolean equals(Object o) {
    if (!(o instanceof BooleanQuery))
      return false;
    BooleanQuery other = (BooleanQuery)o;
    return (this.getBoost() == other.getBoost())
        && this.clauses.equals(other.clauses)
        && this.getMinimumNumberShouldMatch() == other.getMinimumNumberShouldMatch();
  }

  /** Returns a hash code value for this object.*/
  public int hashCode() {
    return Float.floatToIntBits(getBoost()) ^ clauses.hashCode()
           + getMinimumNumberShouldMatch();
  }

}
