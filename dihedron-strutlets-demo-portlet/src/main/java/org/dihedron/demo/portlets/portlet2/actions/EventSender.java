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
package org.dihedron.demo.portlets.portlet2.actions;

import java.util.concurrent.atomic.AtomicInteger;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.exceptions.ActionException;

/**
 * @author Andrea Funto'
 *
 */
@Action
public class EventSender {

	private static AtomicInteger counter = new AtomicInteger(0);
	
	@Invocable(
		results = {
				@Result(value="success", data="/html/portlet2/view.jsp")
		}
	)
	public String sendEvent() throws ActionException {
		ActionContext.fireEvent("TestEvent", "http://www.dihedron.org/events", "event no. " + counter.getAndAdd(1));
		return Action.SUCCESS;
	}
}
