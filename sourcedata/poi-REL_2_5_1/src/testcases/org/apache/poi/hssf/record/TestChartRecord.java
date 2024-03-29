
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
 * Tests the serialization and deserialization of the ChartRecord
 * class works correctly.  Test data taken directly from a real
 * Excel file.
 *

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class TestChartRecord
        extends TestCase
{
    byte[] data = new byte[] {
        (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
        (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
        (byte)0xE8,(byte)0xFF,(byte)0xD0,(byte)0x01,
        (byte)0xC8,(byte)0xCC,(byte)0xE5,(byte)0x00
    };

    public TestChartRecord(String name)
    {
        super(name);
    }

    public void testLoad()
            throws Exception
    {

        ChartRecord record = new ChartRecord((short)0x1002, (short)data.length, data);
        assertEquals( 0, record.getX());
        assertEquals( 0, record.getY());
        assertEquals( 30474216, record.getWidth());
        assertEquals( 15060168, record.getHeight());


        assertEquals( 20, record.getRecordSize() );

        record.validateSid((short)0x1002);
    }

    public void testStore()
    {
        ChartRecord record = new ChartRecord();
        record.setX( 0 );
        record.setY( 0 );
        record.setWidth( 30474216 );
        record.setHeight( 15060168 );


        byte [] recordBytes = record.serialize();
        assertEquals(recordBytes.length - 4, data.length);
        for (int i = 0; i < data.length; i++)
            assertEquals("At offset " + i, data[i], recordBytes[i+4]);
    }
}
