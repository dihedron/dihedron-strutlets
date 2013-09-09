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

package org.dihedron.strutlets.containers.web.jbossas7x;

import org.dihedron.strutlets.containers.ContainerProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class JBossAS7xProbe implements ContainerProbe {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(JBossAS7xProbe.class);

	/**
	 * Constructor has package visibility to prevent construction by anyonw except 
	 * its plugin.
	 */
	JBossAS7xProbe() {		
	}
	
	/**
	 * Returns whether the actual application server the portlet container is running on 
	 * is JBoss 7.x, by trying to detect the existence of some classes.
	 * 
	 * @see org.dihedron.strutlets.containers.ContainerProbe#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		try {
			Class.forName("org.reflections.vfs.Vfs");
			logger.trace("application server is JBossAS 7.x+");
			return true;		
		} catch (ClassNotFoundException e) {
			logger.trace("application server is not JBossAS 7.x+");
		}
		return false;
	}
}
