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

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.Interceptors;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.exceptions.ActionException;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Interceptors("default_02")
public class MyAction extends Action {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MyAction.class);


	/* (non-Javadoc)
	 * @see org.dihedron.portlets.actions.Action#execute()
	 */
	
	@Invocable (
		results= {
			@Result(value="success", data="/jsp/html/MyAction/execute_success.jsp"),
			@Result(value="error", data="/jsp/html/MyAction/execute_error.jsp"),
			@Result(value="input", data="/jsp/html/MyAction/execute_input.jsp")
		},
		events = {				
		}
	)	
	@Override public String execute() throws ActionException {
		logger.info("action!execute");
		return Action.SUCCESS;
	}
	
	@Invocable (
		results = {
			@Result(value="success", data="/jsp/html/MyAction/myMethod_success.jsp"),
			@Result(value="error", data="/jsp/html/MyAction/myMethod_error.jsp"),
			@Result(value="input", data="/jsp/html/MyAction/myMethod_input.jsp"),
			@Result(value="whatever")
		}
	)	
	public String myMethod() throws StrutletsException {
		logger.info("action!myMethod");
		return Action.SUCCESS;
	}

}
