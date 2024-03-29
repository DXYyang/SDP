/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse.transport.vfs;

public class VFSConstants {
    // vfs transport prefix (e.g. used in an out EPR etc)
    public static final String VFS_PREFIX = "vfs:";

    public static final String TRANSPORT_FILE_ACTION_AFTER_PROCESS = "transport.vfs.ActionAfterProcess";
    public static final String TRANSPORT_FILE_ACTION_AFTER_ERRORS = "transport.vfs.ActionAfterErrors";
    public static final String TRANSPORT_FILE_ACTION_AFTER_FAILURE = "transport.vfs.ActionAfterFailure";

    public static final String TRANSPORT_FILE_MOVE_AFTER_PROCESS = "transport.vfs.MoveAfterProcess";
    public static final String TRANSPORT_FILE_MOVE_AFTER_ERRORS = "transport.vfs.MoveAfterErrors";
    public static final String TRANSPORT_FILE_MOVE_AFTER_FAILURE = "transport.vfs.MoveAfterFailure";

    public static final String TRANSPORT_FILE_FILE_URI = "transport.vfs.FileURI";
    public static final String TRANSPORT_FILE_FILE_NAME_PATTERN = "transport.vfs.FileNamePattern";
    public static final String TRANSPORT_FILE_CONTENT_TYPE = "transport.vfs.ContentType";

    public static final String REPLY_FILE_URI = "transport.vfs.ReplyFileURI";
    public static final String REPLY_FILE_NAME = "transport.vfs.ReplyFileName";

    public static final String DEFAULT_RESPONSE_FILE = "response.xml";

    // transport header property names used by the VFS transport
    public static final String FILE_PATH = "FILE_PATH";
    public static final String FILE_NAME = "FILE_NAME";
    public static final String FILE_LENGTH = "FILE_LENGTH";
    public static final String LAST_MODIFIED = "LAST_MODIFIED";
}
