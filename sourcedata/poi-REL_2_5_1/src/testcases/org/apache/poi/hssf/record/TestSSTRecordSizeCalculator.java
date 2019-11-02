
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

import java.util.List;
import java.util.ArrayList;

import org.apache.poi.util.BinaryTree;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianConsts;

/**
 * Tests that records size calculates correctly.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class TestSSTRecordSizeCalculator
        extends TestCase
{
    private static final String SMALL_STRING = "Small string";
    private static final int COMPRESSED_PLAIN_STRING_OVERHEAD = 3;
//    private List recordLengths;
    private BinaryTree strings;
    private static final int OPTION_FIELD_SIZE = 1;

    public TestSSTRecordSizeCalculator( String s )
    {
        super( s );
    }

    public void testBasic()
            throws Exception
    {
        strings.put(new Integer(0), makeUnicodeString(SMALL_STRING));
        SSTRecordSizeCalculator calculator = new SSTRecordSizeCalculator(strings);
        assertEquals(SSTRecord.SST_RECORD_OVERHEAD + COMPRESSED_PLAIN_STRING_OVERHEAD + SMALL_STRING.length(),
                calculator.getRecordSize());
    }

    public void testBigStringAcrossUnicode()
            throws Exception
    {
        String bigString = new String(new char[SSTRecord.MAX_DATA_SPACE + 100]);
        strings.put(new Integer(0), makeUnicodeString(bigString));
        SSTRecordSizeCalculator calculator = new SSTRecordSizeCalculator(strings);
        assertEquals(SSTRecord.SST_RECORD_OVERHEAD
                + COMPRESSED_PLAIN_STRING_OVERHEAD
                + SSTRecord.MAX_DATA_SPACE
                + SSTRecord.STD_RECORD_OVERHEAD
                + OPTION_FIELD_SIZE
                + 100,
                calculator.getRecordSize());

    }

    public void testPerfectFit()
            throws Exception
    {
        String perfectFit = new String(new char[SSTRecord.MAX_DATA_SPACE - COMPRESSED_PLAIN_STRING_OVERHEAD]);
        strings.put(new Integer(0), makeUnicodeString(perfectFit));
        SSTRecordSizeCalculator calculator = new SSTRecordSizeCalculator(strings);
        assertEquals(SSTRecord.SST_RECORD_OVERHEAD
                + COMPRESSED_PLAIN_STRING_OVERHEAD
                + perfectFit.length(),
                calculator.getRecordSize());
    }

    public void testJustOversized()
            throws Exception
    {
        String tooBig = new String(new char[SSTRecord.MAX_DATA_SPACE - COMPRESSED_PLAIN_STRING_OVERHEAD + 1]);
        strings.put(new Integer(0), makeUnicodeString(tooBig));
        SSTRecordSizeCalculator calculator = new SSTRecordSizeCalculator(strings);
        assertEquals(SSTRecord.SST_RECORD_OVERHEAD
                + COMPRESSED_PLAIN_STRING_OVERHEAD
                + tooBig.length() - 1
                // continue record
                + SSTRecord.STD_RECORD_OVERHEAD
                + OPTION_FIELD_SIZE
                + 1,
                calculator.getRecordSize());

    }

    public void testSecondStringStartsOnNewContinuation()
            throws Exception
    {
        String perfectFit = new String(new char[SSTRecord.MAX_DATA_SPACE - COMPRESSED_PLAIN_STRING_OVERHEAD]);
        strings.put(new Integer(0), makeUnicodeString(perfectFit));
        strings.put(new Integer(1), makeUnicodeString(SMALL_STRING));
        SSTRecordSizeCalculator calculator = new SSTRecordSizeCalculator(strings);
        assertEquals(SSTRecord.SST_RECORD_OVERHEAD
                + SSTRecord.MAX_DATA_SPACE
                // second string
                + SSTRecord.STD_RECORD_OVERHEAD
                + COMPRESSED_PLAIN_STRING_OVERHEAD
                + SMALL_STRING.length(),
                calculator.getRecordSize());
    }

    public void testHeaderCrossesNormalContinuePoint()
            throws Exception
    {
        String almostPerfectFit = new String(new char[SSTRecord.MAX_DATA_SPACE - COMPRESSED_PLAIN_STRING_OVERHEAD - 2]);
        strings.put(new Integer(0), makeUnicodeString(almostPerfectFit));
        String oneCharString = new String(new char[1]);
        strings.put(new Integer(1), makeUnicodeString(oneCharString));
        SSTRecordSizeCalculator calculator = new SSTRecordSizeCalculator(strings);
        assertEquals(SSTRecord.SST_RECORD_OVERHEAD
                + COMPRESSED_PLAIN_STRING_OVERHEAD
                + almostPerfectFit.length()
                // second string
                + SSTRecord.STD_RECORD_OVERHEAD
                + COMPRESSED_PLAIN_STRING_OVERHEAD
                + oneCharString.length(),
                calculator.getRecordSize());

    }


    public void setUp()
    {
        strings = new BinaryTree();
    }


    private UnicodeString makeUnicodeString( String s )
    {
        int length = SSTRecord.STRING_MINIMAL_OVERHEAD + s.length();
        byte[] unicodeStringBuffer = new byte[length];
        LittleEndian.putUShort( unicodeStringBuffer, 0, s.length() );
        int offset = LittleEndianConsts.SHORT_SIZE;
        unicodeStringBuffer[offset++] = 0;
        System.arraycopy( s.getBytes(), 0, unicodeStringBuffer, offset, s.length() );
        return new UnicodeString( UnicodeString.sid, (short) unicodeStringBuffer.length, unicodeStringBuffer );
    }

}
