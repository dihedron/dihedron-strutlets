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

package org.dihedron.strutlets.containers.portlet.liferay61x;

import org.dihedron.strutlets.containers.portlet.PortalServerPluginFactory;
import org.dihedron.strutlets.plugins.Plugin;
import org.dihedron.strutlets.plugins.Probe;

/**
 * @author Andrea Funto'
 */
public class Liferay61xFactory implements PortalServerPluginFactory {

	/**
	 * @see org.dihedron.strutlets.plugins.PluginFactory#makeProbe()
	 */
	@Override
	public Probe makeProbe() {
		return new Liferay61xProbe();
	}
	
	/**
	 * @see org.dihedron.strutlets.plugins.PluginFactory#makePlugin()
	 */
	@Override
	public Plugin makePlugin() {
		return (Plugin) new Liferay61x();
	}
}
