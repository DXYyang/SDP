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

package org.apache.poi.hslf.usermodel;

import org.apache.poi.hslf.*;
import org.apache.poi.hslf.blip.*;
import org.apache.poi.hslf.model.*;
import junit.framework.TestCase;

import java.io.*;
import java.util.Arrays;

/**
 * Test adding/reading pictures
 *
 * @author Yegor Kozlov
 */
public class TestPictures extends TestCase{

    protected File cwd;

    public void setUp() throws Exception {
        cwd = new File(System.getProperty("HSLF.testdata.path"));
    }

    /**
     * Test read/write Macintosh PICT
     */
    public void testPICT() throws Exception {
        SlideShow ppt = new SlideShow();

        Slide slide = ppt.createSlide();
        File img = new File(cwd, "cow.pict");
        int idx = ppt.addPicture(img, Picture.PICT);
        Picture pict = new Picture(idx);
        assertEquals(idx, pict.getPictureIndex());
        slide.addShape(pict);

        //serialize and read again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));

        //make sure we can read this picture shape and it refers to the correct picture data
        Shape[] sh = ppt.getSlides()[0].getShapes();
        assertEquals(1, sh.length);
        pict = (Picture)sh[0];
        assertEquals(idx, pict.getPictureIndex());

        //check picture data
        PictureData[] pictures = ppt.getPictureData();
        //the Picture shape refers to the PictureData object in the Presentation
        assertEquals(pict.getPictureData(), pictures[0]);

        assertEquals(1, pictures.length);
        assertEquals(Picture.PICT, pictures[0].getType());
        assertTrue(pictures[0] instanceof PICT);
        //compare the content of the initial file with what is stored in the PictureData
        byte[] src_bytes = read(img);
        byte[] ppt_bytes = pictures[0].getData();
        assertEquals(src_bytes.length, ppt_bytes.length);
        //in PICT the first 512 bytes are MAC specific and may not be preserved, ignore them
        byte[] b1 = new byte[src_bytes.length-512];
        System.arraycopy(src_bytes, 512, b1, 0, b1.length);
        byte[] b2 = new byte[ppt_bytes.length-512];
        System.arraycopy(ppt_bytes, 512, b2, 0, b2.length);
        assertTrue(Arrays.equals(b1, b2));
    }

    /**
     * Test read/write WMF
     */
    public void testWMF() throws Exception {
        SlideShow ppt = new SlideShow();

        Slide slide = ppt.createSlide();
        File img = new File(cwd, "santa.wmf");
        int idx = ppt.addPicture(img, Picture.WMF);
        Picture pict = new Picture(idx);
        assertEquals(idx, pict.getPictureIndex());
        slide.addShape(pict);

        //serialize and read again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));

        //make sure we can read this picture shape and it refers to the correct picture data
        Shape[] sh = ppt.getSlides()[0].getShapes();
        assertEquals(1, sh.length);
        pict = (Picture)sh[0];
        assertEquals(idx, pict.getPictureIndex());

        //check picture data
        PictureData[] pictures = ppt.getPictureData();
        //the Picture shape refers to the PictureData object in the Presentation
        assertEquals(pict.getPictureData(), pictures[0]);

        assertEquals(1, pictures.length);
        assertEquals(Picture.WMF, pictures[0].getType());
        assertTrue(pictures[0] instanceof WMF);
        //compare the content of the initial file with what is stored in the PictureData
        byte[] src_bytes = read(img);
        byte[] ppt_bytes = pictures[0].getData();
        assertEquals(src_bytes.length, ppt_bytes.length);
        //in WMF the first 22 bytes - is a metafile header
        byte[] b1 = new byte[src_bytes.length-22];
        System.arraycopy(src_bytes, 22, b1, 0, b1.length);
        byte[] b2 = new byte[ppt_bytes.length-22];
        System.arraycopy(ppt_bytes, 22, b2, 0, b2.length);
        assertTrue(Arrays.equals(b1, b2));
    }

    /**
     * Test read/write EMF
     */
    public void testEMF() throws Exception {
        SlideShow ppt = new SlideShow();

        Slide slide = ppt.createSlide();
        File img = new File(cwd, "wrench.emf");
        int idx = ppt.addPicture(img, Picture.EMF);
        Picture pict = new Picture(idx);
        assertEquals(idx, pict.getPictureIndex());
        slide.addShape(pict);

        //serialize and read again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));

        //make sure we can get this picture shape and it refers to the correct picture data
        Shape[] sh = ppt.getSlides()[0].getShapes();
        assertEquals(1, sh.length);
        pict = (Picture)sh[0];
        assertEquals(idx, pict.getPictureIndex());

        //check picture data
        PictureData[] pictures = ppt.getPictureData();
        //the Picture shape refers to the PictureData object in the Presentation
        assertEquals(pict.getPictureData(), pictures[0]);

        assertEquals(1, pictures.length);
        assertEquals(Picture.EMF, pictures[0].getType());
        assertTrue(pictures[0] instanceof EMF);
        //compare the content of the initial file with what is stored in the PictureData
        byte[] src_bytes = read(img);
        byte[] ppt_bytes = pictures[0].getData();
        assertTrue(Arrays.equals(src_bytes, ppt_bytes));
    }

