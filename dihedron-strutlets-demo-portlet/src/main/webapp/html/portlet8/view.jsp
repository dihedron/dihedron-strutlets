<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Vector" %>

<portlet:defineObjects />
<strutlets:defineObjects />

 
<br>
<portlet:actionURL name="ModelAction!processUser" var="formUrl"></portlet:actionURL>

<br>

You can submit an arbitrary form to an Action; it will be bounced back by this example.
<br>&nbsp;<br> 
<aui:form method="post" action="${formUrl}">
	<aui:fieldset label="Personal Info">
		<aui:input label="Name (min 3, max 20):" name="user:name" type="text" value=""/>
		<aui:input label="Surname:" name="user:surname" type="text" value=""/>
		<aui:input label="Phone (06-555-12345):" name="user:phone" type="text" value=""/>
		<aui:input label="Email:" name="user:email" type="text" value=""/>
		<aui:input label="Street:" name="user:address.street" type="text" value=""/>
		<aui:input label="Street no.:" name="user:address.number" type="text" value=""/>
		<aui:input label="ZIP Code:" name="user:address.zip" type="text" value=""/>
		<aui:input label="Town:" name="user:address.town" type="text" value=""/>
	</aui:fieldset>
	<%--
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
	--%>	
	<br>
	<aui:button type="submit" value="Submit!"/> 
</aui:form> 

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>