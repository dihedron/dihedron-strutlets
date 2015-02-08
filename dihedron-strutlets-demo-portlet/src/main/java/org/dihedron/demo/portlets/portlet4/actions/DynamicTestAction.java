/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
