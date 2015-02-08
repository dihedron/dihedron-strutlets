/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.plugins;


/**
 * The base interface of plugins: they must provide a factory method to create a 
 * probe, which will be used to detect whether the given plugin is supported under
 * the current conditions; the probe must not statically link any resources which
 * might not be available at runtime (e.g. some application-server-specific JARs 
 * and classes), it should resort to Reflection based methods to asses whether 
 * the necessary runtime components would be available if the plugin were to be
 * used.
 * Plugins must provide another factory method to instantiate the actual
 * <code>pluggable</code> object, which is allowed to statically link classes of
 * since the runtime is supposed to be availble at this stage, after the probe
 * has been run.
 *   
 * @author Andrea Funto'
 */
public interface PluginFactory {

	/**
	 * Creates a new <code>Probe</code> object, which will employ Java Reflection 
	 * and other artifices to detect if the given <code>Plugin</code>
	 * business object can be instantiated. The probe is a lightweight dependency 
	 * in that it does not require the availability of its supporting classes, 
	 * it will simply sniff for their availability, and only if so will the
	 * <code>PluginManager</code> proceed to actual <code>Plugin</code> 
	 * instantiation.
	 * 
	 * @return
	 *   a <code>Probe</code> object instance.
	 */
	Probe makeProbe();
	
	/**
	 * Creates a new <code>Plugin</code> object.
	 * 
	 * @return
	 *   a new <code>Plugin</code> object.
	 */
	Plugin makePlugin();
}
