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
package org.dihedron.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;

/**
 * @author Andrea Funto'
 */
public class TypesTest {
	
	@SuppressWarnings("unused")
	private void myMethod(
			Set<List<Map<String, Vector<String>>>> parameter1, 
			String parameter2, 
			int parameter3) {		
	}
	

	@Test
	public void test() {
		
		Method[] methods = TypesTest.class.getDeclaredMethods();
		
		for(Method method : methods) {
			
			if(!method.getName().equals("myMethod")) {
				continue;
			}
		
			Type[] types = method.getGenericParameterTypes();
			
			assertTrue(Types.isGeneric(types[0]));
			assertFalse(Types.isSimple(types[0]));
			assertTrue(Types.isOfClass(types[0],  Set.class));
			
			
			
			assertFalse(Types.isGeneric(types[1]));
			assertTrue(Types.isSimple(types[1]));
			assertTrue(Types.isOfClass(types[1],  String.class));

			assertFalse(Types.isGeneric(types[2]));
			assertTrue(Types.isSimple(types[2]));
			assertTrue(Types.isOfClass(types[2],  Integer.TYPE));
			
		}
//		fail("Not yet implemented");
	}

}
