<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="org.dihedron.strutlets.Strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />

<strutlets:useBean name="result" var="result" type="java.lang.String" scopes="render" />

Result of submit is:
<pre>
<%=result%>
</pre>

<portlet:renderURL var="backUrl">	
	<portlet:param name="<%= Strutlets.STRUTLETS_TARGET %>" value="FileUploadAction!render"/>
</portlet:renderURL>


<br>&nbsp;<br>
<a href="${backUrl}"><img src="<%= request.getContextPath()%>/img/back-arrow.png" alt="BACK"/>&lt;BACK</a>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />