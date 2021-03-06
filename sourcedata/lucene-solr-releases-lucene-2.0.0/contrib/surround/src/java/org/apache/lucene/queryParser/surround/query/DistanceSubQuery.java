package org.apache.lucene.queryParser.surround.query;
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

import java.io.IOException;

public interface DistanceSubQuery {
  /** When distanceSubQueryNotAllowed() returns non null, the reason why the subquery
   * is not allowed as a distance subquery is returned.
   * <br>When distanceSubQueryNotAllowed() returns null addSpanNearQueries() can be used
   * in the creation of the span near clause for the subquery.
   */
  String distanceSubQueryNotAllowed();
    
  void addSpanQueries(SpanNearClauseFactory sncf) throws IOException;
}

