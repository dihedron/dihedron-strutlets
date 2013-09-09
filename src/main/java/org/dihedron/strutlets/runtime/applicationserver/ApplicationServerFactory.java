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
package org.dihedron.strutlets.runtime.applicationserver;

import javax.portlet.GenericPortlet;

import org.dihedron.strutlets.InitParameter;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 *
 */
public class ApplicationServerFactory {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ApplicationServerFactory.class);
	
	/**
	 * The current list of "stock" application server plugins.
	 */
	private ApplicationServer appservers[] = {
			new JBossEE6x(),
			new JBossCE7x(),
			new Tomcat7x()
	};
	
	/**
	 * Factory method: tries to load an application-server-specific plugin, by
	 * loading the class declared in the portlet.xml (if any) or alternatively 
	 * trying to detect if any of the existing "stock" plugins support the 
	 * current runtime environment.
	 * 
	 * @param portlet
	 *   the current portlet, to get the value of the 
	 *   <code>APPLICATION_SERVER_PLUGIN</code> initialisation parameter.
	 * @return
	 */
	public ApplicationServer makeApplicationServer(GenericPortlet portlet) {
		String classname = InitParameter.APPLICATION_SERVER_PLUGIN.getValueForPortlet(portlet);
		if(Strings.isValid(classname)) {			
			try {
				logger.trace("trying to load application server plugin '{}'...", classname);
				ApplicationServer appserver = (ApplicationServer) Class.forName(classname).newInstance();
				logger.trace("... application server plugin loaded!");
				return appserver;
			} catch (InstantiationException e) {
				logger.error("error instantiating application server plugin of class '" + classname + "'", e);
			} catch (IllegalAccessException e) {
				logger.error("security restriction apply to class '" + classname + "'", e);
			} catch (ClassNotFoundException e) {
				logger.error("declared application server plugin class '" + classname + "' not found on class path", e);
			}
		} 
		logger.trace("no valid application server plugin declared in portlet.xml, trying to auto-detect");
		for(ApplicationServer appserver : appservers) {
			logger.trace("checking if the {} plugin supports the current environment...", appserver.getName());			
			if(appserver.isAppropriate()) {
				logger.trace("... the {} plugin supports the current environment!", appserver.getName());
				return appserver;
			}
		}
		return null;
	}

}
