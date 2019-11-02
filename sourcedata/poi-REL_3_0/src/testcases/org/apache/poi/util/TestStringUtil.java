
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
        
package org.apache.poi.util;

import junit.framework.*;

import java.text.NumberFormat;

/**
 * Unit test for StringUtil
 *
 * @author  Marc Johnson (mjohnson at apache dot org
 * @author  Glen Stampoultzis (glens at apache.org)
 * @author  Sergei Kozello (sergeikozello at mail.ru)
 */
public class TestStringUtil
        extends TestCase
{
    /**
     * Creates new TestStringUtil
     *
     * @param name
     */
    public TestStringUtil( String name )
    {
        super( name );
    }

    /**
     * test simple form of getFromUnicode
     */
    public void testSimpleGetFromUnicode()
    {
        byte[] test_data = new byte[32];
        int index = 0;

        for ( int k = 0; k < 16; k++ )
        {
            test_data[index++] = (byte) 0;
            test_data[index++] = (byte) ( 'a' + k );
        }

        assertEquals( "abcdefghijklmnop",
                StringUtil.getFromUnicodeBE( test_data ) );
    }

    /**
     * test simple form of getFromUnicode with symbols with code below and more 127
     */
    public void testGetFromUnicodeSymbolsWithCodesMoreThan127()
    {
        byte[] test_data = new byte[]{0x04, 0x22,
                                      0x04, 0x35,
                                      0x04, 0x41,
                                      0x04, 0x42,
                                      0x00, 0x20,
                                      0x00, 0x74,
                                      0x00, 0x65,
                                      0x00, 0x73,
                                      0x00, 0x74,
        };

        assertEquals( "\u0422\u0435\u0441\u0442 test",
                StringUtil.getFromUnicodeBE( test_data ) );
    }

    /**
     * test getFromUnicodeHigh for symbols with code below and more 127
     */
    public void testGetFromUnicodeHighSymbolsWithCodesMoreThan127()
    {
        byte[] test_data = new byte[]{0x22, 0x04,
                                      0x35, 0x04,
                                      0x41, 0x04,
                                      0x42, 0x04,
                                      0x20, 0x00,
                                      0x74, 0x00,
                                      0x65, 0x00,
                                      0x73, 0x00,
                                      0x74, 0x00,
        };


        assertEquals( "\u0422\u0435\u0441\u0442 test",
                StringUtil.getFromUnicodeLE( test_data ) );
    }

    /**
     * Test more complex form of getFromUnicode
     */
    public void testComplexGetFromUnicode()
    {
        byte[] test_data = new byte[32];
        int index = 0;
        for ( int k = 0; k < 16; k++ )
        {
            test_data[index++] = (byte) 0;
            test_data[index++] = (byte) ( 'a' + k );
        }
        assertEquals( "abcdefghijklmno",
                StringUtil.getFromUnicodeBE( test_data, 0, 15 ) );
        assertEquals( "bcdefghijklmnop",
                StringUtil.getFromUnicodeBE( test_data, 2, 15 ) );
        try
        {
            StringUtil.getFromUnicodeBE( test_data, -1, 16 );
            fail( "Should have caught ArrayIndexOutOfBoundsException" );
        }
        catch ( ArrayIndexOutOfBoundsException ignored )
        {
            // as expected
        }

        try
        {
            StringUtil.getFromUnicodeBE( test_data, 32, 16 );
            fail( "Should have caught ArrayIndexOutOfBoundsException" );
        }
        catch ( ArrayIndexOutOfBoundsException ignored )
        {
            // as expected
        }

        try
        {
            StringUtil.getFromUnicodeBE( test_data, 1, 16 );
            fail( "Should have caught IllegalArgumentException" );
        }
        catch ( IllegalArgumentException ignored )
        {
            // as expected
        }

        try
        {
            StringUtil.getFromUnicodeBE( test_data, 1, -1 );
            fail( "Should have caught IllegalArgumentException" );
        }
        catch ( IllegalArgumentException ignored )
        {
            // as expected
        }
    }

    /**
     * Test putCompressedUnicode
     */
    public void testPutCompressedUnicode() throws Exception
    {
        byte[] output = new byte[100];
        byte[] expected_output =
                {
                    (byte) 'H', (byte) 'e', (byte) 'l', (byte) 'l',
                    (byte) 'o', (byte) ' ', (byte) 'W', (byte) 'o',
                    (byte) 'r', (byte) 'l', (byte) 'd', (byte) 0xAE
                };
        String input = new String( expected_output, StringUtil.getPreferredEncoding() );

        StringUtil.putCompressedUnicode( input, output, 0 );
        for ( int j = 0; j < expected_output.length; j++ )
        {
            assertEquals( "testing offset " + j, expected_output[j],
                    output[j] );
        }
        StringUtil.putCompressedUnicode( input, output,
                100 - expected_output.length );
        for ( int j = 0; j < expected_output.length; j++ )
        {
            assertEquals( "testing offset " + j, expected_output[j],
                    output[100 + j - expected_output.length] );
        }
        try
        {
            StringUtil.putCompressedUnicode( input, output,
                    101 - expected_output.length );
            fail( "Should have caught ArrayIndexOutOfBoundsException" );
        }
        catch ( ArrayIndexOutOfBoundsException ignored )
        {
            // as expected
        }
    }

    /**
     * Test putUncompressedUnicode
     */
    public void testPutUncompressedUnicode()
    {
        byte[] output = new byte[100];
        String input = "Hello World";
        byte[] expected_output =
                {
                    (byte) 'H', (byte) 0, (byte) 'e', (byte) 0, (byte) 'l',
                    (byte) 0, (byte) 'l', (byte) 0, (byte) 'o', (byte) 0,
                    (byte) ' ', (byte) 0, (byte) 'W', (byte) 0, (byte) 'o',
                    (byte) 0, (byte) 'r', (byte) 0, (byte) 'l', (byte) 0,
                    (byte) 'd', (byte) 0
                };

        StringUtil.putUnicodeLE( input, output, 0 );
        for ( int j = 0; j < expected_output.length; j++ )
        {
            assertEquals( "testing offset " + j, expected_output[j],
                    output[j] );
        }
        StringUtil.putUnicodeLE( input, output,
                100 - expected_output.length );
        for ( int j = 0; j < expected_output.length; j++ )
        {
            assertEquals( "testing offset " + j, expected_output[j],
                    output[100 + j - expected_output.length] );
        }
        try
        {
            StringUtil.putUnicodeLE( input, output,
                    101 - expected_output.length );
            fail( "Should have caught ArrayIndexOutOfBoundsException" );
        }
        catch ( ArrayIndexOutOfBoundsException ignored )
        {
            // as expected
        }
    }


    public void testFormat()
            throws Exception
    {
        assertEquals( "This is a test " + fmt( 1.2345, 2, 2 ),
                StringUtil.format( "This is a test %2.2", new Object[]
                {
                    new Double( 1.2345 )
                } ) );
        assertEquals( "This is a test " + fmt( 1.2345, -1, 3 ),
                StringUtil.format( "This is a test %.3", new Object[]
                {
                    new Double( 1.2345 )
                } ) );
        assertEquals( "This is a great test " + fmt( 1.2345, -1, 3 ),
                StringUtil.format( "This is a % test %.3", new Object[]
                {
                    "great", new Double( 1.2345 )
                } ) );
        assertEquals( "This is a test 1",
                StringUtil.format( "This is a test %", new Object[]
                {
                    new Integer( 1 )
                } ) );
        assertEquals( "This is a test 1",
                StringUtil.format( "This is a test %", new Object[]
                {
                    new Integer( 1 ), new Integer( 1 )
                } ) );
        assertEquals( "This is a test 1.x",
                StringUtil.format( "This is a test %1.x", new Object[]
                {
                    new Integer( 1 )
                } ) );
        assertEquals( "This is a test ?missing data?1.x",
                StringUtil.format( "This is a test %1.x", new Object[]
                {
                } ) );
        assertEquals( "This is a test %1.x",
                StringUtil.format( "This is a test \\%1.x", new Object[]
                {
                } ) );
    }


    private String fmt( double num, int minIntDigits, int maxFracDigitis )
    {
        NumberFormat nf = NumberFormat.getInstance();

        if ( minIntDigits != -1 )
        {
            nf.setMinimumIntegerDigits( minIntDigits );
        }
        if ( maxFracDigitis != -1 )
        {
            nf.setMaximumFractionDigits( maxFracDigitis );
        }

        return nf.format( num );
    }


    /**
     * main
     *
     * @param ignored_args
     */
    public static void main( String[] ignored_args )
    {
        System.out.println( "Testing util.StringUtil functionality" );
        junit.textui.TestRunner.run( TestStringUtil.class );
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        // System.setProperty()
    }

}

