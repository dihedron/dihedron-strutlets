/**
 * Copyright (c) 2012, Andrea Funto'
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Andrea Funto' nor the names of its contributors 
 *       may be used to endorse or promote products derived from this software 
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ANDREA FUNTO' BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dihedron.demo.portlets.portlet1.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.dihedron.commons.utils.Types;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.annotations.Scope;
import org.dihedron.strutlets.aop.$;
import org.dihedron.strutlets.exceptions.InvalidPhaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action
public class ProxiedAction {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProxiedAction.class);
	
	
	@Invocable (
		idempotent = true,
		results = {
			@Result(value = "success", renderer = "jsp", data = "/html/portlet1/view.jsp")	
		}
	)
	public String initView(
			@Out(value="applAttr1", scope = Scope.APPLICATION) $<Integer> arg0,
			@Out(value="applAttr2", scope = Scope.APPLICATION) $<Set<List<Map<String, Vector<String>>>>> arg1,
			@Out(value="portAttr1", scope = Scope.PORTLET) $<String> arg2,
			@Out(value="portAttr2", scope = Scope.PORTLET) $<Boolean> arg3			
		) {
		logger.debug("initialising view, storing parameters into session");
		arg0.set(100);
		arg1.set(new HashSet<List<Map<String, Vector<String>>>>());
		arg2.set("this is a string");
		arg3.set(true);		
		return Action.SUCCESS;
	}
	
	@Invocable(
		idempotent = true,
		results = {
			@Result(value="success", renderer="jsp", data="/html/portlet1/result.jsp")
		}
	)
	public String dumpInputs(
			@In(value = "formParam", scopes = Scope.FORM) String arg0 
			,@In(value = "applAttr1", scopes = Scope.APPLICATION) Integer arg1 
			,@In("applAttr2") Set<List<Map<String, Vector<String>>>> arg2
			,@In(value = "portAttr1", scopes = Scope.PORTLET) String arg3 
			,@In("portAttr2") Boolean arg4
			,String arg5
			,double arg6 
			,@Out(value = "inputs", scope = Scope.RENDER) $<String> arg7 
			,@In("portAttr2") @Out("portAttr3") $<Set<List<Map<String, Vector<String>>>>> arg8  
	) throws InvalidPhaseException {
		logger.debug("dumping input parameters from session & form");
		StringBuilder buffer = new StringBuilder("{\n");
		buffer.append("\t'arg0' : '").append(arg0).append("' (").append(Types.getAsString(arg0.getClass())).append("),\n");
		buffer.append("\t'arg1' : '").append(arg1).append("' (").append(Types.getAsString(arg1.getClass())).append("),\n");
		buffer.append("\t'arg2' : '").append(arg2).append("' (").append(Types.getAsString(arg2.getClass())).append("),\n");
		buffer.append("\t'arg3' : '").append(arg3).append("' (").append(Types.getAsString(arg3.getClass())).append("),\n");
		buffer.append("\t'arg4' : '").append(arg4).append("' (").append(Types.getAsString(arg4.getClass())).append("),\n");
//		buffer.append("\t'arg5' : '").append(arg5).append("' (").append(Types.getAsString(arg5.getClass())).append("),\n");
//		buffer.append("\t'arg6' : '").append(arg6).append("',\n");
		buffer.append("\t'arg8' : '").append(arg8).append("' (").append(Types.getAsString(arg8.get().getClass())).append("),\n");
		buffer.append("}");
		arg7.set(buffer.toString());
		return Action.SUCCESS;
	}	
}
