
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
 * Title: Recalc Id Record<P>
 * Description:  This record contains an ID that marks when a worksheet was last
 *               recalculated. It's an optimization Excel uses to determine if it
 *               needs to  recalculate the spreadsheet when it's opened. So far, only
 *               the two values <code>0xC1 0x01 0x00 0x00 0x80 0x38 0x01 0x00</code>
 *               (do not recalculate) and <code>0xC1 0x01 0x00 0x00 0x60 0x69 0x01
 *               0x00</code> have been seen. If the field <code>isNeeded</code> is
 *               set to false (default), then this record is swallowed during the
 *               serialization process<P>
 * REFERENCE:  http://chicago.sourceforge.net/devel/docs/excel/biff8.html<P>
 * @author Luc Girardin (luc dot girardin at macrofocus dot com)
 * @version 2.0-pre
 * @see org.apache.poi.hssf.model.Workbook
 */

public class RecalcIdRecord
    extends Record
{
    public final static short sid = 0x1c1;
    public short[]            field_1_recalcids;

    private boolean isNeeded = false;

    public RecalcIdRecord()
    {
    }

    /**
     * Constructs a RECALCID record and sets its fields appropriately.
     *
     * @param id     id must be 0x13d or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     */

    public RecalcIdRecord(short id, short size, byte [] data)
    {
        super(id, size, data);
    }

    /**
     * Constructs a RECALCID record and sets its fields appropriately.
     *
     * @param id     id must be 0x13d or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     * @param offset of the record
     */

    public RecalcIdRecord(short id, short size, byte [] data, int offset)
    {
        super(id, size, data, offset);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A RECALCID RECORD");
        }
    }

    protected void fillFields(byte [] data, short size, int offset)
    {
        field_1_recalcids = new short[ size / 2 ];
        for (int k = 0; k < field_1_recalcids.length; k++)
        {
            field_1_recalcids[ k ] = LittleEndian.getShort(data,
                                                        (k * 2) + offset);
        }
    }

    /**
     * set the recalc array.
     * @param array of recalc id's
     */

    public void setRecalcIdArray(short [] array)
    {
        field_1_recalcids = array;
    }

    /**
     * get the recalc array.
     * @return array of recalc id's
     */

    public short [] getRecalcIdArray()
    {
        return field_1_recalcids;
    }

    public void setIsNeeded(boolean isNeeded) {
        this.isNeeded = isNeeded;
    }

    public boolean isNeeded() {
        return isNeeded;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[RECALCID]\n");
        buffer.append("    .elements        = ").append(field_1_recalcids.length)
            .append("\n");
        for (int k = 0; k < field_1_recalcids.length; k++)
        {
            buffer.append("    .element_" + k + "       = ")
                .append(field_1_recalcids[ k ]).append("\n");
        }
        buffer.append("[/RECALCID]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        short[] tabids     = getRecalcIdArray();
        short   length     = ( short ) (tabids.length * 2);
        int     byteoffset = 4;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,
                              (( short ) length));

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
        return 4 + (getRecalcIdArray().length * 2);
    }

    public short getSid()
    {
        return this.sid;
    }
}
