/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.atom;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

/**
 * Filter used by the {@link org.apache.camel.component.atom.AtomEntryPollingConsumer} to filter entries
 * from the feed.
 *
 * @version $Revision$
 */
public interface EntryFilter {

    /**
     * Tests to be used as filtering the feed for only entries of interest, such as only new entries, etc.
     *
     * @param endpoint  the endpoint
     * @param feed      the Atom feed
     * @param entry     the given entry to filter
     * @return  <tt>true</tt> to include the entry, <ff>false</tt> to skip it
     */
    boolean isValidEntry(AtomEndpoint endpoint, Document<Feed> feed, Entry entry);

}
