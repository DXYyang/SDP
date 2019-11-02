
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

import org.apache.poi.util.LittleEndian;

/**
 * Title: Sheet Tab Index Array Record<P>
 * Description:  Contains an array of sheet id's.  Sheets always keep their ID
 *               regardless of what their name is.<P>
 * REFERENCE:  PG 412 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @version 2.0-pre
 */

public class TabIdRecord
    extends Record
{
    public final static short sid = 0x13d;
    public short[]            field_1_tabids;

    public TabIdRecord()
    {
    }

    /**
     * Constructs a TabID record and sets its fields appropriately.
     *
     * @param id     id must be 0x13d or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     */

    public TabIdRecord(short id, short size, byte [] data)
    {
        super(id, size, data);
    }

    /**
     * Constructs a TabID record and sets its fields appropriately.
     *
     * @param id     id must be 0x13d or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     * @param offset of the record
     */

    public TabIdRecord(short id, short size, byte [] data, int offset)
    {
        super(id, size, data, offset);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A TABID RECORD");
        }
    }

    protected void fillFields(byte [] data, short size, int offset)
    {
        field_1_tabids = new short[ size / 2 ];
        for (int k = 0; k < field_1_tabids.length; k++)
        {
            field_1_tabids[ k ] = LittleEndian.getShort(data,
                                                        (k * 2) + offset);
        }
    }

    /**
     * set the tab array.  (0,1,2).
     * @param array of tab id's {0,1,2}
     */

    public void setTabIdArray(short [] array)
    {
        field_1_tabids = array;
    }

    /**
     * get the tab array.  (0,1,2).
     * @return array of tab id's {0,1,2}
     */

    public short [] getTabIdArray()
    {
        return field_1_tabids;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[TABID]\n");
        buffer.append("    .elements        = ").append(field_1_tabids.length)
            .append("\n");
        for (int k = 0; k < field_1_tabids.length; k++)
        {
            buffer.append("    .element_" + k + "       = ")
                .append(field_1_tabids[ k ]).append("\n");
        }
        buffer.append("[/TABID]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        short[] tabids     = getTabIdArray();
        short   length     = ( short ) (tabids.length * 2);
        int     byteoffset = 4;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,
                              (( short ) length));   // nubmer tabids *

        // 2 (num bytes in a short)
        for (int k = 0; k < (length / 2); k++)
        {
            LittleEndian.putShort(data, byteoffset + offset, tabids[ k ]);
            byteoffset += 2;
        }
        return getRecordSize();
    }

    public int getRecordSize()
    {
        return 4 + (getTabIdArray().length * 2);
    }

    public short getSid()
    {
        return this.sid;
    }
}
