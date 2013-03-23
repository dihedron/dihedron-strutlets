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

package org.dihedron.strutlets.el;

import org.dihedron.el.operators.Selector;
import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of class <code>Selector</code> provides the logic to
 * bind expression to one of the several object trees found in the 
 * <code>ActionContext</code> object.
 * 
 * @author Andrea Funto'
 */
public class ActionContextSelector extends Selector {
	
	/**
	 * The constant indicating a per-request context.
	 */
	public static final String CONTEXT_REQUEST = "request";
	
	/**
	 * The constant indicating a per-session context.
	 */
	public static final String CONTEXT_SESSION = "session";
	
	/**
	 * The constant indicating a per-application context.
	 */
	public static final String CONTEXT_APPLICATION = "application";

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionContextSelector.class);
	
	/**
	 * Selects the root element of the object hierarchy from within the
	 * <code>ActionContext</code>.
	 *  
	 * @see org.dihedron.el.operators.Selector#apply(java.lang.Object)
	 */
	@Override
	public Object apply(Object object) throws StrutletsException {
		if(!(object instanceof ActionContext)) {
			logger.error("this root selector should only be applied to ActionContext objects");
			throw new StrutletsException("error applying selector to non-ActionContext object");
		}
		return null;
	}

}
