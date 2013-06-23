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
package org.dihedron.strutlets.runtimes;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andrea Funto'
 */
public abstract class RuntimeEnvironment {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RuntimeEnvironment.class);

	/**
	 * Factory method returning the current runtime environment, depending on 
	 * the environment variables available and on other possible sources of 
	 * information such as the presence of some classes in the class loader path 
	 * etc.
	 * 
	 * @return
	 *   an instance of RuntimeEnvironment, for the caller to perform application
	 *   server-specific initialisation.
	 */
	public static RuntimeEnvironment getRuntimeEnvironment() {
		
		// dump current environment
		Map<String, String> environment = System.getenv();
		StringBuilder buffer = new StringBuilder("runtime environment:\n");
		for(String key : environment.keySet()) {
			buffer.append("\t- '").append(key).append("' = '").append(environment.get(key)).append("'\n");
		}
		logger.trace(buffer.toString());
		
		if(isJBoss()) {
			return new JBoss();			
		} else if(isTomcat()) {
			return new Tomcat();
		} else {
			// TODO: implement other app servers here
		}
		
		// TODO: implement other app servers here!
		return null;
	}
	
	/**
	 * Returns whether the current runtime environment is JBoss application server
	 * version 7.X or later, based on the presence of class <code>Vfs</code> on 
	 * the classpath.
	 *  
	 * @return
	 *   whether the current runtime is JBoss application server.
	 */
	public static boolean isJBoss() {
		// JBoss
		try {
			Class.forName("org.reflections.vfs.Vfs");
			logger.info("runtime environment is JBoss 7.x+");
			return true;		
		} catch (ClassNotFoundException e) {
			logger.info("runtime environment is not JBoss 7.x+");
		}
		return false;
	}
	
	/**
	 * Returns whether the current runtime environment is Tomcat web container.
	 * 
	 * @return
	 *   whether the current runtime environment is Tomcat web container.
	 */
	public static boolean isTomcat() {
		// TODO: implement
		return true;
	}
	
	/**
	 * Initialises the runtime environment depending on the current application 
	 * server. 
	 */
	public abstract void initialise();	
}
