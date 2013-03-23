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


/**
 * Utility package for string operations.
 * 
 * @author Andrea Funto'
 */
public class Strings {

	/**
	 * Checks whether the given string is neither null nor blank.
	 * 
	 * @param string
	 *   the string to be checked.
	 * @return
	 *   <code>true</code> if the string is not null and has some content besides 
	 *   blank spaces.
	 */
	public static boolean isValid(String string) {
		return (string != null && string.trim().length() > 0);
	}

	/**
	 * Trims the input string if it is not null.
	 * 
	 * @param string
	 *   the string to be trimmed if not null.
	 * @return
	 *   the trimmed string, or null.
	 */
	public static String trim(String string) {
		if(string != null) {
			return string.trim();
		}
		return string;
	}
	
	/**
	 * Returns a safe concatenation of the input strings, or null if all strings 
	 * are null.
	 * 
	 * @param strings
	 *   the set of string to concatenate.
	 * @return
	 *   the concatenation of the input strings, or null if all strings are null.
	 */
	public static String concatenate(String... strings) {
		StringBuilder builder = new StringBuilder();
		builder.setLength(0);
		for(String string : strings) {
			if(string != null) {
				builder.append(string);
			}
		}
		if(builder.length() > 0) {
			return builder.toString();
		}
		return null;
	}
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private Strings() {
	}	
}
