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
package org.apache.html.dom;
    

import org.w3c.dom.DOMException;
import org.w3c.dom.html.*;
import org.apache.xerces.dom.DOMImplementationImpl;


/**
 * Provides number of methods for performing operations that are independent
 * of any particular instance of the document object model. This class is
 * unconstructable, the only way to obtain an instance of a DOM implementation
 * is by calling the static method {@link #getDOMImplementation}.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.DOMImplementation
 */
public class HTMLDOMImplementationImpl
    extends DOMImplementationImpl
    implements HTMLDOMImplementation
{


    /**
     * Holds a reference to the single instance of the DOM implementation.
     * Only one instance is required since this class is multiple entry.
     */
    private static HTMLDOMImplementation _instance = new HTMLDOMImplementationImpl();


    /**
     * Private constructor assures that an object of this class cannot
     * be created. The only way to obtain an object is by calling {@link
     * #getDOMImplementation}.
     */
    private HTMLDOMImplementationImpl()
    {
    }


    /**
     * Create a new HTML document of the specified <TT>TITLE</TT> text.
     *
     * @param title The document title text
     * @return New HTML document
     */
    public final HTMLDocument createHTMLDocument( String title )
        throws DOMException
    {
	HTMLDocument doc;

	if ( title == null )
	    throw new NullPointerException( "HTM014 Argument 'title' is null." );
	doc = new HTMLDocumentImpl();
	doc.setTitle( title );
	return doc;
    }


    /**
     * Returns an instance of a {@link HTMLDOMImplementation} that can be
     * used to perform operations that are not specific to a particular
     * document instance, e.g. to create a new document.
     *
     * @return Reference to a valid DOM implementation
     */
    public static HTMLDOMImplementation getHTMLDOMImplementation()
    {
	return _instance;
    }


}
