/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.containers.web.jbossas7x;

import org.dihedron.strutlets.containers.web.ApplicationServerPluginFactory;
import org.dihedron.strutlets.plugins.Plugin;
import org.dihedron.strutlets.plugins.Probe;

/**
 * The JBossAS-specific plugin (factory class).
 * 
 * @author Andrea Funto'
 */
public class JBossAS7xFactory implements ApplicationServerPluginFactory {

	@Override
	public Probe makeProbe() {
		return new JBossAS7xProbe();
	}
	
	@Override
	public Plugin makePlugin() {
		return new JBossAS7x();
	}
}
