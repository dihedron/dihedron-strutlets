/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.containers.portlet.liferay;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dihedron.strutlets.plugins.Probe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public abstract class LiferayProbe implements Probe {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(LiferayProbe.class);

	/**
	 * @see org.dihedron.strutlets.plugins.Probe#isAvailable()
	 */
	@Override
	public boolean isSupportedEnvironment() {
		boolean supported = false;
		try {
			logger.trace("trying to load Liferay specific classes");
			Class<?> clazz = Class.forName("com.liferay.portal.kernel.util.ReleaseInfo");
			Method getName = clazz.getMethod("getName");
			String name = (String)getName.invoke(null);
			Method getVersion = clazz.getMethod("getVersion");
			String version = (String)getVersion.invoke(null);
			logger.trace("container name: '{}', version: '{}'", name, version);
			supported = name.startsWith(getReferenceName()) && version.startsWith(getReferenceVersion());
		} catch (ClassNotFoundException e) {
			logger.warn("not running on Liferay Portal, please remove this plugin from your deployment to stop seeing this message");
		} catch (IllegalAccessException e) {
			logger.error("error accessing class for server information retrieval", e);
		} catch (NoSuchMethodException e) {
			logger.error("no method found on server info class", e);
		} catch (SecurityException e) {
			logger.error("security violation accessing server info class method", e);
		} catch (IllegalArgumentException e) {
			logger.error("invalid argument invoking server info class method", e);
		} catch (InvocationTargetException e) {
			logger.error("error invoking server info class method", e);
		}
		return supported;
	}
	
	/**
	 * Implemented by subclasses to return the reference name against which the
	 * container-provided name will be compared: this way we're factoring out the
	 * common logic and leaving to version-specific probes the task of providing
	 * their name.
	 * 
	 * @return
	 *   the name of the version-specific portlet container.
	 */
	protected abstract String getReferenceName();

	/**
	 * Implemented by subclasses to return the reference version against which the
	 * container-provided version will be compared: this way we're factoring out the
	 * common logic and leaving to version-specific probes the task of providing
	 * their version number.
	 * 
	 * @return
	 *   the version number of the version-specific portlet container.
	 */
	protected abstract String getReferenceVersion();
}
