/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.ognl;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 * 
 * @see 
 *   for details on how to embed OGNL expression evaluation in code, see 
 *   http://commons.apache.org/proper/commons-ognl/developer-guide.html
 */
public class OgnlExpression {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(OgnlExpression.class);

	/**
	 * The parsed OGNL expression object.
	 */
	private Object expression;

	/**
	 * Constructor.
	 *
	 * @param string
	 *   a string representing the OGNL expression.
	 * @throws OgnlException
	 *   if the expression cannot be parsed.
	 */
	public OgnlExpression(String string) throws OgnlException {
		logger.trace("parsing OGNL expression: '{}'", string);
		expression = Ognl.parseExpression(string);
	}

	/**
	 * Retrieves a value from the given object, according to the OGNL expression 
	 * value.
	 * 
	 * @param object
	 *   the object to which the expression will be applied.
	 * @return
	 *   the result of applying the expression to the given object.
	 * @throws OgnlException
	 *   if any error occurs while evaluating the OGNL expression against the 
	 *   given object tree.
	 */
	public Object getValue(Object object) throws OgnlException {
		return Ognl.getValue(expression, object);
	}
	
	/**
	 * Retrieves a value from the given object, according to the OGNL expression 
	 * value.
	 * 
	 * @param context
	 *   an OGNL context.
	 * @param object
	 *   the object to which the expression will be applied.
	 * @return
	 *   the result of applying the expression to the given object.
	 * @throws OgnlException
	 *   if any error occurs while evaluating the OGNL expression against the 
	 *   given object tree.
	 */
	public Object getValue(OgnlContext context, Object object) throws OgnlException {
		return Ognl.getValue(expression, context, object);
	}

	/**
	 * Sets a value into the given object, according to the OGNL expression 
	 * value.
	 * 
	 * @param object
	 *   the object to which the expression will be applied.
	 * @param value
	 *   the value to be set into the object tree.
	 * @throws OgnlException
	 *   if any error occurs while evaluating the OGNL expression against the 
	 *   given object tree.
	 */
	public void setValue(Object object, Object value) throws OgnlException {
		Ognl.setValue(expression, object, value);
	}

	/**
	 * Sets a value into the given object, according to the OGNL expression 
	 * value.
	 * 
	 * @param context
	 *   an OGNL context.
	 * @param object
	 *   the object to which the expression will be applied.
	 * @param value
	 *   the value to be set into the object tree.
	 * @throws OgnlException
	 *   if any error occurs while evaluating the OGNL expression against the 
	 *   given object tree.
	 */
	public void setValue(OgnlContext context, Object object, Object value) throws OgnlException {
		Ognl.setValue(expression, context, object, value);
	}
}