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

package org.dihedron.strutlets.interceptors.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.dihedron.reflection.Reflector;
import org.dihedron.reflection.ReflectorException;
import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.ActionInvocation;
import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.annotations.In.Scope;
import org.dihedron.strutlets.exceptions.InterceptorException;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Parameters extends Interceptor {

	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(Parameters.class);

	/**
	 * Measures and prints out the time it takes to execute the nested interceptors 
	 * (if any) and the action.
	 * 
	 * @param invocation
	 *   the current action invocation.
	 * @return
	 *   the result of the nested components' execution.
	 * @throws StrutletsException 
	 * @see 
	 *   org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws StrutletsException {
		logger.debug("injecting parameters into action");
		try {
			injectInputs(invocation);
			String result = invocation.invoke();
			extractOutputs(invocation);
			return result;
		} catch(ReflectorException e) {
			throw new InterceptorException("error setting input fields", e);
		}		
	}
	
	private void injectInputs(ActionInvocation invocation) throws ReflectorException {
		
		logger.trace("injecting inputs for method '{}' on action '{}'", invocation.getMethod(), invocation.getAction().getClass().getSimpleName());
		// get the method through reflection
		Set<Method> methods = Reflector.getMethods(invocation.getAction().getClass(), invocation.getMethod());
		if(methods.size() == 1) {
			// now get the input fields for the given method, as declared in the annotation
			String [] filter = {};
			Method method = (Method)methods.toArray()[0];
			if(method.isAnnotationPresent(Invocable.class)) {
				Invocable annotation = method.getAnnotation(Invocable.class);
				filter = annotation.inputs();
				// get the corresponding fields
				Set<Field> fields = Reflector.getFields(invocation.getAction().getClass(), filter);
				for(Field field : fields) {
					injectField(field, invocation);
				}		
			}
		}
	}
	
	private void injectField(Field field, ActionInvocation invocation) throws ReflectorException {
		logger.trace("looking up value of field '{}' in request", field.getName());
		if(field.isAnnotationPresent(In.class)) {
			
			In annotation = field.getAnnotation(In.class);
			
			// get the name of the parameter to look up; if none provided in the annotation,
			// take the name of the field itself			
			String parameter = annotation.value().length() > 0 ? annotation.value() : field.getName();
			
			// now, depending on the scope, try to locate the parameter in the appropriate context
			Scope scope = annotation.scope();
			Object value = null;
			do {
				// FORM scope is scanned first if specified (or if ANY is provided)
				if(scope == Scope.FORM || scope == Scope.ANY) {
					value = ActionContext.acquireContext().getParameterValues(parameter);
					if(value != null) {
						logger.trace("value for parameter '{}' found in FORM parameters: '{}'", parameter, value);
						break;
					}
				}
				// next comes the REQUEST scope (or if ANY is specified)
				if(scope == Scope.REQUEST || scope == Scope.ANY) {
					value = ActionContext.acquireContext().getRequestAttribute(parameter);
					if(value != null) {
						logger.trace("value for parameter '{}' found in REQUEST attributes: '{}'", parameter, value);
						break;
					}
				}
				// next comes the SESSION scope (or if ANY is specified)
				if(scope == Scope.SESSION || scope == Scope.ANY) {
					value = ActionContext.acquireContext().getSessionAttribute(parameter);
					if(value != null) {
						logger.trace("value for parameter '{}' found in SESSION attributes: '{}'", parameter, value);
						break;
					}
				}
				// last comes the APPLICATION scope (or if ANY is specified)
				if(scope == Scope.APPLICATION || scope == Scope.ANY) {
					value = ActionContext.acquireContext().getApplicationAttribute(parameter);
					if(value != null) {
						logger.trace("value for parameter '{}' found in APPLICATION attributes: '{}'", parameter, value);
						break;
					}
				}				
			} while(false);
			
			if(value != null) {
				Action action = invocation.getAction();
				if(field.getType() == value.getClass()) { 
					// if both are strings, or whatever, but equal, assign directly
					new Reflector(action).setFieldValue(field.getName(), value);
				} else if(field.getType().isArray() && value.getClass().isArray()){
					// both arrays, try to assign 
					new Reflector(action).setFieldValue(field.getName(), value);
				} else if(field.getType() == String.class && value.getClass().isArray()) {
					// pick just the first value
					new Reflector(action).setFieldValue(field.getName(), new Reflector(value).getElementAtIndex(0));
				}
			} else {
				logger.warn("no value found for field '{}' in scope '{}'", scope.name());
			}			
		}
	}
	
	private void extractOutputs(ActionInvocation invocation) throws ReflectorException {
		String methodName = invocation.getMethod();				 
		Action action = invocation.getAction();
		logger.trace("extracting outputs for method '{}' on action '{}'", methodName, action.getClass().getSimpleName());
		Set<Method> methods = Reflector.getMethods(action.getClass(), methodName);
//		if(methods.isEmpty()) {
//			logger.error("no method found with the given name ('{}') on action of class '{}'", methodName, action.getClass().getSimpleName());
//			return;
//		} else {
//			for(Method method: methods) {
//				logger.trace("method found: '{}'", method.getName());
//			}
//		}
		String [] filter = {};
		Method method = (Method) methods.toArray()[0];
		if(method.isAnnotationPresent(Invocable.class)) {
			Invocable annotation = method.getAnnotation(Invocable.class);
			filter = annotation.outputs();				
		}
		
		Set<Field> fields = Reflector.getFields(action.getClass(), filter);
		for(Field field : fields) {
			logger.trace("setting value of field '{}' in response", field.getName());
			if(field.isAnnotationPresent(Out.class)) {				
				Out annotation = field.getAnnotation(Out.class);
				String parameter = annotation.value().length() > 0 ? annotation.value() : field.getName();
				String value = (String)new Reflector(action).getFieldValue(field.getName());
				// TODO: this method only supports single values, implement switch on String[]
				ActionContext.acquireContext().setRenderParameter(parameter, value);
//				if(value == null || value.trim().length() == 0) {
//					logger.trace("parameter '{}' not available in request, using default '{}'", parameter, annotation.withDefault());
//					value = annotation.withDefault();
//				} else {
//					logger.trace("parameter '{}' available in request, using value '{}'", parameter, value);					
//				}
//				new Reflector(action).setFieldValue(field.getName(), value);
			}
		}		
		
	}
}
