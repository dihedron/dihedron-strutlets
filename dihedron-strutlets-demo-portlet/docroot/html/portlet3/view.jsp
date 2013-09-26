<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<portlet:defineObjects />
<strutlets:defineObjects />

<h3>JSR-286 Event Receiving through Actions</h3>

<p>

Waiting for an event...

<p>

In order to enable event receiving, you have to register the events you are going to listen for in your 
<code>portlet.xml</code> by including the following snippet:<br>
<pre>
    &lt;supported-processing-event&gt;         
      &lt;qname xmlns:demo='http://www.dihedron.org/events'&gt;demo:TestEvent&lt;/qname&gt;
    &lt;/supported-processing-event&gt;
</pre>
The same event must be defined at the end of the portlet.xml file, along with its payload type:
<pre>
    &lt;event-definition&gt;
      &lt;qname xmlns:demo="http://www.dihedron.org/events"&gt;demo:TestEvent&lt;/qname&gt;
      &lt;value-type&gt;java.lang.String&lt;/value-type&gt;
    &lt;/event-definition&gt;
</pre>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />

<%--
<br>

<div id="<portlet:namespace />control" style="cursor:hand">
	<img src="<%= request.getContextPath()%>/img/question.png" alt="HOWTO"/>
</div>
<div id="<portlet:namespace />hideable" style="display:none;">
<br>
In order to enable event receiving, you must declare what events your portlet is going to listen to
in your <code>portlet.xml</code>, as follows:<br><br>
<pre>
    &lt;supported-publishing-event xmlns:demo='http://www.dihedron.org/events'&gt;
      &lt;qname&gt;demo:TestEvent&lt;/qname&gt;
    &lt;/supported-publishing-event&gt;
</pre>
</div>

<p>

<br>
&nbsp;&nbsp;Powered by <strutlets:version />

<br>


<script src="http://yui.yahooapis.com/3.9.1/build/yui/yui-min.js"></script>
	<script type="text/javascript">
	YUI().use('node', 'event', 'transition', function(Y) {
		Y.on("click", function(e) {
			Y.one('#<portlet:namespace />hideable').toggleView();
		}, "#<portlet:namespace />control");
	});
</script>
--%>