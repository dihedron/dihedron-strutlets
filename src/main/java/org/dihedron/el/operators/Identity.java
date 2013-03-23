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
package org.dihedron.el.operators;

import org.dihedron.strutlets.exceptions.StrutletsException;

/**
 * An implementation of the <code>Selector</code> class; this dummy implementation
 * simply binds the expression to the context object itself.
 * 
 * @author Andrea Funto'
 */
public class Identity extends Selector {


	/** 
	 * The dummy implementation of the <code>Selector</code> object; this
	 * implementation simply returns the given object.
	 * 
	 * @param context
	 *   the object to bind to the expression.
	 * @return 
	 *   the input object: it behaves as an identity operator.
	 * 
	 * @see org.dihedron.el.operators.Selector#apply(java.lang.Object)
	 */
	@Override
	public Object apply(Object context) throws StrutletsException {		
		return context;
	}

}
