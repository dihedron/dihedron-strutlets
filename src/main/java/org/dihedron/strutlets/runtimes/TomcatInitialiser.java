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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing the TomcatInitialiser runtime environment.
 * 
 * @author Andrea Funto'
 */
public class TomcatInitialiser extends RuntimeInitialiser {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(TomcatInitialiser.class);

	/**
	 * Performs TomcatInitialiser-specific initialisation tasks.
	 * 
	 * @see org.dihedron.strutlets.runtimes.RuntimeInitialiser#initialise()
	 */
	public void initialise() {
		logger.debug("initialising TomcatInitialiser runtime environment...");
	}
}
