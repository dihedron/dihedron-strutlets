/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.containers.web.tomcat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dihedron.strutlets.plugins.Probe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public abstract class TomcatProbe implements Probe {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(TomcatProbe.class);
	
	/**
	 * @see org.dihedron.strutlets.plugins.Probe#isSupportedEnvironment()
	 */
	@Override
	public boolean isSupportedEnvironment() {
		boolean supported = false;
		try {
			logger.trace("trying to load Apache Tomcat specific classes");
			Class<?> clazz = Class.forName("org.apache.catalina.util.ServerInfo");
			Method getServerInfo = clazz.getMethod("getServerInfo");
			String identification = (String)getServerInfo.invoke(null);
			Method getServerNumber = clazz.getMethod("getServerNumber");
			String number = (String)getServerNumber.invoke(null);
			logger.trace("server info: '{}', server number: '{}'", identification, number);
			supported = identification.startsWith(getReferenceName()) && number.startsWith(getReferenceVersion());
		} catch (ClassNotFoundException e) {
			logger.warn("not running on Apache Tomcat, please remove this plugin from your deployment to stop seeing this message");
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
	
	protected abstract String getReferenceName();
	
	protected abstract String getReferenceVersion();
}
