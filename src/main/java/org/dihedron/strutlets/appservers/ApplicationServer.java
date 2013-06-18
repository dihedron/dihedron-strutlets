/**
 * Copyright (c) 2013, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Crypto library ("Crypto").
 *
 * Crypto is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Crypto is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Crypto. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.strutlets.appservers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andrea Funto'
 */
public abstract class ApplicationServer {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ApplicationServer.class);

	/**
	 * Returns the current runtime environment, depending on the environment
	 * variables available and on other possible sources of information such
	 * as the presence of some classes in the class loader path etc.
	 * 
	 * @return
	 */
	public static ApplicationServer getApplicationServer() {
		
		// dump current environment
		Map<String, String> environment = System.getenv();		
		for(String key : environment.keySet()) {
			logger.info("'{}' := '{}'", key, environment.get(key));
		}
		
		// JBoss
		try {
			Class.forName("org.jboss.vfs.Vfs");
			logger.info("Strutlets running on JBoss");
			return new JBoss();			
		} catch (ClassNotFoundException e) {
			logger.info("Strutlets is not running on JBoss");
		}
		
		// implement other app servers here!
		return null;
	}
	
	/**
	 * Initialises the runtime environment depending on the current application 
	 * server. 
	 */
	public abstract void initialise();	
}
