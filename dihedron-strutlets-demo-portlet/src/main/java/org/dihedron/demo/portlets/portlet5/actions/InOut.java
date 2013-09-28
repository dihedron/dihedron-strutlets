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
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dihedron.demo.portlets.portlet5.actions;

import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.annotations.Scope;
import org.dihedron.strutlets.aop.$;
import org.dihedron.strutlets.exceptions.ActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action(alias = "InOutAction")
public class InOut {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(InOut.class);

//	@In( scopes = Scope.FORM ) private String input1;
//	@In( scopes = Scope.FORM ) private String input2;
//	@In( scopes = Scope.FORM ) private String input3;
//	
//	@Out ( scope = Scope.RENDER ) private String output1;
//	@Out ( scope = Scope.REQUEST ) private String output2;
//
//	@In( scopes = Scope.REQUEST )	@Out( scope = Scope.REQUEST ) String requestAttribute;	
//	@In( scopes = Scope.PORTLET ) @Out( scope = Scope.PORTLET ) String portletAttribute;
//	@In( scopes = Scope.APPLICATION ) @Out( scope = Scope.APPLICATION ) String applicationAttribute;
	
	
	@Invocable (
			idempotent = true,
//			inputs = { 
//				"input1", "input2", "input3" 
//			}, 
			results = {
				@Result(value = "success", data = "/html/portlet5/result.jsp")
			}
		)
		public String testInputOutput(
			@In("input1") String input1,
			@In("input2") String input2,
			@In("input3") String input3,
			@Out(value = "output1", scope = Scope.RENDER) $<String> output1,
			@Out(value = "output2", scope = Scope.REQUEST) $<String> output2,
			@Out(value = "portletAttribute", scope = Scope.PORTLET ) $<String> portletAttribute
		) throws ActionException {		
			logger.info("parameter input1 i2 '{}'", input1);		
			logger.info("parameter input1 i2 '{}'", input2);
			logger.info("parameter input1 i2 '{}'", input3);
			output1.set("{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }");
			output2.set("{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }");
			logger.info("output parameter set to '{}'", output1);
			
			portletAttribute.set("ciao");
			return Action.SUCCESS;
		}
	
	/*@Invocable (
		idempotent = true,
		outputs = { "requestAttribute", "portletAttribute", "applicationAttribute" },
		results = {
			@Result(value = AbstractAction.SUCCESS, data = "/html/portlet5/form.jsp")
		}
	)
	public String initForm() throws ActionException {
		requestAttribute = "request attribute value";
		portletAttribute = "portlet attribute value";
		applicationAttribute = "application attribute value";
		logger.trace("request attribute: '{}'", requestAttribute);
		logger.trace("portlet attribute: '{}'", portletAttribute);
		logger.trace("application attribute: '{}'", applicationAttribute);
		return AbstractAction.SUCCESS;
	}
	
	@Invocable (
			idempotent = true,
			inputs = { 
				"input1", "input2", "input3", "requestAttribute", "portletAttribute", "applicationAttribute"
			}, 
			outputs = {
					// FIXME
			},
			results = {
				@Result(value = "success", data = "/html/portlet5/result.jsp")
			}
		)
	public String onFormSubmitted() throws ActionException {
		logger.info("parameter input1 i2 '{}'", input1);		
		logger.info("parameter input1 i2 '{}'", input2);
		logger.info("parameter input1 i2 '{}'", input3);
		output1 = "{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }";
		output2 = "{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }";
//			output1 = "{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }";
		
		logger.info("output parameter set to '{}'", output1);
		return AbstractAction.SUCCESS;
	}*/
	
	


//	/* (non-Javadoc)
//	 * @see org.dihedron.portlets.actions.Action#execute()
//	 */
//	@Override
//	@Invocable( inputs = { "result" } )
//	public String execute() throws ActionException {
////		String result = ActionContextImpl.getFirstParameterValue("result");
//		logger.debug("default method is requested to return '{}'", result);
//		if(result.equalsIgnoreCase("success")) {
//			return AbstractAction.SUCCESS;
//		} else if(result.equalsIgnoreCase("error")) {
//			return AbstractAction.ERROR;
//		}
//		logger.warn("result '{}' is unsupported by default method", result);
//		return AbstractAction.ERROR;
//	}
//	
//	@Invocable(
//		inputs = { "result" },
//		events = {
//			@Event(value="userCreatedEvent", namespace="https://www.dihedron.org/events")	
//		},
//		results = {
//			@Result(value="success", url="/html/portlet1/dynamic/DynamicTestAction/testMethod/success.jsp"),
//			@Result(value="error", url="/html/portlet1/dynamic/DynamicTestAction/testMethod/error.jsp")				
//		}
//	)
//	public String testMethod() throws ActionException {
////		String result = ActionContextImpl.getFirstParameterValue("result");
//		logger.debug("test method is requested to return '{}'", result);
//		if(result.equalsIgnoreCase("success")) {
//			return AbstractAction.SUCCESS;
//		} else if(result.equalsIgnoreCase("error")) {
//			return AbstractAction.ERROR;
//		}
//		logger.warn("result '{}' is unsupported by test method", result);
//		return AbstractAction.ERROR;
//	}
//
//	@Invocable(
//		inputs = { "result" },
//		semantics=Semantics.READ_ONLY,
//		results = {
//			@Result(value="success", url="/html/portlet1/dynamic/DynamicTestAction/testRenderMethod/success.jsp"),
//			@Result(value="error", url="/html/portlet1/dynamic/DynamicTestAction/testRenderMethod/error.jsp")				
//		}
//	)
//	public String testRenderMethod() throws ActionException {
////		String result = ActionContextImpl.getFirstParameterValue("result");
//		logger.debug("test method is requested to return '{}'", result);
//		if(result.equalsIgnoreCase("success")) {
//			return AbstractAction.SUCCESS;
//		} else if(result.equalsIgnoreCase("error")) {
//			return AbstractAction.ERROR;
//		}
//		logger.warn("result '{}' is unsupported by test method", result);
//		return AbstractAction.ERROR;
//	}	
}
