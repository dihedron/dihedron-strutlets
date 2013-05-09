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
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowStateException;
import javax.servlet.http.Cookie;
import javax.xml.namespace.QName;

import org.dihedron.strutlets.actions.PortletMode;
import org.dihedron.strutlets.actions.WindowState;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object provides mediated access to the underlying JSR-286 features, such
 * as session, parameters, remote user information and the like.
 * 
 * @author Andrea Funto'
 */
public final class ActionContext {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionContext.class);
	
	/**
	 * The number of milliseconds in a second.
	 */
	private static final int MILLISECONDS_PER_SEC = 1000;
	
	/**
	 * The 4 possible phases in the portlet lifecycle.
	 * 
	 * @author Andrea Funto'
	 */
	public enum Phase {
		/**
		 * The phase in which action processing occurs.
		 */
		ACTION(PortletRequest.ACTION_PHASE),
		
		/**
		 * The phase in which the portlet handles events.
		 */
		EVENT(PortletRequest.EVENT_PHASE),
		
		/**
		 * The phase in which the portlet is requested to repaint itself.
		 */
		RENDER(PortletRequest.RENDER_PHASE),
		
		/**
		 * The phase in which the portlet is serving resources as is.
		 */
		RESOURCE(PortletRequest.RESOURCE_PHASE);	
		
		/**
		 * Returns the standard (JSR-286) textual representation of the given phase.
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return value;
		}
		
		/**
		 * Constructor.
		 *
		 * @param value
		 *   the standard (JSR-286) name for the given phase.
		 */
		private Phase(String value) {
			this.value = value;
		}
		
		/**
		 * The standard (JSR-286) name of the phase.
		 */
		private String value;
	};
	
	/**
	 * The scope for the attributes.
	 * 
	 * @author Andrea Funto'
	 */
	public enum Scope {
		/**
		 * Attributes at request level will be accessible to the portlet that set 
		 * them and to included JSPs and servlets until the next action request 
		 * comes. The data lifecycle encompasses event and resource serving
		 * methods, up to the <em>next</em> action processing request, when they
		 * will be reset.
		 */
		REQUEST(0x00),
		
		/**
		 * Attributes set at <em>application</em> scope are accessible throughout the 
		 * application: all portlets, JSPs and servlets packaged in the same WAR
		 * file will have access to these attributes on a per-user basis. JSPs
		 * and servlets will have direct access to tese attributes through
		 * <code>HttsSession</code> attributes.
		 */
		APPLICATION(PortletSession.APPLICATION_SCOPE),
		
		/**
		 * Attributes set at <em>session</em> will be available to all resources 
		 * sharing the same window id, that is the very portlet that set them and
		 * its included JSPs and servlets. JSPs and servlets will <em>not</em>
		 * have direct access to the resource, because it will be stored in the
		 * <code>HttpSession</code> object under a namespaced attribute key.
		 * The fabricated attribute name will contain the window ID.
		 */
		PORTLET(PortletSession.PORTLET_SCOPE);
		
		/**
		 * Returns the numeric value of the constant.
		 * 
		 * @return
		 *   the numeric value of the constant.
		 */
		public int getValue() {
			return value;
		}
		
		/**
		 * Constructor.
		 *
		 * @param value
		 *   the numeric value of the constant.
		 */
		private Scope(int value) {
			this.value = value;
		}
		
		/**
		 * The numeric value of the constant.
		 */
		private int value;
	}
	
	public static final String ACTION_SCOPED_ATTRIBUTES_KEY = "org.dihedron.ActionScopedRequestAttributes";
	
	/**
	 * The per-thread instance.
	 */
	private static ThreadLocal<ActionContext> context = new ThreadLocal<ActionContext>() {
		@Override protected ActionContext initialValue() {
			logger.debug("creating action context instance for thread {}", Thread.currentThread().getId());
			return new ActionContext();
		}		
	};

	/**
	 * The portlet request (it might be an <code>ActionRequest</code>, a 
	 * <code>RenderRequest</code> or an <code>EventRequest</code>, depending on
	 * the lifecycle and the phase).
	 */
	private PortletRequest request;
	
	/**
	 * The portlet response (it might be an <code>ActionResponse</code>, a 
	 * <code>RenderResponse</code> or an <code>EventResponse</code>, depending on
	 * the lifecycle and the phase).
	 */
	private PortletResponse response;
		
	/**
	 * The action invocation object.
	 */
	private ActionInvocation invocation;
	
	/**
	 * The controller portlet reference.
	 */
	private ActionController portlet;
	
	/**
	 * Retrieves the per-thread instance.
	 * 
	 * @return
	 *   the per-thread instance.
	 */
	private static ActionContext getContext() {
		return context.get();
	} 
				
	/**
	 * Initialise the attributes map used to emulate the per-request attributes;
	 * this map will simulate action-scoped request parameters, and will be populated 
	 * with attributes that must be visible to all the render, serve resource and 
	 * event handling requests coming after an action processing request. These 
	 * parameters will be reset by the <code>ActionController</code>as soon as a 
	 * new action processing request comes. 
	 * 
	 * @param request
	 *   the portlet request.
	 * @param response
	 *   the portlet response.
	 * @param invocation
	 *   the optional <code>ActionInvocation</code> object, only available in the
	 *   context of an action or event processing, not in the render phase.
	 */
	static void bindContext(ActionController portlet, PortletRequest request, PortletResponse response, ActionInvocation... invocation) {
		
		logger.debug("initialising the action context for thread {}", Thread.currentThread().getId());
		
		getContext().portlet = portlet;
		getContext().request = request;
		getContext().response = response;
				
		if(invocation != null && invocation.length > 0) {
			getContext().invocation = invocation[0];
		}
		
		PortletSession session = request.getPortletSession();
		
		// remove all request-scoped attributes from previous invocations		
		@SuppressWarnings("unchecked")
		Map<String, Object> map = 
			(Map<String, Object>)session.getAttribute(
					ACTION_SCOPED_ATTRIBUTES_KEY, PortletSession.PORTLET_SCOPE);
		if(map != null) {
			map.clear();
		} else {
			session.setAttribute(
					ACTION_SCOPED_ATTRIBUTES_KEY, 
					new HashMap<String, Object>(), 
					PortletSession.PORTLET_SCOPE);
		}
	}
	
	/**
	 * Cleans up the internal status of the <code>ActionContext</code> in order to
	 * avoid memory leaks due to persisting portal objects stored in the per-thread
	 * local storage; afterwards it removes the thread local entry altogether, so
	 * the application server does not complain about left-over data in TLS when
	 * re-deploying the portlet.
	 */	
	static void unbindContext() {
		logger.debug("removing action context for thread {}", Thread.currentThread().getId());
		context.get().invocation = null;
		context.get().request = null;
		context.get().response = null;
		context.get().portlet = null;
		context.remove();
	}
	
	/**
	 * Returns the current phase of the action request lifecycle.
	 * 
	 * @return
	 *   the current phase of the action request lifecycle.
	 */
	public static Phase getActionPhase() {
		String currentPhase = (String)getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE);
		for(Phase phase: Phase.values()) {
			if(currentPhase.equals(phase.toString())) {
				return phase;
			}
		}
		return null;
	}
	
	/**
	 * Returns whether the portlet is currently in the action phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the action phase.
	 */
	public static boolean isActionPhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.ACTION_PHASE);
	}

	/**
	 * Returns whether the portlet is currently in the event phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the event phase.
	 */
	public static boolean isEventPhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.EVENT_PHASE);
	}
	
	/**
	 * Returns whether the portlet is currently in the resource phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the resource phase.
	 */
	public static boolean isResourcePhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.RESOURCE_PHASE);
	}

	/**
	 * Returns whether the portlet is currently in the render phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the render phase.
	 */
	public static boolean isRenderPhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.RENDER_PHASE);
	}
	
	/**
	 * Returns the current portlet's name.
	 * 
	 * @return
	 *   the current portlet's name.
	 */
	public static String getPortletName() {
		return getContext().portlet.getPortletName();
	}
	
	/**
	 * Returns the value of the given portlet's initialisation parameter.
	 * 
	 * @param name
	 *  the name of the parameter.
	 * @return
	 *   the value of the given portlet's initialisation parameter.
	 */
	public static String getPortletInitialisationParameter(String name) {
		return getContext().portlet.getInitParameter(name);
	}
	
	// TODO: get other stuff from portlet.xml and web.xml
	// PortletContext and PorteltConfig (see Ashish Sarin pages 119-120)
	
	/**
	 * Retrieves the <code>ActionInvocation</code> object.
	 * 
	 * @return
	 *   the <code>ActionInvocation</code> object.
	 */
	public static ActionInvocation getActionInvocation() {
		return getContext().invocation;
	}
	
	/**
	 * In case of an <code>EventRequest</code>, returns the name of the event.
	 * 
	 * @return
	 *   the name of the event.
	 */
	public static String getEventName() {
		Event event = getEvent();
		if(event != null) {
			return event.getName();
		}
		return null;
	}

	/**
	 * In case of an <code>EventRequest</code>, returns the <code>QName</code>
	 * of the event.
	 * 
	 * @return
	 *   the <code>QName</code> of the event.
	 */
	public static QName getEventQName() {
		Event event = getEvent();
		if(event != null) {
			return event.getQName();
		}
		return null;
	}
	
	/**
	 * In case of an <code>EventRequest</code>, returns the serializable payload
	 * of the event.
	 * 
	 * @return
	 *   the serializable payload of the event.
	 */
	public static Serializable getEventPayload() {
		Event event = getEvent();
		if(event != null) {
			return event.getValue();
		}
		return null;		
	}
	
	/**
	 * Fires an event, for inter-portlet communication.
	 * 
	 * @param name
	 *   the fully-qualified name of the event.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 */
	public static void fireEvent(String name, Serializable payload) {
		if(getContext().response instanceof StateAwareResponse) {
			((StateAwareResponse)getContext().response).setEvent(name, payload);
		}
	}
	
	/**
	 * Fires and event, for inter-portlet communication.
	 * 
	 * @param name
	 *   the name of the event.
	 * @param namespace
	 *   the event namespace.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 */
	public static void fireEvent(String name, String namespace, Serializable payload) {
		QName qname = new QName(namespace, name);
		fireEvent(qname,  payload);
	}
	
	/**
	 * Fires and event, for inter-portlet communication.
	 * 
	 * @param qname 
	 *   an object representing the fully-qualified name of the event.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 */
	public static void fireEvent(QName qname, Serializable payload) {
		if(getContext().response instanceof StateAwareResponse) {
			((StateAwareResponse)getContext().response).setEvent(qname, payload);
		}
	}
	
	/**
	 * Returns a string representing the authentication type.
	 * 
	 * @return
	 *   a string representing the authentication type.
	 */
	public static String getAuthType() {
		return getContext().request.getAuthType();
	}
	
	/**
	 * Checks whether the client request came through a secured connection.
	 * 
	 * @return
	 *   whether the client request came through a secured connection.
	 */
	public static boolean isSecure() {
		return getContext().request.isSecure();
	}
	
	/**
	 * Returns the name of the remote authenticated user.
	 * 
	 * @return
	 *   the name of the remote authenticated user.
	 */
	public static String getRemoteUser() {
		return getContext().request.getRemoteUser();
	}

	/**
	 * Returns the user principal associated with the request.
	 * 
	 * @return
	 *   the user principal.
	 */
	public static Principal getUserPrincipal() {
		return getContext().request.getUserPrincipal();
	}

	/**
	 * Checks whether the user has the given role. 
	 * 
	 * @param role
	 *   the name of the role
	 * @return
	 *   whether the user has the given role.
	 */
	public static boolean isUserInRole(String role) {
		return getContext().request.isUserInRole(role);
	}
	
	/**
	 * Returns the locale associated with the user's request.
	 * 
	 * @return
	 *   the request locale.
	 */
	public static Locale getLocale() {
		return getContext().request.getLocale();
	}
	
	/**
	 * Returns an Enumeration of Locale objects indicating, in decreasing order 
	 * starting with the preferred locale in which the portal will accept content 
	 * for this request. The Locales may be based on the Accept-Language header of the client.
	 *
	 * @return
	 *   an Enumeration of Locales, in decreasing order, in which the portal will 
	 *   accept content for this request
	 */	
	public static Enumeration<Locale> getLocales(){
		return getContext().request.getLocales();
	}
	
	/**
	 * Returns the set of available information for the current user as per the
	 * portlet's configuration in portlet.xml.
	 * 
	 * In order to have user information available in the portlet, the portlet.xml
	 * must include the following lines after all the portlets have been defined:
	 * <pre>
	 *   &lt;portlet-app ...&gt;
	 *     &lt;portlet&gt;
	 *     &lt;portlet-name&gt;MyPortlet&lt;/portlet-name&gt;
	 *       ...
	 *     &lt;/portlet&gt;
	 *     ...
	 *     &lt;user-attribute&gt;
	 *       &lt;description&gt;User First Name&lt;/description&gt;
	 *       &lt;name&gt;user.name.given&lt;/name&gt;
	 *     &lt;/user-attribute&gt;
	 *     &lt;user-attribute&gt;
	 *       &lt;description&gt;User Last Name&lt;/description&gt;
	 *       &lt;name&gt;user.name.family&lt;/name&gt;
	 *     &lt;/user-attribute&gt;
	 *   &lt;/portlet-app&gt;
	 * </pre>
	 * where {@code user.name.given} and {@code user.name.family} are two of the
	 * possible values; the following is a pretty complete list of acceptable 
	 * values:<ul>
	 *   <li>user.bdate</li>
	 *   <li>user.gender</li>
	 *   <li>user.employer</li>
	 *   <li>user.department</li>
	 *   <li>user.jobtitle</li>
	 *   <li>user.name.prefix</li>
	 *   <li>user.name.given</li>
	 *   <li>user.name.family</li>
	 *   <li>user.name.middle</li>
	 *   <li>user.name.suffix</li>
	 *   <li>user.name.nickName</li>
	 *   <li>user.home-info.postal.name</li>
	 *   <li>user.home-info.postal.street</li>
	 *   <li>user.home-info.postal.city</li>
	 *   <li>user.home-info.postal.stateprov</li>
	 *   <li>user.home-info.postal.postalcode</li>
	 *   <li>user.home-info.postal.country</li>
	 *   <li>user.home-info.postal.organization</li>
	 *   <li>user.home-info.telecom.telephone.intcode</li>
	 *   <li>user.home-info.telecom.telephone.loccode</li>
	 *   <li>user.home-info.telecom.telephone.number</li>
	 *   <li>user.home-info.telecom.telephone.ext</li>
	 *   <li>user.home-info.telecom.telephone.comment</li>
	 *   <li>user.home-info.telecom.fax.intcode</li>
	 *   <li>user.home-info.telecom.fax.loccode</li>
	 *   <li>user.home-info.telecom.fax.number</li>
	 *   <li>user.home-info.telecom.fax.ext</li>
	 *   <li>user.home-info.telecom.fax.comment</li>
	 *   <li>user.home-info.telecom.mobile.intcode</li>
	 *   <li>user.home-info.telecom.mobile.loccode</li>
	 *   <li>user.home-info.telecom.mobile.number</li>
	 *   <li>user.home-info.telecom.mobile.ext</li>
	 *   <li>user.home-info.telecom.mobile.comment</li>
	 *   <li>user.home-info.telecom.pager.intcode</li>
	 *   <li>user.home-info.telecom.pager.loccode</li>
	 *   <li>user.home-info.telecom.pager.number</li>
	 *   <li>user.home-info.telecom.pager.ext</li>
	 *   <li>user.home-info.telecom.pager.comment</li>
	 *   <li>user.home-info.online.email</li>
	 *   <li>user.home-info.online.uri</li>
	 *   <li>user.business-info.postal.name</li>
	 *   <li>user.business-info.postal.street</li>
	 *   <li>user.business-info.postal.city</li>
	 *   <li>user.business-info.postal.stateprov</li>
	 *   <li>user.business-info.postal.postalcode</li>
	 *   <li>user.business-info.postal.country</li>
	 *   <li>user.business-info.postal.organization</li>
	 *   <li>user.business-info.telecom.telephone.intcode</li>
	 *   <li>user.business-info.telecom.telephone.loccode</li>
	 *   <li>user.business-info.telecom.telephone.number</li>
	 *   <li>user.business-info.telecom.telephone.ext</li>
	 *   <li>user.business-info.telecom.telephone.comment</li>
	 *   <li>user.business-info.telecom.fax.intcode</li>
	 *   <li>user.business-info.telecom.fax.loccode</li>
	 *   <li>user.business-info.telecom.fax.number</li>
	 *   <li>user.business-info.telecom.fax.ext</li>
	 *   <li>user.business-info.telecom.fax.comment</li>
	 *   <li>user.business-info.telecom.mobile.intcode</li>
	 *   <li>user.business-info.telecom.mobile.loccode</li>
	 *   <li>user.business-info.telecom.mobile.number</li>
	 *   <li>user.business-info.telecom.mobile.ext</li>
	 *   <li>user.business-info.telecom.mobile.comment</li>
	 *   <li>user.business-info.telecom.pager.intcode</li>
	 *   <li>user.business-info.telecom.pager.loccode</li>
	 *   <li>user.business-info.telecom.pager.number</li>
	 *   <li>user.business-info.telecom.pager.ext</li>
	 *   <li>user.business-info.telecom.pager.comment</li>
	 *   <li>user.business-info.online.email</li>
	 *   <li>user.business-info.online.uri</li>
	 * </ul>
	 *     
	 * @return
	 *   a map representing the user information available to the portlets in 
	 *   the curent application. 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUserInformation() {
		return (Map<String, Object>)request.getAttribute(PortletRequest.USER_INFO);		
	}
	
	/**
	 * Returns the current portal context, containing information about the 
	 * current portal server.
	 * 
	 * @return
	 *   the portal context.
	 */
	public PortalContext getPortalContext() {
		return request.getPortalContext();
	}
	
	/**
	 * Returns the current portlet mode.
	 * 
	 * @return
	 *   the current portlet mode.
	 */
	public PortletMode getPortletMode() {
		return PortletMode.getPortletMode(request.getPortletMode().toString());
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
	 */
	@Deprecated
	public void setPortletMode(PortletMode mode) throws PortletModeException {
		if(response instanceof StateAwareResponse) {
			if(request.isPortletModeAllowed(mode)) {
				logger.trace("changing portlet mode to '{}'", mode);
				((StateAwareResponse)response).setPortletMode(mode);
			} else {
				logger.warn("unsupported portlet mode '{}'", mode);
			}
		}		
	}
	
	/**
	 * Returns the current portlet window state.
	 * 
	 * @return
	 *   the current portlet window state.
	 */
	public WindowState getWindowState() {
		return WindowState.getWindowState(request.getWindowState().toString());
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
	 */
	@Deprecated
	public void setWindowState(WindowState state) throws WindowStateException {
		if(response instanceof StateAwareResponse) {
			if(request.isWindowStateAllowed(state)) {
				logger.trace("changing window state to '{}'", state);
				((StateAwareResponse)response).setWindowState(state);
			} else {
				logger.warn("unsupported window state '{}'", state);
			}
		}
	}
	
	/**
	 * Returns the portlet window ID. The portlet window ID is unique for this 
	 * portlet window and is constant for the lifetime of the portlet window.
	 * 	This ID is the same that is used by the portlet container for scoping 
	 * the portlet-scope session attributes.
	 * 
	 * @return
	 *   the portlet window ID.
	 */
	public static String getPortletWindowId() {
		return getContext().request.getWindowID();
	}
	
	/**
	 * Returns the session ID indicated in the client request. This session ID 
	 * may not be a valid one, it may be an old one that has expired or has been 
	 * invalidated. If the client request did not specify a session ID, this 
	 * method returns null.
	 *  
	 * @return
	 *   a String specifying the session ID, or null if the request did not 
	 *   specify a session ID.
	 * @see isRequestedSessionIdValid()
	 */
	public static String getRequestedSessionId() {
		return getContext().request.getRequestedSessionId();
	}
	

	/**
	 * Checks whether the requested session ID is still valid.
	 * 
	 * @return
	 *   true if this request has an id for a valid session in the current 
     *   session context; false otherwise.
     * @see getRequestedSessionId()
     * @see getPortletSession()
     */
	public static boolean isRequestedSessionIdValid() {
		return getContext().request.isRequestedSessionIdValid(); 
	}
	
	/**
	 * Returns whether the <code>PortletSession</code> is still valid.
	 * 
	 * @return
	 *   whether the <code>PortletSession</code> is still valid.
	 */
	public static boolean isSessionValid() {
		PortletSession session = getContext().request.getPortletSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (elapsed < session.getMaxInactiveInterval() * MILLISECONDS_PER_SEC);
	}
	
	/**
	 * Returns the number of seconds left before the session gets invalidated 
	 * by the container.
	 * 
	 * @return
	 *   the number of seconds left before the session gets invalidated by the 
	 *   container.
	 */
	public static long getSecondsToSessionInvalid() {
		PortletSession session = getContext().request.getPortletSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (long)((elapsed - session.getMaxInactiveInterval() * MILLISECONDS_PER_SEC) / MILLISECONDS_PER_SEC);		
	}
	
	/**
	 * Returns the number of seconds since the last access to the session object.
	 * 
	 * @return
	 *   the number of seconds since the last access to the session object.
	 */
	public static long getTimeOfLastAccessToSession() {
		return getContext().request.getPortletSession().getLastAccessedTime();
	}
	
	/**
	 * Returns the maximum amount of inactivity seconds before the session is 
	 * considered stale.
	 * 
	 * @return
	 *   the maximum number of seconds before the session is considered stale.
	 */
	public static int getMaxInactiveSessionInterval() {
		return getContext().request.getPortletSession().getMaxInactiveInterval();
	}
	
	/**
	 * Sets the session timeout duration in seconds.
	 * 
	 * @param time
	 *   the session timeout duration, in seconds.
	 */
	public static void setMaxInactiveSessionInterval(int time) {
		getContext().request.getPortletSession().setMaxInactiveInterval(time);
	}
	
	/**
	 * Sets the title of the portlet; this method can only be invoked in the render 
	 * phase.
	 * 
	 * @param title
	 */
	public void setPortletTitle(String title) {
		if(isRenderPhase() && response instanceof RenderResponse) {
			logger.trace("setting the portlet title to '{}'", title);
			((RenderResponse)response).setTitle(title);
		} else {
			logger.warn("cannot set the title out of the render phase");
		}
	}
	
	/**
	 * Encodes the given URL; ths URL is not prefixed with the current context 
	 * path, and is therefore considered as absolute. An example of such URLs is
	 * <code>/MyApplication/myServlet</code>.
	 * 
	 * @param url
	 *   the absolute URL to be encoded.
	 * @return
	 *   the URL, in encoded form.
	 */
	public String encodeAbsoluteURL(String url) {
		String encoded = response.encodeURL(url);
		logger.trace("url '{}' encoded as '{}'", url, encoded);
		return encoded;
	}
	
	/**
	 * Encodes the given URL; the URL is prefixed with the current context path, 
	 * and is therefore considered as relative to it. An example of such URLs is
	 * <code>/css/myStyleSheet.css</code>.
	 * 
	 * @param url
	 *   the relative URL to be encoded.
	 * @return
	 *   the URL, in encoded form.
	 */	
	public String encodeRelativeURL(String url) {
		String unencoded = request.getContextPath() + url;
		String encoded = response.encodeURL(unencoded);
		logger.trace("url '{}' encoded as '{}'", unencoded, encoded);
		return encoded;
	}
	
	/**
	 * Redirects to a different URL, with no referrer URL unless it is specified 
	 * in the URL itself. 
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void sendRedirect(String url) throws IOException {
		if(isActionPhase() && response instanceof ActionResponse) {
			((ActionResponse)response).sendRedirect(url);
		}
	}

	/**
	 * Redirects to a different URL, adding a referrer to provide a "back" address 
	 * to the destination page.
	 * 
	 * @param url
	 * @param referrer
	 * @throws IOException
	 */
	public void sendRedirect(String url, String referrer) throws IOException {
		if(isActionPhase() && response instanceof ActionResponse) {
			((ActionResponse)response).sendRedirect(url, referrer);
		}
	}
	
	/**
	 * Returns the resource bundle associated with the underlying portlet, for 
	 * the given locale.
	 * 
	 * @param locale
	 *   the selected locale.
	 * @return
	 *   the portlet's configured resource bundle.
	 */
	public ResourceBundle getResouceBundle(Locale locale) {
		return portlet.getResourceBundle(locale);
	}
	
	/**
	 * Returns the per-user portlet preferences.
	 * 
	 * @return
	 *   the per-user portlet preferences.
	 */
	public PortletPreferences getPortletPreferences() {
		return request.getPreferences();
	}
	
	/**
	 * Returns the application-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	public static Object getApplicationAttribute(String key) {
		return getAttribute(key, Scope.APPLICATION);
	}
	
	/**
	 * Adds or replaces an attribute in the map of attributes at application scope.
	 * The attribute will be shared among all portlets, JSPs and servlets belonging
	 * to the same application, on a per-user basis.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 */
	public static void setApplicationAttribute(String key, Object value) {
		setAttribute(key, value, Scope.APPLICATION);
	}
	
	/**
	 * Removes the application-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the previous value of the attribute, or null if not set.
	 */	
	public static Object removeApplicationAttribute(String key) {
		return removeAttribute(key, Scope.APPLICATION);
	}

	/**
	 * Returns the portlet-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	public static Object getPortletAttribute(String key) {
		return getAttribute(key, Scope.PORTLET);
	}
	
	/**
	 * Adds or replaces an attribute in the map of attributes at portlet scope.
	 * The attribute will be visible to the portlet itself (but not to other 
	 * instances of the same portlet), and to JSPs and servlets included by the 
	 * portlet, on a per-user basis.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 */
	public static void setPortletAttribute(String key, Object value) {
		setAttribute(key, value, Scope.PORTLET);
	}	
	
	/**
	 * Removes the portlet-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the previous value of the attribute, or null if not set.
	 */	
	public static Object removePortletAttribute(String key) {
		return removeAttribute(key, Scope.PORTLET);
	}	
	
	/**
	 * Returns the value of the reqest-scoped attribute.
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the value of the request-scoped attribute, or null if not set.
	 */
	public static Object getRequestAttribute(String key) {
		return getAttribute(key, Scope.REQUEST);
	}

	/**
	 * Adds or replaces an attribute in the map of attributes at request scope.
	 * The attribute will be available to all following render requests until a 
	 * new action request comes to reset them.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 */
	public static void setRequestAttribute(String key, Object value) {
		setAttribute(key, value, Scope.REQUEST);
	}
	
	/**
	 * Removes the request-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the previous value of the attribute, or null if not set.
	 */	
	public static Object removeRequestAttribute(String key) {
		return removeAttribute(key, Scope.REQUEST);
	}

	/**
	 * Returns the value of the given attribute in the proper application-, 
	 * session- or portlet-level map, depending on the scope.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param scope
	 *   the requested scope.
	 * @return
	 *   the requested attribute value, or null if not found.
	 */
	public static Object getAttribute(String key, Scope scope) {
		return getAttributes(scope).get(key);
	}
		
	/**
	 * Returns the proper map of attributes at application-, session- or portlet-
	 * level, depending on the requested scope.
	 * 
	 * @param scope
	 *   the requested scope.
	 * @return
	 *   the map of attributes at the requested scope scope.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAttributes(Scope scope) {
		Map<String, Object> map = null;
		if(getContext().request != null) {
			PortletSession session = getContext().request.getPortletSession();
			// TODO: check on this
			//session.isNew(); 
			switch(scope) {
			case APPLICATION:
				map = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);
			case PORTLET:
				map = session.getAttributeMap(PortletSession.PORTLET_SCOPE);
			case REQUEST:
				Map<String, Object> attributes = session.getAttributeMap(PortletSession.PORTLET_SCOPE);
				map = (Map<String, Object>)attributes.get(ACTION_SCOPED_ATTRIBUTES_KEY);
			}			
		}
		return map;
	}	
	
	/**
	 * Stores the given attribute in the proper map, depending on the requested 
	 * scope.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 * @param scope
	 *   the requested scope.
	 */
	public static void setAttribute(String key, Object value, Scope scope) {
		getAttributes(scope).put(key, value);
	}
	  
	/**
	 * Merges all the entries in the given map into the appropriate attributes
	 * map; depending on the scope, the destination map will be the set of
	 * attributes contained in the <code>PortletSession</code> or in the
	 * <code>PortletRequest</code>, with the following semantics:<ul>  
	 * <li>in the case of <em>application</em> scope, the attributes are stored 
	 * in the attributes map at the <code>PortletSession</code>'s 
	 * <code>APPLICATION_SCOPE</code>; these attributes will be visible throughout
	 * the application to all portlets, JSPs and servlets on a per-user basis</li>
	 * <li>in the case of <em>session</em> scope, the attributes are stored 
	 * in the attributes map at the <code>PortletSession</code>'s 
	 * <code>PORTLET_SCOPE</code>; these attributes will be visible to the portlet
	 * itself (but not to other instances of the same portlet), and to JSPs and 
	 * servlets included by the portlet, on a per-user basis</li>
	 * <li>in the case of <em>request</em> scope, the attributes will be stored 
	 * and made available to all following render requests until a new action 
	 * request comes to reset them; this involves a bit of management by the
	 * <code>ActionController</code>, which has to reset action scoped request
	 * attributes when a new action comes</li>.
	 * </ul>.
	 * <em>NOTE</em>: Liferay 6.x and many other portlet containers do not support 
	 * action-request-scoped attributes, so instead of making request attributes 
	 * available to all the following render requests, they make these attributes 
	 * available only to the one render request that immediately follows, or to none
	 * at all. The expected behaviour has been simulated in this method by putting 
	 * these request-scoped parameters in a reserved and dedicated area in the 
	 * <code>PortletSession</code>; if you want to leverage the container's 
	 * native behaviour, use the deprecated setActionScopedAttributes() instead
	 * and remember to enable the <em>actionScopedRequestParmeters</em> runtime 
	 * option.
	 * 	 
	 * @param attributes
	 *   a map of attributes to be set at application level.
	 * @param scope
	 *   the scope at which the attributes should be set.
	 */
	public static void setAttributes(Map<String, Object> attributes, Scope scope) {
		PortletSession session = getContext().request.getPortletSession();
		Map<String, Object> map;
		switch(scope) {
		case APPLICATION:
			map = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);		
			map.putAll(attributes);
			break;
		case PORTLET:
			map = session.getAttributeMap(PortletSession.PORTLET_SCOPE);		
			map.putAll(attributes);
			break;
		case REQUEST:
			map = session.getAttributeMap(PortletSession.PORTLET_SCOPE);		
			@SuppressWarnings("unchecked")
			Map<String, Object> attrs = (Map<String, Object>)map.get(ACTION_SCOPED_ATTRIBUTES_KEY);
			attrs.putAll(attributes);
			break;			
		}
	}
	
	/**
	 * Removes the give attribute from the proper map, depending on the requested
	 * scope.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param scope
	 *   the requested scope.
	 * @return
	 *   the previous value of the attribute, or null if not found.
	 */
	public static Object removeAttribute(String key, Scope scope) {
		return getAttributes(scope).remove(key);		
	}
	
	/**
	 * Removes all attributes from the map at the requested scope.
	 * 
	 * @param scope
	 *   the requested scope.
	 */
	public static void clearAttributes(Scope scope) {
		getAttributes(scope).clear();
	}
	
	/**
	 * Returns the map of all parameters set in the client request.
	 * 
	 * @return
	 *   the map of input parameters.
	 */
	public static Map<String, String[]> getParameters() {
		if(getContext().request != null) {
			return getContext().request.getParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all request parameters.
	 * 
	 * @return
	 *   the names of all request parameters.
	 */
	public static Set<String> getParameterNames() {
		Set<String> set = new HashSet<String>();
		Enumeration<String> e = getContext().request.getParameterNames();
		while(e.hasMoreElements()) {
			set.add(e.nextElement());
		}
		return set;
	}

	/**
	 * Returns the set of values associated with the given parameter key.
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @return
	 *   the array of parameter values.
	 */
	public static String[] getParameterValues(String key) {
		if(getContext().request != null) {
			return getContext().request.getParameterValues(key);
		}
		return null;
	}
	
	/**
	 * Returns only the first of the set of values associated with the given
	 * parameter key.
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @return
	 *   the first value of the array, or null if not found.
	 */
	public static String getFirstParameterValue(String key) {
		if(getContext().request != null) {
			return getContext().request.getParameter(key);
		}
		return null;
	}

	/**
	 * Returns the map of all public parameters set in the client request.
	 * 
	 * @return
	 *   the map of input public parameters.
	 */
	public static Map<String, String[]> getPublicParameters() {
		if(getContext().request != null) {
			return getContext().request.getPublicParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all public request parameters.
	 * 
	 * @return
	 *   the names of all request public parameters.
	 */
	public static Set<String> getPublicParameterNames() {
		if(getContext().request != null) {
			return getContext().request.getPublicParameterMap().keySet();
		}
		return null;
	}

	/**
	 * Returns the set of values associated with the given public parameter key.
	 * 
	 * @param key
	 *   the name of the public parameter.
	 * @return
	 *   the array of parameter values.
	 */
	public static String[] getPublicParameterValues(String key) {
		if(getContext().request != null) {
			return getContext().request.getPublicParameterMap().get(key);
		}
		return null;
	}
	
	/**
	 * Returns only the first of the set of values associated with the given
	 * public parameter key.
	 * 
	 * @param key
	 *   the name of the public parameter.
	 * @return
	 *   the first value of the array, or null if not found.
	 */
	public static String getFirstPublicParameterValue(String key) {
		if(getContext().request != null) {
			String [] values = getContext().request.getPublicParameterMap().get(key);
			if(values != null && values.length > 0) {
				return values[0];
			}
		}
		return null;
	}
	
	/**
	 * Returns the map of all private parameters set in the client request.
	 * 
	 * @return
	 *   the map of input private parameters.
	 */
	public static Map<String, String[]> getPrivateParameters() {
		if(getContext().request != null) {
			return getContext().request.getPrivateParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all private request parameters.
	 * 
	 * @return
	 *   the names of all request private parameters.
	 */
	public static Set<String> getPrivateParameterNames() {
		if(getContext().request != null) {
			return getContext().request.getPrivateParameterMap().keySet();
		}
		return null;
	}

	/**
	 * Returns the set of values associated with the given private parameter key.
	 * 
	 * @param key
	 *   the name of the private parameter.
	 * @return
	 *   the array of parameter values.
	 */
	public static String[] getPrivateParameterValues(String key) {
		if(getContext().request != null) {
			return getContext().request.getPrivateParameterMap().get(key);
		}
		return null;
	}
	
	/**
	 * Returns only the first of the set of values associated with the given
	 * private parameter key.
	 * 
	 * @param key
	 *   the name of the private parameter.
	 * @return
	 *   the first value of the array, or null if not found.
	 */
	public static String getFirstPrivateParameterValue(String key) {
		if(getContext().request != null) {
			String [] values = getContext().request.getPrivateParameterMap().get(key);
			if(values != null && values.length > 0) {
				return values[0];
			}
		}
		return null;
	}
	
	/**
	 * Returns the map of currently set render parameters.
	 * 
	 * @return
	 *   a map of render parameters names an values, or null if unsupported by 
	 *   the current type of request/response.
	 */
	public static Map<String, String[]> getRenderParameterMap() {
		if(getContext().response instanceof StateAwareResponse) {
			return ((StateAwareResponse)getContext().response).getRenderParameterMap();
		}
		return null;
	}
	
	/**
	 * Sets a render parameter. The parameter value(s) must all be string(s).
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @param values
	 *   the parameter value(s).
	 */
	public static void setRenderParameter(String key, String... values) {
		if(Strings.isValid(key) && values != null && getContext().response instanceof StateAwareResponse) {
			if(values.length == 1) {
				((StateAwareResponse)getContext().response).setRenderParameter(key, values[0]);
			} else {
				((StateAwareResponse)getContext().response).setRenderParameter(key, values);
			}
		}
	}
	
	
	/**
	 * Returns an array containing all of the Cookie properties. This method 
	 * returns null if no cookies exist.
	 * 
	 * @return
	 *   the array of cookie properties, or null if no cookies exist.
	 */
	public static Cookie[] getCookies() {
		if(getContext().request != null) {
			return getContext().request.getCookies();
		}
		return null;
	}
	
	/**
	 * Adds a cookie to the client.
	 * 
	 * @param cookie
	 *   the cookie to be added to the client.
	 */
	public static void setCookie(Cookie cookie) {
		if(getContext().response != null) {
			getContext().response.addProperty(cookie);
		}
	}
	
	/**
	 * Returns the underlying portlet request object.
	 * 
	 * @return
	 *   the underlying portlet request object.
	 */
	@Deprecated
	public static PortletRequest getPortletRequest() {
		return getContext().request;
	}
	
	/**
	 * Returns the underlying portlet response object.
	 * 
	 * @return
	 *   the underlying portlet response object.
	 */
	@Deprecated
	public static PortletResponse getPortletResponse() {
		return getContext().response;
	}
		
	/**
	 * Returns the underlying portlet session object.
	 * 
	 * @return
	 *   the underlying portlet session object.
	 */
	@Deprecated
	public static PortletSession getPortletSession() {
		return getContext().request.getPortletSession();
	}
	
	/**
	 * Returns the event, if this invocation was doe to an inter-portlet communication
	 * even being fired.
	 *  
	 * @return
	 *   the event object.
	 */
	@Deprecated
	public static Event getEvent() {
		if(getContext().request instanceof EventRequest) {
			return ((EventRequest)getContext().request).getEvent();
		}
		return null;
	}
	
	/**
	 * Private constructor, so this object cannot be instantiated by anyone else.
	 */
	private ActionContext() {		
	}

//	/**
//	 * Provides a pretty printed representation of the <code>ActionContext</code>,
//	 * with all attributes, properties and parameters.
//	 * 
//	 * @return 
//	 *   a pretty printed representation of the <code>ActionContext</code>
//	 * @see 
//	 *   java.lang.Object#toString()
//	 */
//	public String toString() {
//		StringBuilder buffer = new StringBuilder();
//		Map<String, Object> attributes = null;
//		buffer.append("--------- APPLICATION ---------\n");
//		attributes = getAttributes(Scope.APPLICATION);
//		if(attributes != null) {
//			for(Entry<String, Object> entry : attributes.entrySet()) {
//				buffer.append("attribute\n");
//				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
//				buffer.append(String.format(" + value  : '%1$s'\n", entry.getValue()));
//			}
//		}
//		buffer.append("----------- SESSION -----------\n");
//		attributes = getAttributes(Scope.SESSION);
//		if(attributes != null) {
//			for(Entry<String, Object> entry : attributes.entrySet()) {
//				buffer.append("attribute\n");
//				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
//				buffer.append(String.format(" + value  : '%1$s'\n", entry.getValue()));
//			}
//		}
//		buffer.append("----------- REQUEST -----------\n");
//		attributes = getAttributes(Scope.REQUEST);
//		if(attributes != null) {
//			for(Entry<String, Object> entry : attributes.entrySet()) {
//				buffer.append("attribute\n");
//				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
//				buffer.append(String.format(" + value  : '%1$s'\n", entry.getValue()));
//			}
//		}
//		buffer.append("---------- PARAMETERS ---------\n");
//		Map<String, String[]> parameters = getParameters();
//		if(parameters != null) {
//			for(Entry<String, String[]> entry : parameters.entrySet()) {
//				buffer.append("parameter\n");
//				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
//				buffer.append(" + values : ");
//				for(String value : entry.getValue()) {
//					buffer.append("'").append(value).append("', ");
//				}
//				buffer.append("\n");
//			}
//		}
//		buffer.append("----------- PUBLIC ------------\n");
//		parameters = getPublicParameters();
//		if(parameters != null) {
//			for(Entry<String, String[]> entry : parameters.entrySet()) {
//				buffer.append("parameter\n");
//				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
//				buffer.append(" + values : ");
//				for(String value : entry.getValue()) {
//					buffer.append("'").append(value).append("', ");
//				}
//				buffer.append("\n");
//			}
//		}
//		buffer.append("----------- PRIVATE -----------\n");
//		parameters = getPublicParameters();
//		if(parameters!= null) {
//			for(Entry<String, String[]> entry : parameters.entrySet()) {
//				buffer.append("parameter\n");
//				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
//				buffer.append(" + values : ");
//				for(String value : entry.getValue()) {
//					buffer.append("'").append(value).append("', ");
//				}
//				buffer.append("\n");
//			}
//		}
//		buffer.append("-------------------------------\n");
//		return buffer.toString();
//	}
}
