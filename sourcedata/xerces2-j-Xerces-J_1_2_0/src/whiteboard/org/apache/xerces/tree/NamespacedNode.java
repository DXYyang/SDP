/*
 * $Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
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
 * 4. The names "Crimson" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Sun Microsystems, Inc., 
 * http://www.sun.com.  For more information on the Apache Software 
 * Foundation, please see <http://www.apache.org/>.
 */

package org.apache.xerces.tree;

import org.w3c.dom.*;
import org.apache.xerces.tree.XmlNames;

/**
 * This adds the notion of namespaces to the ParentNode class for certain
 * types of nodes such as DOM Element and Attr nodes.
 *
 * @author Edwin Goei
 * @version $Revision$
 */
abstract class NamespacedNode extends ParentNode {
    // Here "name" corresponds to "QName" in the XML Namespaces REC
    protected String name;
    protected String namespaceURI;

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.  This is not a computed value.
     *
     * @since DOM Level 2
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @since DOM Level 2
     */
    public String getPrefix() {
        return XmlNames.getPrefix(name);
    }

    /**
     * Sets the namespace prefix of this node.
     *
     * @since DOM Level 2
     */
    public void setPrefix(String prefix) throws DOMException {
        if (readonly) {
            throw new DomEx(DomEx.NO_MODIFICATION_ALLOWED_ERR);
        }

	int index = name.indexOf(':');

        // prefix == null implies reset to no default namespace
	if (prefix == null) {
	    if (index >= 0) {
	    	name = name.substring(index + 1);
            }
	    return;
	}

        // Replace or add new prefix
   	StringBuffer tmp = new StringBuffer(prefix);
	tmp.append(':');
	if (index < 0 ) {
	    tmp.append(name);
	} else {
	    tmp.append(name.substring(index + 1));
        }
	name = tmp.toString();
    }

    /**
     * Returns the local part of the qualified name of this node.
     *
     * @since DOM Level 2
     */
    public String getLocalName() {
        return XmlNames.getLocalPart(name);
    }
}
