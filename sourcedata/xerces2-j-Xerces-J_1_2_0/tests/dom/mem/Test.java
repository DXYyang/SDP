/* $Id$ */
/*
 * The Apache Software License, Version 1.1
 * 
 * Copyright (c) 1999-2000 The Apache Software Foundation.  All rights 
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
 *    permission, please contact apache\@apache.org.
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
 * individuals on behalf of the Apache Software Foundation, and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com .  For more information
 * on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

//
//  Various DOM tests.
//     Contents include
//       1.  Basic functionality for DOMString
//       2.  Regression tests for bugs fixed.
//     All individual are wrapped in a memory leak checker.
//
//     This is NOT a complete test of DOM functionality.
//

package dom.mem;
import org.w3c.dom.*;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.NotationImpl;
import java.lang.reflect.*;
import dom.util.Assertion;

public class Test {

    /**
     * version 3.0 01/25/99
     * 
     * @return boolean
     * @param node java.lang.Object
     * @param mNameIndex int
     * @param signatureIndex int
     * @param parameters java.lang.Object[]
     * @param code short
     *
     * @author Philip W. Davis
     */
    public static boolean DOMExceptionsTest(Object node,
					    String methodName,
					    Class[] methodSignature,
					    Object[] parameters,
					    short code)
    {
	boolean asExpected = false;
	Method method;

	try {
	    method = node.getClass().getMethod(methodName,methodSignature);
	    method.invoke(node, parameters);
	} catch(InvocationTargetException exc) {
	    Throwable realE = exc.getTargetException(); 
	    if(realE instanceof DOMException) {
		asExpected = (((DOMException)realE).code== code);
		if(!asExpected)
		    System.out.println("Wrong DOMException(" +
				       ((DOMException)realE).code + ")");
	    } else {
		System.out.println("Wrong Exception (" + code + ")");
	    }
	    if(!asExpected) {
		System.out.println("Expected DOMException (" +
				   code + ") not thrown");
	    }
	} catch(Exception exc) {
	    System.out.println("test invocation failure (" + exc + ")");
	}
	return (asExpected);
    }

    public static void main(String argv[])
    {
    System.out.print("DOM Memory Test.\n");

    //
    //  Test Doc01      Create a new empty document
    //
    {
        Document    doc;
        doc = new DocumentImpl();
    }
    

    //
    //  Test Doc02      Create one of each kind of node using the
    //                  document createXXX methods.
    //                  Watch for memory leaks.
    //
    {
        //  Do all operations in a preconditioning step, to force the
        //  creation of implementation objects that are set up on first use.
        //  Don't watch for leaks in this block (no  / )
        Document doc = new DocumentImpl();
        Element     el = doc.createElement("Doc02Element");
        DocumentFragment frag = doc.createDocumentFragment ();
        Text  text = doc.createTextNode("Doc02TextNode");
        Comment comment = doc.createComment("Doc02Comment");
        CDATASection  cdataSec = doc.createCDATASection("Doc02CDataSection");
        DocumentType  docType = doc.getImplementation().createDocumentType("Doc02DocumentType", null, null);
        Notation notation = ((DocumentImpl) doc).createNotation("Doc02Notation");
        ProcessingInstruction pi = doc.createProcessingInstruction("Doc02PITarget",
                                    "Doc02PIData");
        NodeList    nodeList = doc.getElementsByTagName("*");
    }


    
    {
        Document doc = new DocumentImpl();
        Element     el = doc.createElement("Doc02Element");
    }
    

    
    {
        Document    doc = new DocumentImpl();
        DocumentFragment frag = doc.createDocumentFragment ();
    };
    


    
    {
        Document doc = new DocumentImpl();
        Element     el = doc.createElement("Doc02Element");
    }
    

    
    {
        Document doc = new DocumentImpl();
        Text  text = doc.createTextNode("Doc02TextNode");
    }
    

    
    {
        Document doc = new DocumentImpl();
        Comment comment = doc.createComment("Doc02Comment");
    }
    

    
    {
        Document doc = new DocumentImpl();
        CDATASection  cdataSec = doc.createCDATASection("Doc02CDataSection");
    }
    


    
    {
        Document doc = new DocumentImpl();
        DocumentType  docType = doc.getImplementation().createDocumentType("Doc02DocumentType", null, null);
    }
    


    
    {
        Document doc = new DocumentImpl();
        Notation notation = ((DocumentImpl)doc).createNotation("Doc02Notation");
    }
    


    
    {
        Document doc = new DocumentImpl();
        ProcessingInstruction pi = doc.createProcessingInstruction("Doc02PITarget",
                                    "Doc02PIData");
    }
    

    
    {
        Document doc = new DocumentImpl();
        Attr  attribute = doc.createAttribute("Doc02Attribute");
    }
    


    
    {
        Document doc = new DocumentImpl();
        EntityReference  er = doc.createEntityReference("Doc02EntityReference");
    }
    

    
    {
        Document doc = new DocumentImpl();
        NodeList    nodeList = doc.getElementsByTagName("*");
    }
    

    
    //
    //  Doc03 - Create a small document tree
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl = doc.createElement("Doc03RootElement");
        doc.appendChild(rootEl);
        Text        textNode = doc.createTextNode("Doc03 text stuff");
        rootEl.appendChild(textNode);

        NodeList    nodeList = doc.getElementsByTagName("*");
    };
    


    //
    //  Attr01
    //
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);
        {
            Attr        attr01  = doc.createAttribute("Attr01");
            rootEl.setAttributeNode(attr01);
        }
        
        
        {
            Attr attr02 = doc.createAttribute("Attr01");
            rootEl.setAttributeNode(attr02);  
        }
        
    };

    //
    //  Attr02
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);
        Attr        attr01  = doc.createAttribute("Attr02");
        rootEl.setAttributeNode(attr01);
        Attr        attr02 = doc.createAttribute("Attr02");
        rootEl.setAttributeNode(attr02);  
    }
    


    //
    //  Attr03
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);
        Attr        attr01  = doc.createAttribute("Attr03");
        rootEl.setAttributeNode(attr01);

        attr01.setValue("Attr03Value1");
        attr01.setValue("Attr03Value2");
    }
    



    //
    //  Text01
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     rootEl  = doc.createElement("RootElement");
        doc.appendChild(rootEl);


        Text        txt1 = doc.createTextNode("Hello Goodbye");
        rootEl.appendChild(txt1);

        txt1.splitText(6);
        rootEl.normalize();

    }
    


    //
    //  Notation01
    //
    
    { 
	/*
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        DocumentType    dt  =
	  impl.createDocumentType("DocType_for_Notation01", null, null, null);
        doc.appendChild(dt);


        NamedNodeMap notationMap = dt.getNotations();
        Notation    nt1 = ((DocumentImpl) doc).createNotation("Notation01");
        ((NotationImpl) nt1).setPublicId("Notation01PublicId");
        notationMap.setNamedItem (nt1);
        Notation    nt2 = (Notation)notationMap.getNamedItem("Notation01");
        Assertion.assert(nt1==nt2);
        nt2 = new NotationImpl((DocumentImpl)doc, null);
        nt1 = null;
        nt2 = (Notation)notationMap.getNamedItem("Notation01");
      
    */
    }
    


    //
    //  NamedNodeMap01 - comparison operators.
    //
    
    {
        NamedNodeMap    nnm = null;
        Assertion.assert(nnm == null);

        Document        doc = new DocumentImpl();
        nnm = doc.getAttributes();    // Should be null, because node type
                                      //   is not Element.
        Assertion.assert(nnm == null);
        Assertion.assert(!(nnm != null));

        Element el = doc.createElement("NamedNodeMap01");
        NamedNodeMap nnm2 = el.getAttributes();    // Should be an empty, but non-null map.
        Assertion.assert(nnm2 != null);
        Assertion.assert(nnm != nnm2);
        nnm = nnm2;
        Assertion.assert(nnm == nnm2);
    }
    


    //
    //  importNode quick test
    //
    
    {
        Document    doc1 = new DocumentImpl();
        Document    doc2 = new DocumentImpl();
        
        Element     el1  = doc1.createElement("abc");
        doc1.appendChild(el1);
        Assertion.assert(el1.getParentNode() != null);
        el1.setAttribute("foo", "foovalue");
        Node        el2  = doc2.importNode(el1, true);
        Assertion.assert(el2.getParentNode() == null);
        String       tagName = el2.getNodeName();
        Assertion.equals(tagName, "abc");
        Assertion.assert(el2.getOwnerDocument() == doc2);
        Assertion.equals(((Element) el2).getAttribute("foo"), "foovalue");
        Assertion.assert(doc1 != doc2);
    }
    

    //
    //  getLength() tests.  Both Node CharacterData and NodeList implement
    //                  getLength().  Early versions of the DOM had a clash
    //                  between the two, originating in the implementation class
    //                  hirearchy, which has NodeList as a (distant) base class
    //                  of CharacterData.  This is a regression test to verify
    //                  that the problem stays fixed.
    //
    
    {
        Document     doc = new DocumentImpl();
        Text          tx = doc.createTextNode("Hello");
        Element       el = doc.createElement("abc");
        el.appendChild(tx);

        int     textLength = tx.getLength();
        Assertion.assert(textLength == 5);

        NodeList      nl = tx.getChildNodes();
        int      nodeListLen = nl.getLength();
        Assertion.assert(nodeListLen == 0);

        nl = el.getChildNodes();
        nodeListLen = nl.getLength();
        Assertion.assert(nodeListLen == 1);
    }


    //
    //  NodeList - comparison operators, basic operation.
    //
    
    {
        NodeList    nl = null;
        NodeList    nl2 = null;
        Assertion.assert(nl == null);
        Assertion.assert(!(nl != null));
        Assertion.assert(nl == nl2);

        Document        doc = new DocumentImpl();
        nl = doc.getChildNodes();    // Should be non-null, but empty

        Assertion.assert(nl != null);
        int len = nl.getLength();
        Assertion.assert(len == 0);

        Element el = doc.createElement("NodeList01");
        doc.appendChild(el);
        len = nl.getLength();
        Assertion.assert(len == 1);
        Assertion.assert(nl != nl2);
        nl2 = nl;
        Assertion.assert(nl == nl2);
    }
    


 
    //
    //  Name validity checking.
    //
    
    {
         Document        doc = new DocumentImpl();
         Assertion.assert(DOMExceptionsTest(doc, "createElement",
					  new Class[]{String.class},
					  new Object[]{"!@@ bad element name"},
					  DOMException.INVALID_CHARACTER_ERR));
    }
    


    //
    //  Assignment ops return value
    //
    
    {
        Document        doc = new DocumentImpl();
        Element el = doc.createElement("NodeList01");
        doc.appendChild(el);
        
        Element n1, n2, n3;
        
        n1 = n2 = n3 = el;
        Assertion.assert(n1 == n2);
        Assertion.assert(n1 == n3);
        Assertion.assert(n1 == el);
        Assertion.assert(n1 != null);
        n1 = n2 = n3 = null;
        Assertion.assert(n1 == null);
    }
    


    //
    //  Cloning of a node with attributes. Regression test for a ref counting 
    //  bug in attributes of cloned nodes that occured when the "owned" flag
    //  was not set in the clone.
    //
    
    {
        Document    doc = new DocumentImpl();
        Element     root = doc.createElement("CTestRoot");
        root.setAttribute("CTestAttr", "CTestAttrValue");

        String s = root.getAttribute("CTestAttr");
        Assertion.equals(s, "CTestAttrValue");

        Element     cloned = (Element)root.cloneNode(true);
        Attr a = cloned.getAttributeNode("CTestAttr");
        Assertion.assert(a != null);
        s = a.getValue();
        Assertion.equals(s, "CTestAttrValue");
        a = null;

        a = cloned.getAttributeNode("CTestAttr");
        Assertion.assert(a != null);
        s = a.getValue();
        Assertion.equals(s, "CTestAttrValue");

    }
    


    //
    //  DOM Level 2 tests.  These should be split out as a separate test.
    //


    //
    // hasFeature.  The set of supported options tested here is for Xerces 1.1
    //
    
    {
        DOMImplementation  impl = DOMImplementationImpl.getDOMImplementation();
        Assertion.assert(impl.hasFeature("XML", "2.0")    == true);
        Assertion.assert(impl.hasFeature("XML", null)       == true);
        //  We also support 1.0
        Assertion.assert(impl.hasFeature("XML", "1.0")    == true);
        Assertion.assert(impl.hasFeature("XML", "3.0")    == false);
        Assertion.assert(impl.hasFeature("Traversal", null) == true);


        Assertion.assert(impl.hasFeature("HTML", null)           == false);
        Assertion.assert(impl.hasFeature("Views", null)          == false);
        Assertion.assert(impl.hasFeature("StyleSheets", null)    == false);
        Assertion.assert(impl.hasFeature("CSS", null)            == false);
        Assertion.assert(impl.hasFeature("CSS2", null)           == false);
        Assertion.assert(impl.hasFeature("Events", null)         == true);
        Assertion.assert(impl.hasFeature("UIEvents", null)       == false);
        Assertion.assert(impl.hasFeature("MouseEvents", null)    == false);
        Assertion.assert(impl.hasFeature("MutationEvents", null) == true);
        Assertion.assert(impl.hasFeature("HTMLEvents", null)     == false);
        Assertion.assert(impl.hasFeature("Range", null)          == false);
    }
    


    //
    // CreateDocumentType
    //
    
    {
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        Assertion.assert(dt != null);
        Assertion.assert(dt.getNodeType() == Node.DOCUMENT_TYPE_NODE);
        Assertion.equals(dt.getNodeName(), qName);
        Assertion.assert(dt.getNamespaceURI() == null);
        Assertion.assert(dt.getPrefix() == null);
        Assertion.assert(dt.getLocalName() == null);
        Assertion.equals(dt.getPublicId(), pubId);
        Assertion.equals(dt.getSystemId(), sysId);
        Assertion.assert(dt.getInternalSubset() == null);
        Assertion.assert(dt.getOwnerDocument() == null);
        
        NamedNodeMap nnm = dt.getEntities();
        Assertion.assert(nnm.getLength() == 0);
        nnm = dt.getNotations();
        Assertion.assert(nnm.getLength() == 0);

        //
        // Qualified name without prefix should also work.
        //
        qName = "docName";
        dt = impl.createDocumentType(qName, pubId, sysId);

        Assertion.assert(dt != null);
        Assertion.assert(dt.getNodeType() == Node.DOCUMENT_TYPE_NODE);
        Assertion.equals(dt.getNodeName(), qName);
        Assertion.assert(dt.getNamespaceURI() == null);
        Assertion.assert(dt.getPrefix() == null);
        Assertion.assert(dt.getLocalName() == null);
        Assertion.equals(dt.getPublicId(), pubId);
        Assertion.equals(dt.getSystemId(), sysId);
        Assertion.assert(dt.getInternalSubset() == null);
        Assertion.assert(dt.getOwnerDocument() == null);

        // Creating a DocumentType with invalid or malformed qName should fail.
        Assertion.assert(DOMExceptionsTest(impl, "createDocumentType",
			new Class[]{String.class, String.class, String.class},
			new Object[]{"<docName", pubId, sysId},
			DOMException.INVALID_CHARACTER_ERR));     
        Assertion.assert(DOMExceptionsTest(impl, "createDocumentType",
			new Class[]{String.class, String.class, String.class},
			new Object[]{":docName", pubId, sysId},
			DOMException.NAMESPACE_ERR));     
        Assertion.assert(DOMExceptionsTest(impl, "createDocumentType",
			new Class[]{String.class, String.class, String.class},
			new Object[]{"docName:", pubId, sysId},
			DOMException.NAMESPACE_ERR));     
        Assertion.assert(DOMExceptionsTest(impl, "createDocumentType",
			new Class[]{String.class, String.class, String.class},
			new Object[]{"<doc::Name", pubId, sysId},
			DOMException.INVALID_CHARACTER_ERR));     
        Assertion.assert(DOMExceptionsTest(impl, "createDocumentType",
			new Class[]{String.class, String.class, String.class},
			new Object[]{"<doc:N:ame", pubId, sysId},
			DOMException.INVALID_CHARACTER_ERR));     
    }

    //
    //  DOMImplementation.CreateDocument
    //
    
    {
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
        Document doc = impl.createDocument(docNSURI, qName, dt);

        Assertion.assert(dt.getOwnerDocument() == doc);
        Assertion.assert(doc.getOwnerDocument() == null);

        Assertion.assert(doc.getNodeType() == Node.DOCUMENT_NODE);
        Assertion.assert(doc.getDoctype() == dt);
        Assertion.equals(doc.getNodeName(), "#document");
        Assertion.assert(doc.getNodeValue() == null);

        Element el = doc.getDocumentElement();

        Assertion.equals(el.getLocalName(), "docName");
        Assertion.equals(el.getNamespaceURI(), docNSURI);
        Assertion.equals(el.getNodeName(), qName);
        Assertion.assert(el.getOwnerDocument() == doc);
        Assertion.assert(el.getParentNode() == doc);
        Assertion.equals(el.getPrefix(), "foo");
        Assertion.equals(el.getTagName(), qName);
        Assertion.assert(el.hasChildNodes() == false);

        //
        // Creating a second document with the same docType object should fail.
        //
        Assertion.assert(DOMExceptionsTest(impl, "createDocument",
					   new Class[]{String.class,
						       String.class,
						       DocumentType.class},
					   new Object[]{docNSURI, qName, dt},
					   DOMException.WRONG_DOCUMENT_ERR));

        // Namespace tests of createDocument are covered by createElementNS below
    }
    
    
    //
    //  CreateElementNS methods
    //
    
    {
        
        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
	Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // CreateElementNS
        //
        Element ela = doc.createElementNS("http://nsa", "a:ela");  // prefix and URI
        Element elb = doc.createElementNS("http://nsb", "elb");    //  URI, no prefix.
        Element elc = doc.createElementNS(null, "elc");              // No URI, no prefix.

        rootEl.appendChild(ela);
        rootEl.appendChild(elb);
        rootEl.appendChild(elc);

        Assertion.equals(ela.getNodeName(), "a:ela");
        Assertion.equals(ela.getNamespaceURI(), "http://nsa");
        Assertion.equals(ela.getPrefix(), "a");
        Assertion.equals(ela.getLocalName(), "ela");
        Assertion.equals(ela.getTagName(), "a:ela");

        Assertion.equals(elb.getNodeName(), "elb");
        Assertion.equals(elb.getNamespaceURI(), "http://nsb");
        Assertion.assert(elb.getPrefix() == null);
        Assertion.equals(elb.getLocalName(), "elb");
        Assertion.equals(elb.getTagName(), "elb");

        Assertion.equals(elc.getNodeName(), "elc");
        Assertion.assert(elc.getNamespaceURI() == null);
        Assertion.assert(elc.getPrefix() ==  null);
        Assertion.equals(elc.getLocalName(), "elc");
        Assertion.equals(elc.getTagName(), "elc");

        // Badly formed qualified name
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "<a"},
				      DOMException.NAMESPACE_ERR));
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", ":a"},
				      DOMException.NAMESPACE_ERR));
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "a:"},
				      DOMException.NAMESPACE_ERR));
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "a::a"},
				      DOMException.NAMESPACE_ERR));
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "a:a:a"},
				      DOMException.NAMESPACE_ERR));

        // xml:a must have namespaceURI == "http://www.w3.org/XML/1998/namespace"
	String xmlURI = "http://www.w3.org/XML/1998/namespace";
	Assertion.equals(doc.createElementNS(xmlURI, "xml:a").getNamespaceURI(), xmlURI);
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "xml:a"},
				      DOMException.NAMESPACE_ERR));
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"", "xml:a"},
				      DOMException.NAMESPACE_ERR));
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{null, "xml:a"},
				      DOMException.NAMESPACE_ERR));

        //unlike Attribute, xmlns (no different from foo) can have any namespaceURI for Element
        Assertion.equals(doc.createElementNS("http://nsa", "xmlns").getNamespaceURI(), "http://nsa");
        Assertion.equals(doc.createElementNS(xmlURI, "xmlns").getNamespaceURI(), xmlURI);
        Assertion.equals(doc.createElementNS("", "xmlns").getNamespaceURI(), "");
        Assertion.assert(doc.createElementNS(null, "xmlns").getNamespaceURI() == null);

        //unlike Attribute, xmlns:a (no different from foo:a) can have any
	// namespaceURI for Element except null
        Assertion.equals(doc.createElementNS("http://nsa", "xmlns:a").getNamespaceURI(), "http://nsa");
        Assertion.equals(doc.createElementNS(xmlURI, "xmlns:a").getNamespaceURI(), xmlURI);
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{null, "xmlns:a"},
				      DOMException.NAMESPACE_ERR));

        //In fact, any prefix != null should have a namespaceURI != null
        Assertion.equals(doc.createElementNS("http://nsa", "foo:a").getNamespaceURI(), "http://nsa");
	Assertion.assert(DOMExceptionsTest(doc, "createElementNS",
				      new Class[]{String.class, String.class},
				      new Object[]{null, "foo:a"},
				      DOMException.NAMESPACE_ERR));

        //Change prefix
        Element elem = doc.createElementNS("http://nsa", "foo:a");
        elem.setPrefix("bar");
        Assertion.equals(elem.getNodeName(), "bar:a");
        Assertion.equals(elem.getNamespaceURI(), "http://nsa");
        Assertion.equals(elem.getPrefix(), "bar");
        Assertion.equals(elem.getLocalName(), "a");
        Assertion.equals(elem.getTagName(), "bar:a");
        //The spec does not prevent us from setting prefix to a node without prefix
        elem = doc.createElementNS("http://nsa", "a");
        Assertion.equals(elem.getPrefix(), null);
        elem.setPrefix("bar");
        Assertion.equals(elem.getNodeName(), "bar:a");
        Assertion.equals(elem.getNamespaceURI(), "http://nsa");
        Assertion.equals(elem.getPrefix(), "bar");
        Assertion.equals(elem.getLocalName(), "a");
        Assertion.equals(elem.getTagName(), "bar:a");
        //Special case for xml:a where namespaceURI must be xmlURI
        elem = doc.createElementNS(xmlURI, "foo:a");
        elem.setPrefix("xml");
        elem = doc.createElementNS("http://nsa", "foo:a");
        Assertion.assert(DOMExceptionsTest(elem, "setPrefix",
					  new Class[]{String.class},
					  new Object[]{"xml"},
					  DOMException.NAMESPACE_ERR));
        //However, there is no restriction on prefix xmlns
        elem.setPrefix("xmlns");
        //Also an element can not have a prefix with namespaceURI == null
        elem = doc.createElementNS(null, "a");
        Assertion.assert(DOMExceptionsTest(elem, "setPrefix",
					  new Class[]{String.class},
					  new Object[]{"foo"},
					  DOMException.NAMESPACE_ERR));

        //Only prefix of Element and Attribute can be changed
        Assertion.assert(DOMExceptionsTest(doc, "setPrefix",
					  new Class[]{String.class},
					  new Object[]{"foo"},
					  DOMException.NAMESPACE_ERR));

        //Prefix of readonly Element can not be changed.
        //However, there is no way to create such Element for testing yet.
    }
    



    //
    //  CreateAttributeNS methods
    //
    
    {
        
        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
        Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // CreateAttributeNS
        //
        Attr attra = doc.createAttributeNS("http://nsa", "a:attra");       // prefix and URI
        Attr attrb = doc.createAttributeNS("http://nsb", "attrb");         //  URI, no prefix.
        Attr attrc = doc.createAttributeNS(null, "attrc");    // No URI, no prefix.

        Assertion.equals(attra.getNodeName(), "a:attra");
        Assertion.equals(attra.getNamespaceURI(), "http://nsa");
        Assertion.equals(attra.getPrefix(), "a");
        Assertion.equals(attra.getLocalName(), "attra");
        Assertion.equals(attra.getName(), "a:attra");
        Assertion.assert(attra.getOwnerElement() == null);

        Assertion.equals(attrb.getNodeName(), "attrb");
        Assertion.equals(attrb.getNamespaceURI(), "http://nsb");
        Assertion.equals(attrb.getPrefix(), null);
        Assertion.equals(attrb.getLocalName(), "attrb");
        Assertion.equals(attrb.getName(), "attrb");
        Assertion.assert(attrb.getOwnerElement() == null);

        Assertion.equals(attrc.getNodeName(), "attrc");
        Assertion.assert(attrc.getNamespaceURI() == null);
        Assertion.assert(attrc.getPrefix() == null);
        Assertion.equals(attrc.getLocalName(), "attrc");
        Assertion.equals(attrc.getName(), "attrc");
        Assertion.assert(attrc.getOwnerElement() == null);


        // Badly formed qualified name
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "<a"},
				      DOMException.INVALID_CHARACTER_ERR));
	Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", ":a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "a:"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "a::a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "a:a:a"},
				      DOMException.NAMESPACE_ERR));

        // xml:a must have namespaceURI == "http://www.w3.org/XML/1998/namespace"
        String xmlURI = "http://www.w3.org/XML/1998/namespace";
        Assertion.equals(doc.createAttributeNS(xmlURI, "xml:a").getNamespaceURI(), xmlURI);
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "xml:a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"", "xml:a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{null,  "xml:a"},
				      DOMException.NAMESPACE_ERR));

        //unlike Element, xmlns must have namespaceURI == "http://www.w3.org/2000/xmlns/"
        String xmlnsURI = "http://www.w3.org/2000/xmlns/";
        Assertion.equals(doc.createAttributeNS(xmlnsURI, "xmlns").getNamespaceURI(), xmlnsURI);
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "xmlns"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{xmlURI, "xmlns"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"", "xmlns"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{null,  "xmlns"},
				      DOMException.NAMESPACE_ERR));

        //unlike Element, xmlns:a must have namespaceURI == "http://www.w3.org/2000/xmlns/"
        Assertion.equals(doc.createAttributeNS(xmlnsURI, "xmlns:a").getNamespaceURI(), xmlnsURI);
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"http://nsa", "xmlns:a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{xmlURI, "xmlns:a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"", "xmlns:a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{null,  "xmlns:a"},
				      DOMException.NAMESPACE_ERR));

        //In fact, any prefix != null should have a namespaceURI != null
        Assertion.equals(doc.createAttributeNS("http://nsa", "foo:a").getNamespaceURI(), "http://nsa");
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{"", "foo:a"},
				      DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(doc, "createAttributeNS",
				      new Class[]{String.class, String.class},
				      new Object[]{null,  "foo:a"},
				      DOMException.NAMESPACE_ERR));

        //Change prefix
        Attr attr = doc.createAttributeNS("http://nsa", "foo:a");
        attr.setPrefix("bar");
        Assertion.equals(attr.getNodeName(), "bar:a");
        Assertion.equals(attr.getNamespaceURI(), "http://nsa");
        Assertion.equals(attr.getPrefix(), "bar");
        Assertion.equals(attr.getLocalName(), "a");
        Assertion.equals(attr.getName(), "bar:a");
        //The spec does not prevent us from setting prefix to a node without prefix
        attr = doc.createAttributeNS("http://nsa", "a");
        Assertion.assert(attr.getPrefix() == null);
        attr.setPrefix("bar");
        Assertion.equals(attr.getNodeName(), "bar:a");
        Assertion.equals(attr.getNamespaceURI(), "http://nsa");
        Assertion.equals(attr.getPrefix(), "bar");
        Assertion.equals(attr.getLocalName(), "a");
        Assertion.equals(attr.getName(), "bar:a");
        //Special case for xml:a where namespaceURI must be xmlURI
        attr = doc.createAttributeNS(xmlURI, "foo:a");
        attr.setPrefix("xml");
        attr = doc.createAttributeNS("http://nsa", "foo:a");
        Assertion.assert(DOMExceptionsTest(attr, "setPrefix",
					   new Class[]{String.class},
					   new Object[]{"xml"},
					   DOMException.NAMESPACE_ERR));
        //Special case for xmlns:a where namespaceURI must be xmlURI
        attr = doc.createAttributeNS(xmlnsURI, "foo:a");
        attr.setPrefix("xmlns");
        attr = doc.createAttributeNS("http://nsa", "foo:a");
        Assertion.assert(DOMExceptionsTest(attr, "setPrefix",
					   new Class[]{String.class},
					   new Object[]{"xmlns"},
					   DOMException.NAMESPACE_ERR));
        //Special case for xmlns where no prefix can be set
        attr = doc.createAttributeNS(xmlnsURI, "xmlns");
        Assertion.assert(DOMExceptionsTest(attr, "setPrefix",
					   new Class[]{String.class},
					   new Object[]{"xml"},
					   DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(attr, "setPrefix",
					   new Class[]{String.class},
					   new Object[]{"foo"},
					   DOMException.NAMESPACE_ERR));
        Assertion.assert(DOMExceptionsTest(attr, "setPrefix",
					   new Class[]{String.class},
					   new Object[]{"xmlns"},
					   DOMException.NAMESPACE_ERR));
        //Also an attribute can not have a prefix with namespaceURI == null
        attr = doc.createAttributeNS(null, "a");
        Assertion.assert(DOMExceptionsTest(attr, "setPrefix",
					   new Class[]{String.class},
					   new Object[]{"foo"},
					   DOMException.NAMESPACE_ERR));
        
        //Only prefix of Element and Attribute can be changed
        Assertion.assert(DOMExceptionsTest(attr, "setPrefix",
					   new Class[]{String.class},
					   new Object[]{"foo"},
					   DOMException.NAMESPACE_ERR));

        //Prefix of readonly Attribute can not be changed.
        //However, there is no way to create such DOM_Attribute for testing yet.
    }
    

    //
    //  getElementsByTagName*
    //
    
    {
        
        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
	Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // Populate the document
        //
        Element ela = doc.createElementNS("http://nsa", "a:ela");  
        rootEl.appendChild(ela);
        Element elb = doc.createElementNS("http://nsb", "elb");   
        rootEl.appendChild(elb);
        Element elc = doc.createElementNS(null,           "elc");  
        rootEl.appendChild(elc);
        Element eld = doc.createElementNS("http://nsa", "d:ela");
        rootEl.appendChild(eld);
        Element ele = doc.createElementNS("http://nse", "elb");   
        rootEl.appendChild(ele);


        // 
        // Access with DOM Level 1 getElementsByTagName
        //

        NodeList nl = doc.getElementsByTagName("a:ela");
        Assertion.assert(nl.getLength() == 1);
        Assertion.assert(nl.item(0) == ela);

        nl = doc.getElementsByTagName("elb");
        Assertion.assert(nl.getLength() == 2);
        Assertion.assert(nl.item(0) == elb);
        Assertion.assert(nl.item(1) == ele);

        nl = doc.getElementsByTagName("d:ela");
        Assertion.assert(nl.getLength() == 1);
        Assertion.assert(nl.item(0) == eld);

        //
        //  Access with DOM Level 2 getElementsByTagNameNS
        //

        nl = doc.getElementsByTagNameNS(null, "elc");
        Assertion.assert(nl.getLength() == 1);
        Assertion.assert(nl.item(0) == elc);
       
        nl = doc.getElementsByTagNameNS("http://nsa", "ela");
        Assertion.assert(nl.getLength() == 2);
        Assertion.assert(nl.item(0) == ela);
        Assertion.assert(nl.item(1) == eld);

        nl = doc.getElementsByTagNameNS(null, "elb");
        Assertion.assert(nl.getLength() == 0);

        nl = doc.getElementsByTagNameNS("http://nsb", "elb");
        Assertion.assert(nl.getLength() == 1);
        Assertion.assert(nl.item(0) == elb);

        nl = doc.getElementsByTagNameNS("*", "elb");
        Assertion.assert(nl.getLength() == 2);
        Assertion.assert(nl.item(0) == elb);
        Assertion.assert(nl.item(1) == ele);

        nl = doc.getElementsByTagNameNS("http://nsa", "*");
        Assertion.assert(nl.getLength() == 2);
        Assertion.assert(nl.item(0) == ela);
        Assertion.assert(nl.item(1) == eld);

        nl = doc.getElementsByTagNameNS("*", "*");
        Assertion.assert(nl.getLength() == 6);     // Gets the document root element, plus 5 more

        Assertion.assert(nl.item(6) == null);
        // Assertion.assert(nl.item(-1) == 0);

        nl = rootEl.getElementsByTagNameNS("*", "*");
        Assertion.assert(nl.getLength() == 5);


        nl = doc.getElementsByTagNameNS("http://nsa", "d:ela");
        Assertion.assert(nl.getLength() == 0);


        //
        // Node lists are Live
        //

        nl = doc.getElementsByTagNameNS("*", "*");
        NodeList nla = ela.getElementsByTagNameNS("*", "*");

        Assertion.assert(nl.getLength() == 6); 
        Assertion.assert(nla.getLength() == 0);

        rootEl.removeChild(elc);
        Assertion.assert(nl.getLength() == 5);
        Assertion.assert(nla.getLength() == 0);

        ela.appendChild(elc);
        Assertion.assert(nl.getLength() == 6);
        Assertion.assert(nla.getLength() == 1);
    }


   //
    // Attributes and NamedNodeMaps.
    //
    {

        // Set up an initial (root element only) document.
        // 
        DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
        
        String qName = "foo:docName";
        String pubId = "pubId";
        String sysId = "http://sysId";
        DocumentType dt = impl.createDocumentType(qName, pubId, sysId);
        
        String docNSURI = "http://document.namespace";
        Document doc = impl.createDocument(docNSURI, qName, dt);
        Element rootEl = doc.getDocumentElement();

        //
        // Create a set of attributes and hang them on the root element.
        //
        Attr attra = doc.createAttributeNS("http://nsa", "a:attra");  
        rootEl.setAttributeNodeNS(attra);
        Attr attrb = doc.createAttributeNS("http://nsb", "attrb");   
        rootEl.setAttributeNodeNS(attrb);
        Attr attrc = doc.createAttributeNS(null,           "attrc");  
        rootEl.setAttributeNodeNS(attrc);
        Attr attrd = doc.createAttributeNS("http://nsa", "d:attra");
        rootEl.setAttributeNodeNS(attrd);
        Attr attre = doc.createAttributeNS("http://nse", "attrb");   
        rootEl.setAttributeNodeNS(attre);

        //
        // Check that the attribute nodes were created with the correct properties.
        //
        Assertion.equals(attra.getNodeName(), "a:attra");
        Assertion.equals(attra.getNamespaceURI(), "http://nsa");
        Assertion.equals(attra.getLocalName(), "attra");
        Assertion.equals(attra.getName(), "a:attra");
        Assertion.assert(attra.getNodeType() == Node.ATTRIBUTE_NODE);
        Assertion.equals(attra.getNodeValue(), "");
        Assertion.equals(attra.getPrefix(), "a");
        Assertion.assert(attra.getSpecified() == true);
        Assertion.equals(attra.getValue(), "");
        Assertion.assert(attra.getOwnerElement() == null);

        // Test methods of NamedNodeMap
        NamedNodeMap nnm = rootEl.getAttributes();
        Assertion.assert(nnm.getLength() == 4);
        Assertion.assert(nnm.getNamedItemNS("http://nsa", "attra") == attrd);
        Assertion.assert(nnm.getNamedItemNS("http://nsb", "attrb") == attrb);
        Assertion.assert(nnm.getNamedItemNS("http://nse", "attrb") == attre);
        Assertion.assert(nnm.getNamedItemNS(null, "attrc") == attrc);
        Assertion.assert(nnm.getNamedItemNS(null, "attra") == null);
        Assertion.assert(nnm.getNamedItemNS("http://nsa", "attrb") == null);
    }
    };
}    
