<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<portlet:defineObjects />
<strutlets:defineObjects />

<portlet:resourceURL id="AjaxAction!serveResource" var="ajaxUrl">
	<portlet:param name="resourceId" value="myResource"/>
</portlet:resourceURL>

<h3>AJAX Portlet</h3>

<p>

Click <input type="button" id="<portlet:namespace />launch" value="here" /> or <input type="button" id="<portlet:namespace />launch2" value="here" /> (with popup) to make an AJAX request...

<p>

<div id="<portlet:namespace />result">
	<pre></pre>
</div>

<br>
<br>
&nbsp;&nbsp;Powered by <strutlets:version />
<br>
 
<aui:script use='aui-io-request, aui-node'>

A.one("#<portlet:namespace />launch").on('click', function() {
	A.io.request(
		'${ajaxUrl}',
		{
			data: {
				<portlet:namespace />value: 'pippo'
			},
			dataType: 'json',
			"arguments": A.one("#<portlet:namespace />result"), 
			on: {
				success: function(id, response, xmlHttp, targetNode) {
					var data = this.get('responseData');						
					targetNode.setContent('<pre>'+ JSON.stringify(data) + '</pre>');
				}
			}
		}
	);
});

A.one("#<portlet:namespace />launch2").on('click', function() {
	if (confirm("Confirm?")){						//confirm
	  A.io.request(
		'${ajaxUrl}',
		{
			data: {
				<portlet:namespace />value: 'pippo'
			},
			dataType: 'json',
			"arguments": A.one("#<portlet:namespace />result"), 
			on: {
				success: function(id, response, xmlHttp, targetNode) {
					var data = this.get('responseData');						
					targetNode.setContent('<pre>'+ JSON.stringify(data) + '</pre>');
				}
			}
		}
	  );
	}
});

</aui:script>