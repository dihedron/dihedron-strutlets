/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.adminconsole;




import org.dihedron.core.properties.Properties;
import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.ActionController;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.annotations.Scope;
import org.dihedron.strutlets.aop.$;
import org.dihedron.strutlets.targets.registry.TargetRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action(alias="StrutletsAdminConsole")
public class AdministrativeConsoleAction {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(AdministrativeConsoleAction.class);

	/**
	 * Constructor.
	 */
	
	@Invocable(
		idempotent = true,
		results = @Result(value = Action.SUCCESS, data = "/strutlets/admin/view.jsp")
	)
	@SuppressWarnings("deprecation")
	public String render(
		@Out(value="org.dihedron.strutlets:configuration", to = Scope.REQUEST) $<Properties> configuration,
		@Out(value="org.dihedron.strutlets:registry", to = Scope.REQUEST) $<TargetRegistry> registry
	) {
		ActionController controller = ActionContext.getActionController();
		
		logger.trace("storing the current configuration into REQUEST scope...");
		configuration.set(controller.getConfiguration());
		logger.trace("... done!");
		
		logger.trace("storing the current targets registry into REQUEST scope...");
		registry.set(controller.getTargetRegistry());
		logger.trace("... done!");
		
		return Action.SUCCESS;
	}
	
	
}
