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
package org.dihedron.strutlets.runtime.portletcontainer;

import javax.portlet.GenericPortlet;

import org.dihedron.strutlets.InitParameter;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 *
 */
public class PortletContainerFactory {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PortletContainerFactory.class);
	
	/**
	 * The current list of "stock" application server plugins.
	 */
	private PortletContainer containers[] = {
			new LiferayCE61x(),
			new LiferayEE61x(),
	};
	
	/**
	 * Factory method: tries to load a portlet-container-specific plugin, by
	 * loading the class declared in the portlet.xml (if any) or alternatively 
	 * trying to detect if any of the existing "stock" plugins support the 
	 * current runtime environment.
	 * 
	 * @param portlet
	 *   the current portlet, to get the value of the 
	 *   <code>PORTLET_CONTAINER_PLUGIN</code> initialisation parameter.
	 * @return
	 */
	public PortletContainer makePortletContainer(GenericPortlet portlet) {
		String classname = InitParameter.PORTLET_CONTAINER_PLUGIN.getValueForPortlet(portlet);
		if(Strings.isValid(classname)) {			
			try {
				logger.trace("trying to load portlet container plugin '{}'...", classname);
				PortletContainer appserver = (PortletContainer) Class.forName(classname).newInstance();
				logger.trace("... portlet container plugin loaded!");
				return appserver;
			} catch (InstantiationException e) {
				logger.error("error instantiating portlet container plugin of class '" + classname + "'", e);
			} catch (IllegalAccessException e) {
				logger.error("security restriction apply to class '" + classname + "'", e);
			} catch (ClassNotFoundException e) {
				logger.error("declared portlet container plugin class '" + classname + "' not found on class path", e);
			}
		} 
		logger.trace("no valid application server plugin declared in portlet.xml, trying to auto-detect");
		for(PortletContainer container : containers) {
			logger.trace("checking if the {} plugin supports the current environment...", container.getName());			
			if(container.isAppropriate()) {
				logger.trace("... the {} plugin supports the current environment!", container.getName());
				return container;
			}
		}
		return null;
	}

}
