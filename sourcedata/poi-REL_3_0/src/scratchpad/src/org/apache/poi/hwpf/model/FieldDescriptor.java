
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

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;

public class FieldDescriptor
{
  byte _fieldBoundaryType;
  byte _info;
    private final static BitField fZombieEmbed = BitFieldFactory.getInstance(0x02);
    private final static BitField fResultDiry = BitFieldFactory.getInstance(0x04);
    private final static BitField fResultEdited = BitFieldFactory.getInstance(0x08);
    private final static BitField fLocked = BitFieldFactory.getInstance(0x10);
    private final static BitField fPrivateResult = BitFieldFactory.getInstance(0x20);
    private final static BitField fNested = BitFieldFactory.getInstance(0x40);
    private final static BitField fHasSep = BitFieldFactory.getInstance(0x80);


  public FieldDescriptor()
  {
  }
}
