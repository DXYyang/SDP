
/* ====================================================================
   Copyright 2002-2004   Apache Software Foundation

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
        

package org.apache.poi.poifs.storage;

import java.io.*;

import java.util.*;

import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.util.IntegerField;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianConsts;

/**
 * A block of Property instances
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class PropertyBlock
    extends BigBlock
{
    private static final int _properties_per_block =
        POIFSConstants.BIG_BLOCK_SIZE / POIFSConstants.PROPERTY_SIZE;
    private Property[]       _properties;

    /**
     * Create a single instance initialized with default values
     *
     * @param properties the properties to be inserted
     * @param offset the offset into the properties array
     */

    private PropertyBlock(final Property [] properties, final int offset)
    {
        _properties = new Property[ _properties_per_block ];
        for (int j = 0; j < _properties_per_block; j++)
        {
            _properties[ j ] = properties[ j + offset ];
        }
    }

    /**
     * Create an array of PropertyBlocks from an array of Property
     * instances, creating empty Property instances to make up any
     * shortfall
     *
     * @param properties the Property instances to be converted into
     *                   PropertyBlocks, in a java List
     *
     * @return the array of newly created PropertyBlock instances
     */

    public static BlockWritable [] createPropertyBlockArray(
            final List properties)
    {
        int        block_count   =
            (properties.size() + _properties_per_block - 1)
            / _properties_per_block;
        Property[] to_be_written =
            new Property[ block_count * _properties_per_block ];

        System.arraycopy(properties.toArray(new Property[ 0 ]), 0,
                         to_be_written, 0, properties.size());
        for (int j = properties.size(); j < to_be_written.length; j++)
        {

            // create an instance of an anonymous inner class that
            // extends Property
            to_be_written[ j ] = new Property()
            {
                protected void preWrite()
                {
                }

                public boolean isDirectory()
                {
                    return false;
                }
            };
        }
        BlockWritable[] rvalue = new BlockWritable[ block_count ];

        for (int j = 0; j < block_count; j++)
        {
            rvalue[ j ] = new PropertyBlock(to_be_written,
                                            j * _properties_per_block);
        }
        return rvalue;
    }

    /* ********** START extension of BigBlock ********** */

    /**
     * Write the block's data to an OutputStream
     *
     * @param stream the OutputStream to which the stored data should
     *               be written
     *
     * @exception IOException on problems writing to the specified
     *            stream
     */

    void writeData(final OutputStream stream)
        throws IOException
    {
        for (int j = 0; j < _properties_per_block; j++)
        {
            _properties[ j ].writeData(stream);
        }
    }

    /* **********  END  extension of BigBlock ********** */
}   // end public class PropertyBlock

