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

import java.util.ArrayList;

import org.dihedron.el.operators.FieldAccess;
import org.dihedron.el.operators.Invoke;
import org.dihedron.el.operators.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object graph navigation expression; by applying the epxressio to a root
 * object, it will navigate the obect graph and return the final navigation 
 * point.
 * 
 * @author Andrea Funto'
 */
public class Expression extends ArrayList<Operator> {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 3892797423915196306L;
	
	/**
	 * The logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(Expression.class);
	
	/**
	 * Constructor.
	 */
	public Expression() {
	}
	
	/**
	 * Evaluates the expression on the given root operand.
	 * 
	 * @param operand
	 *   the evaluation starting point.
	 * @return
	 *   the result of the evaluation.
	 * @throws Exception
	 */
	public Object evaluate(Object operand) throws Exception {
		logger.info("evaluating expression");
		Object object = operand;
		if(!isEmpty()) { 
			for(Operator operator : this) {
				object = operator.apply(object); 
			}
		}
		return object;
	}
	
	/**
	 * Adds an <code>Operator</code> to the list of operators; before adding it,
	 * it checks if the expression is empty and only admits field accessors and
	 * method invocations as head operators.
	 */
	@Override
	public boolean add(Operator operator) throws IllegalArgumentException {
		if(isEmpty() && !((operator instanceof FieldAccess) 
				|| (operator instanceof Invoke) 
				|| (operator instanceof FieldAccess)))  {
			throw new IllegalArgumentException("first operator must be a field accessor, a method call or a root selector");
		}
		return super.add(operator);
	}
	
	/**
	 * Adds a list of operators to the <code>Expression</code>.
	 * 
	 * @param operators
	 *   an array represeting a list of <code>Operator</code>s.
	 * @return
	 *   <code>true</code> (@see java.util.Collection#add()).
	 */
	public boolean addAll(Operator[] operators) {
		boolean result = true;
		for(Operator operator : operators) {
			result = result && add(operator);
		}
		return result;
	}
	
	/**
	 * Returns a string representation of the expression.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(!isEmpty()) { 
			sb.append("<operand>");
			for(Operator operator : this) {
				sb.append(operator.toString()); 
			}
			sb.append(";");
		} else {
			sb.append("<empty expression>");
		}
		return sb.toString();
	}
}
