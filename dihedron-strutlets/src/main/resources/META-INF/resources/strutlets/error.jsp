<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<strutlets:errorInfo var="error"/>

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

<p>
	The portlet experienced an internal, unrecoverable error. This may be due to a 
	momentary problem or to a bug in the software. 
</p>

<p>	
	If the problem persists, please contact technical support and provide the 
	attached <a href="#" onclick="toggleErrorInfo(); return false;">diagnostic information</a>.
</p>

<div id="<portlet:namespace />errorInfo" class="strutlets-error-hidden">

	<fieldset>
		<legend>Error Information</legend>
		
		<p>
			${error.message}
		</p> 
		
		<br/>
		
		<pre>${error.stackTrace}</pre>
	</fieldset>
	
</div>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>