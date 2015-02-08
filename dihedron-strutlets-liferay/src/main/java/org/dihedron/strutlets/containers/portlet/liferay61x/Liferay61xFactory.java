/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.containers.portlet.liferay61x;

import org.dihedron.strutlets.containers.portlet.PortalServerPluginFactory;
import org.dihedron.strutlets.plugins.Plugin;
import org.dihedron.strutlets.plugins.Probe;

/**
 * @author Andrea Funto'
 */
public class Liferay61xFactory implements PortalServerPluginFactory {

	/**
	 * @see org.dihedron.strutlets.plugins.PluginFactory#makeProbe()
	 */
	@Override
	public Probe makeProbe() {
		return new Liferay61xProbe();
	}
	
	/**
	 * @see org.dihedron.strutlets.plugins.PluginFactory#makePlugin()
	 */
	@Override
	public Plugin makePlugin() {
		return (Plugin) new Liferay61x();
	}
}
