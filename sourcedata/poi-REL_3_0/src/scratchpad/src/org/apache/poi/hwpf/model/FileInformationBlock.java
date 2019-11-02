
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

import java.util.HashSet;
import java.io.IOException;

import org.apache.poi.hwpf.model.io.*;


import org.apache.poi.hwpf.model.types.FIBAbstractType;

/**
 *
 * @author  andy
 */
public class FileInformationBlock extends FIBAbstractType
  implements Cloneable
{

    FIBLongHandler _longHandler;
    FIBShortHandler _shortHandler;
    FIBFieldHandler _fieldHandler;

    /** Creates a new instance of FileInformationBlock */
    public FileInformationBlock(byte[] mainDocument)
    {
        fillFields(mainDocument, 0);
    }

    public void fillVariableFields(byte[] mainDocument, byte[] tableStream)
    {
      HashSet fieldSet = new HashSet();
      fieldSet.add(new Integer(FIBFieldHandler.STSHF));
      fieldSet.add(new Integer(FIBFieldHandler.CLX));
      fieldSet.add(new Integer(FIBFieldHandler.DOP));
      fieldSet.add(new Integer(FIBFieldHandler.PLCFBTECHPX));
      fieldSet.add(new Integer(FIBFieldHandler.PLCFBTEPAPX));
      fieldSet.add(new Integer(FIBFieldHandler.PLCFSED));
      fieldSet.add(new Integer(FIBFieldHandler.PLCFLST));
      fieldSet.add(new Integer(FIBFieldHandler.PLFLFO));
      fieldSet.add(new Integer(FIBFieldHandler.PLCFFLDMOM));
      fieldSet.add(new Integer(FIBFieldHandler.STTBFFFN));
      fieldSet.add(new Integer(FIBFieldHandler.STTBSAVEDBY));
      fieldSet.add(new Integer(FIBFieldHandler.MODIFIED));


      _shortHandler = new FIBShortHandler(mainDocument);
      _longHandler = new FIBLongHandler(mainDocument, _shortHandler.START + _shortHandler.sizeInBytes());
      _fieldHandler = new FIBFieldHandler(mainDocument,
                                          _shortHandler.START + _shortHandler.sizeInBytes() + _longHandler.sizeInBytes(),
                                          tableStream, fieldSet, true);
    }

    public int getFcDop()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.DOP);
    }

    public void setFcDop(int fcDop)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.DOP, fcDop);
    }

    public int getLcbDop()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.DOP);
    }

    public void setLcbDop(int lcbDop)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.DOP, lcbDop);
    }

    public int getFcStshf()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.STSHF);
    }

    public int getLcbStshf()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.STSHF);
    }

    public void setFcStshf(int fcStshf)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.STSHF, fcStshf);
    }

    public void setLcbStshf(int lcbStshf)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.STSHF, lcbStshf);
    }

    public int getFcClx()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.CLX);
    }

    public int getLcbClx()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.CLX);
    }

    public void setFcClx(int fcClx)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.CLX, fcClx);
    }

    public void setLcbClx(int lcbClx)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.CLX, lcbClx);
    }

    public int getFcPlcfbteChpx()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.PLCFBTECHPX);
    }

    public int getLcbPlcfbteChpx()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.PLCFBTECHPX);
    }

    public void setFcPlcfbteChpx(int fcPlcfBteChpx)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.PLCFBTECHPX, fcPlcfBteChpx);
    }

    public void setLcbPlcfbteChpx(int lcbPlcfBteChpx)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.PLCFBTECHPX, lcbPlcfBteChpx);
    }

    public int getFcPlcfbtePapx()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.PLCFBTEPAPX);
    }

    public int getLcbPlcfbtePapx()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.PLCFBTEPAPX);
    }

    public void setFcPlcfbtePapx(int fcPlcfBtePapx)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.PLCFBTEPAPX, fcPlcfBtePapx);
    }

    public void setLcbPlcfbtePapx(int lcbPlcfBtePapx)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.PLCFBTEPAPX, lcbPlcfBtePapx);
    }

    public int getFcPlcfsed()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.PLCFSED);
    }

    public int getLcbPlcfsed()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.PLCFSED);
    }

    public void setFcPlcfsed(int fcPlcfSed)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.PLCFSED, fcPlcfSed);
    }

    public void setLcbPlcfsed(int lcbPlcfSed)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.PLCFSED, lcbPlcfSed);
    }

    public int getFcPlcfLst()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.PLCFLST);
    }

    public int getLcbPlcfLst()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.PLCFLST);
    }

    public void setFcPlcfLst(int fcPlcfLst)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.PLCFLST, fcPlcfLst);
    }

    public void setLcbPlcfLst(int lcbPlcfLst)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.PLCFLST, lcbPlcfLst);
    }

    public int getFcPlfLfo()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.PLFLFO);
    }

    public int getLcbPlfLfo()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.PLFLFO);
    }

    public void setFcPlfLfo(int fcPlfLfo)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.PLFLFO, fcPlfLfo);
    }

    public void setLcbPlfLfo(int lcbPlfLfo)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.PLFLFO, lcbPlfLfo);
    }

    public int getFcSttbfffn()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.STTBFFFN);
    }

    public int getLcbSttbfffn()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.STTBFFFN);
    }

    public void setFcSttbfffn(int fcSttbFffn)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.STTBFFFN, fcSttbFffn);
    }

    public void setLcbSttbfffn(int lcbSttbFffn)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.STTBFFFN, lcbSttbFffn);
    }

    public int getFcSttbSavedBy()
    {
        return _fieldHandler.getFieldOffset(FIBFieldHandler.STTBSAVEDBY);
    }

    public int getLcbSttbSavedBy()
    {
        return _fieldHandler.getFieldSize(FIBFieldHandler.STTBSAVEDBY);
    }

    public void setFcSttbSavedBy(int fcSttbSavedBy)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.STTBSAVEDBY, fcSttbSavedBy);
    }

    public void setLcbSttbSavedBy(int fcSttbSavedBy)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.STTBSAVEDBY, fcSttbSavedBy);
    }

    public int getModifiedLow()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.PLFLFO);
    }

    public int getModifiedHigh()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.PLFLFO);
    }

    public void setModifiedLow(int modifiedLow)
    {
      _fieldHandler.setFieldOffset(FIBFieldHandler.PLFLFO, modifiedLow);
    }

    public void setModifiedHigh(int modifiedHigh)
    {
      _fieldHandler.setFieldSize(FIBFieldHandler.PLFLFO, modifiedHigh);
    }

    public void setCbMac(int cbMac)
    {
      _longHandler.setLong(FIBLongHandler.CBMAC, cbMac);
    }

    public void clearOffsetsSizes()
    {
      _fieldHandler.clearFields();
    }

    public int getFcPlcffldMom()
    {
      return _fieldHandler.getFieldOffset(FIBFieldHandler.PLCFFLDMOM);
    }

    public int getLcbPlcffldMom()
    {
      return _fieldHandler.getFieldSize(FIBFieldHandler.PLCFFLDMOM);
    }

    public void writeTo (byte[] mainStream, HWPFOutputStream tableStream)
      throws IOException
    {
      //HWPFOutputStream mainDocument = sys.getStream("WordDocument");
      //HWPFOutputStream tableStream = sys.getStream("1Table");

      super.serialize(mainStream, 0);

      int size = super.getSize();
      _shortHandler.serialize(mainStream);
      _longHandler.serialize(mainStream, size + _shortHandler.sizeInBytes());
      _fieldHandler.writeTo(mainStream,
        super.getSize() + _shortHandler.sizeInBytes() + _longHandler.sizeInBytes(), tableStream);

    }

    public int getSize()
    {
      return super.getSize() + _shortHandler.sizeInBytes() +
        _longHandler.sizeInBytes() + _fieldHandler.sizeInBytes();
    }
//    public Object clone()
//    {
//      try
//      {
//        return super.clone();
//      }
//      catch (CloneNotSupportedException e)
//      {
//        e.printStackTrace();
//        return null;
//      }
//    }
}

