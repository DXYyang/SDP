/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.hssf.record;


import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Tests the serialization and deserialization of the NoteRecord
 * class works correctly.  Test data taken directly from a real
 * Excel file.
 *
 * @author Yegor Kozlov
 */
public class TestNoteStructureSubRecord
        extends TestCase
{
    private byte[] data = new byte[] {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0x80, 0x00, 0x00, 0x00,
        0x00, 0x00, (byte)0xBF, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0x81, 0x01,
        (byte)0xCC, (byte)0xEC
    };

    public TestNoteStructureSubRecord(String name)
    {
        super(name);
    }

    public void testRead()
            throws Exception
    {

        NoteStructureSubRecord record = new NoteStructureSubRecord(new TestcaseRecordInputStream(NoteStructureSubRecord.sid, (short)data.length, data));

        assertEquals(NoteStructureSubRecord.sid, record.getSid());
        record.validateSid(NoteStructureSubRecord.sid);
        assertEquals(data.length + 4, record.getRecordSize());

    }

    public void testWrite()
    {
        NoteStructureSubRecord record = new NoteStructureSubRecord();
        assertEquals(NoteStructureSubRecord.sid, record.getSid());
        record.validateSid(NoteStructureSubRecord.sid);
        assertEquals(data.length + 4, record.getRecordSize());

        byte [] ser = record.serialize();
        assertEquals(ser.length - 4, data.length);

    }
}
