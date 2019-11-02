/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.wml;

/**
 * <p>The interface is modeled after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from 
 * <a href="http://www.wapforum.org/DTD/wml_1.1.xml">
 * http://www.wapforum.org/DTD/wml_1.1.xml</a></p>
 *
 * <p>'input' element specifies a text entry object.
 * (Section 11.6.3, WAP WML Version 16-Jun-1999)</p>
 *
 * @version $Id$
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */

public interface WMLInputElement extends WMLElement {

    /**
     * 'name' specifies the name of a variable after the user enters the text.
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setName(String newValue);
    public String getName();

    /**
     * 'value' specifies the default value of the variable in 'name' attribute
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setValue(String newValue);
    public String getValue();

    /**
     * 'type' specifies the type of text input area. 
     * Two values are allowed: 'text' and 'password' and default is 'text'
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setType(String newValue);
    public String getType();

    /**
     * 'format' specifies the input mask for user input.
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setFormat(String newValue);
    public String getFormat();

    /**
     * 'emptyok' specifies whether a empty input is allowed when a
     * non-empty 'format' is specified. Default to be 'false'
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setEmptyOk(boolean newValue);
    public boolean getEmptyOk();

    /**
     * 'size' specifies the width of the input in characters
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setSize(int newValue);
    public int getSize();

    /**
     * 'maxlength' specifies the maximum number of characters to be
     * enter.
     * (Section 11.6.3, WAP WML Version 16-Jun-1999) 
     */
    public void setMaxLength(int newValue);
    public int getMaxLength();

    /**
     * 'title' specifies a title for this element
     * (Section 11.6.3, WAP WML Version 16-Jun-1999) 
     */
    public void setTitle(String newValue);
    public String getTitle();

    /**
     * 'tabindex' specifies the tabbing position of the element
     * (Section 11.6.1, WAP WML Version 16-Jun-1999)
     */
    public void setTabIndex(int newValue);
    public int getTabIndex();

    /**
     * 'xml:lang' specifics the natural or formal language in which
     * the document is written.  
     * (Section 8.8, WAP WML Version 16-Jun-1999) 
     */
    public void setXmlLang(String newValue);
    public String getXmlLang();
}