    /**
     * Test read/write PNG
     */
    public void testPNG() throws Exception {
        SlideShow ppt = new SlideShow();

        Slide slide = ppt.createSlide();
        File img = new File(cwd, "tomcat.png");
        int idx = ppt.addPicture(img, Picture.PNG);
        Picture pict = new Picture(idx);
        assertEquals(idx, pict.getPictureIndex());
        slide.addShape(pict);

        //serialize and read again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));

        //make sure we can read this picture shape and it refers to the correct picture data
        Shape[] sh = ppt.getSlides()[0].getShapes();
        assertEquals(1, sh.length);
        pict = (Picture)sh[0];
        assertEquals(idx, pict.getPictureIndex());

        //check picture data
        PictureData[] pictures = ppt.getPictureData();
        //the Picture shape refers to the PictureData object in the Presentation
        assertEquals(pict.getPictureData(), pictures[0]);

        assertEquals(1, pictures.length);
        assertEquals(Picture.PNG, pictures[0].getType());
        assertTrue(pictures[0] instanceof PNG);
        //compare the content of the initial file with what is stored in the PictureData
        byte[] src_bytes = read(img);
        byte[] ppt_bytes = pictures[0].getData();
        assertTrue(Arrays.equals(src_bytes, ppt_bytes));
    }

    /**
     * Test read/write JPEG
     */
    public void testJPEG() throws Exception {
        SlideShow ppt = new SlideShow();

        Slide slide = ppt.createSlide();
        File img = new File(cwd, "clock.jpg");
        int idx = ppt.addPicture(img, Picture.JPEG);
        Picture pict = new Picture(idx);
        assertEquals(idx, pict.getPictureIndex());
        slide.addShape(pict);

        //serialize and read again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));

        //make sure we can read this picture shape and it refers to the correct picture data
        Shape[] sh = ppt.getSlides()[0].getShapes();
        assertEquals(1, sh.length);
        pict = (Picture)sh[0];
        assertEquals(idx, pict.getPictureIndex());

        //check picture data
        PictureData[] pictures = ppt.getPictureData();
        //the Picture shape refers to the PictureData object in the Presentation
        assertEquals(pict.getPictureData(), pictures[0]);

        assertEquals(1, pictures.length);
        assertEquals(Picture.JPEG, pictures[0].getType());
        assertTrue(pictures[0] instanceof JPEG);
        //compare the content of the initial file with what is stored in the PictureData
        byte[] src_bytes = read(img);
        byte[] ppt_bytes = pictures[0].getData();
        assertTrue(Arrays.equals(src_bytes, ppt_bytes));
    }

    /**
     * Test read/write DIB
     */
    public void testDIB() throws Exception {
        SlideShow ppt = new SlideShow();

        Slide slide = ppt.createSlide();
        File img = new File(cwd, "sci_cec.dib");
        
        // Check we can read the test DIB image
        assertTrue(img.exists());
        
        // Add the image
        int idx = ppt.addPicture(img, Picture.DIB);
        Picture pict = new Picture(idx);
        assertEquals(idx, pict.getPictureIndex());
        slide.addShape(pict);

        //serialize and read again
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        out.close();

        ppt = new SlideShow(new HSLFSlideShow(new ByteArrayInputStream(out.toByteArray())));

        //make sure we can read this picture shape and it refers to the correct picture data
        Shape[] sh = ppt.getSlides()[0].getShapes();
        assertEquals(1, sh.length);
        pict = (Picture)sh[0];
        assertEquals(idx, pict.getPictureIndex());

        //check picture data
        PictureData[] pictures = ppt.getPictureData();
        //the Picture shape refers to the PictureData object in the Presentation
        assertEquals(pict.getPictureData(), pictures[0]);

        assertEquals(1, pictures.length);
        assertEquals(Picture.DIB, pictures[0].getType());
        assertTrue(pictures[0] instanceof DIB);
        //compare the content of the initial file with what is stored in the PictureData
        byte[] src_bytes = read(img);
        byte[] ppt_bytes = pictures[0].getData();
        assertTrue(Arrays.equals(src_bytes, ppt_bytes));
    }

    /**
     * Read file into a byte array
     */
    protected byte[] read(File f) throws IOException {
        byte[] bytes = new byte[(int)f.length()];
        FileInputStream is = new FileInputStream(f);
        is.read(bytes);
        is.close();
        return bytes;
    }

