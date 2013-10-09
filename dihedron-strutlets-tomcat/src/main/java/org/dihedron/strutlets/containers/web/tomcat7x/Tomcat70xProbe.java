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

package org.dihedron.strutlets.containers.web.tomcat7x;

import org.dihedron.strutlets.containers.web.tomcat.TomcatProbe;

/**
 * @author Andrea Funto'
 */
public class Tomcat70xProbe extends TomcatProbe {
	
	/**
	 * Constructor has package visibility to prevent construction by anyonw except 
	 * its plugin.
	 */
	Tomcat70xProbe() {		
	}

	/**
	 * @see org.dihedron.strutlets.containers.web.tomcat.TomcatProbe#getReferenceName()
	 */
	@Override
	protected String getReferenceName() {
		return "Apache Tomcat";
	}

	/**
	 * @see org.dihedron.strutlets.containers.web.tomcat.TomcatProbe#getReferenceVersion()
	 */
	@Override
	protected String getReferenceVersion() {
		return "7.0.";
	}
	
}
