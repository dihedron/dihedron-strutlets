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

import org.dihedron.strutlets.containers.web.WebContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing the Tomcat runtime environment.
 * 
 * @author Andrea Funto'
 */
public class Tomcat7x implements WebContainer {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Tomcat7x.class);
	
	/**
	 * Constructor has package visibility to prevent construction by anyone except 
	 * its plugin.
	 */
	Tomcat7x() {		
	}

	/**
	 * Returns the label of the Apache Tomcat Application Server.
	 * 
	 * @return
	 *   the label of the Apache Tomcat Application Server.
	 */
	@Override
	public String getName() {
		return "Apache Tomcat ver. 7.0.x";
	}
	
	/**
	 * Performs Tomcat-specific initialisation tasks.
	 * 
	 * @see org.dihedron.strutlets.containers.web.WebContainer#initialise()
	 */
	public boolean initialise() {
		logger.debug("initialising Tomcat 7.x runtime environment...");
		return true;
	}

	/**
	 * No Tomcat-specific cleanup tasks.
	 */
	@Override
	public void cleanup() {
	}
}
