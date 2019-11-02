/* ====================================================================
   Copyright 2003-2004   Apache Software Foundation

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

package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.hssf.model.Workbook;
/**
 * Number
 * Stores a floating point value in a formula
 * value stored in a 8 byte field using IEEE notation
 * @author  Avik Sengupta
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class NumberPtg
    extends Ptg
{
    public final static int  SIZE = 9;
    public final static byte sid  = 0x1f;
    private double            field_1_value;

    private NumberPtg() {
      //Required for clone methods
    }
        
    /** Create a NumberPtg from a byte array read from disk */
    public NumberPtg(byte [] data, int offset)
    {
        setValue(LittleEndian.getDouble(data, offset + 1));
    }
    
    /** Create a NumberPtg from a string representation of  the number
     *  Number format is not checked, it is expected to be validated in the parser
     *   that calls this method. 
     *  @param value : String representation of a floating point number
     */
    public NumberPtg(String value) {
        setValue(Double.parseDouble(value));
    }
    
    
    public void setValue(double value)
    {
        field_1_value = value;
    }
    
    
    public double getValue()
    {
        return field_1_value;
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[ offset + 0 ] = sid;
        LittleEndian.putDouble(array, offset + 1, getValue());
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        return "" + getValue();
    }
       public byte getDefaultOperandClass() {return Ptg.CLASS_VALUE;}

    public Object clone() {
      NumberPtg ptg = new NumberPtg();
      ptg.field_1_value = field_1_value;
      return ptg;
    }
}
