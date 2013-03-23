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

package org.dihedron.el;

import org.dihedron.el.operators.Operator;
import org.dihedron.el.operators.OperatorFactory;
import org.dihedron.strings.StringTokeniser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tis class is responsible of the translation of an input expression string
 * into an executable <code>Expression</code>, which will take care of
 * navigating the object graph and retrieving the requested value.
 * 
 * @author Andrea Funto'
 */
public class Parser {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(Parser.class);
		
	/**
	 * The <code>Operator</code>s' factory.
	 */
	private OperatorFactory factory = null;
	
	/**
	 * Constructor.
	 */
	public Parser() {
		logger.info("creating new parser");
	}
	
	/**
	 * Sets the object responsible of the instantiation of <code>Operator</code>s.
	 * 
	 * @param factory
	 *   the <code>Operator</code>s' factory.
	 */
	public void setOperatorFactory(OperatorFactory factory) {
		this.factory = factory; 
	}
	
	/**
	 * Parses an expression and instantiates the proper chain of operators.
	 *  
	 * @param string
	 *   the input expression string.
	 * @return
	 *   the <code>Expression</code> as a chain of <code>Operator</code>s.
	 */
	public Expression parse(String string) {
		assert string != null : "error";
		
		Expression expression = new Expression();
		
		StringTokeniser tokeniser = new StringTokeniser(".");
		String[] tokens = tokeniser.tokenise(string);
		for(String token : tokens) {
			logger.info("token: '{}'", token);
			Operator[] operators = factory.makeOperators(token);
			expression.addAll(operators);
		}
		logger.info("expression: {}", expression.toString());
		return expression;
	}
}
