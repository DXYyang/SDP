/* ====================================================================
   Copyright 2003-2004   Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hssf.record;

public class DrawingSelectionRecord extends AbstractEscherHolderRecord
{
    public static final short sid = 0xED;

    public DrawingSelectionRecord()
    {
    }

    public DrawingSelectionRecord( short id, short size, byte[] data )
    {
        super( id, size, data );
    }

    public DrawingSelectionRecord( short id, short size, byte[] data, int offset )
    {
        super( id, size, data, offset );
    }

    protected String getRecordName()
    {
        return "MSODRAWINGSELECTION";
    }

    public short getSid()
    {
        return sid;
    }
}
