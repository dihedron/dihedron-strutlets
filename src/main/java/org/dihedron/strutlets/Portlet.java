/**
 * Copyright (c) 2012, 2013, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.strutlets;


/**
 * The controller portlet reference, for use whenever a method (in interceptors,
 * renderers or business methods) needs to access information pertaining to the 
 * portlet; this avoids having to drag the reference around.
 * 
 * @author Andrea Funto'
 */
public class Portlet {
	
    private static final ThreadLocal<ActionController> portlet = new ThreadLocal<ActionController>() {
    	
        @Override 
        protected ActionController initialValue() {
            return null;
        }
    };   
    
    public static ActionController get() {
    	return Portlet.portlet.get();
    }
    
    public static void set(ActionController portlet) {
    	Portlet.portlet.set(portlet);
    }
    
    public static void remove() {
    	Portlet.portlet.remove();
    }
}
