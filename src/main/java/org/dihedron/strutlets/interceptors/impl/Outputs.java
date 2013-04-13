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
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.exceptions.InterceptorException;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts the action method's outputs (as per the {@code @Invocable} annotation's
 * {@code outputs} fields), and stores their values into an output storage; the 
 * storage scope can be indicated in the {@code @Out}-annotated field through the
 * annotation's {@code scope} value, and the name under which it will be stored can
 * be altered by using the same annotatio's {@code value} attribute. 
 * 
 * @author Andrea Funto'
 */
public class Outputs extends Interceptor {

	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(Outputs.class);

	/**
	 * Scans the {@code Action}'s invocable method's fields for @Out annotations,
	 * and then invokes the output extraction method for each of them. 
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
			String result = invocation.invoke();
			extractOutputs(invocation);			
			return result;
		} catch(ReflectorException e) {
			throw new InterceptorException("error setting input fields", e);
		}		
	}
	
	/**
	 * It scans the fields to extract, and then invokes the field extraction 
	 * method for each of them.
	 * 
	 * @param invocation
	 *   the current invocation object.
	 * @throws ReflectorException
	 * @throws StrutletsException
	 */
	private void extractOutputs(ActionInvocation invocation) throws ReflectorException, StrutletsException {
		
		logger.trace("extracting outputs for method '{}' on action '{}'", invocation.getMethod(), invocation.getAction().getClass().getSimpleName());
		// get the method through reflection
		Set<Method> methods = Reflector.getMethods(invocation.getAction().getClass(), invocation.getMethod());
		if(methods.size() == 1) {
			// now get the output fields for the given method, as declared in the annotation
			String [] filter = {};
			Method method = (Method)methods.toArray()[0];
			if(method.isAnnotationPresent(Invocable.class)) {
				Invocable annotation = method.getAnnotation(Invocable.class);
				filter = annotation.outputs();
				// get the corresponding fields
				Set<Field> fields = Reflector.getFields(invocation.getAction().getClass(), filter);
				for(Field field : fields) {
					extractField(field, invocation);
				}		
			}
		}
	}

	/**
	 * For each output field, it checks the name under which is should be stored
	 * and the scope into which it will be set, athen extracts the value and
	 * puts it into the appropriate output scope, through reflection.
	 * 
	 * @param field
	 *   the field to analyse.
	 * @param invocation
	 *   the current invocation object.
	 * @throws ReflectorException
	 * @throws StrutletsException
	 */
	private void extractField(Field field, ActionInvocation invocation) throws ReflectorException, StrutletsException {
		logger.trace("looking up output storage for field '{}'", field.getName());
		if(field.isAnnotationPresent(Out.class)) {
			
			Out annotation = field.getAnnotation(Out.class);
			
			// get the name of the parameter to look up; if none provided in the annotation,
			// take the name of the field itself			
			String parameter = annotation.value().length() > 0 ? annotation.value() : field.getName();
			
			// now, depending on the declared scope, try to copy the parameter value into the appropriate context 
			Object value = null;
			Reflector reflector = new Reflector(invocation.getAction());
			value = reflector.getFieldValue(field.getName());
			if(value != null) {
				switch(annotation.scope()) {
				case RENDER:
					if(field.getType().isArray()) {				
						logger.trace("storing field '{}' as '{}' into scope '{}' (String[])", field.getName(), parameter, annotation.scope().name());
						ActionContext.setRenderParameter(parameter, (String [])value);
					} else {
						logger.trace("storing field '{}' as '{}' into scope '{}' (String)", field.getName(), parameter, annotation.scope().name());
						ActionContext.setRenderParameter(parameter, value.toString());
					}
					break;
				case REQUEST:
					value = reflector.getFieldValue(field.getName());				
					logger.trace("storing field '{}' as '{}' into scope '{}'", field.getName(), parameter, annotation.scope().name());
					ActionContext.setRequestAttribute(parameter, (String [])value);
					break;
				case SESSION:
					value = reflector.getFieldValue(field.getName());				
					logger.trace("storing field '{}' as '{}' into scope '{}'", field.getName(), parameter, annotation.scope().name());
					ActionContext.setSessionAttribute(parameter, (String [])value);
					break;
				case APPLICATION:
					value = reflector.getFieldValue(field.getName());				
					logger.trace("storing field '{}' as '{}' into scope '{}'", field.getName(), parameter, annotation.scope().name());
					ActionContext.setApplicationAttribute(parameter, (String [])value);
					break;
				default:
					logger.error("cannot store an output value into a '{}' scope: this is probably a bug!", annotation.scope().name());
					throw new StrutletsException("Cannot store an output message into a " + annotation.scope().name() + " scope: this is probably a bug!");
				}
			}
		}			
	}
}
