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

package org.dihedron.strutlets.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javassist.CannotCompileException;

import org.dihedron.strutlets.annotations.Out;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class ProxyFactoryTest {
	/**
	 * The logger.
	 */
	static final Logger logger = LoggerFactory.getLogger(ProxyFactoryTest.class);

	
	public String myMethod(@Out("parameter") $<Set<List<Map<String, Vector<String>>>>> parameter, String pippo, int plutot) {
		return null;		
	}
	
	/**
	 * Test method for {@link org.dihedron.strutlets.aop.ActionProxyFactory#addProxyMethod(java.lang.Class, java.lang.reflect.Method)}.
	 * @throws CannotCompileException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 */
	@Test
//	@Ignore
	public void testMakeProxyMethod() throws CannotCompileException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {

		
		Method[] methods = ProxyFactoryTest.class.getDeclaredMethods();
		
		for(Method method : methods) {
			
			if(!method.getName().equals("myMethod")) {
				continue;
			}
		
			Annotation[][] annotations = method.getParameterAnnotations();
			
			Type[] types = method.getGenericParameterTypes();
								
			for(int i = 0; i < types.length; ++i) {
				String description = getTypeAsString(types[i]);
				logger.trace("parameter type [{}]: '{}'", i, description);
				
				
//				if(types[i] instanceof ParameterizedType) {
//					ParameterizedType pt = (ParameterizedType)types[i];
//					Object object = pt.getActualTypeArguments()[0];
//					logger.trace("class of parameterised type is '{}'", object == null ? "null" : object.getClass().getCanonicalName());
//					if(object != null) {
//						if(object instanceof Class<?>) {
//							Class<?> gt = (Class<?>) object;
//							logger.trace("type: {}<{}>", ((Class<?>)pt.getRawType()).getCanonicalName(), gt.getCanonicalName());
//						} else if (object instanceof ParameterizedType) {
//							
//						} else {							
//							logger.trace("cannot treat parameterised type, skipping...");
//							continue;
//						}
//					} else {
//						logger.trace("cannot retrieve generics parameterised type, value is null");
//					}				
//				} else if(types[i] instanceof Class<?>){
//					logger.trace("type: {}", ((Class<?>)types[i]).getCanonicalName());
//				}				
			}
		}	
	}		
		
	private String getTypeAsString(Type type) {		
		String result = null;
		if(type instanceof Class<?>) {
			result = ((Class<?>)type).getCanonicalName();			
		} else if(type instanceof ParameterizedType) {
			
			StringBuilder buffer = new StringBuilder();
			
			// grab the name of the container class (e.g. List, Map...)
			ParameterizedType container = (ParameterizedType)type ;
			String containerType = ((Class<?>)container.getRawType()).getCanonicalName();
			logger.trace("container type: '{}'", containerType);
			buffer.append(containerType).append("<");
			// now grab the names of all generic types (those within <...>)
			Type[] generics = container.getActualTypeArguments();
			boolean first = true;
			for(Type generic : generics) {
				String genericType = getTypeAsString(generic);
				logger.trace("generic type: '{}'", genericType);
				buffer.append(first ? "" : ", ").append(genericType);
				first = false;
			}
			buffer.append(">");
			result = buffer.toString();
		}
		return result;
	}
	
	
}
