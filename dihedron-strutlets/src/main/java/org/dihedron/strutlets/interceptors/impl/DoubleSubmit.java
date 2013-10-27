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

package org.dihedron.strutlets.interceptors.impl;

import java.util.HashSet;
import java.util.Set;

import javax.portlet.PortletSession;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.ActionInvocation;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class DoubleSubmit extends Interceptor {

	/**
	 * The timestamp parameter in the form; if available , it will act as a 
	 * signature for the form, and will be used to check if the form has already
	 * been submitted.
	 */
	public final static String FORM_TOKEN = "formDate";
	
	/**
	 * The result returned by the interceptor when a "double submit" is detected.
	 */
	public final static String DOUBLE_SUBMIT_ERROR = "double_submit_error";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DoubleSubmit.class);

	/**
	 * Ensures that the interceptor's per-user data are properly initialised in 
	 * the user's session by retieving or creating a map that will store form names 
	 * and their corresponding timestamps for the current user session. This map 
	 * will be used to keep track of submitted forms and as a synchronisation
	 * point for different instances of the same portlet in the current user 
	 * session.
	 *   
	 * @return
	 *   the set containing form submit timestamps.
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private Set<Long> ensureSubmitDataAvailable() {
		Set<Long> submits = null;
		PortletSession session = ActionContext.getPortletSession();
		 synchronized(session) {
			 submits = (Set<Long>)ActionContext.getInterceptorData(getId());
			 if(submits == null) {
				 submits = new HashSet<Long>();
				 ActionContext.setInterceptorData(getId(), submits);
			 }
		}
		return submits;
	}
	
	/**
	 * Checks if a form has already been submitted by testing the "form timestamp"
	 * parameter, and if so, aborts the request. The timestamp of all prior 
	 * invocations is stored under the portlet session attributes; as soon as a 
	 * form submit starts being processed, the interceptors enters a synchronised 
	 * block that prevents other forms from being processed until the first one
	 * is done; then if a form has already been processed it is discarded and 
	 * the result is retrieved, so it can be re-rendered.
	 * This interceptor tries to be as lightweight as possible, but it severely
	 * interferes with the portal internal parallelism by removing concurrency of
	 * different portlets. Please consider adopting different mechanisms to prevent
	 * double-submit, such as disabling the submit button as soon as it is clicked.
	 * 
	 * @param invocation
	 *   the current action invocation.
	 * @return
	 *   the result of the nested components' execution if this form hasn't been 
	 *   submitted before, an error otherwise.
	 * @see 
	 *   org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws StrutletsException {
		
		String result = null;

		if(ActionContext.isActionPhase() || ActionContext.isResourcePhase()) {
			String[] tokens = ActionContext.getParameterValues(FORM_TOKEN);			
			if(tokens != null && tokens.length > 1) {
				long timestamp = Long.parseLong(tokens[0]);
				Set<Long> submits = ensureSubmitDataAvailable();
				synchronized(submits) {
					if(submits.contains(timestamp)) {
						logger.error("action execution aborted due to double-submit");
						result = DOUBLE_SUBMIT_ERROR;
					} else {
						logger.trace("synchronised action execution forwarded");
						submits.add(timestamp);
						result = invocation.invoke();
					}
				}				
			} else {
				logger.trace("unsynchronised action execution forwarded");
				result = invocation.invoke();
			}
		} else {
			logger.trace("unsynchronisedaction execution forwarded");
			result = invocation.invoke();
		}
		logger.trace("unsynchronisedaction execution forwarded");
		return result;		
	}
}