
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
        

/*
 * LabelRecord.java
 *
 * Created on November 11, 2001, 12:51 PM
 */
package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/**
 * Label Record - read only support for strings stored directly in the cell..  Don't
 * use this (except to read), use LabelSST instead <P>
 * REFERENCE:  PG 325 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 * @see org.apache.poi.hssf.record.LabelSSTRecord
 */

public class LabelRecord
    extends Record
    implements CellValueRecordInterface
{
    public final static short sid = 0x204;
    //private short             field_1_row;
    private int             field_1_row;
    private short             field_2_column;
    private short             field_3_xf_index;
    private short             field_4_string_len;
    private byte              field_5_unicode_flag;
    private String            field_6_value;

    /** Creates new LabelRecord */

    public LabelRecord()
    {
    }

    /**
     * Constructs an Label record and sets its fields appropriately.
     *
     * @param id     id must be 0x204 or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     */

    public LabelRecord(short id, short size, byte [] data)
    {
        super(id, size, data);
    }

    /**
     * Constructs an Label record and sets its fields appropriately.
     *
     * @param id     id must be 0x204 or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     * @param offset of the record
     */

    public LabelRecord(short id, short size, byte [] data, int offset)
    {
        super(id, size, data, offset);
    }

    /**
     * called by constructor, should throw runtime exception in the event of a
     * record passed with a differing ID.
     *
     * @param id alleged id for this record
     */

    protected void validateSid(short id)
    {
        if (id != this.sid)
        {
            throw new RecordFormatException("Not a valid LabelRecord");
        }
    }

    /**
     * called by the constructor, should set class level fields.  Should throw
     * runtime exception for bad/icomplete data.
     *
     * @param data raw data
     * @param size size of data
     */

    protected void fillFields(byte [] data, short size, int offset)
    {
        //field_1_row          = LittleEndian.getShort(data, 0 + offset);
        field_1_row          = LittleEndian.getUShort(data, 0 + offset);
        field_2_column       = LittleEndian.getShort(data, 2 + offset);
        field_3_xf_index     = LittleEndian.getShort(data, 4 + offset);
        field_4_string_len   = LittleEndian.getShort(data, 6 + offset);
        field_5_unicode_flag = data[ 8 + offset ];
        if (field_4_string_len > 0) {
          if (isUnCompressedUnicode()) {
            field_6_value = StringUtil.getFromUnicode(data, 9 + offset,
                                                      field_4_string_len);
        }
          else {
            field_6_value = StringUtil.getFromCompressedUnicode(data, 9 + offset,
                getStringLength());
        }
        } else field_6_value = null;
    }

/* READ ONLY ACCESS... THIS IS FOR COMPATIBILITY ONLY...USE LABELSST!
      public void setRow(short row) {
        field_1_row = row;
      }

      public void setColumn(short col) {
        field_2_column = col;
      }

      public void setXFIndex(short index) {
        field_3_xf_index = index;
      }
  */
    //public short getRow()
    public int getRow()
    {
        return field_1_row;
    }

    public short getColumn()
    {
        return field_2_column;
    }

    public short getXFIndex()
    {
        return field_3_xf_index;
    }

    /**
     * get the number of characters this string contains
     * @return number of characters
     */

    public short getStringLength()
    {
        return field_4_string_len;
    }

    /**
     * is this uncompressed unicode (16bit)?  Or just 8-bit compressed?
     * @return isUnicode - True for 16bit- false for 8bit
     */

    public boolean isUnCompressedUnicode()
    {
        return (field_5_unicode_flag == 1);
    }

    /**
     * get the value
     *
     * @return the text string
     * @see #getStringLength()
     */

    public String getValue()
    {
        return field_6_value;
    }

    /**
     * THROWS A RUNTIME EXCEPTION..  USE LABELSSTRecords.  YOU HAVE NO REASON to use LABELRecord!!
     */

    public int serialize(int offset, byte [] data)
    {
        throw new RecordFormatException(
            "Label Records are supported READ ONLY...convert to LabelSST");
    }

    public short getSid()
    {
        return this.sid;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[LABEL]\n");
        buffer.append("    .row            = ")
            .append(Integer.toHexString(getRow())).append("\n");
        buffer.append("    .column         = ")
            .append(Integer.toHexString(getColumn())).append("\n");
        buffer.append("    .xfindex        = ")
            .append(Integer.toHexString(getXFIndex())).append("\n");
        buffer.append("    .string_len       = ")
            .append(Integer.toHexString(field_4_string_len)).append("\n");
        buffer.append("    .unicode_flag       = ")
            .append(Integer.toHexString(field_5_unicode_flag)).append("\n");
        buffer.append("    .value       = ")
            .append(getValue()).append("\n");
        buffer.append("[/LABEL]\n");
        return buffer.toString();
    }


    public boolean isBefore(CellValueRecordInterface i)
    {
        if (this.getRow() > i.getRow())
        {
            return false;
        }
        if ((this.getRow() == i.getRow())
                && (this.getColumn() > i.getColumn()))
        {
            return false;
        }
        if ((this.getRow() == i.getRow())
                && (this.getColumn() == i.getColumn()))
        {
            return false;
        }
        return true;
    }

    public boolean isAfter(CellValueRecordInterface i)
    {
        if (this.getRow() < i.getRow())
        {
            return false;
        }
        if ((this.getRow() == i.getRow())
                && (this.getColumn() < i.getColumn()))
        {
            return false;
        }
        if ((this.getRow() == i.getRow())
                && (this.getColumn() == i.getColumn()))
        {
            return false;
        }
        return true;
    }

    public boolean isEqual(CellValueRecordInterface i)
    {
        return ((this.getRow() == i.getRow())
                && (this.getColumn() == i.getColumn()));
    }

    public boolean isInValueSection()
    {
        return true;
    }

    public boolean isValue()
    {
        return true;
    }

    /**
     * NO-OP!
     */

    public void setColumn(short col)
    {
    }

    /**
     * NO-OP!
     */

    //public void setRow(short row)
    public void setRow(int row)
    {
    }

    /**
     * no op!
     */

    public void setXFIndex(short xf)
    {
    }

    public Object clone() {
      LabelRecord rec = new LabelRecord();
      rec.field_1_row = field_1_row;
      rec.field_2_column = field_2_column;
      rec.field_3_xf_index = field_3_xf_index;
      rec.field_4_string_len = field_4_string_len;
      rec.field_5_unicode_flag = field_5_unicode_flag;
      rec.field_6_value = field_6_value;
      return rec;
    }
}
