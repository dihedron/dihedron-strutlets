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
	<aui:fieldset label="Personal Info">
		<aui:input label="Name (min 3, max 20):" name="nameParameter" type="text" value=""/>
		<aui:input label="Surname:" name="surnameParameter" type="text" value=""/>
		<aui:input label="Phone (06-555-12345):" name="phoneParameter" type="text" value=""/>
		<aui:input label="Email:" name="emailParameter" type="text" value=""/>
	</aui:fieldset>
	<aui:fieldset label="Loves:">
		<aui:input label="Animals" name="loves" type="checkbox" value="animals"/>
		<aui:input label="Flowers" name="loves" type="checkbox" value="flowers"/>
		<aui:input label="Food" name="loves" type="checkbox" value="food"/>
		<aui:input label="Music" name="loves" type="checkbox" value="music"/>
		<aui:input label="Movies" name="loves" type="checkbox" value="movies"/>
	</aui:fieldset>
	<aui:fieldset label="Redirect?">
			<aui:input inlineLabel="right" name="redirect" type="radio" value="notatall" label="No, just go on..." checked="true"/>
			<aui:input inlineLabel="right" name="redirect" type="radio" value="homepage" label="Go to the homepage"/>
			<aui:input inlineLabel="right" name="redirect" type="radio" value="absolute" label="Go to www.google.com" />			
			<aui:input inlineLabel="right" name="redirect" type="radio" value="internal" label="Go to a demo JSP"/>
	</aui:fieldset>	
	<br>
	<aui:button type="submit" value="Submit!"/> 
</aui:form> 

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>