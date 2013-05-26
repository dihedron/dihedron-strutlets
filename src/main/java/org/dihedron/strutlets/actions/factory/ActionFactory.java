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

package org.dihedron.strutlets.actions.factory;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.targets.TargetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public final class ActionFactory {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionFactory.class);
	
	/**
	 * 
	 * @param target
	 * @return
	 * @throws StrutletsException 
	 * @throws Exception
	 */
	public static Action makeAction(TargetData target) throws StrutletsException {
		Action action = null;
		if(target != null) {
			logger.trace("instantiating action of class '{}'", target.getAction().getSimpleName());
			try {
				action = target.getAction().newInstance();
			} catch (Exception e) {
				logger.error("error instantiating action for target '{}'", target);
				throw new StrutletsException("Error instantiating action", e);
			}
		}
		return action;
	}
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private ActionFactory() {
	}
}
