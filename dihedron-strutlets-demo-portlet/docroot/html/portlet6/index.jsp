<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<portlet:defineObjects />

<portlet:actionURL name="ParameterFillingAction!fillParameters" var="actionUrl">
	<portlet:param name="parameter" value="parameter-value"/>
</portlet:actionURL>

Click <a href="${actionUrl}">here</a> to launch a BUSINESS action capable of setting render parameters and attributes.

<br>
<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>