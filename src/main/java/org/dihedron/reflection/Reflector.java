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

package org.dihedron.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper to access object properties and method through reflection.
 * 
 * @author Andrea Funto'
 */
public class Reflector {

	/**
	 * Default value for whether the field access should be through getter methods
	 * or by reading the object field directly (value: <code>false</code>, meaning 
	 * that the fields are read directly through reflection, bypassing their getter 
	 * methods).
	 */
	public static final boolean DEFAULT_USE_GETTER = false;
	
	/**
	 * Default value for whether the non-public fields and methods should be
	 * made available through the inspector (value: <code>true</code>, meaning
	 * that fields and methods will be accessed regardless of their being protected
	 * or private).
	 */
	public static final boolean DEFAULT_EXPOSE_PRIVATE_FIELDS = true;
	
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Reflector.class);
	
	/**
	 * The object under inspection.
	 */
	private Object object;
	
	/**
	 * Whether fields should be accessed using their getter method.
	 */
	private boolean useGetter = DEFAULT_USE_GETTER;
	
	/**
	 * Whether private and protected fields and methods should be directly 
	 * accessed (if <code>true</code>) or the getter and setter methods should 
	 * be used instead (if <code>false</code>).
	 */
	private boolean exposePrivateFields = DEFAULT_EXPOSE_PRIVATE_FIELDS;
	
	/**
	 * Returns the set of instance fields of the given class, including those 
	 * inherited from the super-classes.
	 * 
	 * @param clazz
	 *   the class whose fields are being retrieved.
	 * @param filter
	 *   an optional set of names for the fields to be looked up; only fields 
	 *   with those names will be returned.
	 * @return
	 *   the set of fields from the given class and its super-classes.
	 */
	public static Set<Field> getFields(Class<?> clazz, String... filter) {
		Set<Field> fields = new HashSet<Field>();
		Set<String> acceptable = new HashSet<String>();
		acceptable.addAll(Arrays.asList(filter));
		
		Class<?> cursor = clazz;
		while(cursor != null && cursor != Object.class) {
			Field[] array = cursor.getDeclaredFields();
			for(Field field : array) {
				if(!Modifier.isStatic(field.getModifiers())) {
					if(acceptable.isEmpty() || acceptable.contains(field.getName())) {					
						logger.trace("adding field '{}' in class '{}' to instance fields", field.getName(), cursor.getSimpleName());
						fields.add(field);
					}
				}
			}
			cursor = cursor.getSuperclass();
		}		
		return fields;
	}
	
	/**
	 * Returns the set of class (static) fields of the given class, including those 
	 * inherited from the super-classes.
	 * 
	 * @param clazz
	 *   the class whose fields are being retrieved.
	 * @param filter
	 *   an optional set of names for the fields to be looked up; only fields 
	 *   with those names will be returned.
	 * @return
	 *   the set of static fields from the given class and its super-classes.
	 */
	public static Set<Field> getClassFields(Class<?> clazz, String... filter) {
		Set<Field> fields = new HashSet<Field>();
		Set<String> acceptable = new HashSet<String>();
		acceptable.addAll(Arrays.asList(filter));

		Class<?> cursor = clazz;
		while(cursor != null && cursor != Object.class) {
			Field[] array = cursor.getDeclaredFields();
			for(Field field : array) {
				if(Modifier.isStatic(field.getModifiers())) {
					if(acceptable.isEmpty() || acceptable.contains(field.getName())) {
						logger.trace("adding field '{}' in class '{}' to static fields", field.getName(), cursor.getSimpleName());
						fields.add(field);
					}
				}
			}
			cursor = cursor.getSuperclass();
		}		
		return fields;
	}	
	
	/**
	 * Returns the set of instance methods of the given class, including those 
	 * inherited from the super-classes.
	 * 
	 * @param clazz
	 *   the class whose methods are being retrieved.
	 * @param filter
	 *   an optional set of names for the methods to be looked up; only methods 
	 *   with those names will be returned.
	 * @return
	 *   the set of methods from the given class and its super-classes.
	 */
	public static Set<Method> getMethods(Class<?> clazz, String... filter) {
		Set<Method> methods = new HashSet<Method>();
		Set<String> acceptable = new HashSet<String>();
		acceptable.addAll(Arrays.asList(filter));
		
		Class<?> cursor = clazz;
		while(cursor != null && cursor != Object.class) {
			Method[] array = cursor.getDeclaredMethods();
			for(Method method : array) {
				if(!Modifier.isStatic(method.getModifiers())) {
					logger.debug("checking method '{}'", method.getName());
					if(acceptable.isEmpty() || acceptable.contains(method.getName())) {
						logger.debug("adding method '{}' in class '{}' to instance methods", method.getName(), cursor.getSimpleName());
						methods.add(method);
					}
				}
			}
			cursor = cursor.getSuperclass();
		}		
		return methods;
	}	
	
	/**
	 * Returns the set of class methods of the given class, including those 
	 * inherited from the super-classes.
	 * 
	 * @param clazz
	 *   the class whose methods are being retrieved.
	 * @param filter
	 *   an optional set of names for the methods to be looked up; only methods 
	 *   with those names will be returned.
	 * @return
	 *   the set of class methods from the given class and its super-classes.
	 */
	public static Set<Method> getClassMethods(Class<?> clazz, String... filter) {
		Set<Method> methods = new HashSet<Method>();
		Set<String> acceptable = new HashSet<String>();
		acceptable.addAll(Arrays.asList(filter));
		
		Class<?> cursor = clazz;
		while(cursor != null && cursor != Object.class) {
			Method[] array = cursor.getDeclaredMethods();
			for(Method method : array) {
				if(Modifier.isStatic(method.getModifiers())) {
					if(acceptable.isEmpty() || acceptable.contains(method.getName())) {
						logger.trace("adding method '{}' in class '{}' to static methods", method.getName(), cursor.getSimpleName());
						methods.add(method);
					}
				}
			}
			cursor = cursor.getSuperclass();
		}		
		return methods;
	}	
	
	/**
	 * Constructor.
	 * 
	 * @param object
	 *   the object under inspection.
	 */
	public Reflector(Object object) {
		this(object, DEFAULT_USE_GETTER);
	}

	/**
	 * Constructor.
	 * 
	 * @param object
	 *   the object under inspection. 
	 * @param useGetter
	 *   whether fields should be accessed only through their getter.
	 */
	public Reflector(Object object, boolean useGetter) {
		this(object, useGetter, DEFAULT_EXPOSE_PRIVATE_FIELDS);
	}	

	/**
	 * Constructor.
	 * 
	 * @param object
	 *   the object under inspection. 
	 * @param useGetter
	 *   whether fields should be accessed only through their getter.
	 * @param expose
	 *   whether private and protected fields and methods should be made available
	 *   through the inspector as if they were public, by means of on-the-fly
	 *   transparent unprotection.
	 */
	public Reflector(Object object, boolean useGetter, boolean expose) {
		this.object = object;
		this.useGetter = useGetter;
		this.exposePrivateFields = expose;
	}	

	/**
	 * Returns if access to fields is restricted to getter method invocation.
	 * 
	 * @return
	 *   <code>true</code> if fields are to be read only by calling their getter 
	 *   method, <code>false</code> if their value can be read directly through 
	 *   reflection, actually bypassing their getter method.
	 */
	public boolean isUseGetter() {
		return useGetter;
	}

	/**
	 * Sets whether the inspector should go through a getter methdo invocation to
	 * access the object's fields.
	 * 
	 * @param useGetter
	 *   set this to <code>true</code> if fields are to be read only by calling 
	 *   their getter method, to <code>false</code> if their value can be read 
	 *   directly through reflection, actually bypassing their getter method.
	 */
	public void setUseGetter(boolean useGetter) {
		this.useGetter = useGetter;
	}

	/**
	 * Returns whether the inspector will make private and protected methods and 
	 * fields available as if they were public.
	 * 
	 * @return
	 *   <code>true</code> if the inspector will treat public and private/protected
	 *   methods and fields alike, <code>false</code> if private and protected fields
	 *   and methods will be kept so and not exposed through the inspector. 
	 */
	public boolean isExposePrivateFields() {
		return exposePrivateFields;
	}

	/**
	 * Sets whether the inspector will make private and protected fields and 
	 * methods available to callers.
	 * 
	 * @param expose
	 *   set this to <code>true</code> to gain access to protected and private 
	 *   fields and methods through the inspector, to <code>false</code> to
	 *   keep private things private.
	 */
	public void setExposePrivateFields(boolean expose) {
		this.exposePrivateFields = expose;
	}

	/**
	 * Retrieves the value of a field.
	 * 
	 * @param fieldName
	 *   the name of the field.
	 * @return
	 *   the field value.
	 * @throws ReflectorException 
	 */
	public Object getFieldValue(String fieldName) throws ReflectorException {
		
		assert fieldName != null : "error: field name must not be null";
		
		Object result = null;
		String name = fieldName.trim();
		if(useGetter) {
			logger.info("accessing value using getter");
			String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);			
			result = invoke(methodName);
		} else {
			logger.info("accessing value through exposePrivateFields field reading");
			Field field = null;			
			boolean needReprotect = false;
			try {
				field = object.getClass().getDeclaredField(name);				
				if(exposePrivateFields) {
					needReprotect = unprotect(field);
				}
				result = field.get(object);
			} catch (Exception e) {
				logger.error("error accessing field '" + fieldName + "'", e);
				throw new ReflectorException("error accessing field '" + fieldName + "'", e);
			} finally {
				if(needReprotect) {				
					protect(field);
				}
			}
		}
		return result;		
	}
	
	/**
	 * Sets the value of the field.
	 * 
	 * @param fieldName
	 *   the name of the field.
	 * @param value
	 *   the new value of the field.
	 * @throws ReflectorException 
	 */
	public void setFieldValue(String fieldName, Object value) throws ReflectorException  {
		assert fieldName != null : "error: field name must not be null";

		String name = fieldName.trim();
		if(useGetter) {
			logger.info("accessing value using setter");
			String methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			invoke(methodName);
		} else {
			logger.info("accessing value through exposePrivateFields field reading");
			Field field = null;
			boolean needReprotect = false;
			try {
				field = object.getClass().getDeclaredField(name);
				if(exposePrivateFields) {
					needReprotect = unprotect(field);
				}
				field.set(object, value);
			} catch (Exception e) {
				logger.error("error accessing field '" + fieldName + "'", e);
				throw new ReflectorException("error accessing field '" + fieldName + "'", e);
			} finally {				
				if(needReprotect) {				
					protect(field);
				}		
			}
		}		
	}
	
	/**
	 * If the object is an array or a subclass of <code>List</code>, it retrieves
	 * the element at the given index.
	 * 
	 * @param index
	 *   the offset of the element to be retrieved; this value can be positive 
	 *   (and the offset is calculated from the start of the array or list), or
	 *   negative, in which case the offset is calculated according to the rule
	 *   that element -1 is the last, -2 is the one before the last, etc.
	 * @return
	 *   the element at the given index.
	 * @throws ReflectorException 
	 *   if the index is not valid for the given object.
	 */
	public Object getElementAtIndex(int index) throws ReflectorException   {		
		Object result = null;
		if(object.getClass().isArray()) {
			result = Array.get(object, translateArrayIndex(index, getArrayLength()));
		} else if (object instanceof List<?>){
			result = ((List<?>)object).get(translateArrayIndex(index, getArrayLength()));
		} else {
			throw new ReflectorException("object is not an array or a list");
		}
		return result;
	}
	
	/**
	 * Sets the value of the n-th element in an array or a list.
	 * 
	 * @param index
	 *   the offset of the element to be set; this value can be positive 
	 *   (and the offset is calculated from the start of the array or list), or
	 *   negative, in which case the offset is calculated according to the rule
	 *   that element -1 is the last, -2 is the one before the last, etc.
	 * @throws ReflectorException 
	 *   if the object is not a list or an array.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setElementAtIndex(int index, Object value) throws ReflectorException   {
		
		if(object.getClass().isArray()) {
			 Array.set(object, translateArrayIndex(index, getArrayLength()), value);
		} else if (object instanceof List<?>){
			((List)object).set(index, value);
		} else {
			throw new ReflectorException("object is not an array or a list");
		}		
	}
	
	/**
	 * Returns the number of elements in the array or list object;
	 * 
	 * @return
	 *   the number of elements in the array or list object.
	 * @throws ReflectorException 
	 *   if the object is not a list or an array.
	 */
	public int getArrayLength() throws ReflectorException   {
		int length = 0;
		if(object.getClass().isArray()) {
			length = Array.getLength(object);
		} else if (object instanceof List<?>){
			length = ((List<?>)object).size();
		} else {
			throw new ReflectorException("object is not an array or a list");
		}
		return length;
	}
	
	/**
	 * If the object is a <code>Map</code> or a subclass, it retrieves the 
	 * element corresponding to the given key.
	 * 
	 * @param key
	 *   the key corresponding to the element to be retrieved.
	 * @return
	 *   the element corresponding to the given key, or <code>null</code> if none 
	 *   found.
	 * @throws ReflectorException 
	 *   if the object is not <code>Map</code>.
	 */
	public Object getValueForKey(Object key) throws ReflectorException   {		
		Object result = null;
		if(object instanceof Map) {
			result = ((Map<?, ?>)object).get(key);
		} else {
			throw new ReflectorException("object is not a map");
		}
		return result;
	}

	/**
	 * If the object is a <code>Map</code> or a subclass, it sets the 
	 * element corresponding to the given key.
	 * 
	 * @param key
	 *   the key corresponding to the element to be set.
	 * @param value
	 *   the value to be set.
	 * @throws ReflectorException
	 *   if the object is not <code>Map</code>.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValueForKey(Object key, Object value) throws ReflectorException   {
		if(object instanceof Map) {
			((Map)object).put(key, value);
		} else {
			throw new ReflectorException("object is not a map");
		}
	}
	
	
	/**
	 * Invokes a method on the object under inspection.
	 * 
	 * @param methodName
	 *   the name of the method.
	 * @param args
	 *   the optional method arguments.
	 * @return
	 *   the return value of the given method.
	 * @throws ReflectorException 
	 *   if any of the intermediate reflection methods raises a problem during
	 *   the object access.
	 */
	public Object invoke(String methodName, Object... args) throws ReflectorException  {
		assert methodName != null : "error: method name must not be null";
		
		Method method = null;
		Object result = null;
		boolean needReprotect = false;
		try {
			method = object.getClass().getMethod(methodName);			
			if(exposePrivateFields) {
				needReprotect = unprotect(method);
			}
			result = method.invoke(object, args);
		} catch (Exception e) {
			logger.error("error invoking method '" + methodName + "'", e);
			throw new ReflectorException("error invoking method '" + methodName + "'", e);
		} finally {	
			if(needReprotect) {
				protect(method);
			}
		}
		return result;
	}
	
	/**
	 * Checks if the given field or method is protected or private, and if so
	 * makes it publicly accessible.
	 * 
	 * @param accessible
	 *   the field or method to be made public.
	 * @return
	 *   <code>true</code> if the field or method had to be modified in order to
	 *   be made accessible, <code>false</code> if no change was needed.
	 */
	public boolean unprotect(AccessibleObject accessible) {
		if(!accessible.isAccessible()) {
			accessible.setAccessible(true);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given field or method is public, and if so makes it 
	 * unaccessible.
	 * 
	 * @param accessible
	 *   the field or method to be made private.
	 * @return
	 *   <code>true</code> if the field or method had to be modified in order to
	 *   be made private, <code>false</code> if no change was needed.
	 */
	public boolean protect(AccessibleObject accessible) {
		if(accessible.isAccessible()) {
			accessible.setAccessible(false);
			return true;
		}
		return false;
	}
	
	/**
	 * Utility method that translates an array index, either positive or negative, 
	 * into its positive representation. The method ensures that the index is within
	 * array bounds, then it translates negatiuve indexes to their positive 
	 * counterparts according to the simple rule that element at index -1 is the 
	 * last element in the array, index -2 is the one before the last, and so on.
	 *  
	 * @param index
	 *   the element offset within the array or the list.
	 * @param length
	 *   the length of the array or the list.
	 * @return
	 *   the actual (positive) index of the element.
	 */
	private int translateArrayIndex(int index, int length) {
		assert (index > 0 ? index < length : Math.abs(index) <= Math.abs(length)) : "index must be less than number of elements";
		int translated = index;
		
		if(!(translated > 0 ? translated < length : Math.abs(translated) <= Math.abs(length))){
			logger.error("index {} is out of bounds", translated);
			throw new ArrayIndexOutOfBoundsException();
		}
		if(translated < 0) {
			translated = length + translated;
		}
		return translated;
	}
}
