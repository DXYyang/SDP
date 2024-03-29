
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
 * Tests the serialization and deserialization of the CommonObjectDataSubRecord
 * class works correctly.  Test data taken directly from a real
 * Excel file.
 *

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class TestCommonObjectDataSubRecord
        extends TestCase
{
    byte[] data = new byte[] {
        (byte)0x12,(byte)0x00,(byte)0x01,(byte)0x00,
        (byte)0x01,(byte)0x00,(byte)0x11,(byte)0x60,
        (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
        (byte)0x00,(byte)0x0D,(byte)0x26,(byte)0x01,
        (byte)0x00,(byte)0x00,
    };

    public TestCommonObjectDataSubRecord(String name)
    {
        super(name);
    }

    public void testLoad()
            throws Exception
    {
        CommonObjectDataSubRecord record = new CommonObjectDataSubRecord((short)0x15, (short)data.length, data);
        

        assertEquals( CommonObjectDataSubRecord.OBJECT_TYPE_LIST_BOX, record.getObjectType());
        assertEquals( (short)1, record.getObjectId());
        assertEquals( (short)1, record.getOption());
        assertEquals( true , record.isLocked() );
        assertEquals( false, record.isPrintable() );
        assertEquals( false, record.isAutofill() );
        assertEquals( false, record.isAutoline() );
        assertEquals( (int)24593, record.getReserved1());
        assertEquals( (int)218103808, record.getReserved2());
        assertEquals( (int)294, record.getReserved3());
        assertEquals( 22 , record.getRecordSize() );

        record.validateSid((short)0x15);
    }

    public void testStore()
    {
        CommonObjectDataSubRecord record = new CommonObjectDataSubRecord();

        record.setObjectType( CommonObjectDataSubRecord.OBJECT_TYPE_LIST_BOX );
        record.setObjectId( (short) 1);
        record.setOption( (short) 1);
        record.setLocked( true );
        record.setPrintable( false );
        record.setAutofill( false );
        record.setAutoline( false );
        record.setReserved1( (int) 24593);
        record.setReserved2( (int) 218103808);
        record.setReserved3( (int) 294);

        byte [] recordBytes = record.serialize();
        assertEquals(recordBytes.length - 4, data.length);
        for (int i = 0; i < data.length; i++)
            assertEquals("At offset " + i, data[i], recordBytes[i+4]);
    }
}
