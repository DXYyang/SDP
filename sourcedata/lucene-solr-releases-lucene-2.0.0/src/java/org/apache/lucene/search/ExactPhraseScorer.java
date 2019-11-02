package org.apache.lucene.search;

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
import org.apache.lucene.index.*;

final class ExactPhraseScorer extends PhraseScorer {

  ExactPhraseScorer(Weight weight, TermPositions[] tps, int[] positions, Similarity similarity,
                    byte[] norms) {
    super(weight, tps, positions, similarity, norms);
  }

  protected final float phraseFreq() throws IOException {
    // sort list with pq
    for (PhrasePositions pp = first; pp != null; pp = pp.next) {
      pp.firstPosition();
      pq.put(pp);				  // build pq from list
    }
    pqToList();					  // rebuild list from pq

    int freq = 0;
    do {					  // find position w/ all terms
      while (first.position < last.position) {	  // scan forward in first
	do {
	  if (!first.nextPosition())
	    return (float)freq;
	} while (first.position < last.position);
	firstToLast();
      }
      freq++;					  // all equal: a match
    } while (last.nextPosition());
  
    return (float)freq;
  }
}
