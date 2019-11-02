
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
        

package org.apache.poi.hssf.model;

import junit.framework.TestCase;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.record.aggregates.ColumnInfoRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.RowRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.ValueRecordsAggregate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Unit test for the Sheet class.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class TestSheet extends TestCase
{
    public void testCreateSheet() throws Exception
    {
        // Check we're adding row and cell aggregates
        List records = new ArrayList();
        records.add( new BOFRecord() );
        records.add( new DimensionsRecord() );
        records.add( new EOFRecord() );
        Sheet sheet = Sheet.createSheet( records, 0, 0 );

        int pos = 0;
        assertTrue( sheet.records.get(pos++) instanceof BOFRecord );
        assertTrue( sheet.records.get(pos++) instanceof ColumnInfoRecordsAggregate );
        assertTrue( sheet.records.get(pos++) instanceof DimensionsRecord );
        assertTrue( sheet.records.get(pos++) instanceof RowRecordsAggregate );
        assertTrue( sheet.records.get(pos++) instanceof ValueRecordsAggregate );
        assertTrue( sheet.records.get(pos++) instanceof EOFRecord );
    }

    public void testAddMergedRegion()
    {
        Sheet sheet = Sheet.createSheet();
        int regionsToAdd = 4096;
        int startRecords = sheet.getRecords().size();

        //simple test that adds a load of regions
        for (int n = 0; n < regionsToAdd; n++)
        {
            int index = sheet.addMergedRegion(0, (short) 0, 1, (short) 1);
            assertTrue("Merged region index expected to be " + n + " got " + index, index == n);
        }

        //test all the regions were indeed added
        assertTrue(sheet.getNumMergedRegions() == regionsToAdd);

        //test that the regions were spread out over the appropriate number of records
        int recordsAdded    = sheet.getRecords().size() - startRecords;
        int recordsExpected = regionsToAdd/1027;
        if ((regionsToAdd % 1027) != 0)
            recordsExpected++;
        assertTrue("The " + regionsToAdd + " merged regions should have been spread out over " + recordsExpected + " records, not " + recordsAdded, recordsAdded == recordsExpected);
    }

    public void testRemoveMergedRegion()
    {
        Sheet sheet = Sheet.createSheet();
        int regionsToAdd = 4096;

        for (int n = 0; n < regionsToAdd; n++)
            sheet.addMergedRegion(0, (short) 0, 1, (short) 1);

        int records = sheet.getRecords().size();

        //remove a third from the beginning
        for (int n = 0; n < regionsToAdd/3; n++)
        {
            sheet.removeMergedRegion(0);
            //assert they have been deleted
            assertTrue("Num of regions should be " + (regionsToAdd - n - 1) + " not " + sheet.getNumMergedRegions(), sheet.getNumMergedRegions() == regionsToAdd - n - 1);
        }

        //assert any record removing was done
        int recordsRemoved = (regionsToAdd/3)/1027; //doesn't work for particular values of regionsToAdd
        assertTrue("Expected " + recordsRemoved + " record to be removed from the starting " + records + ".  Currently there are " + sheet.getRecords().size() + " records", records - sheet.getRecords().size() == recordsRemoved);
    }

    /**
     * Bug: 22922 (Reported by Xuemin Guan)
     * <p>
     * Remove mergedregion fails when a sheet loses records after an initial CreateSheet
     * fills up the records.
     *
     */
    public void testMovingMergedRegion() {
        List records = new ArrayList();

        MergeCellsRecord merged = new MergeCellsRecord();
        merged.addArea(0, (short)0, 1, (short)2);
        records.add(new RowRecord());
        records.add(new RowRecord());
        records.add(new RowRecord());
        records.add(merged);

        Sheet sheet = Sheet.createSheet(records, 0);
        sheet.records.remove(0);

        //stub object to throw off list INDEX operations
        sheet.removeMergedRegion(0);
        assertEquals("Should be no more merged regions", 0, sheet.getNumMergedRegions());
    }

    public void testGetMergedRegionAt()
    {
        //TODO
    }

    public void testGetNumMergedRegions()
    {
        //TODO
    }

    /**
     * Makes sure all rows registered for this sheet are aggregated, they were being skipped
     *
     */
    public void testRowAggregation() {
        List records = new ArrayList();
        RowRecord row = new RowRecord();
        row.setRowNumber(0);
        records.add(row);

        row = new RowRecord();
        row.setRowNumber(1);
        records.add(row);

        records.add(new StringRecord());

        row = new RowRecord();
        row.setRowNumber(2);
        records.add(row);


        Sheet sheet = Sheet.createSheet(records, 0);
        assertNotNull("Row [2] was skipped", sheet.getRow(2));

    }

    /**
     * Make sure page break functionality works (in memory)
     *
     */
    public void testRowPageBreaks(){
        short colFrom = 0;
        short colTo = 255;

        Sheet sheet = Sheet.createSheet();
        sheet.setRowBreak(0, colFrom, colTo);

        assertTrue("no row break at 0", sheet.isRowBroken(0));
        assertEquals("1 row break available", 1, sheet.getNumRowBreaks());

        sheet.setRowBreak(0, colFrom, colTo);
        sheet.setRowBreak(0, colFrom, colTo);

        assertTrue("no row break at 0", sheet.isRowBroken(0));
        assertEquals("1 row break available", 1, sheet.getNumRowBreaks());

        sheet.setRowBreak(10, colFrom, colTo);
        sheet.setRowBreak(11, colFrom, colTo);

        assertTrue("no row break at 10", sheet.isRowBroken(10));
        assertTrue("no row break at 11", sheet.isRowBroken(11));
        assertEquals("3 row break available", 3, sheet.getNumRowBreaks());


        boolean is10 = false;
        boolean is0 = false;
        boolean is11 = false;

        Iterator iterator = sheet.getRowBreaks();
        while (iterator.hasNext()) {
            PageBreakRecord.Break breakItem = (PageBreakRecord.Break)iterator.next();
            int main = (int)breakItem.main;
            if (main != 0 && main != 10 && main != 11) fail("Invalid page break");
            if (main == 0) 	is0 = true;
            if (main == 10) is10= true;
            if (main == 11) is11 = true;
        }

        assertTrue("one of the breaks didnt make it", is0 && is10 && is11);

        sheet.removeRowBreak(11);
        assertFalse("row should be removed", sheet.isRowBroken(11));

        sheet.removeRowBreak(0);
        assertFalse("row should be removed", sheet.isRowBroken(0));

        sheet.removeRowBreak(10);
        assertFalse("row should be removed", sheet.isRowBroken(10));

        assertEquals("no more breaks", 0, sheet.getNumRowBreaks());


    }

    /**
     * Make sure column pag breaks works properly (in-memory)
     *
     */
    public void testColPageBreaks(){
        short rowFrom = 0;
        short rowTo = (short)65535;

        Sheet sheet = Sheet.createSheet();
        sheet.setColumnBreak((short)0, rowFrom, rowTo);

        assertTrue("no col break at 0", sheet.isColumnBroken((short)0));
        assertEquals("1 col break available", 1, sheet.getNumColumnBreaks());

        sheet.setColumnBreak((short)0, rowFrom, rowTo);

        assertTrue("no col break at 0", sheet.isColumnBroken((short)0));
        assertEquals("1 col break available", 1, sheet.getNumColumnBreaks());

        sheet.setColumnBreak((short)1, rowFrom, rowTo);
        sheet.setColumnBreak((short)10, rowFrom, rowTo);
        sheet.setColumnBreak((short)15, rowFrom, rowTo);

        assertTrue("no col break at 1", sheet.isColumnBroken((short)1));
        assertTrue("no col break at 10", sheet.isColumnBroken((short)10));
        assertTrue("no col break at 15", sheet.isColumnBroken((short)15));
        assertEquals("4 col break available", 4, sheet.getNumColumnBreaks());

        boolean is10 = false;
        boolean is0 = false;
        boolean is1 = false;
        boolean is15 = false;

        Iterator iterator = sheet.getColumnBreaks();
        while (iterator.hasNext()) {
            PageBreakRecord.Break breakItem = (PageBreakRecord.Break)iterator.next();
            int main = (int)breakItem.main;
            if (main != 0 && main != 1 && main != 10 && main != 15) fail("Invalid page break");
            if (main == 0) 	is0 = true;
            if (main == 1) 	is1 = true;
            if (main == 10) is10= true;
            if (main == 15) is15 = true;
        }

        assertTrue("one of the breaks didnt make it", is0 && is1 && is10 && is15);

        sheet.removeColumnBreak((short)15);
        assertFalse("column break should not be there", sheet.isColumnBroken((short)15));

        sheet.removeColumnBreak((short)0);
        assertFalse("column break should not be there", sheet.isColumnBroken((short)0));

        sheet.removeColumnBreak((short)1);
        assertFalse("column break should not be there", sheet.isColumnBroken((short)1));

        sheet.removeColumnBreak((short)10);
        assertFalse("column break should not be there", sheet.isColumnBroken((short)10));

        assertEquals("no more breaks", 0, sheet.getNumColumnBreaks());
    }

    /**
     * test newly added method Sheet.getXFIndexForColAt(..)
     * works as designed.
     */
    public void testXFIndexForColumn() {
        try{
            final short TEST_IDX = 10;
            final short DEFAULT_IDX = 0xF; // 15
            short xfindex = Short.MIN_VALUE;
            Sheet sheet = Sheet.createSheet();
            
            // without ColumnInfoRecord
            xfindex = sheet.getXFIndexForColAt((short) 0);
            assertEquals(DEFAULT_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 1);
            assertEquals(DEFAULT_IDX, xfindex);
            
            ColumnInfoRecord nci = ( ColumnInfoRecord ) sheet.createColInfo();
            sheet.columns.insertColumn(nci);
            
            // single column ColumnInfoRecord
            nci.setFirstColumn((short) 2);
            nci.setLastColumn((short) 2);
            nci.setXFIndex(TEST_IDX);            
            xfindex = sheet.getXFIndexForColAt((short) 0);
            assertEquals(DEFAULT_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 1);
            assertEquals(DEFAULT_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 2);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 3);
            assertEquals(DEFAULT_IDX, xfindex);

            // ten column ColumnInfoRecord
            nci.setFirstColumn((short) 2);
            nci.setLastColumn((short) 11);
            nci.setXFIndex(TEST_IDX);            
            xfindex = sheet.getXFIndexForColAt((short) 1);
            assertEquals(DEFAULT_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 2);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 6);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 11);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 12);
            assertEquals(DEFAULT_IDX, xfindex);

            // single column ColumnInfoRecord starting at index 0
            nci.setFirstColumn((short) 0);
            nci.setLastColumn((short) 0);
            nci.setXFIndex(TEST_IDX);            
            xfindex = sheet.getXFIndexForColAt((short) 0);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 1);
            assertEquals(DEFAULT_IDX, xfindex);

            // ten column ColumnInfoRecord starting at index 0
            nci.setFirstColumn((short) 0);
            nci.setLastColumn((short) 9);
            nci.setXFIndex(TEST_IDX);            
            xfindex = sheet.getXFIndexForColAt((short) 0);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 7);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 9);
            assertEquals(TEST_IDX, xfindex);
            xfindex = sheet.getXFIndexForColAt((short) 10);
            assertEquals(DEFAULT_IDX, xfindex);
        }
        catch(Exception e){e.printStackTrace();fail(e.getMessage());}
    }

}
