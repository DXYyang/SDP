
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
        

package org.apache.poi.hdf.generator;

import org.apache.poi.generator.FieldIterator;
import org.apache.poi.generator.RecordUtil;

/**
 * This class overrides FieldIterator to handle HDF specific types
 */
public class HDFFieldIterator extends FieldIterator
{

    public HDFFieldIterator()
    {
    }

    public String fillDecoder(String size, String type)
    {

        String result = "";

        if (type.equals("short[]"))
            result = "LittleEndian.getSimpleShortArray(data, 0x" + Integer.toHexString(offset) + " + offset," +  size + ")";
        else if (type.equals("byte[]"))
            result = "LittleEndian.getByteArray(data, 0x" + Integer.toHexString(offset) + " + offset," + size + ")";
        else if (type.equals("BorderCode"))
            result = "new BorderCode(data, 0x" + Integer.toHexString(offset) + " + offset)";
        else if (type.equals("DateAndTime"))
            result = "new DateAndTime(data, 0x" + Integer.toHexString(offset) + " + offset)";
        else if (size.equals("2"))
            result = "LittleEndian.getShort(data, 0x" + Integer.toHexString(offset) + " + offset)";
        else if (size.equals("4"))
            result = "LittleEndian.getInt(data, 0x" + Integer.toHexString(offset) + " + offset)";
        else if (size.equals("1"))
            result = "data[ 0x" + Integer.toHexString(offset) + " + offset ]";
        else if (type.equals("double"))
            result = "LittleEndian.getDouble(data, 0x" + Integer.toHexString(offset) + " + offset)";

        try
        {
            offset += Integer.parseInt(size);
        }
        catch (NumberFormatException ignore)
        {
        }
        return result;
    }

    public String serialiseEncoder( int fieldNumber, String fieldName, String size, String type)
    {
        //String javaType = RecordUtil.getType(size, type, 0);
        String javaFieldName = RecordUtil.getFieldName(fieldNumber,fieldName,0);

        String result = "";


        if (type.equals("short[]"))
            result = "LittleEndian.putShortArray(data, 0x" + Integer.toHexString(offset) + " + offset, " + javaFieldName + ");";
        else if (type.equals("byte[]"))
            result = "System.arraycopy(" + javaFieldName + ", 0, data, 0x" + Integer.toHexString(offset) + " + offset, " + javaFieldName + ".length);";
        else if (type.equals("BorderCode"))
            result = javaFieldName + ".serialize(data, 0x" + Integer.toHexString(offset) + " + offset);";
        else if (type.equals("DateAndTime"))
           result = javaFieldName + ".serialize(data, 0x" + Integer.toHexString(offset) + " + offset);";
        else if (size.equals("2"))
          result = "LittleEndian.putShort(data, 0x" + Integer.toHexString(offset) + " + offset, (short)" + javaFieldName + ");";
        else if (size.equals("4"))
          result = "LittleEndian.putInt(data, 0x" + Integer.toHexString(offset) + " + offset, " + javaFieldName + ");";
        else if (size.equals("1"))
            result = "data[ 0x" + Integer.toHexString(offset) + " + offset] = " + javaFieldName + ";";
        else if (type.equals("double"))
            result = "LittleEndian.putDouble(data, 0x" + Integer.toHexString(offset) + " + offset, " + javaFieldName + ");";

        try
        {
            offset += Integer.parseInt(size);
        }
        catch (NumberFormatException ignore)
        {
        }
        return result;

    }

}
