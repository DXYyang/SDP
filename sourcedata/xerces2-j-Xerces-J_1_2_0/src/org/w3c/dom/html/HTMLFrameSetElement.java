/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See W3C License http://www.w3.org/Consortium/Legal/ for more
 * details.
 */

package org.w3c.dom.html;

/**
 *  Create a grid of frames. See the  FRAMESET element definition in HTML 4.0.
 */
public interface HTMLFrameSetElement extends HTMLElement {
    /**
     *  The number of columns of frames in the frameset. See the  cols 
     * attribute definition in HTML 4.0.
     */
    public String getCols();
    public void setCols(String cols);

    /**
     *  The number of rows of frames in the frameset. See the  rows attribute 
     * definition in HTML 4.0.
     */
    public String getRows();
    public void setRows(String rows);

}

