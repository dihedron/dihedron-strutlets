/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 


package org.dihedron.strutlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dihedron.strutlets.renderers.impl.JspRenderer;

/**
 * Annotation representing the view handler that will 
 * create the visual representation for the given result.
 * Results can be represented as free text strings, and
 * are mapped to the appropriate view handler by the action
 * controller, based on what's in these annotations.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface Result {

	/**
	 * The result string for which the mapping to the renderer is 
	 * being described.
	 * 
	 * @return
	 *   the result string.
	 */
	String value() default "success";
	
	/**
	 * The new mode of the portlet, after the action has been executed; this
	 * parameter only makes sense when the action is executed in an action  or
	 * event phase, since portlets cannot change mode in render or resource 
	 * phase. If "same" is specified here (the default), the portlet mode is
	 * not changed.
	 * 
	 * @return
	 *   the name of the renderer class.
	 */
	String mode() default "same";
	
	/**
	 * The window state after the action has been executed; an action can only 
	 * change its state while in the action or event phase, otherwise this 
	 * parameter is not considered. If "same" is specified here, the portlet 
	 * does not change state.
	 * 
	 * The class of the view handler, which will format the output
	 * based on the annotated action's outcome and the parameters,
	 * as a class object; this propery can be used when the renderer
	 * is known at build time and eagerly bound; if it is not known 
	 * at build time, e.g. because it is in a plug-in that is only 
	 * loaded at runtime, use the <code>classname</code> property 
	 * instead.
	 * based on the annotated action's outcome and the parameters,
	 * as a class name; this property can be used when the renderer
	 * is loaded lazily, and only bound at runtime, e.g. because it is 
	 * in a plug-in and must be located by name; if it is known at build
	 * time, use the <code>classref</code> property instead.
	 * 
	 * @return
	 *   the renderer class.
	 */
	String state() default "same";	
	
	/**
	 * The type of renderer; this parameter can contain the name of a registered
	 * renderer, e.g. "jsp", which must have been registered. A set of core 
	 * renderers are registered by default by the framework, while the user can
	 * specify some additional ones which must implement the {@code Renderer}
	 * interface and be located in a package, as per the portlet's initialisation.
	 *  
	 * @return
	 *   the alias of the renderer.
	 */
	String renderer() default JspRenderer.ID;
	
	/**
	 * The data to be passed on to the renderer; in the case of a JSP renderer,
	 * this data would be the URL of the JSP page to show, whereas in the case 
	 * of a JSON or XML renderer, this data would be the name of the variable or
	 * of the parameter to be rendered as JSON or XML.
	 * 
	 * @return
	 *   the data to be used by the renderer to provide a meaningful output; in 
	 *   the case of a JSP renderer, this would typically be the URL of the JSP
	 *   to be included, whereas for JSON and XML it would be the name of the 
	 *   parameter containing the object to be JSON- or XML-encoded.
	 */
	String data() default "";	
}