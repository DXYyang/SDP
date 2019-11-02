
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
        

package org.apache.poi.hssf.usermodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;


/**
 * 
 * @author ROMANL
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Danny Mui (danny at muibros.com)
 */
public class TestNamedRange
    extends TestCase {
    
    public TestNamedRange(String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        String filename = System.getProperty("HSSF.testdata.path");
        
        // assume andy is running this in the debugger
        if (filename == null)
        {
            if (args != null && args.length == 1) {
            System.setProperty(
                "HSSF.testdata.path",
                args[0]);
            } else {
                System.err.println("Geesh, no HSSF.testdata.path system " +
                          "property, no command line arg with the path "+
                          "what do you expect me to do, guess where teh data " +
                          "files are?  Sorry, I give up!");
                                   
            }
            
        }
        
        
        junit.textui.TestRunner.run(TestNamedRange.class);
    }
    
    /** Test of TestCase method, of class test.RangeTest. */
    public void testNamedRange() 
        throws IOException
    {
        FileInputStream fis = null;
        POIFSFileSystem fs  = null;
        HSSFWorkbook wb     = null;
        
        String filename = System.getProperty("HSSF.testdata.path");

        filename = filename + "/Simple.xls";
        
        
        fis = new FileInputStream(filename);
        fs = new POIFSFileSystem(fis);
        wb = new HSSFWorkbook(fs);
        
        
        //Creating new Named Range
        HSSFName newNamedRange = wb.createName();
        
        //Getting Sheet Name for the reference
        String sheetName = wb.getSheetName(0);
        
        //Setting its name
        newNamedRange.setNameName("RangeTest");
        //Setting its reference
        newNamedRange.setReference(sheetName + "!$D$4:$E$8");
  
        //Getting NAmed Range
        HSSFName namedRange1 = wb.getNameAt(0);
        //Getting it sheet name
        sheetName = namedRange1.getSheetName();
        //Getting its reference
        String referece = namedRange1.getReference();

        // sanity check
        SanityChecker c = new SanityChecker();
        c.checkHSSFWorkbook(wb);

        File             file = File.createTempFile("testNamedRange",
                                        ".xls");

        FileOutputStream fileOut = new FileOutputStream(file);
        wb.write(fileOut);
        fis.close();
        fileOut.close();
        
        assertTrue("file exists",file.exists());
            
        FileInputStream in = new FileInputStream(file);
        wb = new HSSFWorkbook(in);
        HSSFName nm =wb.getNameAt(wb.getNameIndex("RangeTest"));
        assertTrue("Name is "+nm.getNameName(),"RangeTest".equals(nm.getNameName()));
        assertEquals(wb.getSheetName(0)+"!$D$4:$E$8", nm.getReference());
        
        
    }
     
    /**
     * Reads an excel file already containing a named range.
     * <p>
	 * Addresses Bug <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=9632" target="_bug">#9632</a>
     */   
	public void testNamedRead() throws IOException
	{        
		FileInputStream fis = null;
 	 	POIFSFileSystem fs  = null;
		HSSFWorkbook wb     = null;
        
		String filename = System.getProperty("HSSF.testdata.path");

		filename = filename + "/namedinput.xls";
        
        
		fis = new FileInputStream(filename);
		fs = new POIFSFileSystem(fis);
		wb = new HSSFWorkbook(fs);

		//Get index of the namedrange with the name = "NamedRangeName" , which was defined in input.xls as A1:D10
		int NamedRangeIndex	 = wb.getNameIndex("NamedRangeName");

		//Getting NAmed Range
		HSSFName namedRange1 = wb.getNameAt(NamedRangeIndex);
		String sheetName = wb.getSheetName(0);

		//Getting its reference
		String reference = namedRange1.getReference();
		 
		fis.close();
		 
		assertEquals(sheetName+"!$A$1:$D$10", reference);
		
		HSSFName namedRange2 = wb.getNameAt(1);
		
		assertEquals(sheetName+"!$D$17:$G$27", namedRange2.getReference());
		assertEquals("SecondNamedRange", namedRange2.getNameName());
		
	}
        
    /**
     * Reads an excel file already containing a named range and updates it
     * <p>
     * Addresses Bug <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=16411" target="_bug">#16411</a>
     */
    public void testNamedReadModify() throws IOException
    {
		FileInputStream fis = null;
		POIFSFileSystem fs  = null;
		HSSFWorkbook wb     = null;
        
		String filename = System.getProperty("HSSF.testdata.path");

		filename = filename + "/namedinput.xls";
        
        
		fis = new FileInputStream(filename);
		fs = new POIFSFileSystem(fis);
		wb = new HSSFWorkbook(fs);
    	
    	
		HSSFName name = wb.getNameAt(0);
		String sheetName = wb.getSheetName(0);
		
		assertEquals(sheetName+"!$A$1:$D$10", name.getReference());
		
		name = wb.getNameAt(1);		
		String newReference = sheetName +"!$A$1:$C$36"; 
		 
		name.setReference(newReference);		
		assertEquals(newReference, name.getReference()); 

    }
        
	/**
	 * Test that multiple named ranges can be added written and read
	 */
	public void testMultipleNamedWrite() 
	    throws IOException
	{
	    HSSFWorkbook wb     = new HSSFWorkbook();
         

		HSSFSheet sheet = wb.createSheet("Sheet1");
		String sheetName = wb.getSheetName(0);
        
        assertEquals("Sheet1", sheetName);
         
		//Creating new Named Range
		HSSFName newNamedRange = wb.createName();

		newNamedRange.setNameName("RangeTest");
	    newNamedRange.setReference(sheetName + "!$D$4:$E$8");

	    //Creating another new Named Range
	    HSSFName newNamedRange2 = wb.createName();
	    
	    newNamedRange2.setNameName("AnotherTest");
	    newNamedRange2.setReference(sheetName + "!$F$1:$G$6");
	    
	    
		HSSFName namedRange1 = wb.getNameAt(0);
		String referece = namedRange1.getReference();
                                
	    File file = File.createTempFile("testMultiNamedRange", ".xls");
 
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
		
         
		assertTrue("file exists",file.exists());
            
	    
		FileInputStream in = new FileInputStream(file);
		wb = new HSSFWorkbook(in);
		HSSFName nm =wb.getNameAt(wb.getNameIndex("RangeTest"));
		assertTrue("Name is "+nm.getNameName(),"RangeTest".equals(nm.getNameName()));
		assertTrue("Reference is "+nm.getReference(),(wb.getSheetName(0)+"!$D$4:$E$8").equals(nm.getReference()));
         
	    nm = wb.getNameAt(wb.getNameIndex("AnotherTest"));
	    assertTrue("Name is "+nm.getNameName(),"AnotherTest".equals(nm.getNameName()));
	    assertTrue("Reference is "+nm.getReference(),newNamedRange2.getReference().equals(nm.getReference()));        
	    
	    
	}    

	/**
	 * Test case provided by czhang@cambian.com (Chun Zhang)
	 * <p>
	 * Addresses Bug <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=13775" target="_bug">#13775</a>
	 * @throws IOException
	 */
	public void testMultiNamedRange() 
		 throws IOException
	 {
        
		 // Create a new workbook
		 HSSFWorkbook wb = new HSSFWorkbook ();


		 // Create a worksheet 'sheet1' in the new workbook
		 wb.createSheet ();
		 wb.setSheetName (0, "sheet1");
        
		 // Create another worksheet 'sheet2' in the new workbook
		 wb.createSheet ();
		 wb.setSheetName (1, "sheet2");
        
		 // Create a new named range for worksheet 'sheet1'
		 HSSFName namedRange1 = wb.createName();
        
		 // Set the name for the named range for worksheet 'sheet1'
		 namedRange1.setNameName("RangeTest1");

		 // Set the reference for the named range for worksheet 'sheet1'
		 namedRange1.setReference("sheet1" + "!$A$1:$L$41");

		 // Create a new named range for worksheet 'sheet2'
		 HSSFName namedRange2 = wb.createName();
        
		 // Set the name for the named range for worksheet 'sheet2'
		 namedRange2.setNameName("RangeTest2");

		 // Set the reference for the named range for worksheet 'sheet2'
		 namedRange2.setReference("sheet2" + "!$A$1:$O$21");        
 
		 // Write the workbook to a file
		 File file = File.createTempFile("testMuiltipletNamedRanges", ".xls");
		 FileOutputStream fileOut = new FileOutputStream(file);
		 wb.write(fileOut);
		 fileOut.close();
        
		 assertTrue("file exists",file.exists());
            
		 // Read the Excel file and verify its content
		 FileInputStream in = new FileInputStream(file);
		 wb = new HSSFWorkbook(in);
		 HSSFName nm1 =wb.getNameAt(wb.getNameIndex("RangeTest1"));
		 assertTrue("Name is "+nm1.getNameName(),"RangeTest1".equals(nm1.getNameName()));
		 assertTrue("Reference is "+nm1.getReference(),(wb.getSheetName(0)+"!$A$1:$L$41").equals(nm1.getReference()));

		 HSSFName nm2 =wb.getNameAt(wb.getNameIndex("RangeTest2"));
		 assertTrue("Name is "+nm2.getNameName(),"RangeTest2".equals(nm2.getNameName()));
		 assertTrue("Reference is "+nm2.getReference(),(wb.getSheetName(1)+"!$A$1:$O$21").equals(nm2.getReference()));
	 }       
        
        
	 /**
	  * Test to see if the print areas can be retrieved/created in memory
	  */
	 public void testSinglePrintArea()
	 {
	     HSSFWorkbook workbook = new HSSFWorkbook();        
	     HSSFSheet sheet = workbook.createSheet("Test Print Area");                
	     String sheetName = workbook.getSheetName(0);
	     
	     String reference = sheetName+"!$A$1:$B$1";
	     workbook.setPrintArea(0, reference);
	             
	     String retrievedPrintArea = workbook.getPrintArea(0);
	    
	 	 assertNotNull("Print Area not defined for first sheet", retrievedPrintArea);        
	     assertEquals(reference, retrievedPrintArea);
	     
	 }

	 /**
	  * For Convenience, dont force sheet names to be used
	  */
	 public void testSinglePrintAreaWOSheet()
	 {
		 HSSFWorkbook workbook = new HSSFWorkbook();        
		 HSSFSheet sheet = workbook.createSheet("Test Print Area");                
		 String sheetName = workbook.getSheetName(0);
	     
		 String reference = "$A$1:$B$1";
		 workbook.setPrintArea(0, reference);
	             
		 String retrievedPrintArea = workbook.getPrintArea(0);
	    
		 assertNotNull("Print Area not defined for first sheet", retrievedPrintArea);        
		 assertEquals(sheetName+"!"+reference, retrievedPrintArea);
	     
	 }


	 /**
	  * Test to see if the print area can be retrieved from an excel created file
	  */
	 public void testPrintAreaFileRead()
	 throws IOException
	 {
		FileInputStream fis = null;
		POIFSFileSystem fs  = null;
		HSSFWorkbook workbook     = null;
        
		String filename = System.getProperty("HSSF.testdata.path");

		filename = filename + "/SimpleWithPrintArea.xls";
        
        try {
        
			fis = new FileInputStream(filename);
			fs = new POIFSFileSystem(fis);
			workbook = new HSSFWorkbook(fs);
			
			String sheetName = workbook.getSheetName(0);
			String reference = sheetName+"!$A$1:$C$5";
			
			assertEquals(reference, workbook.getPrintArea(0));
		
        } finally {
			fis.close();
		
        }
		
		     
         
	}


	 /**
	  * Test to see if the print area made it to the file
	  */
	 public void testPrintAreaFile()
	 throws IOException
	 {
	 	HSSFWorkbook workbook = new HSSFWorkbook();        
	 	HSSFSheet sheet = workbook.createSheet("Test Print Area");                
	 	String sheetName = workbook.getSheetName(0);
	     
	 
	 	String reference = sheetName+"!$A$1:$B$1";
	 	workbook.setPrintArea(0, reference);
	     
	    File file = File.createTempFile("testPrintArea",".xls");        
	     
	    FileOutputStream fileOut = new FileOutputStream(file);
	    workbook.write(fileOut);
	    fileOut.close();
	     
	    assertTrue("file exists",file.exists());
	     
	    FileInputStream in = new FileInputStream(file);
	    workbook = new HSSFWorkbook(in);
	     
	 	String retrievedPrintArea = workbook.getPrintArea(0);       
	 	assertNotNull("Print Area not defined for first sheet", retrievedPrintArea);        
	 	assertEquals("References Match", reference, retrievedPrintArea);
         
	}

	/**
	 * Test to see if multiple print areas made it to the file
	 */
	public void testMultiplePrintAreaFile()
	throws IOException
	{
	    HSSFWorkbook workbook = new HSSFWorkbook();        
	    
	    HSSFSheet sheet = workbook.createSheet("Sheet 1");
	    sheet = workbook.createSheet("Sheet 2");
	    sheet = workbook.createSheet("Sheet 3");
	    
	    String sheetName = workbook.getSheetName(0);
		String reference = null;

		reference = sheetName+"!$A$1:$B$1";
		workbook.setPrintArea(0, reference); 

		sheetName = workbook.getSheetName(1);
	    String reference2 = sheetName+"!$B$2:$D$5";
	    workbook.setPrintArea(1, reference2);

		sheetName = workbook.getSheetName(2);
		String reference3 = sheetName+"!$D$2:$F$5";
		workbook.setPrintArea(2, reference3);
	    
	    File file = File.createTempFile("testMultiPrintArea",".xls");        
	    
	    FileOutputStream fileOut = new FileOutputStream(file);
	    workbook.write(fileOut);
	    fileOut.close();
	    
	    assertTrue("file exists",file.exists());
	    
	    FileInputStream in = new FileInputStream(file);
	    workbook = new HSSFWorkbook(in);
	    
	    String retrievedPrintArea = workbook.getPrintArea(0);        
	    assertNotNull("Print Area Not Found (Sheet 1)", retrievedPrintArea);
	    assertEquals(reference, retrievedPrintArea);
	    
		String retrievedPrintArea2 = workbook.getPrintArea(1);        
	    assertNotNull("Print Area Not Found (Sheet 2)", retrievedPrintArea2);
	    assertEquals(reference2, retrievedPrintArea2);
		
		String retrievedPrintArea3 = workbook.getPrintArea(2);        
	    assertNotNull("Print Area Not Found (Sheet 3)", retrievedPrintArea3);
	    assertEquals(reference3, retrievedPrintArea3);
	    
	    
	}
    
    /**
     * Tests the setting of print areas with coordinates (Row/Column designations)
     *
     */
    public void testPrintAreaCoords(){
		HSSFWorkbook workbook = new HSSFWorkbook();        
		HSSFSheet sheet = workbook.createSheet("Test Print Area");                
		String sheetName = workbook.getSheetName(0);
	     
		String reference = sheetName+"!$A$1:$B$1";
		workbook.setPrintArea(0, 0, 1, 0, 0);
	             
		String retrievedPrintArea = workbook.getPrintArea(0);
	    
		assertNotNull("Print Area not defined for first sheet", retrievedPrintArea);        
		assertEquals(reference, retrievedPrintArea);    	
    }
     
    /**
     * Verifies an existing print area is deleted
     *
     */
    public void testPrintAreaRemove() {
		HSSFWorkbook workbook = new HSSFWorkbook();        
		HSSFSheet sheet = workbook.createSheet("Test Print Area");                
		String sheetName = workbook.getSheetName(0);
	     
		String reference = sheetName+"!$A$1:$B$1";
		workbook.setPrintArea(0, 0, 1, 0, 0);
	             
		String retrievedPrintArea = workbook.getPrintArea(0);
	    
		assertNotNull("Print Area not defined for first sheet", retrievedPrintArea);        
    	
    	workbook.removePrintArea(0);
    	assertNull("PrintArea was not removed", workbook.getPrintArea(0)); 
    }
        
}

