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
 * The base interface of runtime plugins: they must provide a factory method to
 * create a runtime probe, which will probably used Java Reflection or other
 * defices to detect whether the given runtime is available on the class path, 
 * and the actual runtime implementation, which will expose its business methods.
 *   
 * @author Andrea Funto'
 */
public interface ContainerPlugin {
	
	/**
	 * Creates a new <code>Container</code> object.
	 * 
	 * @return
	 *   a new <code>Container</code> object.
	 */
	Container makeContainer();
	
	/**
	 * Creates a new <code>ContainerProbe</code> object, which will employ Java
	 * Reflection and other artifices to detect if the given runtime can be 
	 * instantiated. The probe is a lightweight dependency in that it does not
	 * require the availability of its supporting classes, it will simply sniff 
	 * for their availability, and only if so proceed to actual <code>Container</code>
	 * instantiation.
	 * 
	 * @return
	 *   a <code>ContainerProbe</code> object instance.
	 */
	ContainerProbe makeContainerProbe();
}
