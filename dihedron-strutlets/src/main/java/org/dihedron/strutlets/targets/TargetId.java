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

package org.dihedron.strutlets.targets;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.portlet.PortletRequest;

import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.Strutlets;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class representing uniquely an invocation target.
 *  
 * @author Andrea Funto'
 */
public class TargetId implements Serializable {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8421583323942135542L;

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(TargetId.class);
	
    /**
     * Retrieves the the current target from one of the places where it might 
     * have been set by the requester:<ol>
     * <li>in the {@code STRUTLETS_TARGET} render parameter, where it might
     * have been set by a prior action execution (see {@code #doProcess(String, 
     * PortletRequest, StateAwareResponse)}; if this parameter is set, then the 
     * current methodName is being invoked as parte  following render phase and it 
     * should render the appropriate JSP page according to the given action's 
     * result, as per the {@code EXECUTION_RESULT} render parameter, otherwise 
     * the process goes on</li>
     * <li>in the {@code javax.portlet.action} parameter, which is the 
     * parameter to use to have a render URL invoke some business logic prior
     * to its render phase, e.g. to query a database and then render the query 
     * results</li>
     * <li>if nothing valid is found under the previous parameter, then it checks 
     * if the name of a JSP page was provided in LifeRay's {@code jspPage}
     * parameter: by default it should be used to provide the name of web page 
     * (JSP), but this frameworks extends its use to address a target prior to 
     * the rendering of a web page.</li>
     * </ol>
     * Once the target name has been retrieved from any of the aforementioned 
     * places, this method creates a new {@code TargetId} object and returns it.
     * 
     * @param request
     *   the portlet request.
     * @return
     *   the {@code TargetId} object representing the target of the request, or 
     *   null if none valid found. 
     */
    public static final TargetId makeFromRequest(PortletRequest request) {
    	TargetId result = null;
    	String target = null;
 
    	do {
    		target = request.getParameter(Strutlets.STRUTLETS_TARGET);
    		if(Strings.isValid(target) && isValidTarget(target)) {
    			logger.trace("valid target '{}' available through STRUTLETS_TARGET parameter", target);
    			break;
    		}
    		
    		target = request.getParameter(Strutlets.PORTLETS_TARGET);
    		if(Strings.isValid(target) && isValidTarget(target)) {
    			logger.trace("valid target '{}' available through PORTLET_TARGET parameter", target);
    			break;
    		}
    		
    		target = request.getParameter(Strutlets.LIFERAY_TARGET);
    		if(Strings.isValid(target) && isValidTarget(target)) {
    			logger.trace("valid target '{}' available through LIFERAY_TARGET parameter", target);
    			break;
    		}
    		
    		target = null;
    		logger.trace("no valid target in request");
    	} while(false);
    	
    	if(isValidTarget(target)) {
    		try {
				result = new TargetId(target);
			} catch (StrutletsException e) {
				logger.warn("you should never see this message!");
			}
    	}
    	
		return result;
	}
    
	/**
	 * Checks whether the given string represents a valid target specification.
	 * 
	 * @param string
	 *   a string to be checked for compliance with target specifications; valid 
	 *   targets specifications are in the form "MyAction!myMethod".
	 * @return
	 *   whether the given string complies with a target specification. 
	 */
	public static final boolean isValidTarget(String string) {
		return Strings.isValid(string) && REGEX.matches(string);
	}
		
	/**
	 * The name of the action.
	 */
	private String actionName;
	
	/**
	 * The name of the method.
	 */
	private String methodName;
	
