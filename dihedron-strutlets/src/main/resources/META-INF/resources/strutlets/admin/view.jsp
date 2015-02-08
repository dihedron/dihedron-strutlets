<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="org.dihedron.core.properties.Properties" %>
<%@ page import="org.dihedron.strutlets.diagnostics.Error" %>
<%@ page import="org.dihedron.strutlets.targets.Target" %>
<%@ page import="org.dihedron.strutlets.targets.TargetId" %>
<%@ page import="org.dihedron.strutlets.targets.registry.TargetRegistry" %>


<strutlets:useBean name="org.dihedron.strutlets:configuration" type="Properties" scopes="request" var="configuration" />
<strutlets:useBean name="org.dihedron.strutlets:registry" type="TargetRegistry" scopes="request" var="registry" />


<style>

	table.strutlets {
		border-collapse:collapse;
		width:100%;
	}
	
	table.strutlets caption {
		text-align: left;
		padding-left: 5px;
		font-size: 1.5em;
		font-weight: bold;
	}

	table.reference tr:nth-child(odd) {
		background-color:#f1f1f1;
	}

	table.strutlets tr:nth-child(even) {
		background-color:#ffffff;
	}

	table.strutlets th {
		color:#ffffff;
		background-color:#555555;
		border:1px solid #555555;
		padding:3px;
		vertical-align:top;
		text-align:left;
	}

	table.strutlets th a:link,table.reference th a:visited {
		color:#ffffff;
	}

	table.strutlets th a:hover, table.reference th a:active{
		color:#EE872A;
	}

	table.strutlets td {
		border:1px solid #d4d4d4;
		padding:5px;
		padding-top:7px;
		padding-bottom:7px;
		vertical-align:top;
	}

	table.strutlets td.example_code {
		vertical-align:bottom;
	}

	table.summary {
		border:1px solid #d4d4d4;
		padding:5px;
		font-size:100%;
		color:#555555;
		background-color:#fafad2;
	}
		
</style>

<%--
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
 --%>

<div>
	<h3>Strutlets Admin Console</h3>
	
	<% if(configuration != null) { %>
	<table  class="strutlets">
		<caption>Configuration</caption>
		<thead>
			<tr>
				<th>KEY</th>
				<th>VALUE</th>
			</tr>
		</thead>
		<tbody>
		<% for(String key : configuration.getKeys()) { %>
			<tr>
				<td><%= key %></td>
				<td><%= configuration.get(key) %></td>
			</tr>
		<% } %>
		</tbody>
	</table>
	<br>
	<% } %>
	
	<% if(registry != null) { %>
	<table class="strutlets">
		<caption>Targets</caption>
		<thead>
			<tr>
				<th>ACTION</th>
				<th>METHOD</th>
				<th>INTERCEPTOR STACK</th>
			</tr>
		</thead>
		<tbody>
		<% 
		for(TargetId targetId : registry.getTargetIds()) {
			pageContext.setAttribute("target", registry.getTarget(targetId)); 
		%>	
			<tr>
				<td>${target.id.actionName}</td>
				<td>${target.id.methodName}</td>
				<td>${target.interceptorStackId}</td>
				<%--
				<td>...details here...</td>
				--%>
			</tr>
		<% } %>
		</tbody>
	</table>
	<br>
	
	<% } %>
	
	<%--
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
		
		<% 
			for(Error cause : error.getCauses()) {
				pageContext.setAttribute("cause", cause);
		%>
			<p>
			caused by an exception of type ${cause.type} (message: <code>${cause.message}</code>) occurred in method <code>${cause.methodName}</code>
			of class <code>${cause.className}</code> (source file <code>${cause.sourceFileName}</code> at line ${cause.sourceLineNumber})
			</p> 
		<% 
			}
			pageContext.removeAttribute("cause");
		%>		
		
		<b>Complete Stack Trace:</b> 
		<br/>
		<pre style="font-size: 0.65em;">${error.stackTrace}</pre>
	</fieldset>

--%>
	
</div>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>