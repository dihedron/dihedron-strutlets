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

package org.dihedron.demo.portlets.portlet3.actions;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Event;
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
public class EventReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(EventReceiver.class);

	@Invocable(
		events = {
			@Event(value="TestEvent", namespace="http://www.dihedron.org/events")	
		},
		results = {
			@Result(value="success", data="/html/portlet3/event.jsp")
		}		
	)	
	public String onEvent() throws ActionException {
		logger.info("received event");
		String payload = (String)ActionContext.getEventPayload();
		ActionContext.setRenderParameter("EVENT_PAYLOAD", payload);
		return Action.SUCCESS;
	}
}
