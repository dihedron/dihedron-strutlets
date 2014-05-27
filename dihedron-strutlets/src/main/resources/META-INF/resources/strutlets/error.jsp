<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<strutlets:lastError var="error" />

<style>
	.strutlets-error-hidden {
		display: none;
	}
</style>


<script type="text/javascript">
	function toggleErrorInfo() {
		var div = document.getElementById("<portlet:namespace />errorInfo");
		if(div.className.length > 0) {
			div.className = "";
		} else {
			div.className = div.className + " strutlets-error-hidden";
		}
	}
</script>

<div>
	<h3>OOOOOPS!</h3>
	<%--
		IF YOU LIKE THIS ANIMATION, YOU ARE ENCOURAGED TO VISIT THE ARTIST'S
		WEBSITE AT THE FOLLOWING ADDRESS: http://hopstarter.deviantart.com/
	 --%>
	<div style="float: left; width: 48px; height: 48px;">
		<img src="<%=request.getContextPath()%>/strutlets/images/oops.gif" />		
	</div>
	<div>		
		<p style="margin-left: 40px; padding-left: 15px;">
			The portlet experienced an unrecoverable internal error. This may be due 
			to a momentary problem or to a bug in the software.
		</p>		
	</div>
</div>

<p>	
	If the problem persists, please contact your technical support and provide 
	the attached <a href="#" onclick="toggleErrorInfo(); return false;">diagnostic 
	information</a>.
</p>


<div id="<portlet:namespace />errorInfo" class="strutlets-error-hidden">

	<fieldset>
		<legend>Error Information</legend>
				
		<p>
			${error.type}: the error <code>&quot;${error.message}&quot;</code> occurred in method <code>${error.methodName}</code>
			of class <code>${error.className}</code> (source file <code>${error.sourceFileName}</code> at line ${error.sourceLineNumber}).
		</p> 
		
		<br/>
		
		<%--
		<p>			
			<c:forEach var="cause" items="${error.causes}">
				caused by ${cause.type} at line ... </br> 
             </c:forEach>
		
		</p>
		--%>
		
		<b>Complete Stack Trace:</b> 
		<br/>
		<pre style="font-size: 0.65em;">${error.stackTrace}</pre>
	</fieldset>
	
</div>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>