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

import org.apache.lucene.util.PriorityQueue;

final class PhraseQueue extends PriorityQueue {
  PhraseQueue(int size) {
    initialize(size);
  }

  protected final boolean lessThan(Object o1, Object o2) {
    PhrasePositions pp1 = (PhrasePositions)o1;
    PhrasePositions pp2 = (PhrasePositions)o2;
    if (pp1.doc == pp2.doc) 
      return pp1.position < pp2.position;
    else
      return pp1.doc < pp2.doc;
  }
}
