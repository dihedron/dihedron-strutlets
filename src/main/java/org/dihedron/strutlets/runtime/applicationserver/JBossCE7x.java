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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing the JBoss 7.x runtime environment.
 * 
 * @author Andrea Funto'
 */
public class JBossCE7x extends JBoss7x {
	
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(JBossCE7x.class);

	/**
	 * Returns the name of the JBoss Community Edition server.
	 */
	public String getName() {
		return "JBossAS Community Edition ver. 7.x";
	}
	
	/**
	 * Returns whether the actual application server the portlet container is running on 
	 * is JBoss 7.x, by trying to detect the existence of some classes.
	 */
	@Override
	public boolean isAppropriate() {
		if(super.isAppropriate()) {
			logger.info("runtime environment is JBoss 7.x+");
			return true;
		}
		return false;
	}
}
