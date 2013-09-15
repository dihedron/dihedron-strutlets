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

import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.ActionContextImpl.Scope;
import org.dihedron.strutlets.ActionInvocation;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Dumper extends Interceptor {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Dumper.class);
	
	private static final int SECTION_HEADER_LENGTH = 64;
	
	private static final char SECTION_HEADER_PADDING = '=';
	
	private static final String SECTION_FOOTER = "================================================================";

	/**
	 * Dumps the various scopes before and after the action invocation. 
	 * 
	 * @param invocation
	 *   the current action invocation.
	 * @return
	 *   the result of the nested components' execution.
	 * @see 
	 *   org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws StrutletsException {
		StringBuilder builder = new StringBuilder();
		dumpFormParameters(builder);
		dumpAttributes(Scope.REQUEST, builder);
		dumpAttributes(Scope.PORTLET, builder);
		dumpAttributes(Scope.APPLICATION, builder);
		builder.append(SECTION_FOOTER).append("\n");
		logger.debug("action context BEFORE execution:\n{}", builder);
		builder.setLength(0);
		String result = invocation.invoke();
		dumpRenderParameters(builder);
		dumpAttributes(Scope.REQUEST, builder);
		dumpAttributes(Scope.PORTLET, builder);
		dumpAttributes(Scope.APPLICATION, builder);
		builder.append(SECTION_FOOTER).append("\n");
		logger.debug("action context AFTER execution:\n{}", builder);
		return result;		
	}
	
	/**
	 * Dumps any user-submitted web form parameters to the provided buffer.
	 * 
	 * @param builder
	 *   the buffer used for output accumulation.
	 */
	private void dumpFormParameters(StringBuilder builder) {
		Map<String, String[]> parameters = ActionContext.getParameters();		
		builder.append(Strings.centre(" WEB FORM ", SECTION_HEADER_LENGTH, SECTION_HEADER_PADDING)).append("\n");
		for(Entry<String, String[]> entry : parameters.entrySet()) {
			builder.append("'").append(entry.getKey()).append("' = [ ");
			for(String value : entry.getValue()) {
				builder.append("'").append(value).append("', ");
			}
			builder.append("]\n");
		}
	}
	
	/**
	 * Dumps any user-submitted web form parameters to the provided buffer.
	 * 
	 * @param builder
	 *   the buffer used for output accumulation.
	 */
	private void dumpRenderParameters(StringBuilder builder) {
		Map<String, String[]> parameters = ActionContext.getRenderParameterMap();		
		builder.append(Strings.centre(" RENDER PARAMETERS ", SECTION_HEADER_LENGTH, SECTION_HEADER_PADDING)).append("\n");
		if(parameters != null) {
			for(Entry<String, String[]> entry : parameters.entrySet()) {
				builder.append("'").append(entry.getKey()).append("' = [ ");
				for(String value : entry.getValue()) {
					builder.append("'").append(value).append("', ");
				}
				builder.append("]\n");
			}
		}
	}	
	
	/**
	 * Dumps any value available in the given scope to the provided buffer.
	 * 
	 * @param scope
	 *   the scope whose values are being dumped.
	 * @param builder
	 *   the buffer used for output accumulation.
	 */
	private void dumpAttributes(Scope scope, StringBuilder builder) {
		Map<String, Object> attributes = ActionContext.getAttributes(scope);
		builder.append(Strings.centre(" " + scope.name() + " SCOPE ", SECTION_HEADER_LENGTH, SECTION_HEADER_PADDING)).append("\n");
		for(Entry<String, Object> entry : attributes.entrySet()) {
			String value = entry.getValue() != null ? entry.getValue().toString() : null; 
			builder.append("'").append(entry.getKey()).append("' = '").append(value).append("'\n");
		}
	}
	
}
