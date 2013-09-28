<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />


<portlet:renderURL var="backUrl">
	<portlet:param name="jspPage" value="/html/portlet6/index.jsp" />
</portlet:renderURL>

<br>

<strutlets:useBean name="render-parameter-1" scopes="render" var="renderParam1" type="java.lang.String" >
	render parameter 1 is a string, expected 'render-string-1', got '${renderParam1}'
</strutlets:useBean>

<br>

<strutlets:useBean name="render-parameter-2" scopes="render" var="renderParam2" type="java.lang.String[]" >
	render parameter 2 is a string array, expected 'render-string-2', got '${renderParam2[1]}'
</strutlets:useBean>

<br>

<strutlets:useBean name="request-attribute" scopes="request" var="requestAttrib" type="org.dihedron.demo.portlets.portlet6.actions.MyTestBean" >
	request attribute is a MyTestBean, got '${requestAttrib.value1}'
</strutlets:useBean>

<br>

<strutlets:useBean name="portlet-attribute" scopes="portlet" var="portletAttrib" type="org.dihedron.demo.portlets.portlet6.actions.MyTestBean" >
	portlet attribute is a MyTestBean, got '${portletAttrib.value1}'
</strutlets:useBean>

<br>

<strutlets:useBean name="application-attribute" scopes="application" var="applicationAttrib" type="org.dihedron.demo.portlets.portlet6.actions.MyTestBean" >
	application attribute is a MyTestBean, got '${applicationAttrib.value1}'
</strutlets:useBean>

<br>
<br>

<a href="${backUrl}">&lt;BACK</a>

<br>
<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>