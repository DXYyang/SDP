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
package org.apache.poi.hslf.model;

import junit.framework.TestCase;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.HSLFSlideShow;

import java.awt.*;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Test drawing shapes via Graphics2D
 *
 * @author Yegor Kozlov
 */
public class TestShapes extends TestCase {
    private SlideShow ppt;
    private SlideShow pptB;

    protected void setUp() throws Exception {
		String dirname = System.getProperty("HSLF.testdata.path");
		String filename = dirname + "/empty.ppt";
		ppt = new SlideShow(new HSLFSlideShow(filename));
		
		String filenameB = dirname + "/empty_textbox.ppt";
		pptB = new SlideShow(new HSLFSlideShow(filenameB));
    }

    public void testGraphics() throws Exception {
        Slide slide = ppt.createSlide();

        Line line = new Line();
        java.awt.Rectangle lineAnchor = new java.awt.Rectangle(100, 200, 50, 60);
        line.setAnchor(lineAnchor);
        line.setLineWidth(3);
        line.setLineStyle(Line.PEN_DASH);
        line.setLineColor(Color.red);
        slide.addShape(line);

        AutoShape ellipse = new AutoShape(ShapeTypes.Ellipse);
        java.awt.Rectangle ellipseAnchor = new Rectangle(320, 154, 55, 111);
        ellipse.setAnchor(ellipseAnchor);
        ellipse.setLineWidth(2);
        ellipse.setLineStyle(Line.PEN_SOLID);
        ellipse.setLineColor(Color.green);
        ellipse.setFillColor(Color.lightGray);
        slide.addShape(ellipse);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        //read ppt from byte array

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));
        assertEquals(1, ppt.getSlides().length);

        slide = ppt.getSlides()[0];
        Shape[] shape = slide.getShapes();
        assertEquals(2, shape.length);

        assertTrue(shape[0] instanceof Line); //group shape
        assertEquals(lineAnchor, shape[0].getAnchor()); //group shape

        assertTrue(shape[1] instanceof AutoShape); //group shape
        assertEquals(ellipseAnchor, shape[1].getAnchor()); //group shape
    }

    /**
     * Verify that we can read TextBox shapes
     * @throws Exception
     */
    public void testTextBoxRead() throws Exception {
        String dirname = System.getProperty("HSLF.testdata.path");
        String filename = dirname + "/with_textbox.ppt";
        ppt = new SlideShow(new HSLFSlideShow(filename));
        Slide sl = ppt.getSlides()[0];
        Shape[] sh = sl.getShapes();
        for (int i = 0; i < sh.length; i++) {
            assertTrue(sh[i] instanceof TextBox);
            TextBox txtbox = (TextBox)sh[i];
            String text = txtbox.getText();
            assertNotNull(text);

            assertEquals(txtbox.getTextRun().getRichTextRuns().length, 1);
            RichTextRun rt = txtbox.getTextRun().getRichTextRuns()[0];

            if (text.equals("Hello, World!!!")){
                assertEquals(32, rt.getFontSize());
                assertTrue(rt.isBold());
                assertTrue(rt.isItalic());
            } else if (text.equals("I am just a poor boy")){
                assertEquals(44, rt.getFontSize());
                assertTrue(rt.isBold());
            } else if (text.equals("This is Times New Roman")){
                assertEquals(16, rt.getFontSize());
                assertTrue(rt.isBold());
                assertTrue(rt.isItalic());
                assertTrue(rt.isUnderlined());
            } else if (text.equals("Plain Text")){
                assertEquals(18, rt.getFontSize());
            }
        }
    }

    /**
     * Verify that we can add TextBox shapes to a slide
     * and set some of the style attributes
     */
    public void testTextBoxWriteBytes() throws Exception {
        ppt = new SlideShow();
        Slide sl = ppt.createSlide();
        RichTextRun rt;

        String val = "Hello, World!";

        // Create a new textbox, and give it lots of properties
        TextBox txtbox = new TextBox();
        rt = txtbox.getTextRun().getRichTextRuns()[0];
        txtbox.setText(val);
        rt.setFontName("Arial");
        rt.setFontSize(42);
        rt.setBold(true);
        rt.setItalic(true);
        rt.setUnderlined(false);
        rt.setFontColor(Color.red);
        sl.addShape(txtbox);

        // Check it before save
        rt = txtbox.getTextRun().getRichTextRuns()[0];
        assertEquals(val, rt.getText());
        assertEquals(42, rt.getFontSize());
        assertTrue(rt.isBold());
        assertTrue(rt.isItalic());
        assertFalse(rt.isUnderlined());
        assertEquals("Arial", rt.getFontName());
        assertEquals(Color.red, rt.getFontColor());

        // Serialize and read again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));

        txtbox = (TextBox)sl.getShapes()[0];
        rt = txtbox.getTextRun().getRichTextRuns()[0];

        // Check after save
        assertEquals(val, rt.getText());
        assertEquals(42, rt.getFontSize());
        assertTrue(rt.isBold());
        assertTrue(rt.isItalic());
        assertFalse(rt.isUnderlined());
        assertEquals("Arial", rt.getFontName());
        assertEquals(Color.red, rt.getFontColor());
    }
    
    /**
     * Test with an empty text box
     */
    public void testEmptyTextBox() throws Exception {
    	assertEquals(2, pptB.getSlides().length);
    	Slide s1 = pptB.getSlides()[0];
    	Slide s2 = pptB.getSlides()[1];
    	
    	// Check we can get the shapes count
    	assertEquals(2, s1.getShapes().length);
    	assertEquals(2, s2.getShapes().length);
    }

    /**
     * If you iterate over text shapes in a slide and collect them in a set
     * it must be the same as returned by Slide.getTextRuns().
     */
    public void testTextBoxSet() throws Exception {
        textBoxSet("/with_textbox.ppt");
        textBoxSet("/basic_test_ppt_file.ppt");
        textBoxSet("/next_test_ppt_file.ppt");
        textBoxSet("/Single_Coloured_Page.ppt");
        textBoxSet("/Single_Coloured_Page_With_Fonts_and_Alignments.ppt");
        textBoxSet("/incorrect_slide_order.ppt");
    }

    private void textBoxSet(String filename) throws Exception {
        String dirname = System.getProperty("HSLF.testdata.path");
        SlideShow ppt = new SlideShow(new HSLFSlideShow(dirname + filename));
        Slide[] sl = ppt.getSlides();
        for (int k = 0; k < sl.length; k++) {
            ArrayList lst1 = new ArrayList();
            TextRun[] txt = sl[k].getTextRuns();
            for (int i = 0; i < txt.length; i++) {
                lst1.add(txt[i].getText());
            }

            ArrayList lst2 = new ArrayList();
            Shape[] sh = sl[k].getShapes();
            for (int i = 0; i < sh.length; i++) {
                if (sh[i] instanceof TextBox){
                    TextBox tbox = (TextBox)sh[i];
                    lst2.add(tbox.getText());
                }
            }
            assertTrue(lst1.containsAll(lst2));
            assertTrue(lst2.containsAll(lst1));
        }
    }
}
