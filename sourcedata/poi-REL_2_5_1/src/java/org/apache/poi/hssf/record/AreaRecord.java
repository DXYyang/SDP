
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



import org.apache.poi.util.*;

/**
 * The area record is used to define a area chart.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class AreaRecord
    extends Record
{
    public final static short      sid                             = 0x101A;
    private  short      field_1_formatFlags;
    private  BitField   stacked                                     = new BitField(0x1);
    private  BitField   displayAsPercentage                         = new BitField(0x2);
    private  BitField   shadow                                      = new BitField(0x4);


    public AreaRecord()
    {

    }

    /**
     * Constructs a Area record and sets its fields appropriately.
     *
     * @param id    id must be 0x101A or an exception
     *              will be throw upon validation
     * @param size  size the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     */

    public AreaRecord(short id, short size, byte [] data)
    {
        super(id, size, data);
    
    }

    /**
     * Constructs a Area record and sets its fields appropriately.
     *
     * @param id    id must be 0x101A or an exception
     *              will be throw upon validation
     * @param size  size the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     * @param offset of the record's data
     */

    public AreaRecord(short id, short size, byte [] data, int offset)
    {
        super(id, size, data, offset);
    
    }

    /**
     * Checks the sid matches the expected side for this record
     *
     * @param id   the expected sid.
     */
    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("Not a Area record");
        }
    }

    protected void fillFields(byte [] data, short size, int offset)
    {

        int pos = 0;
        field_1_formatFlags            = LittleEndian.getShort(data, pos + 0x0 + offset);

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AREA]\n");
        buffer.append("    .formatFlags          = ")
            .append("0x").append(HexDump.toHex(  getFormatFlags ()))
            .append(" (").append( getFormatFlags() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("         .stacked                  = ").append(isStacked()).append('\n'); 
        buffer.append("         .displayAsPercentage      = ").append(isDisplayAsPercentage()).append('\n'); 
        buffer.append("         .shadow                   = ").append(isShadow()).append('\n'); 

        buffer.append("[/AREA]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShort(data, 4 + offset + pos, field_1_formatFlags);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 2;
    }

    public short getSid()
    {
        return this.sid;
    }

    public Object clone() {
        AreaRecord rec = new AreaRecord();
    
        rec.field_1_formatFlags = field_1_formatFlags;
        return rec;
    }




    /**
     * Get the format flags field for the Area record.
     */
    public short getFormatFlags()
    {
        return field_1_formatFlags;
    }

    /**
     * Set the format flags field for the Area record.
     */
    public void setFormatFlags(short field_1_formatFlags)
    {
        this.field_1_formatFlags = field_1_formatFlags;
    }

    /**
     * Sets the stacked field value.
     * series is stacked
     */
    public void setStacked(boolean value)
    {
        field_1_formatFlags = stacked.setShortBoolean(field_1_formatFlags, value);
    }

    /**
     * series is stacked
     * @return  the stacked field value.
     */
    public boolean isStacked()
    {
        return stacked.isSet(field_1_formatFlags);
    }

    /**
     * Sets the display as percentage field value.
     * results displayed as percentages
     */
    public void setDisplayAsPercentage(boolean value)
    {
        field_1_formatFlags = displayAsPercentage.setShortBoolean(field_1_formatFlags, value);
    }

    /**
     * results displayed as percentages
     * @return  the display as percentage field value.
     */
    public boolean isDisplayAsPercentage()
    {
        return displayAsPercentage.isSet(field_1_formatFlags);
    }

    /**
     * Sets the shadow field value.
     * display a shadow for the chart
     */
    public void setShadow(boolean value)
    {
        field_1_formatFlags = shadow.setShortBoolean(field_1_formatFlags, value);
    }

    /**
     * display a shadow for the chart
     * @return  the shadow field value.
     */
    public boolean isShadow()
    {
        return shadow.isSet(field_1_formatFlags);
    }


}  // END OF CLASS




