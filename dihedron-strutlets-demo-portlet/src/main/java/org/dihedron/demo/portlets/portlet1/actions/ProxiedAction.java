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

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
			@Result(value = Action.SUCCESS, renderer = "jsp", data = "/html/portlet1/view.jsp")	
		}
	)
	public String render(
			@Out(value="friendsAttribute", scope = Scope.REQUEST) $<Set<List<Map<String, Vector<String>>>>> friends,
			@Out(value="descriptionAttribute", scope = Scope.PORTLET) $<String> description,
			@Out(value="ageAttribute", scope = Scope.APPLICATION) $<Integer> age,			
			@Out(value="genderAttribute", scope = Scope.APPLICATION) $<Boolean> gender
		) {
		logger.debug("initialising view, storing parameters into session");
		friends.set(new HashSet<List<Map<String, Vector<String>>>>());
		description.set("a very good person");
		gender.set(true);
		age.set(100);
		return Action.SUCCESS;
	}
	
	/*
	@Invocable (
		idempotent = true,
		results = {
			@Result(value = Action.SUCCESS, renderer = "jsp", data = "/html/portlet1/view.jsp")	
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
	*/
	
	@Invocable(
		idempotent = true,
		results = {
			@Result(value="success", renderer="jsp", data="/html/portlet1/result.jsp")
		}
	)
	public String dumpInputs(
			@In(value = "nameParameter", scopes = Scope.FORM) @Size(min=3, max=20, message="name must between 3 and 20 characters in length") String name, 
			@In(value = "surnameParameter", scopes = Scope.FORM) String surname,
			@In(value = "phoneParameter", scopes = Scope.FORM) @Pattern(regexp="^\\d{2}-\\d{3}-\\d{5}$") String phone,
			@In(value = "emailParameter", scopes = Scope.FORM) String email,
			@In(value="friendsAttribute", scopes = Scope.REQUEST) Set<List<Map<String, Vector<String>>>> friends,
			@In(value="descriptionAttribute", scopes = Scope.PORTLET) String description,
			@In(value="ageAttribute", scopes = Scope.APPLICATION) Integer age,			
			@In(value="genderAttribute", scopes = Scope.APPLICATION) Boolean gender,
			String aString,
			double aDouble,
			@Out(value = "result", scope = Scope.RENDER) $<String> result   
	) throws InvalidPhaseException {
		logger.debug("dumping input parameters from session & form");
		StringBuilder buffer = new StringBuilder("{\n");
		buffer.append("\t'name' : '").append(name).append("' (").append(Types.getAsString(name.getClass())).append("),\n");
		buffer.append("\t'surname' : '").append(surname).append("' (").append(Types.getAsString(surname.getClass())).append("),\n");
		buffer.append("\t'phone' : '").append(phone).append("' (").append(Types.getAsString(phone.getClass())).append("),\n");
		buffer.append("\t'email' : '").append(email).append("' (").append(Types.getAsString(email.getClass())).append("),\n");
		buffer.append("\t'friends' : '").append(friends).append("' (").append(friends != null ? Types.getAsString(friends.getClass()) : "<null>").append("),\n");
		buffer.append("\t'description' : '").append(description).append("' (").append(Types.getAsString(description.getClass())).append("),\n");
		buffer.append("\t'age' : '").append(age).append("' (").append(Types.getAsString(age.getClass())).append("),\n");
		buffer.append("\t'gender' : '").append(gender).append("' (").append(Types.getAsString(gender.getClass())).append("),\n");
		buffer.append("}");
		result.set(buffer.toString());
		return Action.SUCCESS;
	}	
}
