package org.apache.lucene.gdata.server.registry;

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
 * This exception is thrown by the
 * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry} if
 * registering a service or a component fails.
 * 
 * @author Simon Willnauer
 * 
 */
public class RegistryException extends Exception {

 
    private static final long serialVersionUID = -3563720639871194466L;

    /**
     * Constructs a new Registry Exception.
     */
    public RegistryException() {
        super();
        
    }

    /**
     * Constructs a new Registry Exception with the specified detail message.
     * @param arg0 - detail message
     */
    public RegistryException(String arg0) {
        super(arg0);
        
    }

    /**
     * Constructs a new Registry Exception with the specified detail message and nested exception.
     * @param arg0 - detail message
     * @param arg1 - nested exception
     */
    public RegistryException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        
    }

    /** Constructs a new Registry Exception with a nested exception.
     * @param arg0 - nested exception
     */
    public RegistryException(Throwable arg0) {
        super(arg0);
        
    }

}
