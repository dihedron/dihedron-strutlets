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
package org.dihedron.commons.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A library of utility methods for <code>Type</code> manipulation.
 * 
 * @author Andrea Funto'
 */
public class Types {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Types.class);
	
	/**
	 * Returns the string representation of a type; all class names are fully 
	 * qualified, and generics are properly resolved, including their parameter
	 * types.
	 *  
	 * @param type
	 *   the type to describe.
	 * @return
	 *   a textual description of the type.
	 */
	public static String getAsString(Type type) {
		String result = null;
		if(isSimple(type)) {
			result = ((Class<?>)type).getCanonicalName();						
		} else if(isGeneric(type)) {
			StringBuilder buffer = new StringBuilder();
			
			// grab the name of the container class (e.g. List, Map...)
			ParameterizedType container = (ParameterizedType)type ;
			String containerType = ((Class<?>)container.getRawType()).getCanonicalName();
			buffer.append(containerType).append("<");

			// now grab the names of all generic types (those within <...>)
			Type[] generics = container.getActualTypeArguments();
			boolean first = true;
			for(Type generic : generics) {
				String genericType = getAsString(generic);
				buffer.append(first ? "" : ", ").append(genericType);
				first = false;
			}
			buffer.append(">");
			result = buffer.toString();
		}
		return result;		
	}
	
	public static String getAsParametricType(Type type) {
		String result = null;
		if(isSimple(type)) {
			result = ((Class<?>)type).getCanonicalName();
		} else if(isGeneric(type)) {
			StringBuilder buffer = new StringBuilder();
			ParameterizedType container = (ParameterizedType)type ;
			String containerType = ((Class<?>)container.getRawType()).getCanonicalName();
			buffer.append(containerType).append("<");
			// now grab the names of all generic types (those within <...>)
			Type[] generics = container.getActualTypeArguments();
			for(int i = 0; i < generics.length; ++i) {
				buffer.append(i == 0 ? "" : ", ").append("?");
			}
			buffer.append(">");
			result = buffer.toString();			
		}
		return result;
	}
	
	public static String getAsRawType(Type type) {
		String result = null;
		if(isSimple(type)) {
			result = ((Class<?>)type).getCanonicalName();
		} else if(isGeneric(type)) {
			ParameterizedType container = (ParameterizedType)type ;
			result = ((Class<?>)container.getRawType()).getCanonicalName();
		}
		return result;
	}
	
	
	/**
	 * Returns the parameter types for a generic container, e.g. it would return 
	 * <code>{String, int}</code> for a <code>Map&lt;String, int&gt;</code>.
	 * 
	 * @param generic
	 *   the generic class.
	 * @return
	 *   the parameter types of a generic container, null otherwise.
	 *   
	 */
	public static Type[] getParameterTypes(Type generic) {
		Type[] types = null;
		if(isGeneric(generic)) {
			types = ((ParameterizedType)generic).getActualTypeArguments();
		}
		return types;
	}
	
	/**
	 * Checks whether the given type represents a generics.
	 *  
	 * @param type
	 *   the type to check for genericity.
	 * @return
	 *   whether the type represents a generics class.
	 */
	public static boolean isGeneric(Type type) {
		return (type instanceof ParameterizedType);
	}
	
	/**
	 * Returns whether the type represents a simple type (e.g. a class).
	 *  
	 * @param type
	 *   the type to check.
	 * @return
	 *   whether the type represents a simple type (e.g. a class).
	 */
	public static boolean isSimple(Type type) {
		return (type instanceof Class<?>);
	}
	
	/**
	 * Checks if the given type and the give class are the same; this check is 
	 * trivial for simple types (such as <code>java.lang.String</code>), less so 
	 * for generic types (say, <code>List&lt;String&gt;</code>), whereby the 
	 * generic (<code>List</code> in the example) is extracted before testing
	 * against the other class.
	 * 
	 * @param type
	 *   the type to be tested.
	 * @param clazz
	 *   the other class.
	 */
	public static boolean isOfClass(Type type, Class<?> clazz) {
		if(isSimple(type)) {
			logger.trace("simple: {}", ((Class<?>)type));
			return ((Class<?>)type) == clazz;
		} else if(isGeneric(type)) {
			logger.trace("generic: {}", (((ParameterizedType)type).getRawType()));
			return (((ParameterizedType)type).getRawType()) == clazz;
		}
		return false;
	}
	
	/**
	 * Private conctructor, to prevent improper instantiation.
	 */
	private Types() {
	}
}
