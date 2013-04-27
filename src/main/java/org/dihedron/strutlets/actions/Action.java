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

package org.dihedron.strutlets.actions;


import java.util.HashMap;
import java.util.Map;

import org.dihedron.reflection.Reflector;
import org.dihedron.reflection.ReflectorException;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.exceptions.ActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all <code>Action</code>s.
 * 
 * @author Andrea Funto'
 */
public abstract class Action {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Action.class);
	
	/**
	 * The default return code for a successful execution.
	 */
	public static final String SUCCESS = "success";
	
	/**
	 * The default return code to request more input data.
	 */
	public static final String INPUT = "input";
	
	/**
	 * The default return code for a failed execution.
	 */
	public static final String ERROR = "error";
	
	/**
	 * The map of configuration parameters.
	 */
	private Map<String, String> parameters = new HashMap<String, String>();
	
	/**
	 * Sets the action's initialisation parameters, according to
	 * what's defined in the input XML configuration file. 
	 * 
	 * @param parameters
	 *   a map of parameters.
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}
	
	/**
	 * Retrieves the value of a parameter from the initialisation
	 * paratemers map.
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @return
	 *   the parameter value.
	 */
	public String getParameter(String key) {
		return parameters.get(key);
	}
	
	/**
	 * Performs the actual method lookup and invocation.
	 * 
	 * @param method
	 *   the name of the method to be invoked.
	 * @return
	 *   the invocation result string, e.g. "success".
	 * @throws Exception
	 */
	public String invoke(String method) throws ActionException {
		String meth = method;		
		try {
			if(meth == null) {
				meth = Target.DEFAULT_METHOD_NAME;
			}
			logger.info("invoking method '{}'...", meth);
			Reflector helper = new Reflector(this);
			String result = (String)helper.invoke(meth);
			logger.info("... method '{}' invocation returned '{}'", meth, result);
			return result;
		} catch(ReflectorException e) {
			throw new ActionException("error invoking action", e);
		}
	}
	
	/**
	 * The default, do-nothing action method.
	 * 
	 * @return
	 *   the default "success" value.
	 * @throws Exception
	 */
	@Invocable
	public String execute() throws ActionException {
		// do nothing implementation
		return SUCCESS;
	}
}
