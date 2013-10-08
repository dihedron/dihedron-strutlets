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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dihedron.strutlets.plugins.Probe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Tomcat70xProbe implements Probe {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Tomcat70xProbe.class);
	
	/**
	 * Constructor has package visibility to prevent construction by anyonw except 
	 * its plugin.
	 */
	Tomcat70xProbe() {		
	}
	
	/**
	 * @see org.dihedron.strutlets.plugins.Probe#isSupportedEnvironment()
	 */
	@Override
	public boolean isSupportedEnvironment() {
		boolean supported = false;
		try {
			logger.trace("trying to load Apache Tomcat specific classes");
			Class<?> clazz = Class.forName("org.apache.catalina.util.ServerInfo");
			Method getServerInfo = clazz.getMethod("getServerInfo");
			String identification = (String)getServerInfo.invoke(null);
			Method getServerNumber = clazz.getMethod("getServerNumber");
			String number = (String)getServerNumber.invoke(null);
			logger.trace("server info: '{}', server number: '{}'", identification, number);
			supported = identification.startsWith("Apache Tomcat") && number.startsWith("7.0.");
//			supported = true;
		} catch (ClassNotFoundException e) {
			logger.error("not running on Apache Tomcat", e);
//		} catch (InstantiationException e) {
//			logger.error("error instantiating object to retrieve server information", e);
		} catch (IllegalAccessException e) {
			logger.error("error accessing class for server information retrieval", e);
		} catch (NoSuchMethodException e) {
			logger.error("no method found on server info class", e);
		} catch (SecurityException e) {
			logger.error("security violation accessing server info class method", e);
		} catch (IllegalArgumentException e) {
			logger.error("invalid argument invoking server info class method", e);
		} catch (InvocationTargetException e) {
			logger.error("error invoking server info class method", e);
		}
		
//		Map<String, String> environment = System.getenv();
//		if(environment.containsKey("CATALINA_HOME")) {
//			String catalina = environment.get(TOMCAT_HOME);
//			if(catalina != null && catalina.toLowerCase().contains("tomcat")) {
//				logger.trace("application server is Apache Tomcat ver. 7.x");
//				return true;
//			}
//		}
//		logger.trace("application server is not Apache Tomcat ver. 7.x");
//		return false;
		return supported;
	}	
}
