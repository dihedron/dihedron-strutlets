/**
 * Copyright (c) 2012, 2013, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.strutlets.test;

import org.dihedron.strutlets.ActionInvocation;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class MyInterceptor extends Interceptor {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MyInterceptor.class);
	
	/**
	 * @see org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws StrutletsException {
		logger.trace("interceptor {} - before", getParameter("id"));
		String result = invocation.invoke();
		logger.trace("interceptor {} - after", getParameter("id"));
		return result;
	}
}