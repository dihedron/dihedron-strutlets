/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
