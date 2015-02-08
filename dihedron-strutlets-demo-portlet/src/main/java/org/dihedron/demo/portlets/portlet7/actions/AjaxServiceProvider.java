/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.demo.portlets.portlet7.actions;

import org.dihedron.demo.portlets.portlet6.actions.MyTestBean;
import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action(alias="AjaxAction")
public class AjaxServiceProvider {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(AjaxServiceProvider.class);

	
	@Invocable(
		results = {
			@Result( value = Action.SUCCESS, renderer = "json", data = "myBean")
		}
	)
	public String serveResource( @In("resourceId") String resourceId ) {
		logger.trace("trying to serve resource");
		ActionContext.setRequestAttribute("myBean", new MyTestBean("you requested: '" + resourceId + "'", 100));
		return Action.SUCCESS;
	}
}
