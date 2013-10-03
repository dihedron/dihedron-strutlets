<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Vector" %>

<portlet:defineObjects />
<strutlets:defineObjects />

 
<strutlets:useBean var="friends" name="friendsAttribute" scopes="request" type="Set<?>" />
<strutlets:useBean var="description" name="descriptionAttribute" scopes="portlet" type="String" />
<strutlets:useBean var="age" name="ageAttribute" scopes="application" type="Integer" />
<strutlets:useBean var="gender" name="genderAttribute" scopes="application" type="Boolean" />

<br>
<portlet:actionURL name="ProxiedAction!dumpInputs" var="formUrl"></portlet:actionURL>

<fieldset>
	<legend>Existing attributes in various scopes</legend>
	FRIENDS: <%= friends %><br>
	DESCRIPTION: <%= description %><br>	
	AGE: <%= age %><br>
	GENDER: <%= gender %><br>
</fieldset>

<br>

You can submit an arbitrary form to an Action; it will be bounced back by this example.
<br>&nbsp;<br> 
<aui:form method="post" action="${formUrl}">
	<aui:input label="Name:" name="nameParameter" type="text" value=""/>
	<aui:input label="Surname:" name="surnameParameter" type="text" value=""/>
	<aui:input label="Phone:" name="phoneParameter" type="text" value=""/>
	<aui:input label="email:" name="emailParameter" type="text" value=""/>
	<br>
	<aui:button type="submit" value="Submit!"/> 
</aui:form> 

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>