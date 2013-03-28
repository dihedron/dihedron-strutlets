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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.ActionInvocation;
import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
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
	 * @see 
	 *   org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws StrutletsException {
		logger.debug("injecting parameters into action");
		injectInputs(invocation);
		String result = invocation.invoke();
		extractOutputs(invocation);
		return result;
	}
	
	private void injectInputs(ActionInvocation invocation) {
		
		String methodName = invocation.getMethod();
				 
		Action action = invocation.getAction();
				
		Set<String> inputs = new HashSet<String>();
		
		// navigate the object class hierarchy to lookup the given method; if 
		// found, look for its @Invocable annotation and extract the list of input 
		// fields (if available) 
		Class<?> clazz = action.getClass();
		while(clazz != null && clazz != Object.class) {
			try {
				Method method = clazz.getDeclaredMethod(methodName);
				if(method.isAnnotationPresent(Invocable.class)) {
					Invocable annotation = method.getAnnotation(Invocable.class);
					inputs.addAll(Arrays.asList(annotation.inputs()));				
				}
				break;
			} catch (SecurityException e) {
				logger.error("security exception accessing method '{}' on class '{}'", methodName, clazz.getSimpleName());
			} catch (NoSuchMethodException e) {
				logger.error("method '{}' not found on class '{}'", methodName, clazz.getSimpleName());
			} finally {
				clazz = clazz.getSuperclass();
			}
		}
		
		// when we get here, either we have a list of inputs or we have non, in 
		// which case all fields marked with @In are assumed to be inputs; now 
		// we navigate the field hierarchy looking for the input values 
		
		clazz = action.getClass();
		Set<Field> fields = new HashSet<Field>();		
		while(clazz != null && clazz != Object.class) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));			
			clazz = clazz.getSuperclass();
		}
		
		for(Field field : fields) {
			if(field.isAnnotationPresent(In.class)) {				
				if(inputs.contains(field.getName()) || inputs.isEmpty()) {
					// the field is indicated as an input, and is available on
					// the action's class: go grab it!
					In annotation = field.getAnnotation(In.class);
					String parameter = annotation.value().length() > 0 ? annotation.value() : field.getName();
					String value = ActionContext.acquireContext().getFirstParameterValue(parameter);
					if(value == null || value.trim().length() == 0) {
						value = annotation.withDefault();
					}
				}
			}			
		}		
	}
	
	private void extractOutputs(ActionInvocation invocation) {
		
	}
}
