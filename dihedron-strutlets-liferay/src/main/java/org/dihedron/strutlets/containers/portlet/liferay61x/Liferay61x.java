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

import org.dihedron.strutlets.containers.portlet.liferay.Liferay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.ReleaseInfo;

/**
 * @author Andrea Funto'
 */
public class Liferay61x extends Liferay {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Liferay61x.class);

	@Override
	public String getName() {		
		return ReleaseInfo.getServerInfo();
	}

	/**
	 * @see org.dihedron.strutlets.containers.portlet.PortalServer#initialise()
	 */
	@Override
	public boolean initialise() {
		logger.trace("initialising Liferay 61x plugin");
		return true;
	}

	/**
	 * @see org.dihedron.strutlets.containers.portlet.PortalServer#cleanup()
	 */
	@Override
	public void cleanup() {
		// TODO: implement
	}
}
