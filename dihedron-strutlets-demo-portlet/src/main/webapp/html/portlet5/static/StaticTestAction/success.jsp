<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<portlet:renderURL var="backUrl">
	<portlet:param name="jspPage" value="/html/portlet1/index.jsp" />
</portlet:renderURL>

You should see this in VIEW mode as a <b>successful</b> execution of <b>StaticTestAction!testMethod</b>.
<br><br>
<a href="${backUrl}">&lt;BACK</a>
