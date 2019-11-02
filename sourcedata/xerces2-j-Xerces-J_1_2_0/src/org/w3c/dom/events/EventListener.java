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

package org.w3c.dom.events;

/**
 *  The <code>EventListener</code> interface is the primary method for 
 * handling events. Users implement the <code>EventListener</code> interface 
 * and register their listener on an <code>EventTarget</code> using the 
 * <code>AddEventListener</code> method. The users should also remove their 
 * <code>EventListener</code> from its <code>EventTarget</code> after they 
 * have completed using the listener. 
 * @since DOM Level 2
 */
public interface EventListener {
    /**
     *  This method is called whenever an event occurs of the type for which 
     * the <code>EventListener</code> interface was registered. 
     * @param evt  The <code>Event</code> contains contextual information 
     *   about the event. It also contains the <code>stopPropagation</code> 
     *   and  <code>preventDefault</code> methods which are used in 
     *   determining the event's flow and default action. 
     */
    public void handleEvent(Event evt);

}

