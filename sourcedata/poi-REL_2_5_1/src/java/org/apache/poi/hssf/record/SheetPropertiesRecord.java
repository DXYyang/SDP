
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
 * Describes a chart sheet properties record.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class SheetPropertiesRecord
    extends Record
{
    public final static short      sid                             = 0x1044;
    private  short      field_1_flags;
    private  BitField   chartTypeManuallyFormatted                  = new BitField(0x1);
    private  BitField   plotVisibleOnly                             = new BitField(0x2);
    private  BitField   doNotSizeWithWindow                         = new BitField(0x4);
    private  BitField   defaultPlotDimensions                       = new BitField(0x8);
    private  BitField   autoPlotArea                                = new BitField(0x10);
    private  byte       field_2_empty;
    public final static byte        EMPTY_NOT_PLOTTED              = 0;
    public final static byte        EMPTY_ZERO                     = 1;
    public final static byte        EMPTY_INTERPOLATED             = 2;


    public SheetPropertiesRecord()
    {

    }

    /**
     * Constructs a SheetProperties record and sets its fields appropriately.
     *
     * @param id    id must be 0x1044 or an exception
     *              will be throw upon validation
     * @param size  size the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     */

    public SheetPropertiesRecord(short id, short size, byte [] data)
    {
        super(id, size, data);
    
    }

    /**
     * Constructs a SheetProperties record and sets its fields appropriately.
     *
     * @param id    id must be 0x1044 or an exception
     *              will be throw upon validation
     * @param size  size the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     * @param offset of the record's data
     */

    public SheetPropertiesRecord(short id, short size, byte [] data, int offset)
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
            throw new RecordFormatException("Not a SheetProperties record");
        }
    }

    protected void fillFields(byte [] data, short size, int offset)
    {

        int pos = 0;
        field_1_flags                  = LittleEndian.getShort(data, pos + 0x0 + offset);
        field_2_empty                  = data[ pos + 0x2 + offset ];

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SHTPROPS]\n");
        buffer.append("    .flags                = ")
            .append("0x").append(HexDump.toHex(  getFlags ()))
            .append(" (").append( getFlags() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("         .chartTypeManuallyFormatted     = ").append(isChartTypeManuallyFormatted()).append('\n'); 
        buffer.append("         .plotVisibleOnly          = ").append(isPlotVisibleOnly()).append('\n'); 
        buffer.append("         .doNotSizeWithWindow      = ").append(isDoNotSizeWithWindow()).append('\n'); 
        buffer.append("         .defaultPlotDimensions     = ").append(isDefaultPlotDimensions()).append('\n'); 
        buffer.append("         .autoPlotArea             = ").append(isAutoPlotArea()).append('\n'); 
        buffer.append("    .empty                = ")
            .append("0x").append(HexDump.toHex(  getEmpty ()))
            .append(" (").append( getEmpty() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/SHTPROPS]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShort(data, 4 + offset + pos, field_1_flags);
        data[ 6 + offset + pos ] = field_2_empty;

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 2 + 1;
    }

    public short getSid()
    {
        return this.sid;
    }

    public Object clone() {
        SheetPropertiesRecord rec = new SheetPropertiesRecord();
    
        rec.field_1_flags = field_1_flags;
        rec.field_2_empty = field_2_empty;
        return rec;
    }




    /**
     * Get the flags field for the SheetProperties record.
     */
    public short getFlags()
    {
        return field_1_flags;
    }

    /**
     * Set the flags field for the SheetProperties record.
     */
    public void setFlags(short field_1_flags)
    {
        this.field_1_flags = field_1_flags;
    }

    /**
     * Get the empty field for the SheetProperties record.
     *
     * @return  One of 
     *        EMPTY_NOT_PLOTTED
     *        EMPTY_ZERO
     *        EMPTY_INTERPOLATED
     */
    public byte getEmpty()
    {
        return field_2_empty;
    }

    /**
     * Set the empty field for the SheetProperties record.
     *
     * @param field_2_empty
     *        One of 
     *        EMPTY_NOT_PLOTTED
     *        EMPTY_ZERO
     *        EMPTY_INTERPOLATED
     */
    public void setEmpty(byte field_2_empty)
    {
        this.field_2_empty = field_2_empty;
    }

    /**
     * Sets the chart type manually formatted field value.
     * Has the chart type been manually formatted?
     */
    public void setChartTypeManuallyFormatted(boolean value)
    {
        field_1_flags = chartTypeManuallyFormatted.setShortBoolean(field_1_flags, value);
    }

    /**
     * Has the chart type been manually formatted?
     * @return  the chart type manually formatted field value.
     */
    public boolean isChartTypeManuallyFormatted()
    {
        return chartTypeManuallyFormatted.isSet(field_1_flags);
    }

    /**
     * Sets the plot visible only field value.
     * Only show visible cells on the chart.
     */
    public void setPlotVisibleOnly(boolean value)
    {
        field_1_flags = plotVisibleOnly.setShortBoolean(field_1_flags, value);
    }

    /**
     * Only show visible cells on the chart.
     * @return  the plot visible only field value.
     */
    public boolean isPlotVisibleOnly()
    {
        return plotVisibleOnly.isSet(field_1_flags);
    }

    /**
     * Sets the do not size with window field value.
     * Do not size the chart when the window changes size
     */
    public void setDoNotSizeWithWindow(boolean value)
    {
        field_1_flags = doNotSizeWithWindow.setShortBoolean(field_1_flags, value);
    }

    /**
     * Do not size the chart when the window changes size
     * @return  the do not size with window field value.
     */
    public boolean isDoNotSizeWithWindow()
    {
        return doNotSizeWithWindow.isSet(field_1_flags);
    }

    /**
     * Sets the default plot dimensions field value.
     * Indicates that the default area dimensions should be used.
     */
    public void setDefaultPlotDimensions(boolean value)
    {
        field_1_flags = defaultPlotDimensions.setShortBoolean(field_1_flags, value);
    }

    /**
     * Indicates that the default area dimensions should be used.
     * @return  the default plot dimensions field value.
     */
    public boolean isDefaultPlotDimensions()
    {
        return defaultPlotDimensions.isSet(field_1_flags);
    }

    /**
     * Sets the auto plot area field value.
     * ??
     */
    public void setAutoPlotArea(boolean value)
    {
        field_1_flags = autoPlotArea.setShortBoolean(field_1_flags, value);
    }

    /**
     * ??
     * @return  the auto plot area field value.
     */
    public boolean isAutoPlotArea()
    {
        return autoPlotArea.isSet(field_1_flags);
    }


}  // END OF CLASS




