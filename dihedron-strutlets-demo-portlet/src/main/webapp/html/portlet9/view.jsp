<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Vector" %>

<portlet:defineObjects />
<strutlets:defineObjects />

<strutlets:useBean name="result" scopes="request" var="result" type="java.lang.String"/>

 
<br>
<portlet:actionURL name="FileUploadAction!onFileUploadSync" var="actionUrl"></portlet:actionURL>
<portlet:resourceURL id="FileUploadAction!onFileUploadAsync" var="resourceUrl"></portlet:resourceURL>

<br>

You can submit multiple files to an Action; their MD5 checksum will be bounced back by this example.
<br>&nbsp;<br> 
<aui:form name="<portlet:namespace />uploadForm" method="post" enctype="multipart/form-data" action="${actionUrl}">
	<aui:fieldset label="Files to Upload">
		<aui:input label="First:" name="file1" type="file" value="${file1}" placeholder="please choose the first file to upload..."/>
		<aui:input label="Second:" name="file2" type="file" value="${file2}" placeholder="please choose the second file to upload..."/>
		<aui:input label="Third:" name="file3" type="file" value="${file3}" placeholder="please choose the third file to upload..."/>
	</aui:fieldset>
	<br>
	<aui:button type="submit" name="syncSubmit" value="Submit synchronously!"/>
	<aui:button type="submit" name="asyncSubmit" value="Submit with AJAX!"/> 
</aui:form> 

<div id="<portlet:namespace />result">
	<pre>${result}</pre>
</div>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br></p>

<script type="text/javascript">

AUI().ready('aui-base', 'aui-io-form', 'aui-io-upload-iframe', 'aui-io-queue', function(A) {
	/**
	 * Replace ActionURL with ResourceURL, then let event bubble up.
	 */
	A.one("#<portlet:namespace/>asyncSubmit").on('click', function(e) {
		console.log("replacing URL in form");
		A.one("#<portlet:namespace/>uploadForm").set('action', '<%=resourceUrl%>');
		e.preventDefault();
        A.io('<%=resourceUrl%>', {
            method: 'POST',
            form: {
                id: A.one('#<portlet:namespace/>uploadForm'),
                upload: true
            },
            on: {
                success: function() {
                    /*
                    var file = A.JSON.parse(response.responseText), msg = 'Uploaded: ' + file.name + ' (' + file.size + ' bytes)';
                    Y.one('#<portlet:namespace />result').setHTML(msg);
                    */
                    var data = this.get('responseData');
                    alert(data);
                    /*
                    var data = response.responseText;						
					A.one('#<portlet:namespace/>result').setContent('<pre>'+ data + '</pre>');
					*/
                }
            }
        });
		
	});

	/**
	 * Replace ResourceURL with ActionURL, then let event bubble up.
	 */
	A.one("#<portlet:namespace/>syncSubmit").on('click', function(e) {		
		console.log("replacing URL in form");
		A.one("#<portlet:namespace/>uploadForm").set('action', '<%=actionUrl%>');
		/* let the event bubble... */		
	});	
});

</script>

<%--



YUI().use("io-form", function(Y) {
    // Create a configuration object for the file upload transaction.
    // The form configuration should include two defined properties:
    // id: This can be the ID or an object reference to the HTML form.
    // useDisabled: Set this property to "true" to include disabled
    //              HTML form fields, as part of the data.  By
    //              default, disabled fields are excluded from the
    //              serialization.
    // The HTML form data are sent as a UTF-8 encoded key-value string.
    var cfg = {
        method: 'POST',
        form: {
            id: formObject,
            useDisabled: true
        }
    };

    // Define a function to handle the response data.
    function complete(id, o, args) {
      var id = id; // Transaction ID.
      var data = o.responseText; // Response data.
      var args = args[1]; // 'ipsum'.
    };

    // Subscribe to event "io:complete", and pass an array
    // as an argument to the event handler "complete".
    Y.on('io:complete', complete, Y, { 'foo':'bar' });

    // Start the transaction.
    var request = Y.io(uri, cfg);
});






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