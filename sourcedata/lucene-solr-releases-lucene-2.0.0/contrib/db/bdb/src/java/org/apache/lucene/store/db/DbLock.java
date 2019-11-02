package org.apache.lucene.store.db;

/**
 * Copyright 2002-2006 The Apache Software Foundation
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

import org.apache.lucene.store.Lock;

/**
 * This implementation of {@link org.apache.lucene.store.Lock Lock} is
 * trivial as {@link DbDirectory} operations are managed by the Berkeley DB
 * locking system.
 *
 * @author Andi Vajda
 */

public class DbLock extends Lock {

    boolean isLocked = false;

    public DbLock()
    {
    }

    public boolean obtain()
    {
        return (isLocked = true);
    }

    public void release()
    {
        isLocked = false;
    }

    public boolean isLocked()
    {
        return isLocked;
    }
}

