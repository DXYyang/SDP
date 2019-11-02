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

package org.apache.poi.hpsf;

/**
 * <p>This exception is the superclass of all other checked exceptions
 * thrown in this package. It supports a nested "reason" throwable,
 * i.e. an exception that caused this one to be thrown.</p>
 *
 * @author Rainer Klute (klute@rainer-klute.de)
 * @version $Id$
 * @since 2002-02-09
 */
public class HPSFException extends Exception
{

    private Throwable reason;



    /**
     * <p>Creates a new {@link HPSFException}.</p>
     */
    public HPSFException()
    {
        super();
    }



    /**
     * <p>Creates a new {@link HPSFException} with a message
     * string.</p>
     *
     * @param msg The message string.
     */
    public HPSFException(final String msg)
    {
        super(msg);
    }



    /**
     * <p>Creates a new {@link HPSFException} with a reason.</p>
     *
     * @param reason The reason, i.e. a throwable that indirectly
     * caused this exception.
     */
    public HPSFException(final Throwable reason)
    {
        super();
        this.reason = reason;
    }



    /**
     * <p>Creates a new {@link HPSFException} with a message string
     * and a reason.</p>
     *
     * @param msg The message string.
     * @param reason The reason, i.e. a throwable that indirectly
     * caused this exception.
     */
    public HPSFException(final String msg, final Throwable reason)
    {
        super(msg);
        this.reason = reason;
    }



    /**
     * <p>Returns the {@link Throwable} that caused this exception to
     * be thrown or <code>null</code> if there was no such {@link
     * Throwable}.</p>
     *
     * @return The reason
     */
    public Throwable getReason()
    {
        return reason;
    }

}
