/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999, 2000 The Apache Software Foundation.  All rights 
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

package org.apache.xerces.validators.datatype;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Enumeration;
import org.apache.xerces.validators.schema.SchemaSymbols;
import org.apache.xerces.utils.regex.RegularExpression;
import org.apache.xerces.validators.schema.SchemaSymbols;
import org.apache.xerces.validators.datatype.InvalidDatatypeFacetException;
/**
 *
 * BooleanValidator validates that content satisfies the W3C XML Datatype for Boolean
 *
 * @author Ted Leung 
 * @author Jeffrey Rodriguez
 * @version  $Id$
 */

public class BooleanDatatypeValidator extends AbstractDatatypeValidator {
    private Locale                  fLocale          = null;
    private DatatypeValidator       fBaseValidator   = null; //Basetype null means we are a native type
    private String                  fPattern         = null;
    private int                     fFacetsDefined   = 0;
    private DatatypeMessageProvider fMessageProvider = new DatatypeMessageProvider();
    private static  final String    fValueSpace[]    = { "false", "true", "0", "1"};
    private boolean                 fDerivedByList   = false;
    private RegularExpression       fRegex           = null;

    public BooleanDatatypeValidator () throws InvalidDatatypeFacetException {
       this( null, null, false ); // Native, No Facets defined, Restriction
    }

    public BooleanDatatypeValidator ( DatatypeValidator base, Hashtable facets,
                 boolean derivedByList ) throws InvalidDatatypeFacetException {
        setBasetype( base ); // Set base type 

        fDerivedByList = derivedByList;

        // Set Facets if any defined
        if ( facets != null  ) { 
            if ( derivedByList == false ) {
                for (Enumeration e = facets.keys(); e.hasMoreElements();) {
                    String key = (String) e.nextElement();

                    if (key.equals(SchemaSymbols.ELT_PATTERN)) {
                        fFacetsDefined += DatatypeValidator.FACET_PATTERN;
                        fPattern = (String)facets.get(key);
                        if( fPattern != null )
                           fRegex = new RegularExpression(fPattern, "X" );
                    } else {
                        throw new
                           InvalidDatatypeFacetException( 
                                "Only constraining facet in boolean datatype is PATTERN" );
                    }
                }
            } else { // By List

            }
        }// End of facet setting
    }


    /**
     * validate that a string matches the boolean datatype
     * @param content A string containing the content to be validated
     *
     * @exception throws InvalidDatatypeException if the content is
     * is not valid.
     */

    public Object validate(String content, Object state) throws InvalidDatatypeValueException {

        if ( fDerivedByList == true ) {
            ;// What does it mean?
        } else {
            checkContent( content );
        }
        return null;
    }


    /**
     * Compare two boolean data types
     * 
     * @param content1
     * @param content2
     * @return 
     */
    public int compare( String content1, String content2){
        return 0;
    }

    /**
     * Return a Hashtable that contains facet information
     * 
     * @return Hashtable
     */

    public Hashtable getFacets(){
        return null;
    }


    //Begin private method definitions

    /**
     * Sets the base datatype name.
     * 
     * @param base
     */

    private  void setBasetype(DatatypeValidator base) {
        fBaseValidator = base;
    }



    private String getErrorString(int major, int minor, Object args[]) {
        try {
            return fMessageProvider.createMessage(fLocale, major, minor, args);
        } catch (Exception e) {
            return "Illegal Errorcode "+minor;
        }
    }
  /**
     * Returns a copy of this object.
     */
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("clone() is not supported in "+this.getClass().getName());
    }


    /**
     * Checks content for validity.
     * 
     * @param content
     * @exception InvalidDatatypeValueException
     */
    private void checkContent( String content )throws InvalidDatatypeValueException {
        boolean  isContentInDomain = false;
        for ( int i = 0;i<fValueSpace.length;i++ ) {
            if ( content.equals(fValueSpace[i] ) )
                isContentInDomain = true;
        }
        if (isContentInDomain == false)
            throw new InvalidDatatypeValueException(
                                                   getErrorString(DatatypeMessageProvider.NotBoolean,
                                                                  DatatypeMessageProvider.MSG_NONE,
                                                                  new Object[] { content}));
        if ( (fFacetsDefined & DatatypeValidator.FACET_PATTERN ) != 0 ) {
            if ( fRegex == null || fRegex.matches( content) == false )
                throw new InvalidDatatypeValueException("Value'"+content+
                                                        "does not match regular expression facet" + fPattern );
        }
    }
}
