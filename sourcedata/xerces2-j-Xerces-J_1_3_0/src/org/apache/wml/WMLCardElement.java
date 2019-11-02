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
 * <p>'card' element is the basic display unit of WML. A WML decks
 * contains a collection of cards.
 * (Section 11.5, WAP WML Version 16-Jun-1999)</p>
 *
 * @version $Id$
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */

public interface WMLCardElement extends WMLElement {

    /**
     * 'onenterbackward' specifies the event to occur when a user
     * agent into a card using a 'go' task
     * (Section 11.5.1, WAP WML Version 16-Jun-1999)
     */
    public void setOnEnterBackward(String href);
    public String getOnEnterBackward();

    /**
     * 'onenterforward' specifies the event to occur when a user
     * agent into a card using a 'prev' task
     * (Section 11.5.1, WAP WML Version 16-Jun-1999)
     */
    public void setOnEnterForward(String href);
    public String getOnEnterForward();

    /**
     * 'onenterbackward' specifies the event to occur when a timer expires
     * (Section 11.5.1, WAP WML Version 16-Jun-1999)
     */
    public void setOnTimer(String href);
    public String getOnTimer();

    /**
     * 'title' specifies a advisory info about the card
     * (Section 11.5.2, WAP WML Version 16-Jun-1999)
     */
    public void setTitle(String newValue);
    public String getTitle();

    /**
     * 'newcontext' specifies whether a browser context should be
     * re-initialized upon entering the card. Default to be false.
     * (Section 11.5.2, WAP WML Version 16-Jun-1999)
     */
    public void setNewContext(boolean newValue);
    public boolean getNewContext();
    
    /**
     *  'ordered' attribute specifies a hit to user agent about the
     *  organization of the card's content 
     * (Section 11.5.2, WAP WML Version 16-Jun-1999)
     */
    public void setOrdered(boolean newValue);
    public boolean getOrdered();

    /**
     * 'xml:lang' specifics the natural or formal language in which
     * the document is written.  
     * (Section 8.8, WAP WML Version 16-Jun-1999) 
     */
    public void setXmlLang(String newValue);
    public String getXmlLang();
}
