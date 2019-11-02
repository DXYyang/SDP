
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

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

/**
 * Tests the record factory
 * @author Glen Stampoultzis (glens at apache.org)
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Csaba Nagy (ncsaba at yahoo dot com)
 */

public class TestRecordFactory
    extends TestCase
{

    /**
     * Creates new TestRecordFactory
     * @param testCaseName
     */

    public TestRecordFactory(String testCaseName)
    {
        super(testCaseName);
    }

    /**
     * TEST NAME:  Test Basic Record Construction <P>
     * OBJECTIVE:  Test that the RecordFactory given the required parameters for know
     *             record types can construct the proper record w/values.<P>
     * SUCCESS:    Record factory creates the records with the expected values.<P>
     * FAILURE:    The wrong records are creates or contain the wrong values <P>
     *
     */

    public void testBasicRecordConstruction()
        throws Exception
    {
        short    recType = BOFRecord.sid;
        byte[]   data    = new byte[]
        {
            0, 6, 5, 0, -2, 28, -51, 7, -55, 64, 0, 0, 6, 1, 0, 0
        };
        short    size    = 16;
        Record[] record  = RecordFactory.createRecord(new TestcaseRecordInputStream(recType, size, data));

        assertEquals(BOFRecord.class.getName(),
                     record[ 0 ].getClass().getName());
        BOFRecord bofRecord = ( BOFRecord ) record[ 0 ];

        assertEquals(7422, bofRecord.getBuild());
        assertEquals(1997, bofRecord.getBuildYear());
        assertEquals(16585, bofRecord.getHistoryBitMask());
        assertEquals(20, bofRecord.getRecordSize());
        assertEquals(262, bofRecord.getRequiredVersion());
        assertEquals(2057, bofRecord.getSid());
        assertEquals(5, bofRecord.getType());
        assertEquals(1536, bofRecord.getVersion());
        recType = MMSRecord.sid;
        size    = 2;
        data    = new byte[]
        {
            0, 0
        };
        record  = RecordFactory.createRecord(new TestcaseRecordInputStream(recType, size, data));
        assertEquals(MMSRecord.class.getName(),
                     record[ 0 ].getClass().getName());
        MMSRecord mmsRecord = ( MMSRecord ) record[ 0 ];

        assertEquals(0, mmsRecord.getAddMenuCount());
        assertEquals(0, mmsRecord.getDelMenuCount());
        assertEquals(6, mmsRecord.getRecordSize());
        assertEquals(193, mmsRecord.getSid());
    }

    /**
     * TEST NAME:  Test Special Record Construction <P>
     * OBJECTIVE:  Test that the RecordFactory given the required parameters for
     *             constructing a RKRecord will return a NumberRecord.<P>
     * SUCCESS:    Record factory creates the Number record with the expected values.<P>
     * FAILURE:    The wrong records are created or contain the wrong values <P>
     *
     */

    public void testSpecial()
        throws Exception
    {
        short    recType = RKRecord.sid;
        byte[]   data    = new byte[]
        {
            0, 0, 0, 0, 21, 0, 0, 0, 0, 0
        };
        short    size    = 10;
        Record[] record  = RecordFactory.createRecord(new TestcaseRecordInputStream(recType, size, data));

        assertEquals(NumberRecord.class.getName(),
                     record[ 0 ].getClass().getName());
        NumberRecord numberRecord = ( NumberRecord ) record[ 0 ];

        assertEquals(0, numberRecord.getColumn());
        assertEquals(18, numberRecord.getRecordSize());
        assertEquals(0, numberRecord.getRow());
        assertEquals(515, numberRecord.getSid());
        assertEquals(0.0, numberRecord.getValue(), 0.001);
        assertEquals(21, numberRecord.getXFIndex());
    }

    /**
     * TEST NAME:  Test Creating ContinueRecords After Unknown Records From An InputStream <P>
     * OBJECTIVE:  Test that the RecordFactory given an InputStream
     *             constructs the expected array of records.<P>
     * SUCCESS:    Record factory creates the expected records.<P>
     * FAILURE:    The wrong records are created or contain the wrong values <P>
     *
     */
    public void testContinuedUnknownRecord()
    {
        byte[]   data    = new byte[]
        {
            0, -1, 0, 0, // an unknown record with 0 length
            0x3C , 0, 3, 0, 1, 2, 3, // a continuation record with 3 bytes of data
            0x3C , 0, 1, 0, 4 // one more continuation record with 1 byte of data
        };

        ByteArrayInputStream bois = new ByteArrayInputStream(data);
        Record[] records = (Record[])
          RecordFactory.createRecords(bois).toArray(new Record[0]);
        assertEquals("Created record count", 3, records.length);
        assertEquals("1st record's type",
                     UnknownRecord.class.getName(),
                     records[ 0 ].getClass().getName());
        assertEquals("1st record's sid", (short)-256, records[0].getSid());
        assertEquals("2nd record's type",
                     ContinueRecord.class.getName(),
                     records[ 1 ].getClass().getName());
        ContinueRecord record = (ContinueRecord) records[1];
        assertEquals("2nd record's sid", 0x3C, record.getSid());
        assertEquals("1st data byte", 1, record.getData()[ 0 ]);
        assertEquals("2nd data byte", 2, record.getData()[ 1 ]);
        assertEquals("3rd data byte", 3, record.getData()[ 2 ]);
        assertEquals("3rd record's type",
                     ContinueRecord.class.getName(),
                     records[ 2 ].getClass().getName());
        record = (ContinueRecord) records[2];
        assertEquals("3nd record's sid", 0x3C, record.getSid());
        assertEquals("4th data byte", 4, record.getData()[ 0 ]);
    }

    public static void main(String [] ignored_args)
    {
        System.out
            .println("Testing org.apache.poi.hssf.record.TestRecordFactory");
        junit.textui.TestRunner.run(TestRecordFactory.class);
    }
}
