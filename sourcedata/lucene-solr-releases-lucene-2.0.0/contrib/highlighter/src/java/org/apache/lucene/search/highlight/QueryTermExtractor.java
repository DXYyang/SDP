package org.apache.lucene.search.highlight;
/**
 * Copyright 2002-2004 The Apache Software Foundation
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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

/**
 * Utility class used to extract the terms used in a query, plus any weights.
 * This class will not find terms for MultiTermQuery, RangeQuery and PrefixQuery classes
 * so the caller must pass a rewritten query (see Query.rewrite) to obtain a list of 
 * expanded terms. 
 * 
 */
public final class QueryTermExtractor
{

	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @return an array of the terms used in a query, plus their weights.
	 */
	public static final WeightedTerm[] getTerms(Query query) 
	{
		return getTerms(query,false);
	}

	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @param reader used to compute IDF which can be used to a) score selected fragments better 
	 * b) use graded highlights eg chaning intensity of font color
	 * @param fieldName the field on which Inverse Document Frequency (IDF) calculations are based
	 * @return an array of the terms used in a query, plus their weights.
	 */
	public static final WeightedTerm[] getIdfWeightedTerms(Query query, IndexReader reader, String fieldName) 
	{
	    WeightedTerm[] terms=getTerms(query,false, fieldName);
	    int totalNumDocs=reader.numDocs();
	    for (int i = 0; i < terms.length; i++)
        {
	        try
            {
                int docFreq=reader.docFreq(new Term(fieldName,terms[i].term));
                //IDF algorithm taken from DefaultSimilarity class
                float idf=(float)(Math.log((float)totalNumDocs/(double)(docFreq+1)) + 1.0);
                terms[i].weight*=idf;
            } 
	        catch (IOException e)
            {
	            //ignore 
            }
        }
		return terms;
	}

	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @param prohibited <code>true</code> to extract "prohibited" terms, too
	 * @param fieldName  The fieldName used to filter query terms
   * @return an array of the terms used in a query, plus their weights.
   */
	public static final WeightedTerm[] getTerms(Query query, boolean prohibited, String fieldName) 
	{
		HashSet terms=new HashSet();
		if(fieldName!=null)
		{
		    fieldName=fieldName.intern();
		}
		getTerms(query,terms,prohibited,fieldName);
		return (WeightedTerm[]) terms.toArray(new WeightedTerm[0]);
	}
	
	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @param prohibited <code>true</code> to extract "prohibited" terms, too
   * @return an array of the terms used in a query, plus their weights.
   */
	public static final WeightedTerm[] getTerms(Query query, boolean prohibited) 
	{
	    return getTerms(query,prohibited,null);
	}	

	//fieldname MUST be interned prior to this call
	private static final void getTerms(Query query, HashSet terms,boolean prohibited, String fieldName) 
	{
       	try
       	{
       		HashSet nonWeightedTerms=new HashSet();
       		query.extractTerms(nonWeightedTerms);
       		for (Iterator iter = nonWeightedTerms.iterator(); iter.hasNext();)
			{
				Term term = (Term) iter.next();
			    if((fieldName==null)||(term.field()==fieldName))
				{
					terms.add(new WeightedTerm(query.getBoost(),term.text()));
				}
			}
	      }
	      catch(UnsupportedOperationException ignore)
	      {
	    	  //this is non-fatal for our purposes
       	  }		        			        	
	}
}
