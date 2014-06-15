<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="org.dihedron.strutlets.diagnostics.Error" %>

<strutlets:lastError var="error" />
<strutlets:request var="portletRequest" />

<style>
	.strutlets-error-hidden {
		display: none;
		clear: both;
	}
	
	.strutlets-error-frame {
		border-top: 1px solid #555555;
		padding-top: 5px;
	}
	
	.strutlets-code {
		font-family: Monaco,Menlo,Consolas,"Courier New",monospace;
	}
	
	#container {
		width: 100%;
	}

	.strutlets-left {
		float: left;
		width: 48px;
		height: 48px;
	}
	
	.strutlets-right {
		margin: auto;
		margin-left: 10px;
		width: 100%;
	}	
</style>


<script type="text/javascript">
	function toggleErrorInfo() {
		var div = document.getElementById("<portlet:namespace />error-info");
		if(div.className.length > 0) {
			div.className = "";
		} else {
			div.className = div.className + " strutlets-error-hidden";
		}
	}
</script>

<div>
	<h3>OOOOOPS!</h3>
	<div class="strutlets-left">
		 <img src="${portletRequest.contextPath}/strutlets/images/oops.png" />
	</div>
	<div class="strutlets-right">		
		<p>
			The portlet experienced an unrecoverable internal error. This may be 
			due to a temporary unavailability of resources or to a bug in the 
			software.
		</p>
		<p>	
			If the problem persists, please contact your technical support and provide 
			the attached <a href="#" onclick="toggleErrorInfo(); return false;">diagnostic 
			information</a>.
		</p>		
	</div>
</div>


<strutlets:adminConsoleURL var="adminConsoleURL" />
<p> 
	Click <a href="${adminConsoleURL}">here</a> to open the Administrative Console.
</p>

<div id="<portlet:namespace />error-info" class="strutlets-error-hidden">

	<fieldset>
		<legend>Error Information</legend>
				
		<p>
			<span class="strutlets-code">${error.type}</span> in method 
			<span class="strutlets-code">${error.className}#${error.methodName}()</span> 
			(source file <span class="strutlets-code">${error.sourceFileName}</span>
			at line <span class="strutlets-code">${error.sourceLineNumber}</span>):<br>
			<pre>${error.message}</pre>
		</p> 
		
		<% 
			for(Error cause : error.getCauses()) {
				pageContext.setAttribute("cause", cause);
		%>
			<p class="strutlets-error-frame">
				caused by <span class="strutlets-code">${cause.type}</span> in method 
				<span class="strutlets-code">${cause.className}#${cause.methodName}()</span> 
				(source file <span class="strutlets-code">${cause.sourceFileName}</span>
				at line <span class="strutlets-code">${cause.sourceLineNumber}</span>)
			<%if (cause.getMessage() != null && cause.getMessage().length() > 0) { %>
			:<br>
			<pre>${cause.message}</pre>
			<% } %>
			</p> 
		<%
			}
			pageContext.removeAttribute("cause");
		%>
		
		<p class="strutlets-error-frame">
			<b>Complete Stack Trace:</b> 
			<br/>
			<pre style="font-size: 0.65em;">${error.stackTrace}</pre>
		</p>
	</fieldset>
	
</div>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>