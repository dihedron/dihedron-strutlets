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

import java.util.Map;

import org.dihedron.strutlets.containers.ContainerProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Tomcat7xProbe implements ContainerProbe {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Tomcat7xProbe.class);

	/**
	 * The name of a system environment variable holding the path to Tomcat's 
	 * home directory.
	 */
	private static final String TOMCAT_HOME = "CATALINA_HOME";
	
	/**
	 * Constructor has package visibility to prevent construction by anyonw except 
	 * its plugin.
	 */
	Tomcat7xProbe() {		
	}
	
	/**
	 * @see org.dihedron.strutlets.containers.ContainerProbe#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		Map<String, String> environment = System.getenv();
		if(environment.containsKey("CATALINA_HOME")) {
			String catalina = environment.get(TOMCAT_HOME);
			if(catalina != null && catalina.toLowerCase().contains("tomcat")) {
				logger.trace("application server is Apache Tomcat ver. 7.x");
				return true;
			}
		}
		logger.trace("application server is not Apache Tomcat ver. 7.x");
		return false;
	}	
}
