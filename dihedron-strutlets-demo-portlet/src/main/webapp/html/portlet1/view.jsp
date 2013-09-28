<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />


<br>
<portlet:actionURL name="ProxiedAction!dumpInputs" var="formUrl"></portlet:actionURL>

You can submit an arbitrary form to an Action; it will be bounced back by this example.
<br>&nbsp;<br> 
<aui:form method="post" action="${formUrl}">
	<aui:input label="Input 1:" name="formParam" type="text" value=""/>
	<br>
	<aui:button type="submit" value="Submit!"/> 
</aui:form> 

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>