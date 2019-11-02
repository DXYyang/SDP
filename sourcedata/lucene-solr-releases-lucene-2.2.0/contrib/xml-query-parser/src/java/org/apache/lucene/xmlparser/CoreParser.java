package org.apache.lucene.xmlparser;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.xmlparser.builders.BooleanQueryBuilder;
import org.apache.lucene.xmlparser.builders.ConstantScoreQueryBuilder;
import org.apache.lucene.xmlparser.builders.FilteredQueryBuilder;
import org.apache.lucene.xmlparser.builders.MatchAllDocsQueryBuilder;
import org.apache.lucene.xmlparser.builders.CachedFilterBuilder;
import org.apache.lucene.xmlparser.builders.RangeFilterBuilder;
import org.apache.lucene.xmlparser.builders.SpanFirstBuilder;
import org.apache.lucene.xmlparser.builders.SpanNearBuilder;
import org.apache.lucene.xmlparser.builders.SpanNotBuilder;
import org.apache.lucene.xmlparser.builders.SpanOrBuilder;
import org.apache.lucene.xmlparser.builders.SpanOrTermsBuilder;
import org.apache.lucene.xmlparser.builders.SpanQueryBuilderFactory;
import org.apache.lucene.xmlparser.builders.SpanTermBuilder;
import org.apache.lucene.xmlparser.builders.TermQueryBuilder;
import org.apache.lucene.xmlparser.builders.TermsQueryBuilder;
import org.apache.lucene.xmlparser.builders.UserInputQueryBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
 * Assembles a QueryBuilder which uses only core Lucene Query objects
 * @author Mark
 *
 */
public class CoreParser implements QueryBuilder
{
	
	protected Analyzer analyzer;
	protected QueryParser parser;
	protected QueryBuilderFactory queryFactory;
	protected FilterBuilderFactory filterFactory;
	//Controls the max size of the LRU cache used for QueryFilter objects parsed.
	public static int maxNumCachedFilters=20;


	public CoreParser(Analyzer analyzer, QueryParser parser)
	{
		this.analyzer=analyzer;
		this.parser=parser;
		filterFactory = new FilterBuilderFactory();
		filterFactory.addBuilder("RangeFilter",new RangeFilterBuilder());
		
		
		queryFactory = new QueryBuilderFactory();
		queryFactory.addBuilder("TermQuery",new TermQueryBuilder());
		queryFactory.addBuilder("TermsQuery",new TermsQueryBuilder(analyzer));
		queryFactory.addBuilder("MatchAllDocsQuery",new MatchAllDocsQueryBuilder());
		queryFactory.addBuilder("BooleanQuery",new BooleanQueryBuilder(queryFactory));
		queryFactory.addBuilder("UserQuery",new UserInputQueryBuilder(parser));
		queryFactory.addBuilder("FilteredQuery",new FilteredQueryBuilder(filterFactory,queryFactory));
		queryFactory.addBuilder("ConstantScoreQuery",new ConstantScoreQueryBuilder(filterFactory));
		
		filterFactory.addBuilder("CachedFilter",new CachedFilterBuilder(queryFactory,
							filterFactory, maxNumCachedFilters));
		
		
		SpanQueryBuilderFactory sqof=new SpanQueryBuilderFactory();

		SpanNearBuilder snb=new SpanNearBuilder(sqof);
		sqof.addBuilder("SpanNear",snb);
		queryFactory.addBuilder("SpanNear",snb);

		SpanTermBuilder snt=new SpanTermBuilder();
		sqof.addBuilder("SpanTerm",snt);
		queryFactory.addBuilder("SpanTerm",snt);
		
		SpanOrBuilder sot=new SpanOrBuilder(sqof);
		sqof.addBuilder("SpanOr",sot);
		queryFactory.addBuilder("SpanOr",sot);

		SpanOrTermsBuilder sots=new SpanOrTermsBuilder(analyzer);
		sqof.addBuilder("SpanOrTerms",sots);
		queryFactory.addBuilder("SpanOrTerms",sots);		
		
		SpanFirstBuilder sft=new SpanFirstBuilder(sqof);
		sqof.addBuilder("SpanFirst",sft);
		queryFactory.addBuilder("SpanFirst",sft);
		
		SpanNotBuilder snot=new SpanNotBuilder(sqof);
		sqof.addBuilder("SpanNot",snot);
		queryFactory.addBuilder("SpanNot",snot);	
	}
	
	public Query parse(InputStream xmlStream) throws ParserException
	{
		return getQuery(parseXML(xmlStream).getDocumentElement());
	}
	
	public void addQueryBuilder(String nodeName,QueryBuilder builder)
	{
		queryFactory.addBuilder(nodeName,builder);
	}
	public void addFilterBuilder(String nodeName,FilterBuilder builder)
	{
		filterFactory.addBuilder(nodeName,builder);
	}
	
	private static Document parseXML(InputStream pXmlFile) throws ParserException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try
		{
			db = dbf.newDocumentBuilder();
		}
		catch (Exception se)
		{
			throw new ParserException("XML Parser configuration error", se);
		}
		org.w3c.dom.Document doc = null;
		try
		{
			doc = db.parse(pXmlFile);
		}
		catch (Exception se)
		{
			throw new ParserException("Error parsing XML stream:" + se, se);
		}
		return doc;
	}
	

	public Query getQuery(Element e) throws ParserException
	{
		return queryFactory.getQuery(e);
	}
}
