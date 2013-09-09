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

import org.dihedron.strutlets.containers.Container;
import org.dihedron.strutlets.containers.ContainerPlugin;
import org.dihedron.strutlets.containers.ContainerProbe;

/**
 * @author Andrea Funto'
 */
public class Tomcat7xPlugin implements ContainerPlugin {

	/**
	 * @see org.dihedron.strutlets.containers.ContainerPlugin#makeContainer()
	 */
	@Override
	public Container makeContainer() {
		return new Tomcat7x();
	}

	/**
	 * @see org.dihedron.strutlets.containers.ContainerPlugin#makeContainerProbe()
	 */
	@Override
	public ContainerProbe makeContainerProbe() {
		return new Tomcat7xProbe();
	}
}
