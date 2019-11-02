
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
        
package org.apache.poi.ddf;

import junit.framework.TestCase;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.HexRead;

public class TestEscherDggRecord extends TestCase
{
    public void testSerialize() throws Exception
    {
        EscherDggRecord r = createRecord();

        byte[] data = new byte[32];
        int bytesWritten = r.serialize( 0, data, new NullEscherSerializationListener() );
        assertEquals( 32, bytesWritten );
        assertEquals( "[00, 00, " +
                "06, F0, " +
                "18, 00, 00, 00, " +
                "02, 04, 00, 00, " +
                "02, 00, 00, 00, " +
                "02, 00, 00, 00, " +
                "01, 00, 00, 00, " +
                "01, 00, 00, 00, 02, 00, 00, 00, ]",
                HexDump.toHex( data ) );
    }

    public void testFillFields() throws Exception
    {
        String hexData = "00 00 " +
                "06 F0 " +
                "18 00 00 00 " +
                "02 04 00 00 " +
                "02 00 00 00 " +
                "02 00 00 00 " +
                "01 00 00 00 " +
                "01 00 00 00 02 00 00 00";
        byte[] data = HexRead.readFromString( hexData );
        EscherDggRecord r = new EscherDggRecord();
        int bytesWritten = r.fillFields( data, new DefaultEscherRecordFactory() );

        assertEquals( 32, bytesWritten );
        assertEquals( 0x402, r.getShapeIdMax() );
        assertEquals( 0x02, r.getNumIdClusters() );
        assertEquals( 0x02, r.getNumShapesSaved() );
        assertEquals( 0x01, r.getDrawingsSaved() );
        assertEquals( 1, r.getFileIdClusters().length );
        assertEquals( 0x01, r.getFileIdClusters()[0].getDrawingGroupId());
        assertEquals( 0x02, r.getFileIdClusters()[0].getNumShapeIdsUsed());
    }

    public void testToString() throws Exception
    {
        String nl = System.getProperty("line.separator");

        String expected = "org.apache.poi.ddf.EscherDggRecord:" + nl +
                "  RecordId: 0xF006" + nl +
                "  Options: 0x0000" + nl +
                "  ShapeIdMax: 1026" + nl +
                "  NumIdClusters: 2" + nl +
                "  NumShapesSaved: 2" + nl +
                "  DrawingsSaved: 1" + nl +
                "  DrawingGroupId1: 1" + nl +
                "  NumShapeIdsUsed1: 2" + nl;
        assertEquals( expected, createRecord().toString() );
    }

    private EscherDggRecord createRecord()
    {
        EscherDggRecord r = new EscherDggRecord();
        r.setOptions( (short) 0x0000 );
        r.setRecordId( EscherDggRecord.RECORD_ID );
        r.setShapeIdMax( 0x402 );
        r.setNumShapesSaved( 0x02 );
        r.setDrawingsSaved( 0x01 );
        r.setFileIdClusters(new EscherDggRecord.FileIdCluster[] {
            new EscherDggRecord.FileIdCluster( 1, 2 )
        });
        return r;
    }

    public void testGetRecordSize() throws Exception
    {
        EscherDggRecord r = new EscherDggRecord();
        r.setFileIdClusters(new EscherDggRecord.FileIdCluster[] { new EscherDggRecord.FileIdCluster(0,0) } );
        assertEquals(32,r.getRecordSize());

    }

}
