/**
 * Copyright (c) 2012, 2014, Andrea Funto'. All rights reserved.
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
