/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.actions.factory;

import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as a factory for actions.
 *  
 * @author Andrea Funto'
 */
public final class ActionFactory {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionFactory.class);
	
	/**
	 * Creates a new Action object, given the information about the request target.
	 *  
	 * @param target
	 *   information about the requested target (the business service).
	 * @return
	 *   an object that implements the requested chunk of business logic.
	 * @throws Exception
	 */
	public static Object makeAction(Target target) throws StrutletsException {
		Object action = null;
		if(target != null) {
			logger.trace("instantiating action of class '{}'...", target.getActionClass().getSimpleName());
			try {
				action = target.getFactoryMethod().invoke(null);
//				action = target.getActionClass().newInstance();
				logger.trace("... class '{}' instance ready!", target.getActionClass().getSimpleName());
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
