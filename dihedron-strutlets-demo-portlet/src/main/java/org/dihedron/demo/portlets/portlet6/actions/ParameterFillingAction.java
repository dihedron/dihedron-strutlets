/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.demo.portlets.portlet6.actions;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.exceptions.ActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 *
 */
@Action
public class ParameterFillingAction {
	
	private static final Logger logger = LoggerFactory.getLogger(ParameterFillingAction.class);

	
	@Invocable(
		idempotent = true,
		results = {
			@Result(value = Action.SUCCESS, data = "/html/portlet6/index.jsp")
		}
	)
	public String initView() throws ActionException {
		return Action.SUCCESS;
	}
	
	
	@Invocable(
		idempotent = true,
		results = {
			@Result(value = Action.SUCCESS, data = "/html/portlet6/view.jsp")
		}
	)
	public String fillParameters() throws ActionException {
		
		// render parameters
		ActionContext.setRenderParameter("render-parameter-1", "render-string-1");
		ActionContext.setRenderParameter("render-parameter-2", "render-string-1", "render-string-2", "render-string-3");
		
		String [] array = {" ciao", "mondo" };
		logger.trace("type of render-parameter-2: '{}'", array.getClass().getCanonicalName());
		
		// attributes in the session
		ActionContext.setRequestAttribute("request-attribute", new MyTestBean("request-string-1", 1));
		ActionContext.setPortletAttribute("portlet-attribute", new MyTestBean("portlet-string-1", 2));
		ActionContext.setApplicationAttribute("application-attribute", new MyTestBean("application-string-1", 3));
		
		return Action.SUCCESS;
	}
}
