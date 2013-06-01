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

package org.dihedron.strutlets;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.portlet.PortletModeException;
import javax.portlet.WindowStateException;
import javax.xml.namespace.QName;

import org.dihedron.strutlets.actions.PortletMode;
import org.dihedron.strutlets.actions.WindowState;
import org.dihedron.strutlets.exceptions.InvalidPhaseException;

/**
 * A view on the portal functionalities available in the business ("action" 
 * and "event") phases. Some functionalities, those that are more closely related 
 * to event handling, are only available in the "event" phase.  
 * 
 * @author Andrea Funto'
 */
public final class BusinessContext extends ActionContextImpl {

	/**
	 * Fires an event, for inter-portlet communication.
	 * 
	 * @param name
	 *   the fully-qualified name of the event.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	public static void fireEvent(String name, Serializable payload) throws InvalidPhaseException {
		ActionContextImpl.fireEvent(name, payload);
	}
	
	/**
	 * Fires an event, for inter-portlet communication.
	 * 
	 * @param name
	 *   the name of the event.
	 * @param namespace
	 *   the event namespace.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	public static void fireEvent(String name, String namespace, Serializable payload) throws InvalidPhaseException {
		ActionContextImpl.fireEvent(name, namespace, payload);
	}
	
	/**
	 * Fires an event, for inter-portlet communication.
	 * 
	 * @param qname 
	 *   an object representing the fully-qualified name of the event.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	public static void fireEvent(QName qname, Serializable payload) throws InvalidPhaseException {
		ActionContextImpl.fireEvent(qname, payload);
	}
	
	/**
	 * In case of an <code>EventRequest</code>, returns the name of the event.
	 * 
	 * @return
	 *   the name of the event.
	 * @throws InvalidPhaseException
	 *   if invoked out of the "event" phase. 
	 */
	public static String getEventName() throws InvalidPhaseException {
		return ActionContextImpl.getEventName();
	}
	
	/**
	 * In case of an <code>EventRequest</code>, returns the <code>QName</code>
	 * of the event.
	 * 
	 * @return
	 *   the <code>QName</code> of the event.
	 * @throws InvalidPhaseException
	 *   if invoked out of the "event" phase. 
	 */
	public static QName getEventQName() throws InvalidPhaseException {
		return ActionContextImpl.getEventQName();
	}
	
	/**
	 * In case of an <code>EventRequest</code>, returns the serializable payload
	 * of the event.
	 * 
	 * @return
	 *   the serializable payload of the event.
	 * @throws InvalidPhaseException
	 *   if invoked out of the "event" phase. 
	 */
	public static Serializable getEventPayload() throws InvalidPhaseException {
		return ActionContextImpl.getEventPayload();
	}
	
	/**
	 * Sets the current portlet mode; it is preferable not to use this method
	 * directly and let the framework set the portlet mode instead, by
	 * specifying it in the action's results settings.
	 *  
	 * @param mode
	 *   the new portlet mode.
	 * @throws PortletModeException
	 *   if the new portlet mode is not supported by the current portal server 
	 *   runtime environment. 
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	@Deprecated
	public static void setPortletMode(PortletMode mode) throws PortletModeException, InvalidPhaseException {
		ActionContextImpl.setPortletMode(mode);
	}
	
	/**
	 * Sets the current window state; it is preferable not to use this method
	 * directly and let the framework set the portlet window state instead, by
	 * specifying it in the action's results settings.
	 *  
	 * @param state
	 *   the new window state.
	 * @throws WindowStateException
	 *   if the new window state is not supported by the current portal server 
	 *   runtime environment. 
	 * @throws InvalidPhaseException
	 *   if the operation is attempted in the render phase. 
	 */
	@Deprecated
	public static void setWindowState(WindowState state) throws WindowStateException, InvalidPhaseException {
		ActionContextImpl.setWindowState(state);
	}
	
	/**
	 * Redirects to a different URL, with no referrer URL unless it is specified 
	 * in the URL itself. 
	 * 
	 * @param url
	 *   the URL to redirect the browser to (via a 302 HTTP status response).
	 * @throws IOException
	 *   if the redirect operation fails.
	 * @throws InvalidPhaseException 
	 *   if the method is invoked out of the "action" phase.
	 */
	public static void sendRedirect(String url) throws IOException, InvalidPhaseException {
		ActionContextImpl.sendRedirect(url);
	}
	
	/**
	 * Redirects to a different URL, adding a referrer to provide a "back" address 
	 * to the destination page.
	 * 
	 * @param url
	 *   the URL to redirect the browser to (via a 302 HTTP status response).
	 * @param referrer
	 *   the referrer URL, to provide a "back" link.
	 * @throws IOException
	 *   if the redirect operation fails.
	 * @throws InvalidPhaseException 
	 *   if the method is invoked out of the "action" phase.
	 */
	public static void sendRedirect(String url, String referrer) throws IOException, InvalidPhaseException {
		ActionContextImpl.sendRedirect(url, referrer);
	}
	
	/**
	 * Returns the map of currently set render parameters; it can only be invoked 
	 * while in the action and event phases.
	 * 
	 * @return
	 *   a map of render parameters names an values, or null if unsupported by 
	 *   the current type of request/response.
	 * @throws InvalidPhaseException 
	 *   if invoked out of the "action" and "event" phases.
	 */
	public static Map<String, String[]> getRenderParameterMap() throws InvalidPhaseException {
		return ActionContextImpl.getRenderParameterMap();
	}
	
	/**
	 * This method allows an action in the "action phase" or in the "event phase"
	 * to set render parameters, making them available to the following render 
	 * phase.
	 * 
	 * @param key
	 *   the name of the parameter to set.
	 * @param values
	 *   the set of values to be set under the given key. 
	 * @throws InvalidPhaseException
	 *   if invoked out of the "action" and "event" phases.
	 */
	public static void setRenderParameter(String key, String... values) throws InvalidPhaseException {
		ActionContextImpl.setRenderParameter(key, values);
	}
	
	/**
	 * Private constructor, to prevent instantiation.
	 */
	private BusinessContext() {
	}
}