	/**
	 * Constructor.
	 * 
	 * @param target
	 *   a string representing the target identifier, with or without the method 
	 *   name; if no method is specified, the default method ("execute") is 
	 *   automatically selected. This parameter must not be null or blank.
	 *   
	 * @throws StrutletsException 
	 *   if the target id is blank or null.
	 */
	public TargetId(String target) throws StrutletsException {
		this.actionName = getActionName(target);
		this.methodName = getMethodName(target);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param actionName
	 *   the name of the action.
	 * @param methodName
	 *   the name of the method.
	 * @throws StrutletsException
	 *   if the name of the action is null or blank. 
	 *
	 */
	public TargetId(String actionName, String methodName) throws StrutletsException {
		if(!Strings.isValid(actionName)) {
			logger.error("invalid action name in target id creation");
			throw new StrutletsException("Invalid action name specified");
		}
		this.actionName = actionName.trim();
		this.methodName = Strings.isValid(methodName) ? methodName.trim() : DEFAULT_METHOD_NAME;
	}
	
	/**
	 * Constructor; the target identifier has two components:<ol>
	 * <li>The action name, which can be takes from the alias specified in the
	 * <code>@Action</code> annotation, if valid, or the simple name of the class
	 * implementing the target</li>
	 * <li>the name of the method, always takes as such</li>
	 * </ol>.
	 *
	 * @param action
	 *   the action class.
	 * @param method
	 *   the action method.
	 */
	public TargetId(Class<?> action, Method method) {
		Action annotation = action.getAnnotation(Action.class);
		if(Strings.isValid(annotation.alias())) {
			logger.trace("getting target 'class' component for '{}' from alias in annotation: '{}'", action.getSimpleName(), annotation.alias());
			this.actionName = annotation.alias();
		} else {
			logger.trace("getting target 'class' component from class name: '{}'", action.getSimpleName());
			this.actionName = action.getSimpleName();
		}
		this.methodName = method.getName();
	}
	
	/**
	 * Returns the name of the action.
	 * 
	 * @return
	 *   the name of the action.
	 */
	public String getActionName() {
		return actionName;
	}
	
	/**
	 * Returns the name of the method.
	 * 
	 * @return
	 *   the name of the method.
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * Returns the string corresponding to the current target identifier, as a 
	 * concatenation of the action name, a bang ("!") and the name of the method,
	 * e.g. {@code MyAction!myMethod}.
	 * 
	 * @see 
	 *   java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return actionName + METHOD_SEPARATOR + methodName;
	}
	
	/**
	 * Returns the hash code of the current target identifier, to enable use as a 
	 * unique key in a map.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	/**
	 * Returns {@code true} if and only if the other object is a {@code TargetId}
	 * and its string representation corresponds in all aspects to this object's.
	 *  
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof TargetId && ((TargetId)other).toString().equals(this.toString()));
	}
	
	/**
	 * The character separating action and methodName names in the target id.
	 */
	public static final String METHOD_SEPARATOR = "!";
	
	/**
	 * The name of the default <code>ActionInfo</code> methodName.
	 */
	public static final String DEFAULT_METHOD_NAME = "execute";	
	
	/**
	 * A Java regular expression matching a combination of action name and methodName 
	 * name; the original expression is 
	 * <code>^\s*([A-Z]{1,}[a-zA-Z0-9]*)(?:\s*!\s*([a-z]{1,}[a-zA-Z0-9]{1,})){0,1}\s*$</code>
	 * and matches names of the form <code>MyAction!myMethod</code>, where
	 * the action identifier complies with the rules for Java class names and
	 * the methodName identifier complies with the best practices for Java methods
	 * (starting with a lowercase alphabetic character, followed by any alphanumeric
	 * character. 
	 */
	private static final Regex REGEX = 
			new Regex("^\\s*([A-Z]{1,}[a-zA-Z0-9]*)(?:\\s*!\\s*([a-z]{1,}[a-zA-Z0-9]{1,})){0,1}\\s*$");
	
	/**
	 * Given the target specification in the &lt;action&gt;!&lt;methodName&gt;
	 * form (e.g. "MyAction!myMethod", where the methodName part is optional), returns 
	 * the name of the action ("MyAction" in the above example).
	 * 
	 * @param target
	 *   the target specification, including the method name or not.
	 * @return
	 *   the action name.
	 * @throws StrutletsException 
	 *   if the target is a null or blank string.
	 */
	private static final String getActionName(String target) throws StrutletsException {	
		String actionName = null;
		if(Strings.isValid(target)) {
			if(target.contains(METHOD_SEPARATOR)) {
				actionName = target.substring(0, target.indexOf(METHOD_SEPARATOR)).trim();
			} else {
				actionName = target.trim();
			}
			logger.trace("action name: '{}'", actionName);
			return actionName;			
		} else {
			throw new StrutletsException("Invalid target specified");
		}
	}
	
	/**
	 * Given the target specification in the &lt;action&gt;!&lt;methodName&gt;
	 * form (e.g. "MyAction!myMethod"), returns the name of the methodName ("myMethod" 
	 * in the above example); if the name of the methodName is empty, it returns the
	 * default methodName ("execute").
	 * 
	 * @param target
	 *   the action target specification, including the methodName.
	 * @return
	 *   the methodName name, or null if the target is incomplete.
	 */
	private static final String getMethodName(String target) {
		String methodName = null;
		if(Strings.isValid(target) && target.contains(METHOD_SEPARATOR)) {
			methodName = target.substring(target.indexOf(METHOD_SEPARATOR) + 1).trim();
		}
		if(!Strings.isValid(methodName)) {
			methodName = DEFAULT_METHOD_NAME;
		}
		logger.trace("method name: '{}'", methodName);
		return methodName;
	}
}
