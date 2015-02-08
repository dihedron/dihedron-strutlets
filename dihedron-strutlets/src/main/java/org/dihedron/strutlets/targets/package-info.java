/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

/**
 * The package containing all the classes related to the target management; 
 * targets are what is actually specified in the portlet URLs, and each target
 * is actually executed by a specific method of an action class.
 * Each target has an identifier (the AbstractAction + Method combination) and a set
 * of information used by the framework to decide how to handle the request and
 * how to serve the response once the invocation has been accomplished.
 * 
 * @author Andrea Funto'
 */
package org.dihedron.strutlets.targets;