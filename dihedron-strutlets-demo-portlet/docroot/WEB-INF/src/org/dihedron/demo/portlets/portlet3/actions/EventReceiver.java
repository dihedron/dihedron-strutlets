/**
 * 
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
