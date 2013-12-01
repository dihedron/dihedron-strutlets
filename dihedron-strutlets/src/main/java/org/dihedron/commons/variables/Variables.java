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
package org.dihedron.commons.variables;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dihedron.commons.regex.Regex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Variables {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Variables.class);
	
	/**
	 * Whether the variable pattern matching is applied in a case sensitive way
	 * by default.
	 */
	public static final boolean DEFAULT_CASE_SENSITIVE = true;
	
	/**
	 * Regular expression to identify scalar variables.
	 */
	private static final String VARIABLE_PATTERN = "(?:\\$\\{([A-Za-z_][\\-A-Za-z_0-9]*)\\})";

	
	/**
	 * Matches all variables (according to the ${[a-zA-Z_][a-zA-Z0-9_\-]*} pattern)
	 * and replaces them with the value provided by the set of value provides.
	 * The processing is repeated until no more valid variable names can be found 
	 * in the input text, or no more variables can be bound (e.g. when some values 
	 * are not available); the match is performed in a case sensitive fashion. 
	 * 
	 * @param text
	 *   the text possibly containing variable identifiers.
	 * @param providers
	 *   a set of zero or more value providers.
	 * @return
	 *   the text with all variables bound.
	 */
	public static final String replaceVariables(String text, ValueProvider... providers) {
		return replaceVariables(text, DEFAULT_CASE_SENSITIVE, providers);
	}
	
	/**
	 * Matches all variables (according to the ${[a-zA-Z_][a-zA-Z0-9_\-]*} pattern)
	 * and replaces them with the value provided by the set of value provides.
	 * The processing is repeated until no more valid variable names can be found 
	 * in the input text, or no more variables can be bound (e.g. when some values 
	 * are not available). 
	 * 
	 * @param text
	 *   the text possibly containing variable identifiers.
	 * @param caseSensitive
	 *   whether the variable names should be treated in a case sensitive way
	 *   (default: true).  
	 * @param providers
	 *   a set of zero or more value providers.
	 * @return
	 *   the text with all variables bound.
	 */
	public static final String replaceVariables(String text, boolean caseSensitive, ValueProvider... providers) {
		Regex regex = new Regex(VARIABLE_PATTERN, caseSensitive);
		List<String[]> variables = null;
		
		Set<String> unboundVariables = new HashSet<String>();
		
		boolean oneVariableBound = true;
		while(oneVariableBound && (variables = regex.getAllMatches(text)).size() > 0) {
			oneVariableBound = false;			
			logger.trace("analysing text: '{}'", text);
			for(String[] groups : variables) {
				String variable = groups[0];
				logger.trace("... handling variable '{}'...", variable);
				String value = null;
				for(ValueProvider provider : providers) {
					value = provider.onVariable(variable);
					if(value != null) {
						logger.trace("... replacing variable '{}' with value '{}'", value);
						text = text.replace("${" + variable +"}", value);
						logger.trace("... text is now '{}'", text);
						oneVariableBound = true;
						break;
					}
				}
				if(value == null) {
					unboundVariables.add(variable);
				}
			}
		}
		return text;
	}
	 
	
	/**
	 * Private constructor to ensure library is never instantiated.
	 */
	private Variables() {
	}
}
