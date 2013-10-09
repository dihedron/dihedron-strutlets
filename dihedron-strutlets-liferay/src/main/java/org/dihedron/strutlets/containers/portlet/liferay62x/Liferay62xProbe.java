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

package org.dihedron.strutlets.containers.portlet.liferay62x;

import org.dihedron.strutlets.containers.portlet.liferay.LiferayProbe;

/**
 * @author Andrea Funto'
 */
public class Liferay62xProbe extends LiferayProbe {

	/**
	 * Constructor has package visibility to prevent improper construction.
	 */
	Liferay62xProbe() {
	}

	/**
	 * @see org.dihedron.strutlets.containers.portlet.liferay.LiferayProbe#getReferenceName()
	 */
	@Override
	protected String getReferenceName() {
		return "Liferay Portal";
	}

	/**
	 * @see org.dihedron.strutlets.containers.portlet.liferay.LiferayProbe#getReferenceVersion()
	 */
	@Override
	protected String getReferenceVersion() {
		return "6.2.";
	}

}
