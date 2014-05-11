/**
 * Copyright (c) 2012-2014, Andrea Funto'. All rights reserved.
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

package org.dihedron.demo.portlets.portlet4.actions;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action
public class DynamicTestAction {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(DynamicTestAction.class);

	@Invocable (
		idempotent = true,
		results = {
			@Result(value = "success", data = "/html/portlet4/view.jsp")
		}
	)
	public String showHome() {
		logger.info("going to show ordinary home page!");
		return Action.SUCCESS;
	}

	@Invocable
	public String execute() {
		String result = (String)ActionContext.getFirstParameterValue("result");
		logger.debug("default method is requested to return '{}'", result);
		if(result.equalsIgnoreCase("success")) {
			return Action.SUCCESS;
		} else if(result.equalsIgnoreCase("error")) {
			return Action.ERROR;
		}
		logger.warn("result '{}' is unsupported by default method", result);
		return Action.ERROR;
	}
	
	@Invocable
	public String myMethod() {
		String result = (String)ActionContext.getFirstParameterValue("result");
		logger.debug("test method is requested to return '{}'", result);
		if(result.equalsIgnoreCase("success")) {
			return Action.SUCCESS;
		} else if(result.equalsIgnoreCase("error")) {
			return Action.ERROR;
		}
		logger.warn("result '{}' is unsupported by test method", result);
		return Action.ERROR;
	}
}
