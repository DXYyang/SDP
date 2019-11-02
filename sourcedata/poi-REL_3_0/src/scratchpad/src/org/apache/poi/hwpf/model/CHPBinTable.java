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

package org.apache.poi.hwpf.model;

import java.util.List;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.IOException;

import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.hwpf.model.io.*;
import org.apache.poi.hwpf.sprm.SprmBuffer;

/**
 * This class holds all of the character formatting properties.
 *
 * @author Ryan Ackley
 */
public class CHPBinTable
{
  /** List of character properties.*/
  protected ArrayList _textRuns = new ArrayList();


  public CHPBinTable()
  {
  }

  /**
   * Constructor used to read a binTable in from a Word document.
   *
   * @param documentStream
   * @param tableStream
   * @param offset
   * @param size
   * @param fcMin
   */
  public CHPBinTable(byte[] documentStream, byte[] tableStream, int offset,
                     int size, int fcMin)
  {
    PlexOfCps binTable = new PlexOfCps(tableStream, offset, size, 4);

    int length = binTable.length();
    for (int x = 0; x < length; x++)
    {
      GenericPropertyNode node = binTable.getProperty(x);

      int pageNum = LittleEndian.getInt(node.getBytes());
      int pageOffset = POIFSConstants.BIG_BLOCK_SIZE * pageNum;

      CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage(documentStream,
        pageOffset, fcMin);

      int fkpSize = cfkp.size();

      for (int y = 0; y < fkpSize; y++)
      {
        _textRuns.add(cfkp.getCHPX(y));
      }
    }
  }

  public void adjustForDelete(int listIndex, int offset, int length)
  {
    int size = _textRuns.size();
    int endMark = offset + length;
    int endIndex = listIndex;

    CHPX chpx = (CHPX)_textRuns.get(endIndex);
    while (chpx.getEnd() < endMark)
    {
      chpx = (CHPX)_textRuns.get(++endIndex);
    }
    if (listIndex == endIndex)
    {
      chpx = (CHPX)_textRuns.get(endIndex);
      chpx.setEnd((chpx.getEnd() - endMark) + offset);
    }
    else
    {
      chpx = (CHPX)_textRuns.get(listIndex);
      chpx.setEnd(offset);
      for (int x = listIndex + 1; x < endIndex; x++)
      {
        chpx = (CHPX)_textRuns.get(x);
        chpx.setStart(offset);
        chpx.setEnd(offset);
      }
      chpx = (CHPX)_textRuns.get(endIndex);
      chpx.setEnd((chpx.getEnd() - endMark) + offset);
    }

    for (int x = endIndex + 1; x < size; x++)
    {
      chpx = (CHPX)_textRuns.get(x);
      chpx.setStart(chpx.getStart() - length);
      chpx.setEnd(chpx.getEnd() - length);
    }
  }

  public void insert(int listIndex, int cpStart, SprmBuffer buf)
  {
    CHPX insertChpx = new CHPX(cpStart, cpStart, buf);
    if (listIndex == _textRuns.size())
    {
      _textRuns.add(insertChpx);
    }
    else
    {
      CHPX chpx = (CHPX)_textRuns.get(listIndex);
      if (chpx.getStart() < cpStart)
      {
        CHPX clone = new CHPX(cpStart, chpx.getEnd(), chpx.getSprmBuf());
        chpx.setEnd(cpStart);

        _textRuns.add(listIndex + 1, insertChpx);
        _textRuns.add(listIndex + 2, clone);
      }
      else
      {
        _textRuns.add(listIndex, insertChpx);
      }
    }
  }

  public void adjustForInsert(int listIndex, int length)
  {
    int size = _textRuns.size();
    CHPX chpx = (CHPX)_textRuns.get(listIndex);
    chpx.setEnd(chpx.getEnd() + length);

    for (int x = listIndex + 1; x < size; x++)
    {
      chpx = (CHPX)_textRuns.get(x);
      chpx.setStart(chpx.getStart() + length);
      chpx.setEnd(chpx.getEnd() + length);
    }
  }

  public List getTextRuns()
  {
    return _textRuns;
  }

  public void writeTo(HWPFFileSystem sys, int fcMin)
    throws IOException
  {

    HWPFOutputStream docStream = sys.getStream("WordDocument");
    OutputStream tableStream = sys.getStream("1Table");

    PlexOfCps binTable = new PlexOfCps(4);

    // each FKP must start on a 512 byte page.
    int docOffset = docStream.getOffset();
    int mod = docOffset % POIFSConstants.BIG_BLOCK_SIZE;
    if (mod != 0)
    {
      byte[] padding = new byte[POIFSConstants.BIG_BLOCK_SIZE - mod];
      docStream.write(padding);
    }

    // get the page number for the first fkp
    docOffset = docStream.getOffset();
    int pageNum = docOffset/POIFSConstants.BIG_BLOCK_SIZE;

    // get the ending fc
    int endingFc = ((PropertyNode)_textRuns.get(_textRuns.size() - 1)).getEnd();
    endingFc += fcMin;


    ArrayList overflow = _textRuns;
    do
    {
      PropertyNode startingProp = (PropertyNode)overflow.get(0);
      int start = startingProp.getStart() + fcMin;

      CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage();
      cfkp.fill(overflow);

      byte[] bufFkp = cfkp.toByteArray(fcMin);
      docStream.write(bufFkp);
      overflow = cfkp.getOverflow();

      int end = endingFc;
      if (overflow != null)
      {
        end = ((PropertyNode)overflow.get(0)).getStart() + fcMin;
      }

      byte[] intHolder = new byte[4];
      LittleEndian.putInt(intHolder, pageNum++);
      binTable.addProperty(new GenericPropertyNode(start, end, intHolder));

    }
    while (overflow != null);
    tableStream.write(binTable.toByteArray());
  }





}
