/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.containers.web.jbossas7x;

import org.dihedron.strutlets.plugins.Probe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class JBossAS7xProbe implements Probe {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(JBossAS7xProbe.class);
	
	/**
	 * Constructor has package visibility to prevent construction by anyone except 
	 * its plugin.
	 */
	JBossAS7xProbe() {		
	}
	
	/**
	 * Returns whether the actual application server the portlet container is running on 
	 * is JBoss 7.x, by trying to detect the existence of some classes.
	 * 
	 * @see org.dihedron.strutlets.plugins.Probe#isAvailable()
	 */
	@Override
	public boolean isSupportedEnvironment() {
		try {
			Class.forName("org.reflections.vfs.Vfs");
			logger.trace("application server is JBossAS 7.x+");
			return true;		
		} catch (ClassNotFoundException e) {
			logger.warn("not running on JBoss AS, please remove this plugin from your deployment to stop seeing this message");
		}
		return false;
	}
}
