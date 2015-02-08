/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.plugins;


/**
 * The base interface of all container probes, whose task is that of detecting
 * whether the given runtime is available on the host server. Separating probe
 * and runtime support classes is necessary to ensure that when the runtime is
 * actually loaded no <code>UnsatisfiedLinkError</code> is thrown.
 *  
 * @author Andrea Funto'
 */
public interface Probe {

	/**
	 * Returns whether the given <code>Pluggable</code> is supported on the 
	 * host environment; in order to detect whether the <code>Pluggable</code> 
	 * object can be run in the current hosting environment, it must <em>not</em> 
	 * link runtime classes through <code>import</code>, as this would result in
	 * class loading exceptins on unsupporting hosting environments: it should 
	 * employ Java Reflection instead.
	 * 
	 * @return
	 *   whether the current hosting environment is supported by this plugin.
	 */
	boolean isSupportedEnvironment();
}
