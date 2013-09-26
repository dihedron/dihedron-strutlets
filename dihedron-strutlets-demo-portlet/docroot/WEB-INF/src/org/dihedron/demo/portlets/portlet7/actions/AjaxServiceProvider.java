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
