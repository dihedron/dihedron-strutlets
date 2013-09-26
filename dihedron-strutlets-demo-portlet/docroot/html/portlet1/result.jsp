<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />

<%
String inputs = renderRequest.getParameter("inputs");
%>
dumped inputs is<br> 
<%=inputs%>


<br>&nbsp;<br>
<a href="${backUrl}"><img src="<%= request.getContextPath()%>/img/back-arrow.png" alt="BACK"/>&lt;BACK</a>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />