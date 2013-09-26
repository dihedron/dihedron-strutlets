/**
 * 
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
