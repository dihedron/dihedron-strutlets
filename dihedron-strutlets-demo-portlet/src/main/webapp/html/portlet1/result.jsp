<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="org.dihedron.strutlets.Strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />

<strutlets:useBean name="result" var="result" type="java.lang.String" scopes="render" />

<liferay-ui:success key="success-key" message="success-message"/>
<liferay-ui:error key="error-name-key" message="error-message-name" />
<liferay-ui:error key="error-surname-key" message="error-message-surname" />
<liferay-ui:error key="error-phone-key" message="error-message-phone" />
<liferay-ui:error key="error-email-key" message="error-message-email" />

Result of submit is:
<pre>
<%=result%>
</pre>

<portlet:renderURL var="backUrl">	
	<portlet:param name="<%= Strutlets.STRUTLETS_TARGET %>" value="ProxiedAction!render"/>
</portlet:renderURL>


<br>&nbsp;<br>
<a href="${backUrl}"><img src="<%= request.getContextPath()%>/img/back-arrow.png" alt="BACK"/>&lt;BACK</a>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />