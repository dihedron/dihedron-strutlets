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

<style type="text/css">
	.strutlets-warning {
		background-color: #FFCCCC;
		border: 2px solid #FF0000;
		-moz-border-radius: 5px;
		border-radius: 5px;
		padding: 10px;
	}

</style>


<br>
<portlet:actionURL name="ProxiedAction!dumpInputs" var="formUrl"></portlet:actionURL>


<div class="strutlets-warning">
If you see this message it's because there was some error in the input fields you
submitted and the <code>ValidationHandler</code> re-routed you to this page.<br>
This page's purpose is to show that something went wrong in input parameters 
validation and that you can divert the ordinary flow of control to other pages 
by implementing your own check logic inside a custom <code>ValidationHandler</code>.
</div>

<br>

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
	<aui:input label="Name (min 3, max 20):" name="nameParameter" type="text" value=""/>
	<aui:input label="Surname:" name="surnameParameter" type="text" value=""/>
	<aui:input label="Phone (06-555-12345):" name="phoneParameter" type="text" value=""/>
	<aui:input label="email:" name="emailParameter" type="text" value=""/>
	<br>
	<aui:button type="submit" value="Submit!"/> 
</aui:form> 

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>