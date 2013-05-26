/**
 * Copyright (c) 2012, 2013, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * The package containing all the classes related to the target management; 
 * targets are what is actually specified in the portlet URLs, and each target
 * is actually executed by a specific method of an action class.
 * Each target has an identifier (the Action + Method combination) and a set
 * of information used by the framework to decide how to handle the request and
 * how to serve the response once the invocation has been accomplished.
 * 
 * @author Andrea Funto'
 */
package org.dihedron.strutlets.targets;