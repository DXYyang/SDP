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
 *  This contains generic meta-information about the document. See the  META 
 * element definition in HTML 4.0.
 */
public interface HTMLMetaElement extends HTMLElement {
    /**
     *  Associated information. See the  content attribute definition in HTML 
     * 4.0.
     */
    public String getContent();
    public void setContent(String content);

    /**
     *  HTTP response header name. See the  http-equiv attribute definition in 
     * HTML 4.0.
     */
    public String getHttpEquiv();
    public void setHttpEquiv(String httpEquiv);

    /**
     *  Meta information name. See the  name attribute definition in HTML 4.0.
     */
    public String getName();
    public void setName(String name);

    /**
     *  Select form of content. See the  scheme attribute definition in HTML 
     * 4.0.
     */
    public String getScheme();
    public void setScheme(String scheme);

}

