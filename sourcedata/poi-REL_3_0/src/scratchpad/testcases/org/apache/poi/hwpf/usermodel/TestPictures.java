/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.poi.hwpf.usermodel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.util.LittleEndian;

import junit.framework.TestCase;

/**
 * Test the picture handling
 *
 * @author Nick Burch (nick at torchbox dot com)
 */
public class TestPictures extends TestCase {
	private String dirname = System.getProperty("HWPF.testdata.path");
	
    protected void setUp() throws Exception {
    }			
    
    /**
     * two jpegs
     */
    public void testTwoImages() throws Exception {
    	HWPFDocument doc = new HWPFDocument(new FileInputStream(dirname + "/two_images.doc"));
    	List pics = doc.getPicturesTable().getAllPictures();
    	
    	assertNotNull(pics);
    	assertEquals(pics.size(), 2);
    	for(int i=0; i<pics.size(); i++) {
    		Object p = pics.get(i);
    		assertTrue(p instanceof Picture);
    		
    		Picture pic = (Picture)p;
    		assertNotNull(pic.suggestFileExtension());
    		assertNotNull(pic.suggestFullFileName());
    	}
    	
    	Picture picA = (Picture)pics.get(0);
    	Picture picB = (Picture)pics.get(1);
    	assertEquals("jpg", picA.suggestFileExtension());
    	assertEquals("jpg", picA.suggestFileExtension());
    }
    
    /**
     * pngs and jpegs
     */
    public void testDifferentImages() throws Exception {
    	HWPFDocument doc = new HWPFDocument(new FileInputStream(dirname + "/testPictures.doc"));
    	List pics = doc.getPicturesTable().getAllPictures();
    	
    	assertNotNull(pics);
    	assertEquals(7, pics.size());
    	for(int i=0; i<pics.size(); i++) {
    		Object p = pics.get(i);
    		assertTrue(p instanceof Picture);
    		
    		Picture pic = (Picture)p;
    		assertNotNull(pic.suggestFileExtension());
    		assertNotNull(pic.suggestFullFileName());
    	}
    	
    	assertEquals("jpg", ((Picture)pics.get(0)).suggestFileExtension());
    	assertEquals("jpg", ((Picture)pics.get(1)).suggestFileExtension());
    	assertEquals("png", ((Picture)pics.get(3)).suggestFileExtension());
    	assertEquals("png", ((Picture)pics.get(4)).suggestFileExtension());
    	assertEquals("wmf", ((Picture)pics.get(5)).suggestFileExtension());
    	assertEquals("jpg", ((Picture)pics.get(6)).suggestFileExtension());
    }
    
    /**
     * emf image, nice and simple
     */
    public void testEmfImage() throws Exception {
    	HWPFDocument doc = new HWPFDocument(new FileInputStream(dirname + "/vector_image.doc"));
    	List pics = doc.getPicturesTable().getAllPictures();
    	
    	assertNotNull(pics);
    	assertEquals(1, pics.size());
    	
    	Picture pic = (Picture)pics.get(0);
    	assertNotNull(pic.suggestFileExtension());
    	assertNotNull(pic.suggestFullFileName());
    	assertTrue(pic.getSize() > 128);
    	
    	// Check right contents
    	byte[] emf = loadImage("vector_image.emf");
    	byte[] pemf = pic.getContent();
    	assertEquals(emf.length, pemf.length);
    	for(int i=0; i<emf.length; i++) {
    		assertEquals(emf[i], pemf[i]);
    	}
    }
    
    /**
     * emf image, with a crazy offset
     */
    public void testEmfComplexImage() throws Exception {
    	HWPFDocument doc = new HWPFDocument(new FileInputStream(dirname + "/emf_2003_image.doc"));
    	List pics = doc.getPicturesTable().getAllPictures();
    	
    	assertNotNull(pics);
    	assertEquals(1, pics.size());

    	Picture pic = (Picture)pics.get(0);
    	assertNotNull(pic.suggestFileExtension());
    	assertNotNull(pic.suggestFullFileName());
    	
    	// This one's tricky
    	// TODO: Fix once we've sorted bug #41898
    	assertNotNull(pic.getContent());
    	assertNotNull(pic.getRawContent());
    	
    	// These are probably some sort of offset, need to figure them out
    	assertEquals(4, pic.getSize());
    	assertEquals(0x80000000l, LittleEndian.getUInt(pic.getContent()));
    	assertEquals(0x80000000l, LittleEndian.getUInt(pic.getRawContent()));
    }
    
    
    private byte[] loadImage(String filename) throws Exception {
    	ByteArrayOutputStream b = new ByteArrayOutputStream();
    	FileInputStream fis = new FileInputStream(dirname + "/" + filename);
    	
    	byte[] buf = new byte[4096];
    	int read = 0;
    	while( (read = fis.read(buf)) > -1 ) {
    		b.write(buf, 0, read);
    	}
    	return b.toByteArray();
    }
}
