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
package org.apache.poi.hssf.usermodel;

import junit.framework.TestCase;

import java.io.*;

/**
 * Tests TestHSSFCellComment.
 *
 * @author  Yegor Kozlov
 */

public class TestHSSFComment extends TestCase {

    /**
     * Test that we can create cells and add comments to it.
     */
    public static void testWriteComments() throws Exception {
        String cellText = "Hello, World";
        String commentText = "We can set comments in POI";
        String commentAuthor = "Apache Software Foundation";
        int cellRow = 3;
        short cellColumn = 1;

        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();

        HSSFCell cell = sheet.createRow(cellRow).createCell(cellColumn);
        cell.setCellValue(new HSSFRichTextString(cellText));
        assertNull(cell.getCellComment());

        HSSFPatriarch patr = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor();
        anchor.setAnchor( (short)4, 2, 0, 0, (short) 6, 5, 0, 0);
        HSSFComment comment = patr.createComment(anchor);
        HSSFRichTextString string1 = new HSSFRichTextString(commentText);
        comment.setString(string1);
        comment.setAuthor(commentAuthor);
        cell.setCellComment(comment);

        //verify our settings
        assertEquals(HSSFSimpleShape.OBJECT_TYPE_COMMENT, comment.getShapeType());
        assertEquals(commentAuthor, comment.getAuthor());
        assertEquals(commentText, comment.getString().getString());
        assertEquals(cellRow, comment.getRow());
        assertEquals(cellColumn, comment.getColumn());

        //serialize the workbook and read it again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        out.close();

        wb = new HSSFWorkbook(new ByteArrayInputStream(out.toByteArray()));
        sheet = wb.getSheetAt(0);
        cell = sheet.getRow(cellRow).getCell(cellColumn);
        comment = cell.getCellComment();

        assertNotNull(comment);
        assertEquals(HSSFSimpleShape.OBJECT_TYPE_COMMENT, comment.getShapeType());
        assertEquals(commentAuthor, comment.getAuthor());
        assertEquals(commentText, comment.getString().getString());
        assertEquals(cellRow, comment.getRow());
        assertEquals(cellColumn, comment.getColumn());
    }

    /**
     * test that we can read cell comments from an existing workbook.
     */
    public static void testReadComments() throws Exception {

         String dir = System.getProperty("HSSF.testdata.path");
         FileInputStream is = new FileInputStream(new File(dir, "SimpleWithComments.xls"));
         HSSFWorkbook wb = new HSSFWorkbook(is);
         is.close();

         HSSFSheet sheet = wb.getSheetAt(0);

         HSSFCell cell;
         HSSFRow row;
         HSSFComment comment;

         for (int rownum = 0; rownum < 3; rownum++) {
             row = sheet.getRow(rownum);
             cell = row.getCell((short)0);
             comment = cell.getCellComment();
             assertNull("Cells in the first column are not commented", comment);
             assertNull(sheet.getCellComment(rownum, 0));
         }

         for (int rownum = 0; rownum < 3; rownum++) {
             row = sheet.getRow(rownum);
             cell = row.getCell((short)1);
             comment = cell.getCellComment();
             assertNotNull("Cells in the second column have comments", comment);
             assertNotNull("Cells in the second column have comments", sheet.getCellComment(rownum, 1));

             assertEquals(HSSFSimpleShape.OBJECT_TYPE_COMMENT, comment.getShapeType());
             assertEquals("Yegor Kozlov", comment.getAuthor());
             assertFalse("cells in the second column have not empyy notes", 
                     "".equals(comment.getString().getString()));
             assertEquals(rownum, comment.getRow());
             assertEquals(cell.getCellNum(), comment.getColumn());
         }
     }

    /**
     * test that we can modify existing cell comments
     */
    public static void testModifyComments() throws Exception {

         String dir = System.getProperty("HSSF.testdata.path");
         FileInputStream is = new FileInputStream(new File(dir, "SimpleWithComments.xls"));
         HSSFWorkbook wb = new HSSFWorkbook(is);
         is.close();

         HSSFSheet sheet = wb.getSheetAt(0);

         HSSFCell cell;
         HSSFRow row;
         HSSFComment comment;

         for (int rownum = 0; rownum < 3; rownum++) {
             row = sheet.getRow(rownum);
             cell = row.getCell((short)1);
             comment = cell.getCellComment();
             comment.setAuthor("Mofified["+rownum+"] by Yegor");
             comment.setString(new HSSFRichTextString("Modified comment at row " + rownum));
         }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        out.close();

        wb = new HSSFWorkbook(new ByteArrayInputStream(out.toByteArray()));
        sheet = wb.getSheetAt(0);

        for (int rownum = 0; rownum < 3; rownum++) {
            row = sheet.getRow(rownum);
            cell = row.getCell((short)1);
            comment = cell.getCellComment();

            assertEquals("Mofified["+rownum+"] by Yegor", comment.getAuthor());
            assertEquals("Modified comment at row " + rownum, comment.getString().getString());
        }

     }
}
