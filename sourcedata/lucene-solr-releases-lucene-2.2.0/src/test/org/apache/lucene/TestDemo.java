package org.apache.lucene;

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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * A very simple demo used in the API documentation (src/java/overview.html).
 * 
 * @author Daniel Naber
 */
public class TestDemo extends TestCase {

  public void testDemo() throws IOException, ParseException {

    Analyzer analyzer = new StandardAnalyzer();

    // Store the index in memory:
    Directory directory = new RAMDirectory();
    // To store an index on disk, use this instead (note that the 
    // parameter true will overwrite the index in that directory
    // if one exists):
    //Directory directory = FSDirectory.getDirectory("/tmp/testindex", true);
    IndexWriter iwriter = new IndexWriter(directory, analyzer, true);
    iwriter.setMaxFieldLength(25000);
    Document doc = new Document();
    String text = "This is the text to be indexed.";
    doc.add(new Field("fieldname", text, Field.Store.YES,
        Field.Index.TOKENIZED));
    iwriter.addDocument(doc);
    iwriter.close();
    
    // Now search the index:
    IndexSearcher isearcher = new IndexSearcher(directory);
    // Parse a simple query that searches for "text":
    QueryParser parser = new QueryParser("fieldname", analyzer);
    Query query = parser.parse("text");
    Hits hits = isearcher.search(query);
    assertEquals(1, hits.length());
    // Iterate through the results:
    for (int i = 0; i < hits.length(); i++) {
      Document hitDoc = hits.doc(i);
      assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
    }
    isearcher.close();
    directory.close();
    
  }

}
