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

import org.apache.poi.ddf.*;
import org.apache.poi.util.LittleEndian;

import java.util.ArrayList;
import java.util.List;

/**
 *  Represents a group of shapes.
 *
 * @author Yegor Kozlov
 */
public class ShapeGroup extends Shape{

    /**
      * Create a new ShapeGroup. This constructor is used when a new shape is created.
      *
      */
    public ShapeGroup(){
        this(null, null);
        _escherContainer = createSpContainer(false);
    }

    /**
      * Create a ShapeGroup object and initilize it from the supplied Record container.
      *
      * @param escherRecord       <code>EscherSpContainer</code> container which holds information about this shape
      * @param parent    the parent of the shape
      */
    protected ShapeGroup(EscherContainerRecord escherRecord, Shape parent){
        super(escherRecord, parent);
    }

    /**
     * @return the shapes contained in this group container
     */
    public Shape[] getShapes() {
    	// Out escher container record should contain serveral
        //  SpContainers, the first of which is the group shape itself
        List lst = _escherContainer.getChildRecords();

        ArrayList shapeList = new ArrayList();
        // Don't include the first SpContainer, it is always NotPrimitive
        for (int i = 1; i < lst.size(); i++){
        	EscherRecord r = (EscherRecord)lst.get(i);
        	if(r instanceof EscherContainerRecord) {
        		// Create the Shape for it
        		EscherContainerRecord container = (EscherContainerRecord)r;
        		Shape shape = ShapeFactory.createShape(container, this);
        		shapeList.add( shape );
        	} else {
        		// Should we do anything special with these non
        		//  Container records?
        		System.err.println("Shape contained non container escher record, was " + r.getClass().getName());
        	}
        }
        
        // Put the shapes into an array, and return
        Shape[] shapes = (Shape[])shapeList.toArray(new Shape[shapeList.size()]);
        return shapes;
    }

    /**
     * Sets the anchor (the bounding box rectangle) of this shape.
     * All coordinates should be expressed in Master units (576 dpi).
     *
     * @param anchor new anchor
     */
    public void setAnchor(java.awt.Rectangle anchor){

        EscherContainerRecord spContainer = (EscherContainerRecord)_escherContainer.getChildRecords().get(0);

        EscherClientAnchorRecord clientAnchor = (EscherClientAnchorRecord)getEscherChild(spContainer, EscherClientAnchorRecord.RECORD_ID);
        //hack. internal variable EscherClientAnchorRecord.shortRecord can be
        //initialized only in fillFields(). We need to set shortRecord=false;
        byte[] header = new byte[16];
        LittleEndian.putUShort(header, 0, 0);
        LittleEndian.putUShort(header, 2, 0);
        LittleEndian.putInt(header, 4, 8);
        clientAnchor.fillFields(header, 0, null);

        clientAnchor.setFlag((short)(anchor.y*MASTER_DPI/POINT_DPI));
        clientAnchor.setCol1((short)(anchor.x*MASTER_DPI/POINT_DPI));
        clientAnchor.setDx1((short)((anchor.width + anchor.x)*MASTER_DPI/POINT_DPI));
        clientAnchor.setRow1((short)((anchor.height + anchor.y)*MASTER_DPI/POINT_DPI));

        EscherSpgrRecord spgr = (EscherSpgrRecord)getEscherChild(spContainer, EscherSpgrRecord.RECORD_ID);

        spgr.setRectX1(anchor.x*MASTER_DPI/POINT_DPI);
        spgr.setRectY1(anchor.y*MASTER_DPI/POINT_DPI);
        spgr.setRectX2((anchor.x + anchor.width)*MASTER_DPI/POINT_DPI);
        spgr.setRectY2((anchor.y + anchor.height)*MASTER_DPI/POINT_DPI);
    }

    /**
     * Create a new ShapeGroup and create an instance of <code>EscherSpgrContainer</code> which represents a group of shapes
     */
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        EscherContainerRecord spgr = new EscherContainerRecord();
        spgr.setRecordId(EscherContainerRecord.SPGR_CONTAINER);
        spgr.setOptions((short)15);

        //The group itself is a shape, and always appears as the first EscherSpContainer in the group container.
        EscherContainerRecord spcont = new EscherContainerRecord();
        spcont.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spcont.setOptions((short)15);

        EscherSpgrRecord spg = new EscherSpgrRecord();
        spg.setOptions((short)1);
        spcont.addChildRecord(spg);

        EscherSpRecord sp = new EscherSpRecord();
        short type = (ShapeTypes.NotPrimitive << 4) + 2;
        sp.setOptions(type);
        sp.setFlags(EscherSpRecord.FLAG_HAVEANCHOR | EscherSpRecord.FLAG_GROUP);
        spcont.addChildRecord(sp);

        EscherClientAnchorRecord anchor = new EscherClientAnchorRecord();
        spcont.addChildRecord(anchor);

        spgr.addChildRecord(spcont);
        return spgr;
    }

    /**
     * Add a shape to this group.
     *
     * @param shape - the Shape to add
     */
    public void addShape(Shape shape){
        _escherContainer.addChildRecord(shape.getSpContainer());

        Sheet sheet = getSheet();
        shape.setSheet(sheet);
        shape.afterInsert(sheet);

        if(shape instanceof TextBox) {
            TextBox tbox = (TextBox)shape;
            getSheet().getPPDrawing().addTextboxWrapper(tbox._txtbox);
        }
    }

    /**
     * Moves this <code>ShapeGroup</code> to the specified location.
     * <p>
     * @param x the x coordinate of the top left corner of the shape in new location
     * @param y the y coordinate of the top left corner of the shape in new location
     */
    public void moveTo(int x, int y){
        java.awt.Rectangle anchor = getAnchor();
        int dx = x - anchor.x;
        int dy = y - anchor.y;
        anchor.translate(dx, dy);
        setAnchor(anchor);

        Shape[] shape = getShapes();
        for (int i = 0; i < shape.length; i++) {
            java.awt.Rectangle chanchor = shape[i].getAnchor();
            chanchor.translate(dx, dy);
            shape[i].setAnchor(chanchor);
        }
    }

}
