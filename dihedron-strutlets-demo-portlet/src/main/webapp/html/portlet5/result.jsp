<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />


<strutlets:useBean name="portletAttribute" scopes="portlet" var="portletAttribute" type="java.lang.String" >
	using a variable here portlet scope:  ${portletAttribute}  
</strutlets:useBean>
<br>

<%
String output1 = renderRequest.getParameter("output1");
%>
output is <%=output1%>


<br>&nbsp;<br>
<a href="${backUrl}"><img src="<%= request.getContextPath()%>/img/back-arrow.png" alt="BACK"/>&lt;BACK</a>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>