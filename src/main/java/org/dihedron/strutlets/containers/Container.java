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

package org.dihedron.strutlets.containers;


/**
 * @author Andrea Funto'
 */
public interface Container {
	
	/**
	 * Returns the name of the runtime instance.
	 * 
	 * @return
	 *   the name of the runtie intance.
	 */	
	String getName();
	
	/**
	 * Initialises the plugin, and gets it ready for providing services. This
	 * method needs not be reentrant, as it will be called only once per instance
	 * (each portlet will get its own instance, and a portlet is a de-facto 
	 * singleton per portlet container).
	 *  
	 * @return
	 *   <code>true</code> if the initialisation succeeded, <code>false</code>
	 *   otherwise.
	 */	
	boolean initialise();
	
	/**
	 * Cleans up any resources that might have been created or allocated at
	 * initialisation time.
	 */	
	void cleanup();
}
