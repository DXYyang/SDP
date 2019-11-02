package org.apache.lucene.benchmark.byTask.feeds;

/**
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

import org.apache.lucene.benchmark.byTask.utils.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A DocMaker using the Reuters collection for its input.
 *
 * Config properties:
 * docs.dir=&lt;path to the docs dir| Default: reuters-out&gt;

 *
 */
public class ReutersDocMaker extends BasicDocMaker {

  private DateFormat dateFormat;
  private File dataDir = null;
  private ArrayList inputFiles = new ArrayList();
  private int nextFile = 0;
  private int iteration=0;
  
  /* (non-Javadoc)
   * @see SimpleDocMaker#setConfig(java.util.Properties)
   */
  public void setConfig(Config config) {
    super.setConfig(config);
    String d = config.get("docs.dir","reuters-out");
    dataDir = new File(new File("work"),d);


    collectFiles(dataDir,inputFiles);
    if (inputFiles.size()==0) {
      throw new RuntimeException("No txt files in dataDir: "+dataDir.getAbsolutePath());
    }
    // date format: 30-MAR-1987 14:22:36.87
    dateFormat = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss.SSS",Locale.US);
    dateFormat.setLenient(true);
  }

  protected DocData getNextDocData() throws Exception {
    File f = null;
    String name = null;
    synchronized (this) {
      if (nextFile >= inputFiles.size()) { 
        // exhausted files, start a new round, unless forever set to false.
        if (!forever) {
          throw new NoMoreDataException();
        }
        nextFile = 0;
        iteration++;
      }
      f = (File) inputFiles.get(nextFile++);
      name = f.getCanonicalPath()+"_"+iteration;
    }
    
    BufferedReader reader = new BufferedReader(new FileReader(f));
    String line = null;
    //First line is the date, 3rd is the title, rest is body
    String dateStr = reader.readLine();
    reader.readLine();//skip an empty line
    String title = reader.readLine();
    reader.readLine();//skip an empty line
    StringBuffer bodyBuf = new StringBuffer(1024);
    while ((line = reader.readLine()) != null) {
      bodyBuf.append(line).append(' ');
    }
    reader.close();
    
    addBytes(f.length());

    
    Date date = dateFormat.parse(dateStr.trim()); 
    return new DocData(name, bodyBuf.toString(), title, null, date);
  }


  /*
   *  (non-Javadoc)
   * @see DocMaker#resetIinputs()
   */
  public synchronized void resetInputs() {
    super.resetInputs();
    nextFile = 0;
    iteration = 0;
  }

  /*
   *  (non-Javadoc)
   * @see DocMaker#numUniqueTexts()
   */
  public int numUniqueTexts() {
    return inputFiles.size();
  }

}
