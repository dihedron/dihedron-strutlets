<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.dihedron.org/strutlets" prefix="strutlets" %>

<portlet:defineObjects />

<portlet:actionURL name="DynamicTestAction!myMethod" var="dynamicSuccessUrl1">
	<portlet:param name="result" value="success"/>
</portlet:actionURL>

<portlet:actionURL name="DynamicTestAction!myMethod" var="dynamicFailureUrl1">
	<portlet:param name="result" value="error"/>
</portlet:actionURL>

<portlet:actionURL name="DynamicTestAction" var="dynamicSuccessUrl2">
	<portlet:param name="result" value="success"/>
</portlet:actionURL>

<portlet:actionURL name="DynamicTestAction" var="dynamicFailureUrl2">
	<portlet:param name="result" value="error"/>
</portlet:actionURL>


<h3>Annotation-configured Actions</h3>

<p>

Dynamically configured actions are the best way to leverage Strutlets auto-configuring capabilities.
<p>
Throughout the request lifecycle, annotations or (if missing) the configuration-driver conventions adopted by the
framework allow to navigate through requests and JSPs without writing a single line of XML.
<p>
The path to the render resource (typically a JSP) is determined by what's written in the method's <em>with &#64;Result</em> 
annotations; if no annotation can be found for the method's result, the framework will conjure up a URL using what is
specified in the portlet.xml file.
<p>
Annotation driven configuration can be obtained by annotating the Action's method as follows:<br>
<pre>
	&#64;Invocable(
		results = {
			&#64;Result(value="success", data="/html/portlet1/MyAction/myMethod/success.jsp"),
			&#64;Result(value="error", data="/html/portlet1/MyAction/myMethod/error.jsp")				
		}
	)
	public String myMethod() throws ActionException {
		...
	}
</pre>
<p>
If you omit the &#64;Result annotations, the framework will conjure up the view resources according to the
usual conventions and what you specified in the <code>portlet.xml</code>. 
<p>
These links trigger the execution of the default Action method (when you only specify the Action's name, the default 
<code>execute</code> method is invoked):<ul> 
<li><a href="${dynamicSuccessUrl2}">successful</a> execution of DynamicTestAction[!execute]</li>
<li><a href="${dynamicFailureUrl2}">failed</a> execution of DynamicTestAction[!execute]</li>
</ul>
<p>
the following links trigger the execution of:<ul>
<li><a href="${dynamicSuccessUrl1}">successful</a> execution of DynamicTestAction!myMethod</li>
<li><a href="${dynamicFailureUrl1}">failed</a> execution of DynamicTestAction!myMethod</li>
</ul>
In both cases portlet mode stays the same and so does the window state.
<p>
In order to let the framework infer the path to the view resources, specify the following initialisation
parameters in the <code>portlet.xml</code>:<br>
<pre>
		&lt;init-param&gt;
			&lt;name&gt;render.root.directory&lt;/name&gt;
			&lt;value&gt;/html/portlet1/my/path&lt;/value&gt;
		&lt;/init-param&gt;

		&lt;init-param&gt;
			&lt;name&gt;render.path.pattern&lt;/name&gt;
			&lt;value&gt;&#36;{rootdir}/&#36;{action}/&#36;{method}/&#36;{result}.jsp&lt;/value&gt;
		&lt;/init-param&gt;
</pre>
<br>and then annotate the method as follows:<br>
<pre>
	&#64;Invocable public String myMethod() throws ActionException {
		...
	}
</pre>

<p>

&nbsp;&nbsp;Powered by <strutlets:version />

<br>