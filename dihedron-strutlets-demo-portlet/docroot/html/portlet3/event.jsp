<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<portlet:renderURL var="backUrl">
	<portlet:param name="jspPage" value="/html/portlet3/view.jsp" />
</portlet:renderURL>


<%
String payload = request.getParameter("EVENT_PAYLOAD");
%>

Received an event: payload is <b><%=payload %></b>!!!!!

<br>&nbsp;<br>
<a href="${backUrl}"><img src="<%= request.getContextPath()%>/img/back-arrow.png" alt="BACK"/>&lt;BACK</a>
