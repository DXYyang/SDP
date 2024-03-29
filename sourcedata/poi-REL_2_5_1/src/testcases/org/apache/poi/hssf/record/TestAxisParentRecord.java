
/* ====================================================================
   Copyright 2002-2004   Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
        


package org.apache.poi.hssf.record;


import junit.framework.TestCase;

/**
 * Tests the serialization and deserialization of the AxisParentRecord
 * class works correctly.  Test data taken directly from a real
 * Excel file.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class TestAxisParentRecord
        extends TestCase
{
    byte[] data = new byte[] {
        (byte)0x00,(byte)0x00,                                   // axis type
        (byte)0x1D,(byte)0x02,(byte)0x00,(byte)0x00,             // x
        (byte)0xDD,(byte)0x00,(byte)0x00,(byte)0x00,             // y
        (byte)0x31,(byte)0x0B,(byte)0x00,(byte)0x00,             // width
        (byte)0x56,(byte)0x0B,(byte)0x00,(byte)0x00              // height
    };

    public TestAxisParentRecord(String name)
    {
        super(name);
    }

    public void testLoad()
            throws Exception
    {
        AxisParentRecord record = new AxisParentRecord((short)0x1041, (short)data.length, data);
        assertEquals( AxisParentRecord.AXIS_TYPE_MAIN, record.getAxisType());
        assertEquals( 0x021d, record.getX());
        assertEquals( 0xdd, record.getY());
        assertEquals( 0x0b31, record.getWidth());
        assertEquals( 0x0b56, record.getHeight());


        assertEquals( 22, record.getRecordSize() );

        record.validateSid((short)0x1041);
    }

    public void testStore()
    {
        AxisParentRecord record = new AxisParentRecord();
        record.setAxisType( AxisParentRecord.AXIS_TYPE_MAIN );
        record.setX( 0x021d );
        record.setY( 0xdd );
        record.setWidth( 0x0b31 );
        record.setHeight( 0x0b56 );


        byte [] recordBytes = record.serialize();
        assertEquals(recordBytes.length - 4, data.length);
        for (int i = 0; i < data.length; i++)
            assertEquals("At offset " + i, data[i], recordBytes[i+4]);
    }
}
