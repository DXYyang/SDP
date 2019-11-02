
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
        

package org.apache.poi.hpsf.basic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.hpsf.MarkUnsupportedException;
import org.apache.poi.hpsf.NoPropertySetStreamException;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.UnexpectedPropertySetTypeException;



/**
 * <p>Tests the basic HPSF functionality.</p>
 *
 * @author Rainer Klute (klute@rainer-klute.de)
 * @since 2002-07-20
 * @version $Id$
 */
public class TestBasic extends TestCase
{

    static final String POI_FS = "TestGermanWord90.doc";
    static final String[] POI_FILES = new String[]
        {
            "\005SummaryInformation",
            "\005DocumentSummaryInformation",
            "WordDocument",
            "\001CompObj",
            "1Table"
        };
    static final int BYTE_ORDER = 0xfffe;
    static final int FORMAT     = 0x0000;
    static final int OS_VERSION = 0x00020A04;
    static final byte[] CLASS_ID =
        {
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
    static final int[] SECTION_COUNT =
        {1, 2};
    static final boolean[] IS_SUMMARY_INFORMATION =
        {true, false};
    static final boolean[] IS_DOCUMENT_SUMMARY_INFORMATION =
        {false, true};            

    POIFile[] poiFiles;



    public TestBasic(final String name)
    {
        super(name);
    }



    /**
     * <p>Read a the test file from the "data" directory.</p>
     */
    public void setUp() throws FileNotFoundException, IOException
    {
        final File dataDir =
            new File(System.getProperty("HPSF.testdata.path"));
        final File data = new File(dataDir, POI_FS);

        poiFiles = Util.readPOIFiles(data);
    }



    /**
     * <p>Checks the names of the files in the POI filesystem. They
     * are expected to be in a certain order.</p>
     */
    public void testReadFiles() throws IOException
    {
        String[] expected = POI_FILES;
        for (int i = 0; i < expected.length; i++)
            Assert.assertEquals(poiFiles[i].getName(), expected[i]);
    }



    /**
     * <p>Tests whether property sets can be created from the POI
     * files in the POI file system. This test case expects the first
     * file to be a {@link SummaryInformation}, the second file to be
     * a {@link DocumentSummaryInformation} and the rest to be no
     * property sets. In the latter cases a {@link
     * NoPropertySetStreamException} will be thrown when trying to
     * create a {@link PropertySet}.</p>
     */
    public void testCreatePropertySets() throws IOException
    {
        Class[] expected = new Class[]
            {
                SummaryInformation.class,
                DocumentSummaryInformation.class,
                NoPropertySetStreamException.class,
                NoPropertySetStreamException.class,
                NoPropertySetStreamException.class
            };
        for (int i = 0; i < expected.length; i++)
        {
            InputStream in = new ByteArrayInputStream(poiFiles[i].getBytes());
            Object o;
            try
            {
                o = PropertySetFactory.create(in);
            }
            catch (NoPropertySetStreamException ex)
            {
                o = ex;
            }
            catch (UnexpectedPropertySetTypeException ex)
            {
                o = ex;
            }
            catch (MarkUnsupportedException ex)
            {
                o = ex;
            }
            in.close();
            Assert.assertEquals(o.getClass(), expected[i]);
        }
    }



    /**
     * <p>Tests the {@link PropertySet} methods. The test file has two
     * property sets: the first one is a {@link SummaryInformation},
     * the second one is a {@link DocumentSummaryInformation}.</p>
     */
    public void testPropertySetMethods() throws IOException, HPSFException
    {

        /* Loop over the two property sets. */
        for (int i = 0; i < 2; i++)
        {
            byte[] b = poiFiles[i].getBytes();
            PropertySet ps =
                PropertySetFactory.create(new ByteArrayInputStream(b));
            Assert.assertEquals(ps.getByteOrder(), BYTE_ORDER);
            Assert.assertEquals(ps.getFormat(), FORMAT);
            Assert.assertEquals(ps.getOSVersion(), OS_VERSION);
            Assert.assertEquals(new String(ps.getClassID().getBytes()),
                                new String(CLASS_ID));
            Assert.assertEquals(ps.getSectionCount(), SECTION_COUNT[i]);
            Assert.assertEquals(ps.isSummaryInformation(),
                                IS_SUMMARY_INFORMATION[i]);
            Assert.assertEquals(ps.isDocumentSummaryInformation(),
                                IS_DOCUMENT_SUMMARY_INFORMATION[i]);
        }
    }



    /**
     * <p>Runs the test cases stand-alone.</p>
     */
    public static void main(final String[] args) throws Throwable
    {
        System.setProperty("HPSF.testdata.path",
                           "./src/testcases/org/apache/poi/hpsf/data");
        junit.textui.TestRunner.run(TestBasic.class);
    }

}
