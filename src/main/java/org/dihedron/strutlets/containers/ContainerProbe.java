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

package org.dihedron.strutlets.containers;


/**
 * The base interface of all container probes, whose task is that of detecting
 * whether the given runtime is available on the host server. Separating probe
 * and runtime support classes is necessary to ensure that when the runtime is
 * actually loaded no <code>UnsatisfiedLinkError</code> is thrown.
 *  
 * @author Andrea Funto'
 */
public interface ContainerProbe {

	/**
	 * Returns whether the given runtime is available on the host server; in order 
	 * to detect whether the container is appropriate, it must <em>not</em> link
	 * runtime classes through <code>import</code>, as this would result in
	 * <code>UnsatisfiedLinkError</code> on unsupporting container runtimes: it 
	 * should employ Java Reflection instead.
	 * 
	 * @return
	 *   whether the given container runtime is available on the host server.
	 */
	boolean isAvailable();
}
