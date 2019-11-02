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
        

package org.apache.poi.hpsf.basic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.poi.hpsf.Constants;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.Section;
import org.apache.poi.hpsf.SummaryInformation;



/**
 * <p>Tests whether Unicode string can be read from a
 * DocumentSummaryInformation.</p>
 *
 * @author Rainer Klute (klute@rainer-klute.de)
 * @since 2002-12-09
 * @version $Id$
 */
public class TestUnicode extends TestCase
{

    static final String POI_FS = "TestUnicode.xls";
    static final String[] POI_FILES = new String[]
        {
            "\005DocumentSummaryInformation",
        };
    File data;
    POIFile[] poiFiles;



    /**
     * <p>Constructor</p>
     * 
     * @param name the test case's name
     */
    public TestUnicode(final String name)
    {
        super(name);
    }



    /**
     * <p>Read a the test file from the "data" directory.</p>
     * 
     * @exception FileNotFoundException if the file to be read does not exist.
     * @exception IOException if any other I/O exception occurs
     */
    protected void setUp() throws FileNotFoundException, IOException
    {
        final File dataDir =
            new File(System.getProperty("HPSF.testdata.path"));
        data = new File(dataDir, POI_FS);
    }



    /**
     * <p>Tests the {@link PropertySet} methods. The test file has two
     * property set: the first one is a {@link SummaryInformation},
     * the second one is a {@link DocumentSummaryInformation}.</p>
     * 
     * @exception IOException if an I/O exception occurs
     * @exception HPSFException if an HPSF exception occurs
     */
    public void testPropertySetMethods() throws IOException, HPSFException
    {
        POIFile poiFile = Util.readPOIFiles(data, POI_FILES)[0];
        byte[] b = poiFile.getBytes();
        PropertySet ps =
            PropertySetFactory.create(new ByteArrayInputStream(b));
        Assert.assertTrue(ps.isDocumentSummaryInformation());
        Assert.assertEquals(ps.getSectionCount(), 2);
        Section s = (Section) ps.getSections().get(1);
        Assert.assertEquals(s.getProperty(1),
                            new Integer(Constants.CP_UTF16));
        Assert.assertEquals(s.getProperty(2),
                            new Integer(-96070278));
        Assert.assertEquals(s.getProperty(3),
                            "MCon_Info zu Office bei Schreiner");
        Assert.assertEquals(s.getProperty(4),
                            "petrovitsch@schreiner-online.de");
        Assert.assertEquals(s.getProperty(5),
                            "Petrovitsch, Wilhelm");
    }



    /**
     * <p>Runs the test cases stand-alone.</p>
     * 
     * @param args Command-line arguments.
     */
    public static void main(final String[] args)
    {
        System.setProperty("HPSF.testdata.path",
                           "./src/testcases/org/apache/poi/hpsf/data");
        junit.textui.TestRunner.run(TestUnicode.class);
    }

}
