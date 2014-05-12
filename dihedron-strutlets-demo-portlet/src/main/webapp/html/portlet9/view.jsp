<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Vector" %>

<portlet:defineObjects />
<strutlets:defineObjects />

<strutlets:useBean name="user" scopes="portlet" var="user" type="org.dihedron.demo.portlets.portlet8.actions.ModelAction.User"/>

 
<br>
<portlet:actionURL name="FileUploadAction!onFileUpload" var="actionUrl"></portlet:actionURL>
<portlet:resourceURL id="FileUploadAction!onFileUpload" var="resourceUrl"></portlet:resourceURL>

<br>

You can submit multiple files to an Action; their MD5 checksum will be bounced back by this example.
<br>&nbsp;<br> 
<aui:form name="uploadForm" method="post" enctype="multipart/form-data" action="${actionUrl}">
	<aui:fieldset label="Files to Upload">
		<aui:input label="First:" name="file1" type="file" value="${file1}" placeholder="please choose the first file to upload..."/>
		<aui:input label="Second:" name="file2" type="file" value="${file2}" placeholder="please choose the second file to upload..."/>
		<aui:input label="Third:" name="file3" type="file" value="${file3}" placeholder="please choose the third file to upload..."/>
	</aui:fieldset>
	<br>
	<aui:button type="submit" name="syncSubmit" value="Submit synchronously!"/>
	<aui:button type="submit" name="asyncSubmit" value="Submit with AJAX!"/> 
</aui:form> 

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br></p>

<script type="text/javascript">

AUI().ready('aui-base', function(A) {
	/**
	 * Replace ActionURL with ResourceURL, then let event bubble up.
	 */
	A.one("#<portlet:namespace/>asyncSubmit").on('click', function() {
		console.log("replacing URL in form");
		A.one("#<portlet:namespace/>uploadForm").set('action', '<%=resourceUrl%>');		
	});

	/**
	 * Replace ResourceURL with ActionURL, then let event bubble up.
	 */
	A.one("#<portlet:namespace/>syncSubmit").on('click', function() {
		console.log("replacing URL in form");
		A.one("#<portlet:namespace/>uploadForm").set('action', '<%=actionUrl%>');		
	});
	
});

</script>

<%--

<aui:script use="aui-io-request, aui-node">
/*
A.one("#<portlet:namespace/>ajax").on('click', function (A) {
	alert("before <%=resourceUrl%>");
	A.io.request('<%=resourceUrl%>', {
		method: 'POST',
		form: {		
			id: '<portlet:namespace />fm'
		},
		on: {
			success: function() {
				alert(this.get('responseData'));
			}
	  	}
	});
	alert("after <%=resourceUrl%>");
}); 
*/
A.one("#<portlet:namespace />ajax").on('click', function(A) {
	alert("before <%=resourceUrl%>");
	A.io.request(
		'<%=resourceUrl%>',
		{
			data: {
				<portlet:namespace />value: 'pippo'
			},
			dataType: 'json',
			"arguments": A.one("#<portlet:namespace />result"), 
			on: {
				success: function(id, response, xmlHttp, targetNode) {
					var data = this.get('responseData');						
					/*targetNode.setContent('<pre>'+ JSON.stringify(data) + '</pre>');*/
					alert(JSON.stringify(data));
				}
			}
		}
	);
	alert("after <%=resourceUrl%>");
});

</aui:script>
--%>