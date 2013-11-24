<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="org.dihedron.strutlets.Strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />


Hallo, if you see me you have been successfully redirected here!

<br>&nbsp;<br>
<a href="${backUrl}"><img src="<%= request.getContextPath()%>/img/back-arrow.png" alt="BACK"/>&lt;BACK</a>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />