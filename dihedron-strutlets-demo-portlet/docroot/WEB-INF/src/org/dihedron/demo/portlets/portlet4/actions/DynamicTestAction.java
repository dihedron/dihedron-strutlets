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
package org.dihedron.demo.portlets.portlet4.actions;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action
public class DynamicTestAction {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(DynamicTestAction.class);

	@Invocable (
		idempotent = true,
		results = {
			@Result(value = "success", data = "/html/portlet4/view.jsp")
		}
	)
	public String showHome() {
		logger.info("going to show ordinary home page!");
		return Action.SUCCESS;
	}

	@Invocable
	public String execute() {
		String result = ActionContext.getFirstParameterValue("result");
		logger.debug("default method is requested to return '{}'", result);
		if(result.equalsIgnoreCase("success")) {
			return Action.SUCCESS;
		} else if(result.equalsIgnoreCase("error")) {
			return Action.ERROR;
		}
		logger.warn("result '{}' is unsupported by default method", result);
		return Action.ERROR;
	}
	
	@Invocable
	public String myMethod() {
		String result = ActionContext.getFirstParameterValue("result");
		logger.debug("test method is requested to return '{}'", result);
		if(result.equalsIgnoreCase("success")) {
			return Action.SUCCESS;
		} else if(result.equalsIgnoreCase("error")) {
			return Action.ERROR;
		}
		logger.warn("result '{}' is unsupported by test method", result);
		return Action.ERROR;
	}
}
