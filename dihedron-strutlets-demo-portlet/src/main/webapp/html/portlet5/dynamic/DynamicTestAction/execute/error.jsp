<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<portlet:renderURL var="backUrl">
	<portlet:param name="jspPage" value="DynamicTestAction!showHome" />
</portlet:renderURL>

This is the result of a <em>failed</em> execution of <em>DynamicTestAction</em>'s default method (<em>execute</em>).
<br><br>
<a href="${backUrl}">&lt;BACK</a>

