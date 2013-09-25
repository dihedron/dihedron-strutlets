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


import java.util.ArrayList;
import java.util.List;

/**
 * Provider a more flexible implementation of Java's 
 * StringTokenizer, allowing for other types of delimiters
 * besides \r\t\n.
 * 
 * @author Andrea Funto'
 */
public class StringTokeniser {
		
	/**
	 * The default behaviour with respect to empty tokens:
	 * by default they are reported among the output tokens.
	 */
	public static final boolean DEFAULT_SKIM_EMPTY = false;
	
	/**
	 * The default behaviour with respect to trimming of
	 * leading and traling spaces in the tokens as they are
	 * processed: by default they are removed.
	 */
	public static final boolean DEFAULT_TRIM_TOKENS = true;
	
	/**
	 * The delimiter used in the tokenising.
	 */
	private String delimiter = null;
	
	/**
	 * Controls whether the empty tokens should be removed 
	 * from the output.
	 */
	private boolean skimEmpty = DEFAULT_SKIM_EMPTY;
	
	/**
	 * Controls whether the tokens' trailing and leading spaces 
	 * should be trimmed.
	 */
	private boolean trimTokens = DEFAULT_TRIM_TOKENS;
	
	/**
	 * The list of tokens.
	 */
	private String[] tokens = null;
	
	/**
	 * The current index.
	 */
	private int index = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param delimiter
	 *   the delimiter used in the tokenising.
	 */
	public StringTokeniser(String delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
	 * Resets the StringTokeniser internal status, so that 
	 * it can be reused on a new input sequence.
	 * 
	 * @return
	 *   the object itself, for chaining.
	 */
	public StringTokeniser reset() {
		tokens = null;
		return this;
	}
	
	/**
	 * Sets whether the empty tokens should be removed
	 * from the output.
	 * 
	 * @param skimEmpty
	 *   whether the empty tokens should be removed from
	 *   the output.
  	 * @return
	 *   the object itself, for chaining.
	 */
	public StringTokeniser setSkimEmpty(boolean skimEmpty) {
		this.skimEmpty = skimEmpty;
		return this;
	}
	
	/**
	 * Sets whether the tokens should be trimmed as they
	 * are processed.
	 * 
	 * @param trimTokens
	 *   whether the tokens should be trimmed as they
	 *   are processed.
  	 * @return
	 *   the object itself, for chaining.
	 */
	public StringTokeniser setTrimSpaces(boolean trimTokens) {
		this.trimTokens = trimTokens;
		return this;
	}
	
	/**
	 * Tokenises the input string using the given delimiter.
	 * 
	 * @param string
	 *   the input string.
	 * @return
	 *   the list of tokens.
	 */
	public String[] tokenise(String string) {
		String str = string;
		if(str == null || str.length() == 0) {
			return null;
		}
		index = 0;
		tokens = null;
		List<String> list = new ArrayList<String>();
		int length = delimiter.length();
		int idx = str.indexOf(delimiter);
		while(idx != -1) {
			String token = str.substring(0, idx);
			str = str.substring(idx + length);
			if(trimTokens) {
				str = str.trim();
			}			
			if(!skimEmpty || token.length() > 0) {
				list.add(token);
			}			
			idx = str.indexOf(delimiter);
		}
		
		// handle the case where there is only one token
		// or there is a token after the last separator
		if(str.trim().length() > 0) {
			list.add(str);
		}
		
		tokens = new String[list.size()];
		list.toArray(tokens);
		index = 0;
		
		return tokens;
	}
	
	/**
	 * Returns whether there are more tokens available.
	 * 
	 * @return
	 *   whether there are more tokens available.
	 */
	public boolean hasNext() {
		if(tokens == null) {
			return false;
		}
		return index < tokens.length;
	}
	
	/**
	 * Returns the next token in the enumeration.
	 * 
	 * @return
	 *   the next token in the enumeration.
	 */
	public String next() {
		if(hasNext()) {
			return tokens[index++];
		}
		return null;
	}
}
