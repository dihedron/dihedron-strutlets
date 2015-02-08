/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.containers.web.tomcat7x;

import org.dihedron.strutlets.containers.web.ApplicationServerPluginFactory;
import org.dihedron.strutlets.plugins.Plugin;
import org.dihedron.strutlets.plugins.Probe;

/**
 * @author Andrea Funto'
 */
public class Tomcat70xFactory implements ApplicationServerPluginFactory {

	/**
	 * @see org.dihedron.strutlets.plugins.PluginFactory#makeProbe()
	 */
	@Override
	public Probe makeProbe() {
		return new Tomcat70xProbe();
	}
	
	/**
	 * @see org.dihedron.strutlets.plugins.PluginFactory#makePlugin()
	 */
	@Override
	public Plugin makePlugin() {
		return new Tomcat70x();
	}
}
