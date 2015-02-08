/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.plugins;

/**
 * @author Andrea Funto'
 */
public interface Plugin {
	
	/**
	 * Returns the name of the <code>Plugin</code> instance.
	 * 
	 * @return
	 *   the name of the <code>Plugin</code> instance.
	 */	
	String getName();
	
	/**
	 * Initialises the <code>Plugin</code> object, and gets it ready for 
	 * providing services. This method needs not be reentrant, as it will be 
	 * called only once per instance.
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
