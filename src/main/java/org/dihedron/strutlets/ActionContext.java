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

import java.io.Serializable;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.StateAwareResponse;
import javax.servlet.http.Cookie;
import javax.xml.namespace.QName;

import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class ActionContext {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(ActionContext.class);
	
	/**
	 * The scope for the attributes.
	 * 
	 * @author Andrea Funto'
	 */
	public enum Scope {
		/**
		 * Attributes set at <em>application</em> scope are accessible throughout the 
		 * application: all portlets, JSPs and servlets packaged in the same WAR
		 * file will have access to these attributes on a per-user basis. JSPs
		 * and servlets will have direct access to tese attributes through
		 * <code>HttsSession</code> attributes.
		 */
		APPLICATION,
		
		/**
		 * Attributes set at <em>session</em> will be available to all resources 
		 * sharing the same window id, that is the very portlet that set them and
		 * its included JSPs and servlets. JSPs and servlets will <em>not</em>
		 * have direct access to the resource, because it will be stored in the
		 * <code>HttpSession</code> object under a namespaced attribute key.
		 * The fabricated attribute name will contain the window ID.
		 */
		SESSION,
		
		/**
		 * Attributes at request level will be accessible to the portlet that set 
		 * them and to included JSPs and servlets untile the next action request 
		 * comes. The data lifecycle encompasses event and resource serving
		 * methods, up to the <em>next</em> action processing request, when they
		 * will be reset.
		 */
		REQUEST
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
	 * Retrieves the per-thread instance.
	 * 
	 * @return
	 *   the per-thread instance.
	 */
	public static ActionContext acquireContext() {
		logger.debug("retrieving action context for thread {}", Thread.currentThread().getId());
		return context.get();
	} 
	
	/**
	 * Cleans up the internal status of the <code>ActionContext</code> in order to
	 * avoid memory leaks due to persisting portal objects stored in the per-thread
	 * local storage; afterwards it removes the thread local entry altogether, so
	 * the application server does not complain about left-over data in TLS when
	 * re-deploying the portlet.
	 */	
	public static void removeContext() {
		logger.debug("removing action context for thread {}", Thread.currentThread().getId());
		context.get().invocation = null;
		context.get().request = null;
		context.get().response = null;
		context.remove();
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
	 * @return
	 *   the object itself, for method chaining.
	 */
	public ActionContext initialise(PortletRequest request, PortletResponse response, ActionInvocation... invocation) {
		
		logger.debug("initialising the action context for thread {}", Thread.currentThread().getId());
		
		this.request = request;
		this.response = response;
		
		if(invocation != null && invocation.length > 0) {
			this.invocation = invocation[0];
		}
		
		PortletSession session = request.getPortletSession();
		
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
		return this;
	}
	
	/**
	 * Retrieves the <code>ActionInvocation</code> object.
	 * 
	 * @return
	 *   the <code>ActionInvocation</code> object.
	 */
	public ActionInvocation getActionInvocation() {
		return invocation;
	}
	
	/**
	 * In case of an <code>EventRequest</code>, returns the name of the event.
	 * 
	 * @return
	 *   the name of the event.
	 */
	public String getEventName() {
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
	public QName getEventQName() {
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
	public Serializable getEventPayload() {
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
	public void fireEvent(String name, Serializable payload) {
		if(response instanceof StateAwareResponse) {
			((StateAwareResponse)response).setEvent(name, payload);
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
	public void fireEvent(String name, String namespace, Serializable payload) {
		QName qname = new QName(namespace, name);
		this.fireEvent(qname,  payload);
	}
	
	/**
	 * Fires and event, for inter-portlet communication.
	 * 
	 * @param qname 
	 *   an object representing the fully-qualified name of the event.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 */
	public void fireEvent(QName qname, Serializable payload) {
		if(response instanceof StateAwareResponse) {
			((StateAwareResponse)response).setEvent(qname, payload);
		}
	}
	
	/**
	 * Returns a string representing the authentication type.
	 * 
	 * @return
	 *   a string representing the authentication type.
	 */
	public String getAuthType() {
		return request.getAuthType();
	}
	
	/**
	 * Checks whether the client request came through a secured connection.
	 * 
	 * @return
	 *   whether the client request came through a secured connection.
	 */
	public boolean isSecure() {
		return request.isSecure();
	}
	
	/**
	 * Returns the name of the remote authenticated user.
	 * 
	 * @return
	 *   the name of the remote authenticated user.
	 */
	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	/**
	 * Returns the user principal associated with the request.
	 * 
	 * @return
	 *   the user principal.
	 */
	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	/**
	 * Checks whether the user has the given role. 
	 * 
	 * @param role
	 *   the name of the role
	 * @return
	 *   whether the user has the given role.
	 */
	public boolean isUserInRole(String role) {
		return request.isUserInRole(role);
	}
	
	/**
	 * Returns the locale associated with the user's request.
	 * 
	 * @return
	 *   the request locale.
	 */
	public Locale getLocale() {
		return request.getLocale();
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
	public Enumeration<Locale> getLocales(){
		return request.getLocales();
	}
	
	/**
	 * Returns the portlet window ID. The portlet window ID is unique for this 
	 * portlet window and is constant for the lifetime of the portlet window.
	 * 	This ID is the same that is used by the portlet container for scoping 
	 * the portlet-scope session attributes.
	 * 
	 * @return
	 *  	the portlet window ID.
	 */
	public String getPortletWindowId() {
		return request.getWindowID();
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
	public String getRequestedSessionId() {
		return request.getRequestedSessionId();
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
	public boolean isRequestedSessionIdValid() {
		return request.isRequestedSessionIdValid(); 
	}
	
	/**
	 * Returns whether the <code>PortletSession</code> is still valid.
	 * 
	 * @return
	 *   whether the <code>PortletSession</code> is still valid.
	 */
	public boolean isSessionValid() {
		PortletSession session = request.getPortletSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (elapsed < session.getMaxInactiveInterval() * 1000);
	}
	
	/**
	 * Returns the number of seconds left before the session gets invalidated 
	 * by the container.
	 * 
	 * @return
	 *   the number of seconds left before the session gets invalidated by the 
	 *   container.
	 */
	public long getSecondsToSessionInvalid() {
		PortletSession session = request.getPortletSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (long)((elapsed - session.getMaxInactiveInterval() * 1000) / 1000);		
	}
	
	public long getTimeOfLastAccessToSession() {
		return request.getPortletSession().getLastAccessedTime();
	}
	
	public int getMaxInactiveSessionInterval() {
		return request.getPortletSession().getMaxInactiveInterval();
	}
	
	public void setMaxInactiveSessionInterval(int time) {
		request.getPortletSession().setMaxInactiveInterval(time);
	}
	
	/**
	 * Returns the application-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	public Object getApplicationAttribute(String key) {
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
	public void setApplicationAttribute(String key, Object value) {
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
	public Object removeApplicationAttribute(String key) {
		return removeAttribute(key, Scope.APPLICATION);
	}

	/**
	 * Returns the session-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	public Object getSessionAttribute(String key) {
		return getAttribute(key, Scope.SESSION);
	}
	
	/**
	 * Adds or replaces an attribute in the map of attributes at session scope.
	 * The attribute will be visible to the portlet itself (but not to other 
	 * instances of the same portlet), and to JSPs and servlets included by the 
	 * portlet, on a per-user basis.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 */
	public void setSessionAttribute(String key, Object value) {
		setAttribute(key, value, Scope.SESSION);
	}	
	
	/**
	 * Removes the session-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the previous value of the attribute, or null if not set.
	 */	
	public Object removeSessionAttribute(String key) {
		return removeAttribute(key, Scope.SESSION);
	}	
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object getRequestAttribute(String key) {
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
	public void setRequestAttribute(String key, Object value) {
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
	public Object removeRequestAttribute(String key) {
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
	public Object getAttribute(String key, Scope scope) {
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
	public Map<String, Object> getAttributes(Scope scope) {
		Map<String, Object> map = null;
		if(request != null) {
			PortletSession session = request.getPortletSession();
			session.isNew(); // TODO: check on this
			switch(scope) {
			case APPLICATION:
				map = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);
			case SESSION:
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
	public void setAttribute(String key, Object value, Scope scope) {
		getAttributes(scope).put(key,  value);
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
	public void setAttributes(Map<String, Object> attributes, Scope scope) {
		PortletSession session = request.getPortletSession();
		Map<String, Object> map;
		switch(scope) {
		case APPLICATION:
			map = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);		
			map.putAll(attributes);
			break;
		case SESSION:
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
	public Object removeAttribute(String key, Scope scope) {
		return getAttributes(scope).remove(key);		
	}
	
	/**
	 * Removes all attributes from the map at the requested scope.
	 * 
	 * @param scope
	 *   the requested scope.
	 */
	public void clearAttributes(Scope scope) {
		getAttributes(scope).clear();
	}
	
	/**
	 * Returns the map of all parameters set in the client request.
	 * 
	 * @return
	 *   the map of input parameters.
	 */
	public Map<String, String[]> getParameters() {
		if(request != null) {
			return request.getParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all request parameters.
	 * 
	 * @return
	 *   the names of all request parameters.
	 */
	public Set<String> getParameterNames() {
		Set<String> set = new HashSet<String>();
		Enumeration<String> e = request.getParameterNames();
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
	public String[] getParameterValues(String key) {
		if(request != null) {
			return request.getParameterValues(key);
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
	public String getFirstParameterValue(String key) {
		if(request != null) {
			return request.getParameter(key);
		}
		return null;
	}

	/**
	 * Returns the map of all public parameters set in the client request.
	 * 
	 * @return
	 *   the map of input public parameters.
	 */
	public Map<String, String[]> getPublicParameters() {
		if(request != null) {
			return request.getPublicParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all public request parameters.
	 * 
	 * @return
	 *   the names of all request public parameters.
	 */
	public Set<String> getPublicParameterNames() {
		if(request != null) {
			return request.getPublicParameterMap().keySet();
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
	public String[] getPublicParameterValues(String key) {
		if(request != null) {
			return request.getPublicParameterMap().get(key);
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
	public String getFirstPublicParameterValue(String key) {
		if(request != null) {
			String [] values = request.getPublicParameterMap().get(key);
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
	public Map<String, String[]> getPrivateParameters() {
		if(request != null) {
			return request.getPrivateParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all private request parameters.
	 * 
	 * @return
	 *   the names of all request private parameters.
	 */
	public Set<String> getPrivateParameterNames() {
		if(request != null) {
			return request.getPrivateParameterMap().keySet();
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
	public String[] getPrivateParameterValues(String key) {
		if(request != null) {
			return request.getPrivateParameterMap().get(key);
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
	public String getFirstPrivateParameterValue(String key) {
		if(request != null) {
			String [] values = request.getPrivateParameterMap().get(key);
			if(values != null && values.length > 0) {
				return values[0];
			}
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
	public void setRenderParameter(String key, String... values) {
		if(Strings.isValid(key) && values != null && response instanceof StateAwareResponse) {
			if(values.length == 1) {
				((StateAwareResponse)response).setRenderParameter(key, values[0]);
			} else {
				((StateAwareResponse)response).setRenderParameter(key, values);
			}
		}
	}

	/**
	 * Sets a render parameter. The parameter must be a string.
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @param value
	 *   the parameter value.
	 *
	public void setRenderParameter(String key, String[] value) {
		if(Strings.isValid(key)) {
			response.setRenderParameter(key, value);
		}
	}
	*/
	
	/**
	 * Returns an array containing all of the Cookie properties. This method 
	 * returns null if no cookies exist.
	 * 
	 * @return
	 *   the array of cookie properties, or null if no cookies exist.
	 */
	public Cookie[] getCookies() {
		if(request != null) {
			return request.getCookies();
		}
		return null;
	}
	
	/**
	 * Adds a cookie to the client.
	 * 
	 * @param cookie
	 *   the cookie to be added to the client.
	 */
	public void setCookie(Cookie cookie) {
		if(response != null) {
			response.addProperty(cookie);
		}
	}
	
	/**
	 * Returns the underlying portlet request object.
	 * 
	 * @return
	 *   the underlying portlet request object.
	 */
	@Deprecated
	public PortletRequest getPortletRequest() {
		return request;
	}
	
	/**
	 * Returns the underlying portlet response object.
	 * 
	 * @return
	 *   the underlying portlet response object.
	 */
	@Deprecated
	public PortletResponse getPortletResponse() {
		return response;
	}
		
	/**
	 * Returns the underlying portlet session object.
	 * 
	 * @return
	 *   the underlying portlet session object.
	 */
	@Deprecated
	public PortletSession getPortletSession() {
		return request.getPortletSession();
	}
	
	@Deprecated
	public Event getEvent() {
		if(request != null && request instanceof EventRequest) {
			return ((EventRequest)request).getEvent();
		}
		return null;
	}
	

	/**
	 * Provides a pretty printed representation of the <code>ActionContext</code>,
	 * with all attributes, properties and parameters.
	 * 
	 * @return 
	 *   a pretty printed representation of the <code>ActionContext</code>
	 * @see 
	 *   java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		Map<String, Object> attributes = null;
		buffer.append("--------- APPLICATION ---------\n");
		attributes = getAttributes(Scope.APPLICATION);
		if(attributes != null) {
			for(Entry<String, Object> entry : attributes.entrySet()) {
				buffer.append("attribute\n");
				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
				buffer.append(String.format(" + value  : '%1$s'\n", entry.getValue()));
			}
		}
		buffer.append("----------- SESSION -----------\n");
		attributes = getAttributes(Scope.SESSION);
		if(attributes != null) {
			for(Entry<String, Object> entry : attributes.entrySet()) {
				buffer.append("attribute\n");
				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
				buffer.append(String.format(" + value  : '%1$s'\n", entry.getValue()));
			}
		}
		buffer.append("----------- REQUEST -----------\n");
		attributes = getAttributes(Scope.REQUEST);
		if(attributes != null) {
			for(Entry<String, Object> entry : attributes.entrySet()) {
				buffer.append("attribute\n");
				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
				buffer.append(String.format(" + value  : '%1$s'\n", entry.getValue()));
			}
		}
		buffer.append("---------- PARAMETERS ---------\n");
		Map<String, String[]> parameters = getParameters();
		if(parameters != null) {
			for(Entry<String, String[]> entry : parameters.entrySet()) {
				buffer.append("parameter\n");
				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
				buffer.append(" + values : ");
				for(String value : entry.getValue()) {
					buffer.append("'").append(value).append("', ");
				}
				buffer.append("\n");
			}
		}
		buffer.append("----------- PUBLIC ------------\n");
		parameters = getPublicParameters();
		if(parameters != null) {
			for(Entry<String, String[]> entry : parameters.entrySet()) {
				buffer.append("parameter\n");
				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
				buffer.append(" + values : ");
				for(String value : entry.getValue()) {
					buffer.append("'").append(value).append("', ");
				}
				buffer.append("\n");
			}
		}
		buffer.append("----------- PRIVATE -----------\n");
		parameters = getPublicParameters();
		if(parameters!= null) {
			for(Entry<String, String[]> entry : parameters.entrySet()) {
				buffer.append("parameter\n");
				buffer.append(String.format(" + key    : '%1$s'\n", entry.getKey()));
				buffer.append(" + values : ");
				for(String value : entry.getValue()) {
					buffer.append("'").append(value).append("', ");
				}
				buffer.append("\n");
			}
		}
		buffer.append("-------------------------------\n");
		return buffer.toString();
	}
}
