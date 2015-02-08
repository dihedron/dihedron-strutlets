/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.interceptors.impl;

import org.dihedron.strutlets.ActionInvocation;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Profiler extends Interceptor {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Profiler.class);

	/**
	 * Measures and prints out the time it takes to execute the nested interceptors 
	 * (if any) and the action.
	 * 
	 * @param invocation
	 *   the curent action invocation.
	 * @return
	 *   the result of the nested components' execution.
	 * @see 
	 *   org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws StrutletsException {
		long start = System.currentTimeMillis();
		String result = invocation.invoke();
		logger.debug("action execution took {} ms", System.currentTimeMillis() - start);
		return result;		
	}
}