    /**
     * Read pictures in different formats from a reference slide show
     */
    public void testReadPictures() throws Exception {

        byte[] src_bytes, ppt_bytes, b1, b2;
        Picture pict;
        PictureData pdata;

        SlideShow ppt = new SlideShow(new HSLFSlideShow(new File(cwd, "pictures.ppt").getPath()));
        Slide[] slides = ppt.getSlides();
        PictureData[] pictures = ppt.getPictureData();
        assertEquals(5, pictures.length);

        pict = (Picture)slides[0].getShapes()[0]; //the first slide contains JPEG
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof JPEG);
        assertEquals(Picture.JPEG, pdata.getType());
        src_bytes = pdata.getData();
        ppt_bytes = read(new File(cwd, "clock.jpg"));
        assertTrue(Arrays.equals(src_bytes, ppt_bytes));

        pict = (Picture)slides[1].getShapes()[0]; //the second slide contains PNG
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof PNG);
        assertEquals(Picture.PNG, pdata.getType());
        src_bytes = pdata.getData();
        ppt_bytes = read(new File(cwd, "tomcat.png"));
        assertTrue(Arrays.equals(src_bytes, ppt_bytes));

        pict = (Picture)slides[2].getShapes()[0]; //the third slide contains WMF
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof WMF);
        assertEquals(Picture.WMF, pdata.getType());
        src_bytes = pdata.getData();
        ppt_bytes = read(new File(cwd, "santa.wmf"));
        assertEquals(src_bytes.length, ppt_bytes.length);
        //ignore the first 22 bytes - it is a WMF metafile header
        b1 = new byte[src_bytes.length-22];
        System.arraycopy(src_bytes, 22, b1, 0, b1.length);
        b2 = new byte[ppt_bytes.length-22];
        System.arraycopy(ppt_bytes, 22, b2, 0, b2.length);
        assertTrue(Arrays.equals(b1, b2));

        pict = (Picture)slides[3].getShapes()[0]; //the forth slide contains PICT
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof PICT);
        assertEquals(Picture.PICT, pdata.getType());
        src_bytes = pdata.getData();
        ppt_bytes = read(new File(cwd, "cow.pict"));
        assertEquals(src_bytes.length, ppt_bytes.length);
        //ignore the first 512 bytes - it is a MAC specific crap
        b1 = new byte[src_bytes.length-512];
        System.arraycopy(src_bytes, 512, b1, 0, b1.length);
        b2 = new byte[ppt_bytes.length-512];
        System.arraycopy(ppt_bytes, 512, b2, 0, b2.length);
        assertTrue(Arrays.equals(b1, b2));

        pict = (Picture)slides[4].getShapes()[0]; //the fifth slide contains EMF
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof EMF);
        assertEquals(Picture.EMF, pdata.getType());
        src_bytes = pdata.getData();
        ppt_bytes = read(new File(cwd, "wrench.emf"));
        assertTrue(Arrays.equals(src_bytes, ppt_bytes));

    }

	/**
	 * Test that on a party corrupt powerpoint document, which has 
	 *  crazy pictures of type 0, we do our best.
	 */
	public void testZeroPictureType() throws Exception {
		HSLFSlideShow hslf = new HSLFSlideShow(new File(cwd, "PictureTypeZero.ppt").getPath());

		// Should still have 2 real pictures
		assertEquals(2, hslf.getPictures().length);
		// Both are real pictures, both WMF
		assertEquals(Picture.WMF, hslf.getPictures()[0].getType());
		assertEquals(Picture.WMF, hslf.getPictures()[1].getType());

		// Now test what happens when we use the SlideShow interface
		SlideShow ppt = new SlideShow(hslf);
        Slide[] slides = ppt.getSlides();
        PictureData[] pictures = ppt.getPictureData();
        assertEquals(12, slides.length);
        assertEquals(2, pictures.length);
        
		Picture pict;
		PictureData pdata;
		
        pict = (Picture)slides[0].getShapes()[1]; // 2nd object on 1st slide
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof WMF);
        assertEquals(Picture.WMF, pdata.getType());
		
        pict = (Picture)slides[0].getShapes()[2]; // 3rd object on 1st slide
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof WMF);
        assertEquals(Picture.WMF, pdata.getType());
	}
	
	public void testZeroPictureLength() throws Exception {
		HSLFSlideShow hslf = new HSLFSlideShow(new File(cwd, "PictureLengthZero.ppt").getPath());

		// Should still have 2 real pictures
		assertEquals(2, hslf.getPictures().length);
		// Both are real pictures, both WMF
		assertEquals(Picture.WMF, hslf.getPictures()[0].getType());
		assertEquals(Picture.WMF, hslf.getPictures()[1].getType());
		
		// Now test what happens when we use the SlideShow interface
		SlideShow ppt = new SlideShow(hslf);
        Slide[] slides = ppt.getSlides();
        PictureData[] pictures = ppt.getPictureData();
        assertEquals(27, slides.length);
        assertEquals(2, pictures.length);
        
		Picture pict;
		PictureData pdata;
		
        pict = (Picture)slides[6].getShapes()[13];
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof WMF);
        assertEquals(Picture.WMF, pdata.getType());
		
        pict = (Picture)slides[7].getShapes()[13];
        pdata = pict.getPictureData();
        assertTrue(pdata instanceof WMF);
        assertEquals(Picture.WMF, pdata.getType());
	}
}
