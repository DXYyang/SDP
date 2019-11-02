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

package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import org.apache.poi.poifs.filesystem.POIFSWriterListener;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

public class TestEmptyDocument extends TestCase {

  public static void main(String[] args) {
    TestEmptyDocument driver = new TestEmptyDocument();

    System.out.println();
    System.out.println("As only file...");
    System.out.println();

    System.out.print("Trying using createDocument(String,InputStream): ");
    try {
      driver.testSingleEmptyDocument();
      System.out.println("Worked!");
    } catch (IOException exception) {
      System.out.println("failed! ");
      System.out.println(exception.toString());
    }
    System.out.println();

    System.out.print
      ("Trying using createDocument(String,int,POIFSWriterListener): ");
    try {
      driver.testSingleEmptyDocumentEvent();
      System.out.println("Worked!");
    } catch (IOException exception) {
      System.out.println("failed!");
      System.out.println(exception.toString());
    }
    System.out.println();

    System.out.println();
    System.out.println("After another file...");
    System.out.println();

    System.out.print("Trying using createDocument(String,InputStream): ");
    try {
      driver.testEmptyDocumentWithFriend();
      System.out.println("Worked!");
    } catch (IOException exception) {
      System.out.println("failed! ");
      System.out.println(exception.toString());
    }
    System.out.println();

    System.out.print
      ("Trying using createDocument(String,int,POIFSWriterListener): ");
    try {
      driver.testEmptyDocumentWithFriend();
      System.out.println("Worked!");
    } catch (IOException exception) {
      System.out.println("failed!");
      System.out.println(exception.toString());
    }
    System.out.println();
  }

  public void testSingleEmptyDocument() throws IOException {
    POIFSFileSystem fs = new POIFSFileSystem();
    DirectoryEntry dir = fs.getRoot();
    dir.createDocument("Foo", new ByteArrayInputStream(new byte[] { }));
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    fs.writeFilesystem(out);
    new POIFSFileSystem(new ByteArrayInputStream(out.toByteArray()));
  }

  public void testSingleEmptyDocumentEvent() throws IOException {
    POIFSFileSystem fs = new POIFSFileSystem();
    DirectoryEntry dir = fs.getRoot();
    dir.createDocument("Foo", 0, new POIFSWriterListener() {
      public void processPOIFSWriterEvent(POIFSWriterEvent event) {
      	System.out.println("written");
      }
    });
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    fs.writeFilesystem(out);
    new POIFSFileSystem(new ByteArrayInputStream(out.toByteArray()));
  }

  public void testEmptyDocumentWithFriend() throws IOException {
    POIFSFileSystem fs = new POIFSFileSystem();
    DirectoryEntry dir = fs.getRoot();
    dir.createDocument("Bar", new ByteArrayInputStream(new byte[] { 0 }));
    dir.createDocument("Foo", new ByteArrayInputStream(new byte[] { }));
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    fs.writeFilesystem(out);
    new POIFSFileSystem(new ByteArrayInputStream(out.toByteArray()));
  }

  public void testEmptyDocumentEventWithFriend() throws IOException {
    POIFSFileSystem fs = new POIFSFileSystem();
    DirectoryEntry dir = fs.getRoot();
    dir.createDocument("Bar", 1, new POIFSWriterListener() {
      public void processPOIFSWriterEvent(POIFSWriterEvent event) {
        try {
          event.getStream().write(0);
        } catch (IOException exception) {
          throw new RuntimeException("exception on write: " + exception);
        }
      }
    });
    dir.createDocument("Foo", 0, new POIFSWriterListener() {
      public void processPOIFSWriterEvent(POIFSWriterEvent event) {
      }
    });
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    fs.writeFilesystem(out);
    new POIFSFileSystem(new ByteArrayInputStream(out.toByteArray()));
  }
}
