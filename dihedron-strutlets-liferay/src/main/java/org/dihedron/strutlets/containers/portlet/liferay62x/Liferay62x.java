/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.containers.portlet.liferay62x;

import org.dihedron.strutlets.containers.portlet.liferay.Liferay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.ReleaseInfo;

/**
 * @author Andrea Funto'
 */
public class Liferay62x extends Liferay {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Liferay62x.class);

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
