/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;
import javax.portlet.StateAwareResponse;
import javax.portlet.ValidatorException;
import javax.portlet.WindowStateException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.dihedron.core.properties.Properties;
import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.actions.PortletMode;
import org.dihedron.strutlets.actions.WindowState;
import org.dihedron.strutlets.containers.portlet.PortalServer;
import org.dihedron.strutlets.containers.web.ApplicationServer;
import org.dihedron.strutlets.exceptions.InvalidPhaseException;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.dihedron.strutlets.upload.FileUploadConfiguration;
import org.dihedron.strutlets.upload.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object provides mediated access to the underlying JSR-286 features, such
 * as session, parameters, remote user information and the like. This interface 
 * is a superset of all available functionalities; other classes may provide a
 * restricted view base on the current phase, to help developers discover bugs at
 * compile time instead of having to catch exceptions at run time.
 * @author Andrea Funto'
 */
public class ActionContext {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionContext.class);

	/**
	 * The default encoding for file names in multipart/form-data requests.
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * The number of milliseconds in a second.
	 */
	private static final int MILLISECONDS_PER_SEC = 1000;
	
	/**
	 * The 4 possible phases in the portlet lifecycle.
	 * 
	 * @author Andrea Funto'
	 */
	public enum Phase {
		/**
		 * The phase in which action processing occurs.
		 */
		ACTION(PortletRequest.ACTION_PHASE),
		
		/**
		 * The phase in which the portlet handles events.
		 */
		EVENT(PortletRequest.EVENT_PHASE),
		
		/**
		 * The phase in which the portlet is requested to repaint itself.
		 */
		RENDER(PortletRequest.RENDER_PHASE),
		
		/**
		 * The phase in which the portlet is serving resources as is.
		 */
		RESOURCE(PortletRequest.RESOURCE_PHASE);	
		
		/**
		 * Returns the Strulets enumeraition value corresponding to the given
		 * textual representation.
		 * 
		 * @param value
		 *   a string representing the phase, e.g. "ACTION_PHASE".
		 * @return
		 *   an enumeraion value if the input string represents a vaild phase.
		 * @throws IllegalArgumentException
		 *   if the string does not represent a supported phase.
		 */
		public static Phase fromString(String value) {
			if(value != null) {
				for(Phase phase: Phase.values()) {
					if(value.equals(phase.toString())) {
						return phase;
					}
				}
			}
			throw new IllegalArgumentException("Invalid phase '" + value + "'.");			
		}
		
		/**
		 * Returns the standard (JSR-286) textual representation of the given phase.
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return value;
		}
		
		/**
		 * Constructor.
		 *
		 * @param value
		 *   the standard (JSR-286) name for the given phase.
		 */
		private Phase(String value) {
			this.value = value;
		}
		
		/**
		 * The standard (JSR-286) name of the phase.
		 */
		private String value;
	};
	
	/**
	 * The scope for the attributes.
	 * 
	 * @author Andrea Funto'
	 */
	public enum Scope {
		/**
		 * Attributes at request level will be accessible to the portlet that set 
		 * them and to included JSPs and servlets until the next action request 
		 * comes. The data lifecycle encompasses event and resource serving
		 * methods, up to the <em>next</em> action processing request, when they
		 * will be reset.
		 */
		REQUEST(0x00),
		
		/**
		 * Attributes set at <em>application</em> scope are accessible throughout the 
		 * application: all portlets, JSPs and servlets packaged in the same WAR
		 * file will have access to these attributes on a per-user basis. JSPs
		 * and servlets will have direct access to tese attributes through
		 * <code>HttsSession</code> attributes.
		 */
		APPLICATION(PortletSession.APPLICATION_SCOPE),
		
		/**
		 * Attributes set at <em>session</em> will be available to all resources 
		 * sharing the same window id, that is the very portlet that set them and
		 * its included JSPs and servlets. JSPs and servlets will <em>not</em>
		 * have direct access to the resource, because it will be stored in the
		 * <code>HttpSession</code> object under a namespaced attribute key.
		 * The fabricated attribute name will contain the window ID.
		 */
		PORTLET(PortletSession.PORTLET_SCOPE);
		
		/**
		 * Returns the numeric value of the constant.
		 * 
		 * @return
		 *   the numeric value of the constant.
		 */
		public int getValue() {
			return value;
		}
		
		/**
		 * Constructor.
		 *
		 * @param value
		 *   the numeric value of the constant.
		 */
		private Scope(int value) {
			this.value = value;
		}
		
		/**
		 * The numeric value of the constant.
		 */
		private int value;
	}
	
	/**
	 * The key under which request-scoped attributes are stored in the portlet session.
	 */
	protected static final String REQUEST_SCOPED_ATTRIBUTES_KEY = "org.dihedron.strutlets.request_scoped_attributes";
	
	protected static final String INTERCEPTOR_DATA_KEY = "org.dihedron.strutlets.interceptor_data"; 
	
	/**
	 * The per-thread instance.
	 */
	private static ThreadLocal<ActionContext> context = new ThreadLocal<ActionContext>() {
		@Override protected ActionContext initialValue() {
			logger.debug("creating action context instance for thread {}", Thread.currentThread().getId());
			return new ActionContext();
		}		
	};
	
	/**
	 * The action controller portlet.
	 */
	private GenericPortlet portlet;

	/**
	 * The portlet request (it might be an <code>ActionRequest</code>, a 
	 * <code>RenderRequest</code> or an <code>EventRequest</code>, depending on
	 * the lifecycle and the phase).
	 */
	private PortletRequest request;
	
	/**
	 * The portlet response (it might be an <code>ActionResponse</code>, a 
	 * <code>RenderResponse</code> or an <code>EventResponse</code>, depending on
	 * the lifecycle and the phase).
	 */
	private PortletResponse response;
	
	/**
	 * The actions' configuration; this map is read only and it's loaded at startup 
	 * if the URL of a properties file is provided in the portlet.xml.
	 */
	private Properties configuration;
	
	/**
	 * A reference to the <code>ApplicationServer</code> plug-in; this is useful if
	 * the portlets need to exploitm application.server specific APIs.
	 */
	private ApplicationServer server = null;
	
	/**
	 * A reference to the <code> PortalServer</code> plug-in, if available; 
	 * this is useful if the portlets want to use portal-server-specific APIs.
	 */
	private PortalServer portal = null;
	
	/**
	 * The encoding of uploaded file names.
	 */
	private String encoding = DEFAULT_ENCODING;
	
	/**
	 * A map containing names and information about all the form fields in a 
	 * multipart/form-data request; if the form is not multipart, the form values 
	 * can be retrieved directly from the request in the ordinary way (see 
	 * {@link HttpServletRequest#getParameter(String)} for details); if the 
	 * request is multipart, then the standard says that parameters will all be 
	 * mixed up in the request, and the context will extract them and place them 
	 * in this map. Thus, when this map is null, the request is not multipart 
	 * and values will be picked from the request, when not null, all parameters
	 * (be they form fields or uploaded files) will be available inside this map.
	 */
	private Map<String, FileItem> parts = null;
	
	/**
	 * This boolean value is used to keep track of changes to the render parameters,
	 * since a redirect can only be issued in action phase if no render parameters
	 * were changed. This flag is updated if and only if the render parameters are
	 * set through the <code>ActionContext</code>. 
	 */
	private boolean renderParametersChanged = false;
		
	/**
	 * Retrieves the per-thread instance.
	 * 
	 * @return
	 *   the per-thread instance.
	 */
	private static ActionContext getContext() {
		return context.get();
	} 
				
	/**
	 * Initialise the attributes map used to emulate the per-request attributes;
	 * this map will simulate action-scoped request parameters, and will be populated 
	 * with attributes that must be visible to all the render, serve resource and 
	 * event handling requests coming after an action processing request. These 
	 * parameters will be reset by the <code>ActionController</code>as soon as a 
	 * new action processing request comes. 
	 * 
	 * @param response
	 *   the portlet response.
	 * @param invocation
	 *   the optional <code>ActionInvocation</code> object, only available in the
	 *   context of an action or event processing, not in the render phase.
	 * @throws StrutletsException 
	 */
	static void bindContext(GenericPortlet portlet, PortletRequest request, PortletResponse response, Properties configuration, 
			ApplicationServer server, PortalServer portal, FileUploadConfiguration uploadInfo) throws StrutletsException {
		
		logger.debug("initialising the action context for thread {}", Thread.currentThread().getId());
		
		getContext().portlet = portlet;
		getContext().request = request;
		getContext().response = response;
		getContext().configuration = configuration;
		getContext().server = server;
		getContext().portal = portal;
		getContext().renderParametersChanged = false;

		// check if a map for REQUEST-scoped attributes is already available in
		// the PORTLET, if not instantiate it and load it into PORTLET scope
		PortletSession session = request.getPortletSession();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)session.getAttribute(getRequestScopedAttributesKey(), PortletSession.PORTLET_SCOPE);
		if(map == null) {
			logger.trace("installing REQUEST scoped attributes map into PORTLET scope");
			session.setAttribute(getRequestScopedAttributesKey(), new HashMap<String, Object>(), PortletSession.PORTLET_SCOPE);			
		}

		// this might be a multipart/form-data request, in which case we enable
		// support for file uploads and read them from the input stream using
		// a tweaked version of Apache Commons FileUpload facilities (in order 
		// to support file uploads in AJAX requests too!).
    	if(request instanceof ClientDataRequest) {
    		
    		// this is where we try to retrieve all files (if there are any that were 
    		// uploaded) and store them as temporary files on disk; these objects will
    		// be accessible as ordinary values under the "FORM" scope through a 
    		// custom "filename-to-file" map, which will be clened up when the context
    		// is unbound
    		String encoding = ((ClientDataRequest)request).getCharacterEncoding();
            getContext().encoding = Strings.isValid(encoding)? encoding : DEFAULT_ENCODING;
            logger.trace("request encoding is: '{}'", getContext().encoding);

            try {
            	RequestContext context = new ClientDataRequestContext((ClientDataRequest)request);
            	
		        // check that we have a file upload request
		        if(PortletFileUpload.isMultipartContent(context)) {  
	        	
		        	getContext().parts = new HashMap<String, FileItem>();
		        	
		        	logger.trace("handling multipart/form-data request");
		        	
			        // create a factory for disk-based file items
			        DiskFileItemFactory factory = new DiskFileItemFactory();
			        
			        // register a tracker to perform automatic file cleanup
//			        FileCleaningTracker tracker = FileCleanerCleanup.getFileCleaningTracker(getContext().filter.getServletContext());
//			        factory.setFileCleaningTracker(tracker);
			
			        // configure the repository (to ensure a secure temporary location 
			        // is used and the size of the )
			        factory.setRepository(uploadInfo.getRepository());
			        factory.setSizeThreshold(uploadInfo.getInMemorySizeThreshold());
			        
			        // create a new file upload handler
			        PortletFileUpload upload = new PortletFileUpload(factory);
			        upload.setSizeMax(uploadInfo.getMaxUploadableTotalSize());
			        upload.setFileSizeMax(uploadInfo.getMaxUploadableFileSize());
			
			        // parse the request & process the uploaded items
			        List<FileItem> items = upload.parseRequest(context);
			        logger.trace("{} items in the multipart/form-data request", items.size());
			        for(FileItem item : items) {
			        	// parameters would be stored with their fully-qualified 
			        	// name if we didn't remove the portlet namespace
			        	String fieldName = item.getFieldName().replaceFirst(getPortletNamespace(), "");
			        	logger.trace("storing field '{}' (type: '{}') into parts map", fieldName, item.isFormField() ? "field" : "file"); 
			        	getContext().parts.put(fieldName, item);
			        }		        
		        } else {
		        	logger.trace("handling plain form request");
		        }
        	} catch(FileUploadException e) {
        		logger.warn("error handling uploaded file", e);
        		throw new StrutletsException("Error handling uploaded file", e);
        	}		        
    	}
	}
	
	/**
	 * Returns a reference to the action controller; for private use only, do not
	 * mess around with the action controller or you might end up with unpredictable 
	 * results.
	 *   
	 * @return
	 *   a reference to the action controller.
	 */
	@Deprecated
	public static ActionController getActionController() {
		return (ActionController)getContext().portlet;
	}	

	/**
	 * Cleans up the internal status of the <code>ActionContextImpl</code> in order to
	 * avoid memory leaks due to persisting portal objects stored in the per-thread
	 * local storage; afterwards it removes the thread local entry altogether, so
	 * the application server does not complain about left-over data in TLS when
	 * re-deploying the portlet.
	 */	
	static void unbindContext() {
		logger.debug("removing action context for thread {}", Thread.currentThread().getId());
		getContext().request = null;
		getContext().response = null;
		getContext().portlet = null;
		getContext().configuration = null;
		getContext().server = null;
		getContext().portal = null;
		// remove all files if this is a multipart/form-data request, because
		// the file tracker does not seem to work as expected
		if(isMultiPartRequest()) {
			for(Entry<String, FileItem> entry : getContext().parts.entrySet()) {
				if(!entry.getValue().isFormField() && !entry.getValue().isInMemory()) {
					File file = ((DiskFileItem)entry.getValue()).getStoreLocation();
					try {
						logger.trace("removing uploaded file '{}' from disk...", file.getAbsolutePath());
						Files.delete(file.toPath());
						logger.trace("... file deleted");
					} catch(IOException e) {
						logger.trace("... error deleting file", e);
					}
				}
			}
		}		
		getContext().parts = null;
		
//		if(getContext().parts != null) {
//			for(Entry<String, FileItem> entry : getContext().parts.entrySet()) {
//				FileItem part = entry.getValue();
//				if(!part.isFormField() && !part.isInMemory()) {
//					logger.trace("releasing on-disk uploaded file '{}'", part.getFieldName());
//					
//				}
//			}
//		}
		context.remove();
	}
		
	/**
	 * Returns a reference to the current portal-server-specific plug-in, if 
	 * available. If no plug-in was loaded, returns null.
	 * 
	 * @return
	 *   a reference to the current portal-server-specific plug-in.
	 */
	public static PortalServer getPortalServer() {
		return getContext().portal;
	}
	
	/**
	 * Returns a reference to the current application-server-specific plug-in, if 
	 * available. If no plug-in was loaded, returns null.
	 * 
	 * @return
	 *   a reference to the current application-server-specific plug-in.
	 */
	public static ApplicationServer getApplicationServer() {
		return getContext().server;
	}	
	
	/**
	 * Returns the current phase of the action request lifecycle.
	 * 
	 * @return
	 *   the current phase of the action request lifecycle.
	 */
	public static Phase getCurrentPhase() {
		String currentPhase = (String)getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE);
		return Phase.fromString(currentPhase);
	}
	
	/**
	 * Returns whether the portlet is currently in the action phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the action phase.
	 */
	public static boolean isActionPhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.ACTION_PHASE);
	}

	/**
	 * Returns whether the portlet is currently in the event phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the event phase.
	 */
	public static boolean isEventPhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.EVENT_PHASE);
	}
	
	/**
	 * Returns whether the portlet is currently in the resource phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the resource phase.
	 */
	public static boolean isResourcePhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.RESOURCE_PHASE);
	}

	/**
	 * Returns whether the portlet is currently in the render phase.
	 * 
	 * @return
	 *   whether the portlet is currently in the render phase.
	 */
	public static boolean isRenderPhase() {
		return getContext().request.getAttribute(PortletRequest.LIFECYCLE_PHASE).equals(PortletRequest.RENDER_PHASE);
	}
	
	/**
	 * Returns the current portlet's name.
	 * 
	 * @return
	 *   the current portlet's name.
	 */
	public static String getPortletName() {
		return getContext().portlet.getPortletName();
	}
	
	/**
	 * Returns the portlet namespace; this value can be prefixed to DOM elements 
	 * and Javascript functions.
	 * 
	 * @return
	 *   the portlet namespace.
	 */
	public static String getPortletNamespace() {
		return getContext().response.getNamespace();
	}
	
	/**
	 * Returns the value of the given portlet's initialisation parameter.
	 * 
	 * @param name
	 *  the name of the parameter.
	 * @return
	 *   the value of the given portlet's initialisation parameter.
	 */
	public static String getPortletInitialisationParameter(String name) {
		return getContext().portlet.getInitParameter(name);
	}
	
	/**
	 * Factory method: creates a new Action URL. NOTE: this method returns a 
	 * valid result only when called in the action and render phases, otherwise 
	 * it returns null.
	 *  
	 * @return
	 *   returns a new Action URL if invoked in the action or render phase, returns 
	 *   null otherwise.
	 */
	public static PortletURL createActionURL() {
		if(getContext().response instanceof MimeResponse) {
			return ((MimeResponse)getContext().response).createActionURL();
		}
		return null;
	}
	
	/**
	 * Factory method: creates a new Render URL. NOTE: this method returns a 
	 * valid result only when called in the action and render phases, otherwise 
	 * it returns null.
	 *  
	 * @return
	 *   returns a new Render URL if invoked in the action or render phase, returns 
	 *   null otherwise.
	 */
	public static PortletURL createRenderURL() {
		if(getContext().response instanceof MimeResponse) {
			return ((MimeResponse)getContext().response).createRenderURL();
		}
		return null;
	}
	
	/**
	 * Factory method: creates a new Resource URL. NOTE: this method returns a 
	 * valid result only when called in the action and render phases, otherwise 
	 * it returns null.
	 *  
	 * @return
	 *   returns a new Resource URL if invoked in the action or render phase, 
	 *   returns null otherwise.
	 */
	public static ResourceURL createResourceURL() {
		if(getContext().response instanceof MimeResponse) {
			return ((MimeResponse)getContext().response).createResourceURL();
		}
		return null;
	}
	
	
	// TODO: get other stuff from portlet.xml and web.xml
	// PortletContext and PorteltConfig (see Ashish Sarin pages 119-120)
	
	/**
	 * In case of an <code>EventRequest</code>, returns the name of the event.
	 * 
	 * @return
	 *   the name of the event.
	 * @throws InvalidPhaseException
	 *   if invoked out of the "event" phase. 
	 */
	public static String getEventName() throws InvalidPhaseException {
		Event event = getEvent();
		if(event != null) {
			return event.getName();
		}
		return null;
	}

	/**
	 * In case of an <code>EventRequest</code>, returns the <code>QName</code>
	 * of the event.
	 * 
	 * @return
	 *   the <code>QName</code> of the event.
	 * @throws InvalidPhaseException
	 *   if invoked out of the "event" phase. 
	 */
	public static QName getEventQName() throws InvalidPhaseException {
		Event event = getEvent();
		if(event != null) {
			return event.getQName();
		}
		return null;
	}
	
	/**
	 * In case of an <code>EventRequest</code>, returns the serializable payload
	 * of the event.
	 * 
	 * @return
	 *   the serializable payload of the event.
	 * @throws InvalidPhaseException
	 *   if invoked out of the "event" phase. 
	 */
	public static Serializable getEventPayload() throws InvalidPhaseException {
		Event event = getEvent();
		if(event != null) {
			return event.getValue();
		}
		return null;		
	}
	
	/**
	 * Fires an event, for inter-portlet communication.
	 * 
	 * @param name
	 *   the fully-qualified name of the event.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	public static void fireEvent(String name, Serializable payload) throws InvalidPhaseException {
		if(isActionPhase() || isEventPhase()) {
			((StateAwareResponse)getContext().response).setEvent(name, payload);
		} else {
			logger.error("trying to fire an event in the render phase");
			throw new InvalidPhaseException("Events cannot be fired in the render phase.");
		}
	}
	
	/**
	 * Fires an event, for inter-portlet communication.
	 * 
	 * @param name
	 *   the name of the event.
	 * @param namespace
	 *   the event namespace.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	public static void fireEvent(String name, String namespace, Serializable payload) throws InvalidPhaseException {
		QName qname = new QName(namespace, name);
		fireEvent(qname,  payload);
	}
	
	/**
	 * Fires an event, for inter-portlet communication.
	 * 
	 * @param qname 
	 *   an object representing the fully-qualified name of the event.
	 * @param payload
	 *   the event payload, as a serialisable object.
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	public static void fireEvent(QName qname, Serializable payload) throws InvalidPhaseException {
		if(isActionPhase() || isEventPhase()) {
			((StateAwareResponse)getContext().response).setEvent(qname, payload);
		} else {
			logger.error("trying to fire an event in the render phase");
			throw new InvalidPhaseException("Events cannot be fired in the render phase.");
		}
	}
	
	/**
	 * Returns a string representing the authentication type.
	 * 
	 * @return
	 *   a string representing the authentication type.
	 */
	public static String getAuthType() {
		return getContext().request.getAuthType();
	}
	
	/**
	 * Checks whether the client request came through a secured connection.
	 * 
	 * @return
	 *   whether the client request came through a secured connection.
	 */
	public static boolean isSecure() {
		return getContext().request.isSecure();
	}
	
	/**
	 * Returns the name of the remote authenticated user.
	 * 
	 * @return
	 *   the name of the remote authenticated user.
	 */
	public static String getRemoteUser() {
		return getContext().request.getRemoteUser();
	}

	/**
	 * Returns the user principal associated with the request.
	 * 
	 * @return
	 *   the user principal.
	 */
	public static Principal getUserPrincipal() {
		return getContext().request.getUserPrincipal();
	}

	/**
	 * Checks whether the user has the given role. 
	 * 
	 * @param role
	 *   the name of the role
	 * @return
	 *   whether the user has the given role.
	 */
	public static boolean isUserInRole(String role) {
		return getContext().request.isUserInRole(role);
	}
	
	/**
	 * Returns the locale associated with the user's request.
	 * 
	 * @return
	 *   the request locale.
	 */
	public static Locale getLocale() {
		return getContext().request.getLocale();
	}
	
	/**
	 * Returns an Enumeration of Locale objects indicating, in decreasing order 
	 * starting with the preferred locale in which the portal will accept content 
	 * for this request. The Locales may be based on the Accept-Language header of the client.
	 *
	 * @return
	 *   an Enumeration of Locales, in decreasing order, in which the portal will 
	 *   accept content for this request
	 */	
	public static Enumeration<Locale> getLocales(){
		return getContext().request.getLocales();
	}
	
	/**
	 * Returns the set of available information for the current user as per the
	 * portlet's configuration in portlet.xml.
	 * 
	 * In order to have user information available in the portlet, the portlet.xml
	 * must include the following lines after all the portlets have been defined:
	 * <pre>
	 *   &lt;portlet-app ...&gt;
	 *     &lt;portlet&gt;
	 *     &lt;portlet-name&gt;MyPortlet&lt;/portlet-name&gt;
	 *       ...
	 *     &lt;/portlet&gt;
	 *     ...
	 *     &lt;user-attribute&gt;
	 *       &lt;description&gt;User First Name&lt;/description&gt;
	 *       &lt;name&gt;user.name.given&lt;/name&gt;
	 *     &lt;/user-attribute&gt;
	 *     &lt;user-attribute&gt;
	 *       &lt;description&gt;User Last Name&lt;/description&gt;
	 *       &lt;name&gt;user.name.family&lt;/name&gt;
	 *     &lt;/user-attribute&gt;
	 *   &lt;/portlet-app&gt;
	 * </pre>
	 * where {@code user.name.given} and {@code user.name.family} are two of the
	 * possible values; the following is a pretty complete list of acceptable 
	 * values:<ul>
	 *   <li>user.bdate</li>
	 *   <li>user.gender</li>
	 *   <li>user.employer</li>
	 *   <li>user.department</li>
	 *   <li>user.jobtitle</li>
	 *   <li>user.name.prefix</li>
	 *   <li>user.name.given</li>
	 *   <li>user.name.family</li>
	 *   <li>user.name.middle</li>
	 *   <li>user.name.suffix</li>
	 *   <li>user.name.nickName</li>
	 *   <li>user.home-info.postal.name</li>
	 *   <li>user.home-info.postal.street</li>
	 *   <li>user.home-info.postal.city</li>
	 *   <li>user.home-info.postal.stateprov</li>
	 *   <li>user.home-info.postal.postalcode</li>
	 *   <li>user.home-info.postal.country</li>
	 *   <li>user.home-info.postal.organization</li>
	 *   <li>user.home-info.telecom.telephone.intcode</li>
	 *   <li>user.home-info.telecom.telephone.loccode</li>
	 *   <li>user.home-info.telecom.telephone.number</li>
	 *   <li>user.home-info.telecom.telephone.ext</li>
	 *   <li>user.home-info.telecom.telephone.comment</li>
	 *   <li>user.home-info.telecom.fax.intcode</li>
	 *   <li>user.home-info.telecom.fax.loccode</li>
	 *   <li>user.home-info.telecom.fax.number</li>
	 *   <li>user.home-info.telecom.fax.ext</li>
	 *   <li>user.home-info.telecom.fax.comment</li>
	 *   <li>user.home-info.telecom.mobile.intcode</li>
	 *   <li>user.home-info.telecom.mobile.loccode</li>
	 *   <li>user.home-info.telecom.mobile.number</li>
	 *   <li>user.home-info.telecom.mobile.ext</li>
	 *   <li>user.home-info.telecom.mobile.comment</li>
	 *   <li>user.home-info.telecom.pager.intcode</li>
	 *   <li>user.home-info.telecom.pager.loccode</li>
	 *   <li>user.home-info.telecom.pager.number</li>
	 *   <li>user.home-info.telecom.pager.ext</li>
	 *   <li>user.home-info.telecom.pager.comment</li>
	 *   <li>user.home-info.online.email</li>
	 *   <li>user.home-info.online.uri</li>
	 *   <li>user.business-info.postal.name</li>
	 *   <li>user.business-info.postal.street</li>
	 *   <li>user.business-info.postal.city</li>
	 *   <li>user.business-info.postal.stateprov</li>
	 *   <li>user.business-info.postal.postalcode</li>
	 *   <li>user.business-info.postal.country</li>
	 *   <li>user.business-info.postal.organization</li>
	 *   <li>user.business-info.telecom.telephone.intcode</li>
	 *   <li>user.business-info.telecom.telephone.loccode</li>
	 *   <li>user.business-info.telecom.telephone.number</li>
	 *   <li>user.business-info.telecom.telephone.ext</li>
	 *   <li>user.business-info.telecom.telephone.comment</li>
	 *   <li>user.business-info.telecom.fax.intcode</li>
	 *   <li>user.business-info.telecom.fax.loccode</li>
	 *   <li>user.business-info.telecom.fax.number</li>
	 *   <li>user.business-info.telecom.fax.ext</li>
	 *   <li>user.business-info.telecom.fax.comment</li>
	 *   <li>user.business-info.telecom.mobile.intcode</li>
	 *   <li>user.business-info.telecom.mobile.loccode</li>
	 *   <li>user.business-info.telecom.mobile.number</li>
	 *   <li>user.business-info.telecom.mobile.ext</li>
	 *   <li>user.business-info.telecom.mobile.comment</li>
	 *   <li>user.business-info.telecom.pager.intcode</li>
	 *   <li>user.business-info.telecom.pager.loccode</li>
	 *   <li>user.business-info.telecom.pager.number</li>
	 *   <li>user.business-info.telecom.pager.ext</li>
	 *   <li>user.business-info.telecom.pager.comment</li>
	 *   <li>user.business-info.online.email</li>
	 *   <li>user.business-info.online.uri</li>
	 * </ul>
	 *     
	 * @return
	 *   a map representing the user information available to the portlets in 
	 *   the current application. 
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getUserInformation() {
		return (Map<String, Object>)getContext().request.getAttribute(PortletRequest.USER_INFO);		
	}
	
	/**
	 * Returns the current portal context, containing information about the 
	 * current portal server.
	 * 
	 * @return
	 *   the portal context.
	 */
	public static PortalContext getPortalContext() {
		return getContext().request.getPortalContext();
	}
	
	/**
	 * Returns the current portlet mode.
	 * 
	 * @return
	 *   the current portlet mode.
	 */
	public static PortletMode getPortletMode() {
		return PortletMode.fromString(getContext().request.getPortletMode().toString());
	}

	/**
	 * Sets the current portlet mode; it is preferable not to use this method
	 * directly and let the framework set the portlet mode instead, by
	 * specifying it in the action's results settings.
	 *  
	 * @param mode
	 *   the new portlet mode.
	 * @throws PortletModeException
	 *   if the new portlet mode is not supported by the current portal server 
	 *   runtime environment. 
	 * @throws InvalidPhaseException
	 *   if the operation is attempted while in the render phase. 
	 */
	@Deprecated
	public static void setPortletMode(PortletMode mode) throws PortletModeException, InvalidPhaseException {
		if(isActionPhase() || isEventPhase()) {
			if(getContext().request.isPortletModeAllowed(mode)) {
				logger.trace("changing portlet mode to '{}'", mode);
				((StateAwareResponse)getContext().response).setPortletMode(mode);
			} else {
				logger.warn("unsupported portlet mode '{}'", mode);
			}
		} else {
			logger.error("trying to change portlet mode in the render phase");
			 throw new InvalidPhaseException("Portlet mode cannot be changed in the render phase.");
		}			
	}
	
	/**
	 * Returns the current portlet window state.
	 * 
	 * @return
	 *   the current portlet window state.
	 */
	public static WindowState getWindowState() {
		return WindowState.fromString(getContext().request.getWindowState().toString());
	}
	
	/**
	 * Sets the current window state; it is preferable not to use this method
	 * directly and let the framework set the portlet window state instead, by
	 * specifying it in the action's results settings.
	 *  
	 * @param state
	 *   the new window state.
	 * @throws WindowStateException
	 *   if the new window state is not supported by the current portal server 
	 *   runtime environment. 
	 * @throws InvalidPhaseException
	 *   if the operation is attempted in the render phase. 
	 */
	@Deprecated
	public static void setWindowState(WindowState state) throws WindowStateException, InvalidPhaseException {
		if(isActionPhase() || isEventPhase()) {
			if(getContext().request.isWindowStateAllowed(state)) {
				logger.trace("changing window state to '{}'", state);
				((StateAwareResponse)getContext().response).setWindowState(state);
			} else {
				logger.warn("unsupported window state '{}'", state);
			}
		} else {
			logger.error("trying to change window state in the render phase");
			throw new InvalidPhaseException("Windows state cannot be changed in the render phase.");
		}			
	}
	
	/**
	 * Returns the portlet window ID. The portlet window ID is unique for this 
	 * portlet window and is constant for the lifetime of the portlet window.
	 * 	This ID is the same that is used by the portlet container for scoping 
	 * the portlet-scope session attributes.
	 * 
	 * @return
	 *   the portlet window ID.
	 */
	public static String getPortletWindowId() {
		if(getContext().request != null) {
			return getContext().request.getWindowID();
		}
		return null;
	}
	
	/**
	 * Returns the session ID indicated in the client request. This session ID 
	 * may not be a valid one, it may be an old one that has expired or has been 
	 * invalidated. If the client request did not specify a session ID, this 
	 * method returns null.
	 *  
	 * @return
	 *   a String specifying the session ID, or null if the request did not 
	 *   specify a session ID.
	 * @see isRequestedSessionIdValid()
	 */
	public static String getRequestedSessionId() {
		return getContext().request.getRequestedSessionId();
	}
	

	/**
	 * Checks whether the requested session ID is still valid.
	 * 
	 * @return
	 *   true if this request has an id for a valid session in the current 
     *   session context; false otherwise.
     * @see getRequestedSessionId()
     * @see getPortletSession()
     */
	public static boolean isRequestedSessionIdValid() {
		return getContext().request.isRequestedSessionIdValid(); 
	}
	
	/**
	 * Returns whether the <code>PortletSession</code> is still valid.
	 * 
	 * @return
	 *   whether the <code>PortletSession</code> is still valid.
	 */
	public static boolean isSessionValid() {
		PortletSession session = getContext().request.getPortletSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (elapsed < session.getMaxInactiveInterval() * MILLISECONDS_PER_SEC);
	}
	
	/**
	 * Returns the number of seconds left before the session gets invalidated 
	 * by the container.
	 * 
	 * @return
	 *   the number of seconds left before the session gets invalidated by the 
	 *   container.
	 */
	public static long getSecondsToSessionInvalid() {
		PortletSession session = getContext().request.getPortletSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (long)((elapsed - session.getMaxInactiveInterval() * MILLISECONDS_PER_SEC) / MILLISECONDS_PER_SEC);		
	}
	
	/**
	 * Returns the number of seconds since the last access to the session object.
	 * 
	 * @return
	 *   the number of seconds since the last access to the session object.
	 */
	public static long getTimeOfLastAccessToSession() {
		return getContext().request.getPortletSession().getLastAccessedTime();
	}
	
	/**
	 * Returns the maximum amount of inactivity seconds before the session is 
	 * considered stale.
	 * 
	 * @return
	 *   the maximum number of seconds before the session is considered stale.
	 */
	public static int getMaxInactiveSessionInterval() {
		return getContext().request.getPortletSession().getMaxInactiveInterval();
	}
	
	/**
	 * Sets the session timeout duration in seconds.
	 * 
	 * @param time
	 *   the session timeout duration, in seconds.
	 */
	public static void setMaxInactiveSessionInterval(int time) {
		getContext().request.getPortletSession().setMaxInactiveInterval(time);
	}
	
	/**
	 * Returns whether the request is a multipart/form-data request.
	 * 
	 * @return
	 *   whether the request is a multipart/form-data request.
	 */
	public static boolean isMultiPartRequest() {
		return getContext().parts != null;
	}
	
	/**
	 * Sets the title of the portlet; this method can only be invoked in the render 
	 * phase.
	 * 
	 * @param title
	 *   the new title of the portlet.
	 * @throws InvalidPhaseException 
	 *   if the method is invoked out of the "render" phase.
	 */
	public static void setPortletTitle(String title) throws InvalidPhaseException {
		if(isRenderPhase() && getContext().response instanceof RenderResponse) {
			logger.trace("setting the portlet title to '{}'", title);
			((RenderResponse)getContext().response).setTitle(title);
		} else {
			logger.error("cannot set the title out of the render phase");
			throw new InvalidPhaseException("Cannot set the portlet title when not in render phase");
		}
	}
	
	/**
	 * Encodes the given URL; ths URL is not prefixed with the current context 
	 * path, and is therefore considered as absolute. An example of such URLs is
	 * <code>/MyApplication/myServlet</code>.
	 * 
	 * @param url
	 *   the absolute URL to be encoded.
	 * @return
	 *   the URL, in encoded form.
	 */
	public static String encodeAbsoluteURL(String url) {
		String encoded = getContext().response.encodeURL(url);
		logger.trace("url '{}' encoded as '{}'", url, encoded);
		return encoded;
	}
	
	/**
	 * Encodes the given URL; the URL is prefixed with the current context path, 
	 * and is therefore considered as relative to it. An example of such URLs is
	 * <code>/css/myStyleSheet.css</code>.
	 * 
	 * @param url
	 *   the relative URL to be encoded.
	 * @return
	 *   the URL, in encoded form.
	 */	
	public static String encodeRelativeURL(String url) {
		String unencoded = getContext().request.getContextPath() + url;
		String encoded = getContext().response.encodeURL(unencoded);
		logger.trace("url '{}' encoded as '{}'", unencoded, encoded);
		return encoded;
	}
	
	/**
	 * Redirects to a different URL, with no referrer URL unless it is specified 
	 * in the URL itself. 
	 * 
	 * @param url
	 *   the URL to redirect the browser to (via a 302 HTTP status response).
	 * @throws IOException
	 *   if the redirect operation fails.
	 * @throws InvalidPhaseException 
	 *   if the method is invoked out of the "action" phase.
	 */
	public static void sendRedirect(String url) throws IOException, InvalidPhaseException {
		if(isActionPhase() && getContext().response instanceof ActionResponse) {
			((ActionResponse)getContext().response).sendRedirect(url);
		} else {
			logger.warn("trying to redirect while not in action phase");
			throw new InvalidPhaseException("Cannot redirect browser when not in action phase");
		}
	}

	/**
	 * Redirects to a different URL, adding a referrer to provide a "back" address 
	 * to the destination page.
	 * 
	 * @param url
	 *   the URL to redirect the browser to (via a 302 HTTP status response).
	 * @param referrer
	 *   the referrer URL, to provide a "back" link.
	 * @throws IOException
	 *   if the redirect operation fails.
	 * @throws InvalidPhaseException 
	 *   if the method is invoked out of the "action" phase.
	 */
	public static void sendRedirect(String url, String referrer) throws IOException, InvalidPhaseException {
		if(isActionPhase() && getContext().response instanceof ActionResponse) {
			((ActionResponse)getContext().response).sendRedirect(url, referrer);
		} else {
			logger.warn("trying to redirect while not in action phase");
			throw new InvalidPhaseException("Cannot redirect browser when not in action phase");
		}
	}
	
	/**
	 * Returns the resource bundle associated with the underlying portlet, for 
	 * the given locale.
	 * 
	 * @param locale
	 *   the selected locale.
	 * @return
	 *   the portlet's configured resource bundle.
	 */
	public static ResourceBundle getResouceBundle(Locale locale) {
		return getContext().portlet.getResourceBundle(locale);
	}
	
	/**
	 * Returns the per-user portlet preferences.
	 * 
	 * @return
	 *   the per-user portlet preferences.
	 */
	public static PortletPreferences getPortletPreferences() {
		return getContext().request.getPreferences();
	}

	/**
	 * Returns the names of all portlet preference values stored for the current
	 * portlet.
	 * 
	 * @return
	 *   the names of all portlet preference values stored for the current 
	 *   portlet.
	 */
	public static List<String> getPortletPreferenceNames() {
		List<String> names = new ArrayList<String>();
		Enumeration<String> enumeration = getContext().request.getPreferences().getNames();
		while(enumeration.hasMoreElements()) {
			names.add(enumeration.nextElement());
		}
		return names;
	}
	
	/**
	 * Returns the map of all portlet preference keys and values (as a string 
	 * array) for the current portlet.
	 * 
	 * @return
	 *   the map of all portlet preference keys and values (as a string array) 
	 *   for the current portlet.
	 */
	public static Map<String, String[]> getPortletPreferenceMap() {
		return getContext().request.getPreferences().getMap();
	}

	/**
	 * Returns the per-user portlet preference value corresponding to the given 
	 * key, or null if no preference exists under the given key.
	 * 
	 * @param key
	 *   the key of the portlet preference value.
	 * @return
	 *   the value of the preference, or the default value if no valid value was 
	 *   found.
	 */
	public static String getPortletPreferenceValue(String key) {
		return getPortletPreferenceValue(key, null);
	}
	
	/**
	 * Returns the per-user portlet preference value corresponding to the given 
	 * key, or the given default value if no preference exists under the given key.
	 * 
	 * @param key
	 *   the key of the portlet preference value.
	 * @param defaultValue
	 *   the default value, if no valid prefrence value could be found under the
	 *   given key.
	 * @return
	 *   the value of the preference, or the default value if no valid value was 
	 *   found.
	 */
	public static String getPortletPreferenceValue(String key, String defaultValue) {
		PortletPreferences preferences = getPortletPreferences();
		return preferences.getValue(key, defaultValue);
	}

	/**
	 * Returns the per-user portlet preference values corresponding to the given 
	 * key, or null if no preference exists under the given key.
	 * 
	 * @param key
	 *   the key of the portlet preference value set (as an array of strings).
	 * @return
	 *   the values of the preference, or null if no valid values were found.
	 */
	public static String[] getPortletPreferenceValues(String key) {
		return getPortletPreferenceValues(key, null);
	}
	
	/**
	 * Returns the per-user portlet preference value corresponding to the given 
	 * key, or the given default value if no preference exists under the given key.
	 * 
	 * @param key
	 *   the key of the portlet preference value set (as a string array).
	 * @param defaultValues
	 *   the default values, if no valid preference values could be found under 
	 *   the given key.
	 * @return
	 *   the values of the preference, or the default values if no valid values 
	 *   were found.
	 */
	public static String[] getPortletPreferenceValues(String key, String[] defaultValues) {
		PortletPreferences preferences = getPortletPreferences();
		return preferences.getValues(key, defaultValues);
	}
	
	/**
	 * Sets a new value for a portlet preference.
	 * 
	 * @param key
	 *   the name under which the preference is stored.
	 * @param value
	 *   the value of the preference.
	 * @throws StrutletsException
	 *   if the preference is read-only, it canot be validated or an I/O error
	 *   occurs writing it down to persistent store.
	 */
	public static void setPortletPreferenceValue(String key, String value) throws StrutletsException {
		if(Strings.isValid(key)) {
			PortletPreferences preferences = getPortletPreferences();
			try {
				preferences.setValue(key, value);
				preferences.store();
			} catch (ReadOnlyException e) {
				logger.error("invalid attempt to store read-only preference '" + key + "'", e);
				throw new StrutletsException("Invalid attempt to store read-only preference '" + key + "'", e);
			} catch (ValidatorException e) {
				logger.error("error validating preference '" + key + "', value '" + value + "'", e);
				throw new StrutletsException("Error validating preference '" + key + "', value '" + value + "'", e);
			} catch (IOException e) {
				logger.error("error storing preference '" + key + "', value '" + value + "'", e);
				throw new StrutletsException("Error storing preference '" + key + "', value '" + value + "'", e);
			}			
		}
	}
	
	/**
	 * Sets a new sets of values for a portlet preference.
	 * 
	 * @param key
	 *   the name under which the preference is stored.
	 * @param values
	 *   the values of the preference.
	 * @throws StrutletsException
	 *   if the preference is read-only, it canot be validated or an I/O error
	 *   occurs writing it down to persistent store.
	 */
	public static void setPortletPreferenceValues(String key, String[] values) throws StrutletsException {
		if(Strings.isValid(key)) {
			PortletPreferences preferences = getPortletPreferences();
			try {
				preferences.setValues(key, values);
				preferences.store();
			} catch (ReadOnlyException e) {
				logger.error("invalid attempt to store read-only preference '" + key + "'", e);
				throw new StrutletsException("Invalid attempt to store read-only preference '" + key + "'", e);
			} catch (ValidatorException e) {
				logger.error("error validating preference '" + key + "'", e);
				throw new StrutletsException("Error validating preference '" + key + "'", e);
			} catch (IOException e) {
				logger.error("error storing preference '" + key + "'", e);
				throw new StrutletsException("Error storing preference '" + key + "'", e);
			}			
		}
	}
	
	/**
	 * Resets the value(s) of the given prefefence.
	 * 
	 * @param key
	 *   the name of the preference.
	 * @throws StrutletsException
	 *   if the preference is read-only.
	 */
	public static void resetPortletPreference(String key) throws StrutletsException {
		if(Strings.isValid(key)) {
			PortletPreferences preferences = getPortletPreferences();
			try {
				preferences.reset(key);
			} catch (ReadOnlyException e) {
				logger.error("invalid attempt to reset read-only preference '" + key + "'", e);
				throw new StrutletsException("Invalid attempt to reset read-only preference '" + key + "'", e);
			}
		}
	}
	
	/**
	 * Returns whether the given portlet preference is a read-only value.
	 * 
	 * @param key
	 *   the name of the portlet preference.
	 * @return
	 *   whether the given portlet preference is a read-only value.
	 */
	public static boolean isReadOnlyPortletPreference(String key) {
		return getPortletPreferences().isReadOnly(key);
	}
	
	/**
	 * Looks for a parameter in any of the provided scopes, in the given order.
	 *  
	 * @param key
	 *   the name of the parameter to look for.
	 * @param scopes
	 *   the ordered list of scopes to look into.
	 * @return
	 *   the value of the parameter, as soon as it is found; null otherwise. 
	 * @throws StrutletsException
	 *   if the scopes include any other value besides FORM, REQUEST, PORTLET,
	 *   APPLICATION and CONFIGURATION. 
	 */
	public static Object findValueInScopes(String key, org.dihedron.strutlets.annotations.Scope ... scopes) throws StrutletsException {
		// now, depending on the scope, try to locate the parameter in the appropriate context 
		Object value = null;
		
		loop:
		for(org.dihedron.strutlets.annotations.Scope scope : scopes) {
			logger.trace("scanning input scope '{}' for parameter '{}'...", scope.name(), key);
			switch(scope) {
			case FORM:
				value = ActionContext.getParameterValues(key);
				if(value != null) {
					logger.trace("... value for '{}' found in FORM parameters: '{}'", key, value);
					break loop;
				}
				break;
			case REQUEST:	
				value = ActionContext.getRequestAttribute(key);
				if(value != null) {
					logger.trace("... value for '{}' found in REQUEST attributes: '{}'", key, value);
					break loop;
				}
				break;
			case PORTLET:
				value = ActionContext.getPortletAttribute(key);
				if(value != null) {
					logger.trace("... value for '{}' found in PORTLET attributes: '{}'", key, value);
					break loop;
				}
				break;
			case APPLICATION:
				value = ActionContext.getApplicationAttribute(key);
				if(value != null) {
					logger.trace("... value for '{}' found in APPLICATION attributes: '{}'", key, value);
					break loop;
				}
				break;
			case CONFIGURATION:
				value = ActionContext.getConfigurationValue(key);
				if(value != null) {
					logger.trace("... value for '{}' found in CONFIGURATION properties: '{}'", key, value);
					break loop;
				}
				break;
			case HTTP:
				if(getContext().portal != null) {
					value = ActionContext.getHttpParameterValue(key);
					if(value != null) {
						logger.trace("... value for '{}' found in HTTP parameters: '{}'", key, value);
						break loop;						
					}
				}
				break;
			default:				
				logger.error("cannot extract an input value from the {} scope: this is probably a bug!", scope.name());
				throw new StrutletsException("Cannot extract an input value from the " + scope.name() + " scope: this is probably a bug!");					
			}
		}
		return value;
	}

	/**
	 * Looks for all the parameters matching the given pattern in the given set
	 * of scopes, one at a time in the given order.
	 *  
	 * @param pattern
	 *   a pattern (regular expression) to identify the parameters or attributes 
	 *   to pick from the given scope.
	 * @param scopes
	 *   the ordered list of scopes to look into.
	 * @return
	 *   a map of parameter or attribute names and their corresponding values. 
	 * @throws StrutletsException
	 *   if the scopes include any other value besides FORM, REQUEST, PORTLET,
	 *   APPLICATION, CONFIGURATION and HTTP. 
	 */
	public static Map<String, Object> matchValuesInScopes(String pattern, org.dihedron.strutlets.annotations.Scope ... scopes) throws StrutletsException {
		// now, depending on the scope, try to locate the parameter in the appropriate context 
		Map<String, Object> values = new HashMap<String, Object>();
				
		for(org.dihedron.strutlets.annotations.Scope scope : scopes) {
			logger.trace("scanning input scope '{}' for parameters matching '{}'...", scope.name(), pattern);
			Map<String, Object> map = matchValuesInScope(pattern, scope);
			if(map != null && !map.isEmpty()) {
				for(String key : map.keySet()) {
					if(!values.containsKey(key)) {
						Object value = map.get(key);
						logger.trace("... adding parameter '{}' from scope {} (value '{}')", key, scope.name(), value);
						values.put(key,  value);
					}
				}
			}
		}
		return values;
	}
	
	/**
	 * Looks for all the parameters matching the given pattern in the given 
	 * scope.
	 *  
	 * @param pattern
	 *   a pattern (regular expression) to identify the parameters or attributes 
	 *   to pick from the given scope.
	 * @param scope
	 *   the scope to look into.
	 * @return
	 *   a map of parameter or attribute names and their corresponding values. 
	 * @throws StrutletsException
	 *   if the scopes include any other value besides FORM, REQUEST, PORTLET,
	 *   APPLICATION, CONFIGURATION and HTTP. 
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> matchValuesInScope(String pattern, org.dihedron.strutlets.annotations.Scope scope) throws StrutletsException {
		Regex regex = new Regex(pattern);
				
		logger.trace("retrieving values from scope '{}'...", scope.name());
		
		Map<?, ?> map = null;
		switch(scope) {		
		case FORM:			
			map = ActionContext.getParameters();
			break;
		case REQUEST:
			map = ActionContext.getAttributes(Scope.REQUEST);
			break;
		case PORTLET:
			map = ActionContext.getAttributes(Scope.PORTLET);
			break;
		case APPLICATION:
			map = ActionContext.getAttributes(Scope.APPLICATION);
			break;
		case CONFIGURATION:
			map = ActionContext.getConfigurationEntries();
			break;
		case HTTP:
			map = ActionContext.getHttpParametersMap();
			break;
		default:
			logger.error("cannot extract an input value from the {} scope: this is probably a bug!", scope.name());
			throw new StrutletsException("Cannot extract an input value from the " + scope.name() + " scope: this is probably a bug!");					
		}
		
		Set<String> keys = (Set<String>)map.keySet();
		Map<String, Object> values = new HashMap<String, Object>();
		for(String key : keys) {
			if(regex.matches(key)) {
				Object value = map.get(key);
				if(value != null) {
					logger.trace("... parameter '{}' has value '{}' in scope {}", key, value, scope.name());
					values.put(key, value);
				}						
			}
		}			
		return values;
	}
		
	/**
	 * Stores a value into the given scope.
	 *  
	 * @param key
	 *   the name of the parameter to store.
	 * @param scope
	 *   the scope into which the value must be stored.
	 * @param value
	 *   the value to be stored. 
	 * @throws StrutletsException
	 *   if the scope includes any other value besides RENDER, REQUEST, PORTLET,
	 *   and APPLICATION. 
	 */
	public static void storeValueIntoScope(String key, org.dihedron.strutlets.annotations.Scope scope, Object value) throws StrutletsException {
		logger.trace("storing parameter '{}' into scope '{}', value '{}'...", key, scope.name(), value);
		switch(scope) {
		case RENDER: 
			String string = value != null ? value.toString() : null; 
			setRenderParameter(key, string);
			break;
		case REQUEST:
			setRequestAttribute(key, value);
			break;
		case PORTLET:
			setPortletAttribute(key, value);
			break;
		case APPLICATION:
			setApplicationAttribute(key, value);
			break;
		default:
			logger.error("cannot store an output value into {} scope: this is probably a bug!", scope.name());
			throw new StrutletsException("Cannot store an output value into the " + scope.name() + " scope: this is probably a bug!");					
		}			
	}
	
	/**
	 * Removes a value from the given scope (if the value exists).
	 *  
	 * @param key
	 *   the name of the parameter to remove.
	 * @param scope
	 *   the scope from which the value must be removed.
	 * @throws StrutletsException
	 *   if the scope includes any other value besides RENDER, REQUEST, PORTLET,
	 *   and APPLICATION. 
	 */
	public static void removeValueFromScope(String key, org.dihedron.strutlets.annotations.Scope scope) throws StrutletsException {
		logger.trace("removing parameter '{}' from scope '{}'...", key, scope.name());
		switch(scope) {
		case RENDER:  
			removeRenderParameter(key);
			break;
		case REQUEST:
			removeRequestAttribute(key);
			break;
		case PORTLET:
			removePortletAttribute(key);
			break;
		case APPLICATION:
			removeApplicationAttribute(key);
			break;
		default:
			logger.error("cannot remove a value from the {} scope: this is probably a bug!", scope.name());
			throw new StrutletsException("Cannot remove a value from the " + scope.name() + " scope: this is probably a bug!");					
		}			
	}
	
	/**
	 * Retrieves the list of all parameter names in the original HTTP request.
	 * 
	 * @return
	 *   the list of all parameter names in the original HTTP request.
	 */
	public static List<String> getHttpParameterNames() {
		List<String> names = new ArrayList<String>();
		if(getContext().portal != null) {
			HttpServletRequest servlet = getContext().portal.getHTTPServletRequest(getContext().request);
			@SuppressWarnings("unchecked")
			Enumeration<String> enumeration = (Enumeration<String>)servlet.getParameterNames();
			while(enumeration.hasMoreElements()) {
				names.add(enumeration.nextElement());
			}
		}
		return names;
	}
	
	/**
	 * Returns the complete map of all HTTP paramaters along with their values.
	 * 
	 * @return
	 *   the complete map of all HTTP paramaters along with their values.
	 */
	public static Map<String, String[]> getHttpParametersMap() {
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		if(getContext().portal != null) {
			HttpServletRequest servlet = getContext().portal.getHTTPServletRequest(getContext().request);
			for(Object entry : servlet.getParameterMap().entrySet()) {
				String key = (String)(((Entry<?, ?>)entry).getKey());
				String[] values = (String[])(((Entry<?, ?>)entry).getValue());
				parameters.put(key,  values);
			}
		}
		return parameters;
	}
	
	/**
	 * Returns the value of the HTTP paramneter corresponfding to the given key.
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @return
	 *   the parameter value, or null if no portal server plugin is available or 
	 *   no parameter corresponds to the given key.
	 */
	public static String getHttpParameterValue(String key) {
		if(getContext().portal != null) {
			HttpServletRequest servlet = getContext().portal.getHTTPServletRequest(getContext().request);			
			return servlet.getParameter(key);
		}
		return null;
	}
	
	/**
	 * Returns the set of values for the given HTTP parameter key, or null.
	 * 
	 * @param key
	 *   the name of the paramter.
	 * @return
	 *   the set of values for the given HTTP parameter, or null if no portal 
	 *   server plugin is active or no parameter exists with the given name.
	 */
	public static String[] getHttpParameterValues(String key) {
		if(getContext().portal != null) {
			HttpServletRequest servlet = getContext().portal.getHTTPServletRequest(getContext().request);
			return servlet.getParameterValues(key);
		}
		return null;
	}
	
	/**
	 * Returns the list of all HTTP request attribute names.
	 * 
	 * @return
	 *   the list of all HTTP request attribute names.
	 */
	public static List<String> getHttpAttributeNames() {
		List<String> names = new ArrayList<String>();
		if(getContext().portal != null) {
			HttpServletRequest servlet = getContext().portal.getHTTPServletRequest(getContext().request);
			@SuppressWarnings("unchecked")
			Enumeration<String> enumeration = (Enumeration<String>)servlet.getAttributeNames();
			while(enumeration.hasMoreElements()) {
				names.add(enumeration.nextElement());
			}
		}
		return names;
	}
	
	/**
	 * Returns the complete map of all HTTP attributes along with their values.
	 * 
	 * @return
	 *   the complete map of all HTTP attributes along with their values.
	 */
	public static Map<String, Object> getHttpAttributesMap() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		if(getContext().portal != null) {
			HttpServletRequest servlet = getContext().portal.getHTTPServletRequest(getContext().request);		
			for(String attribute : getHttpAttributeNames()) {
				Object value = servlet.getAttribute(attribute);
				attributes.put(attribute, value);
			}
		}
		return attributes;
	}	
	
	/**
	 * Returns the application-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	public static Object getApplicationAttribute(String key) {
		PortletSession session = getContext().request.getPortletSession();
		Object value = session.getAttribute(key, PortletSession.APPLICATION_SCOPE);
		logger.trace("application attribute '{}' has value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);
		return value;
	}
	
	/**
	 * Adds or replaces an attribute in the map of attributes at application scope.
	 * The attribute will be shared among all portlets, JSPs and servlets belonging
	 * to the same application, on a per-user basis.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 */
	public static void setApplicationAttribute(String key, Object value) {
		PortletSession session = getContext().request.getPortletSession();
		session.setAttribute(key, value, PortletSession.APPLICATION_SCOPE);
		logger.trace("application attribute '{}' set to value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);		
	}
	
	/**
	 * Removes the application-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the previous value of the attribute, or null if not set.
	 */	
	public static Object removeApplicationAttribute(String key) {		
		PortletSession session = getContext().request.getPortletSession();
		Object value = session.getAttribute(key, PortletSession.APPLICATION_SCOPE);
		session.removeAttribute(key, PortletSession.APPLICATION_SCOPE);
		logger.trace("application attribute '{}' removed, previous value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);		
		return value;
	}

	/**
	 * Removes all application-level attributes from the session.
	 */
	public static void clearApplicationAttributes() {
		PortletSession session = getContext().request.getPortletSession();				
		Map<String, Object> attributes = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);
		for(Entry<String, Object> attribute : attributes.entrySet()) {
			removeApplicationAttribute(attribute.getKey());
		}
		logger.trace("all attributes at application scope cleared");
	}

	/**
	 * Returns the portlet-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	public static Object getPortletAttribute(String key) {
		PortletSession session = getContext().request.getPortletSession();
		Object value = session.getAttribute(key, PortletSession.PORTLET_SCOPE);
		logger.trace("portlet attribute '{}' has value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);
		return value;
	}
	
	/**
	 * Adds or replaces an attribute in the map of attributes at portlet scope.
	 * The attribute will be visible to the portlet itself (but not to other 
	 * instances of the same portlet), and to JSPs and servlets included by the 
	 * portlet, on a per-user basis.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 */
	public static void setPortletAttribute(String key, Object value) {
		PortletSession session = getContext().request.getPortletSession();
		session.setAttribute(key, value, PortletSession.PORTLET_SCOPE);	
		logger.trace("portlet attribute '{}' set to value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);
	}	
	
	/**
	 * Removes the portlet-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the previous value of the attribute, or null if not set.
	 */	
	public static Object removePortletAttribute(String key) {
		PortletSession session = getContext().request.getPortletSession();
		Object value = session.getAttribute(key, PortletSession.PORTLET_SCOPE);
		session.removeAttribute(key, PortletSession.PORTLET_SCOPE);
		logger.trace("portlet attribute '{}' removed, previous value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);
		return value;		
	}	
	
	/**
	 * Removes all portlet-level attributes from the session.
	 */
	public static void clearPortletAttributes() {
		PortletSession session = getContext().request.getPortletSession();				
		Map<String, Object> attributes = session.getAttributeMap(PortletSession.PORTLET_SCOPE);
		for(Entry<String, Object> attribute : attributes.entrySet()) {
			removePortletAttribute(attribute.getKey());
		}
		logger.trace("all attributes at portlet scope cleared");
	}	
	
	/**
	 * Returns the value of the request-scoped attribute.
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the value of the request-scoped attribute, or null if not set.
	 */
	public static Object getRequestAttribute(String key) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)getPortletAttribute(getRequestScopedAttributesKey()); 
		Object value = map.get(key);
		logger.trace("request attribute '{}' has value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);
		return value;
	}

	/**
	 * Adds or replaces an attribute in the map of attributes at request scope.
	 * The attribute will be available to all following render requests until a 
	 * new action request comes to reset them.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 */
	public static void setRequestAttribute(String key, Object value) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)getPortletAttribute(getRequestScopedAttributesKey()); 
		map.put(key, value);
		logger.trace("request attribute '{}' set to value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);
	}
	
	/**
	 * Removes the request-scoped attribute corresponding to the given key. 
	 * 
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the previous value of the attribute, or null if not set.
	 */	
	public static Object removeRequestAttribute(String key) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)getPortletAttribute(getRequestScopedAttributesKey());
		Object value = map.get(key);
		map.remove(key);
		logger.trace("request attribute '{}' removed, previous value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : Strings.NULL);
		return value;
	}
	
	/**
	 * Removes all request-level attributes from the session.
	 */
	public static void clearRequestAttributes() {
		logger.trace("clearing request attributes");
		PortletSession session = getContext().request.getPortletSession();				
		@SuppressWarnings("unchecked")
		Map<String, Object> attributes = (Map<String, Object>)session.getAttribute(getRequestScopedAttributesKey());
		attributes.clear();
		logger.trace("all attributes at request scope cleared");
	}	

	/**
	 * Returns the value of the given attribute in the proper application-, 
	 * session- or portlet-level map, depending on the scope.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param scope
	 *   the requested scope.
	 * @return
	 *   the requested attribute value, or null if not found.
	 */
	public static Object getAttribute(String key, Scope scope) {
		switch(scope) {
		case REQUEST:
			return getRequestAttribute(key);
		case PORTLET:
			return getPortletAttribute(key);
		case APPLICATION:
			return getApplicationAttribute(key);
		}
		return null;
	}
		
	/**
	 * Returns the proper map of attributes at application-, session- or portlet-
	 * level, depending on the requested scope.
	 * 
	 * @param scope
	 *   the requested scope.
	 * @return
	 *   the <em>immutable</em> map of attributes at the requested scope.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAttributes(Scope scope) {
		Map<String, Object> map = null;
		if(getContext().request != null) {
			PortletSession session = getContext().request.getPortletSession();
			switch(scope) {
			case APPLICATION:
				logger.trace("getting application attributes map");				
				map = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);
				break;
			case PORTLET:
				logger.trace("getting portlet attributes map");
				map = session.getAttributeMap(PortletSession.PORTLET_SCOPE);
				break;
			case REQUEST:
				logger.trace("getting request attributes map");
				map = (Map<String, Object>)getPortletAttribute(getRequestScopedAttributesKey());
				break;
			}			
		}
		return map;
	}	
	
	/**
	 * Stores the given attribute in the proper map, depending on the requested 
	 * scope.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param value
	 *   the attribute value.
	 * @param scope
	 *   the requested scope.
	 */
	public static void setAttribute(String key, Object value, Scope scope) {
		switch(scope) {
		case REQUEST:
			setRequestAttribute(key, value);
			break;
		case PORTLET:
			setPortletAttribute(key, value);
			break;
		case APPLICATION:
			setApplicationAttribute(key, value);
			break;
		}
	}
	  
	/**
	 * Merges all the entries in the given map into the appropriate attributes
	 * map; depending on the scope, the destination map will be the set of
	 * attributes contained in the <code>PortletSession</code> or in the
	 * <code>PortletRequest</code>, with the following semantics:<ul>  
	 * <li>in the case of <em>application</em> scope, the attributes are stored 
	 * in the attributes map at the <code>PortletSession</code>'s 
	 * <code>APPLICATION_SCOPE</code>; these attributes will be visible throughout
	 * the application to all portlets, JSPs and servlets on a per-user basis</li>
	 * <li>in the case of <em>portlet</em> scope, the attributes are stored 
	 * in the attributes map at the <code>PortletSession</code>'s 
	 * <code>PORTLET_SCOPE</code>; these attributes will be visible to the portlet
	 * itself (but not to other instances of the same portlet), and to JSPs and 
	 * servlets included by the portlet, on a per-user basis</li>
	 * <li>in the case of <em>request</em> scope, the attributes will be stored 
	 * and made available to all following render requests until a new action 
	 * request comes to reset them; this involves a bit of management by the
	 * <code>ActionController</code>, which has to reset action scoped request
	 * attributes when a new action comes</li>.
	 * </ul>.
	 * <em>NOTE</em>: Liferay 6.x and many other portlet containers do not support 
	 * action-request-scoped attributes, so instead of making request attributes 
	 * available to all the following render requests, they make these attributes 
	 * available only to the one render request that immediately follows, or to none
	 * at all. The expected behaviour has been simulated in this method by putting 
	 * these request-scoped parameters in a reserved and dedicated area in the 
	 * <code>PortletSession</code>; if you want to leverage the container's 
	 * native behaviour, use the deprecated setActionScopedAttributes() instead
	 * and remember to enable the <em>actionScopedRequestParmeters</em> runtime 
	 * option.
	 * 	 
	 * @param attributes
	 *   a map of attributes to be set at application level.
	 * @param scope
	 *   the scope at which the attributes should be set.
	 */
	public static void setAttributes(Map<String, Object> attributes, Scope scope) {
		for(Entry<String, Object> attribute : attributes.entrySet()) {	
			setAttribute(attribute.getKey(), attribute.getValue(), scope);
		}		
	}
	
	/**
	 * Removes the give attribute from the proper map, depending on the requested
	 * scope.
	 * 
	 * @param key
	 *   the attribute key.
	 * @param scope
	 *   the requested scope.
	 * @return
	 *   the previous value of the attribute, or null if not found.
	 */
	public static Object removeAttribute(String key, Scope scope) {		
		switch(scope) {
		case REQUEST:
			return removeRequestAttribute(key);
		case PORTLET:
			return removePortletAttribute(key);
		case APPLICATION:
			return removeApplicationAttribute(key);
		}
		return null;
	}
	
	/**
	 * Removes all attributes from the map at the requested scope.
	 * 
	 * @param scope
	 *   the requested scope.
	 */
	public static void clearAttributes(Scope scope) {
		switch(scope) {
		case REQUEST:
			clearRequestAttributes();
			break;
		case PORTLET:
			clearPortletAttributes();
			break;
		case APPLICATION:
			clearApplicationAttributes();
			break;
		}
	}
	
	/**
	 * Returns the map of all parameters set in the client request.
	 * 
	 * @return
	 *   the map of input parameters.
	 */
	public static Map<String, String[]> getParameters() {
		if(getContext().request != null) {
			return getContext().request.getParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all request parameters.
	 * 
	 * @return
	 *   the names of all request parameters.
	 */
	public static Set<String> getParameterNames() {
		Set<String> set = new HashSet<String>();
		Enumeration<String> e = getContext().request.getParameterNames();
		while(e.hasMoreElements()) {
			set.add(e.nextElement());
		}
		return set;
	}

	/**
	 * Returns the set of values associated with the given parameter key.
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @return
	 *   the array of parameter values.
	 */
	public static Object getParameterValues(String key) {		
		if(isMultiPartRequest()) {
			logger.trace("looking for parameter named '{}' in multipart/form-data", key);
			FileItem item = getContext().parts.get(key);
			if(item != null) {
				if(item.isFormField()) {
					return new String[] {item.getString()};
				} else {
					return new UploadedFile(item); 
				}
			}
		} else {
			logger.trace("looking for parameter named '{}' in plain form", key);
			return getContext().request.getParameterValues(key);			
		}
		return null;
	}
	
	/**
	 * Returns only the first of the set of values associated with the given
	 * parameter key.
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @return
	 *   the first value of the array, or null if not found.
	 */
	public static Object getFirstParameterValue(String key) {
		if(isMultiPartRequest()) {
			FileItem item = getContext().parts.get(key);
			if(item != null) {
				if(item.isFormField()) {
					return item.getString();
				} else {
					return new UploadedFile(item); 
				}
			}
		} else {
			return getContext().request.getParameter(key);			
		}
		return null;
	}

	/**
	 * Returns the map of all public parameters set in the client request.
	 * 
	 * @return
	 *   the map of input public parameters.
	 */
	public static Map<String, String[]> getPublicParameters() {
		if(getContext().request != null) {
			return getContext().request.getPublicParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all public request parameters.
	 * 
	 * @return
	 *   the names of all request public parameters.
	 */
	public static Set<String> getPublicParameterNames() {
		if(getContext().request != null) {
			return getContext().request.getPublicParameterMap().keySet();
		}
		return null;
	}

	/**
	 * Returns the set of values associated with the given public parameter key.
	 * 
	 * @param key
	 *   the name of the public parameter.
	 * @return
	 *   the array of parameter values.
	 */
	public static String[] getPublicParameterValues(String key) {
		if(getContext().request != null) {
			return getContext().request.getPublicParameterMap().get(key);
		}
		return null;
	}
	
	/**
	 * Returns only the first of the set of values associated with the given
	 * public parameter key.
	 * 
	 * @param key
	 *   the name of the public parameter.
	 * @return
	 *   the first value of the array, or null if not found.
	 */
	public static String getFirstPublicParameterValue(String key) {
		if(getContext().request != null) {
			String [] values = getContext().request.getPublicParameterMap().get(key);
			if(values != null && values.length > 0) {
				return values[0];
			}
		}
		return null;
	}
	
	/**
	 * Returns the map of all private parameters set in the client request.
	 * 
	 * @return
	 *   the map of input private parameters.
	 */
	public static Map<String, String[]> getPrivateParameters() {
		if(getContext().request != null) {
			return getContext().request.getPrivateParameterMap();
		}
		return null;
	}
	
	/**
	 * Returns the names of all private request parameters.
	 * 
	 * @return
	 *   the names of all request private parameters.
	 */
	public static Set<String> getPrivateParameterNames() {
		if(getContext().request != null) {
			return getContext().request.getPrivateParameterMap().keySet();
		}
		return null;
	}

	/**
	 * Returns the set of values associated with the given private parameter key.
	 * 
	 * @param key
	 *   the name of the private parameter.
	 * @return
	 *   the array of parameter values.
	 */
	public static String[] getPrivateParameterValues(String key) {
		if(getContext().request != null) {
			return getContext().request.getPrivateParameterMap().get(key);
		}
		return null;
	}
	
	/**
	 * Returns only the first of the set of values associated with the given
	 * private parameter key.
	 * 
	 * @param key
	 *   the name of the private parameter.
	 * @return
	 *   the first value of the array, or null if not found.
	 */
	public static String getFirstPrivateParameterValue(String key) {
		if(getContext().request != null) {
			String [] values = getContext().request.getPrivateParameterMap().get(key);
			if(values != null && values.length > 0) {
				return values[0];
			}
		}
		return null;
	}
	
	/**
	 * Returns the map of currently set render parameters.
	 * 
	 * @return
	 *   a map of render parameters names an values, or null if unsupported by 
	 *   the current type of request/response. 
	 */
	public static Map<String, String[]> getRenderParameterMap() {
		Map<String, String[]> parameters = null;
		if(getContext().response instanceof StateAwareResponse) {
			parameters = ((StateAwareResponse)getContext().response).getRenderParameterMap();
		} else if(getContext().request instanceof PortletRequest){
			logger.trace("retrieving the render parameter map in the render phase...");
			parameters = new HashMap<String, String[]>();
			PortletRequest request = (PortletRequest)getContext().request;
			Enumeration<String> names = request.getParameterNames();
			while(names.hasMoreElements()) {				
				String name = names.nextElement();
				String [] values = request.getParameterValues(name);
				logger.trace("... parameter '{}' has value '{}'", name, values);
				parameters.put(name, values);
			}
		}
		return parameters;
	}
	
	/**
	 * Replaces the render parameters map with the one supplied.
	 * 
	 * @param parameters
	 *   the new render parameters map.
	 * @throws InvalidPhaseException
	 *   if invoked out of the action or event phase.
	 */
	public static void setRenderParameterMap(Map<String, String[]> parameters) throws InvalidPhaseException {
		if(getContext().response instanceof StateAwareResponse) {
			((StateAwareResponse)getContext().response).setRenderParameters(parameters);
			getContext().renderParametersChanged = true;
		} else {
			logger.error("trying to set the render parameters map in render phase");
			throw new InvalidPhaseException("Render parameters cannot be set in the render phase");		
		}
	}

	/**
	 * Clears the render parameter map.
	 * 
	 * @throws InvalidPhaseException
	 *   if invoked out of the action or event phase.
	 */
	public static void clearRenderParameterMap() throws InvalidPhaseException {
		setRenderParameterMap(new HashMap<String, String[]>());
	}
	
	/**
	 * Sets a render parameter. The parameter value(s) must all be string(s).
	 * 
	 * @param key
	 *   the name of the parameter.
	 * @param values
	 *   the parameter value(s).
	 * @throws InvalidPhaseException
	 *   if invoked outside of the action or event phase.
	 */
	public static void setRenderParameter(String key, String... values) throws InvalidPhaseException {
		if(Strings.isValid(key) && values != null && (isActionPhase() || isEventPhase()) && getContext().response instanceof StateAwareResponse) {
			getContext().renderParametersChanged = true;
			logger.trace("setting render parameter '{}'...", key);
			if(values.length == 1) {
				logger.trace(" ... value is '{}'", values[0]);
				((StateAwareResponse)getContext().response).setRenderParameter(key, values[0]);
			} else {
				((StateAwareResponse)getContext().response).setRenderParameter(key, values);
				for(String value : values) {
					logger.trace(" ... value is '{}'", value);
				}
			} 
		} else {
			logger.error("trying to set render parameter '{}' in render phase", key);
			throw new InvalidPhaseException("Render parameters cannot be set in the render phase");
		}
	}
	
	/**
	 * Removes a render parameter from the render parameters map.
	 * 
	 * @param key
	 *   the key of the parameter to remove.
	 * @throws InvalidPhaseException
	 *   if invoked outside of the action or event phase.
	 */
	public static void removeRenderParameter(String key) throws InvalidPhaseException {
		if(Strings.isValid(key) && (isActionPhase() || isEventPhase()) && getContext().response instanceof StateAwareResponse) {
			getContext().renderParametersChanged = true;
			logger.trace("removing render parameter '{}'...", key);
			Map<String, String[]> parameters = getRenderParameterMap();
			parameters.remove(key);
			setRenderParameterMap(parameters);			
		} else {
			logger.error("trying to remove render parameter '{}' in render phase", key);
			throw new InvalidPhaseException("Render parameters cannot be removed in the render phase");
		}
	}
	
	/**
	 * Returns the names of all request attributes; these are the attributes normally 
	 * available through the request, they cannot be set by portlets and are provided
	 * by the portal instead.
	 * 
	 * @return
	 *   a list of attribute names.
	 */
	public static List<String> getAttributeNames() {
		Enumeration<String> enumeration = getContext().request.getAttributeNames();
		List<String> names = new ArrayList<String>();
		while(enumeration.hasMoreElements()) {
			names.add(enumeration.nextElement());
		}
		return names;
	} 
	
	/**
	 * Returns the value of the request attribute corresponding to the given name.
	 * This attribute is not to be confused with those set in the variuos scopes,
	 * and is a value provided by the portal server.
	 * 
	 * @param key
	 *   the name of the request attribute.
	 * @return
	 *   the request attribute value.
	 */
	public static Object getAttribute(String key) {
		return getContext().request.getAttribute(key);
	}
	
	/**
	 * Returns the values in the actions configuration file as a map of string 
	 * keys and values.
	 * 
	 * @return
	 *   the values in the actions configuration file as a map of string keys 
	 *   and values.
	 */
	public static Map<String, String> getConfigurationEntries() {
		Map<String, String> entries = new HashMap<String, String>();
		if(getContext().configuration != null) {
			for(Entry<String, String> entry : getContext().configuration.entrySet()) {
				entries.put(entry.getKey(), entry.getValue());
			}
		}
		return entries;
	}
	
	/**
	 * Returns the list of all configuration keys in the actions configuration 
	 * properties file, if available, or an emty list otherwise.
	 *  
	 * @return
	 *   the list of all configuration keys in the actions configuration 
	 * properties file, if available, or an emty list otherwise.
	 */
	public static List<String> getConfigurationKeys() {
		List<String> keys = new ArrayList<String>();
		if(getContext().configuration != null) {
			keys.addAll(getContext().configuration.keySet());
		}
		return keys;
	}
	
	/**
	 * Returns the value in the actions configuration properties file corresponding
	 * to the given key, if available; null otherwise.
	 * 
	 * @param key
	 *   the property key.
	 * @return
	 *   the value in the actions configuration properties file corresponding
	 *   to the given key, if available; null otherwise.
	 */
	public static String getConfigurationValue(String key) {
		if(getContext().configuration != null) {
			return getContext().configuration.get(key);
		}
		return null;
	}
	
	/**
	 * Returns an array containing all of the Cookie properties. This method 
	 * returns null if no cookies exist.
	 * 
	 * @return
	 *   the array of cookie properties, or null if no cookies exist.
	 */
	public static Cookie[] getCookies() {
		if(getContext().request != null) {
			return getContext().request.getCookies();
		}
		return null;
	}
	
	/**
	 * Adds a cookie to the client.
	 * 
	 * @param cookie
	 *   the cookie to be added to the client.
	 */
	public static void setCookie(Cookie cookie) {
		if(getContext().response != null) {
			getContext().response.addProperty(cookie);
		}
	}
	
	/**
	 * Returns the underlying portlet request object.
	 * 
	 * @return
	 *   the underlying portlet request object.
	 */
	@Deprecated
	public static PortletRequest getPortletRequest() {
		return getContext().request;
	}
	
	/**
	 * Returns the underlying portlet response object.
	 * 
	 * @return
	 *   the underlying portlet response object.
	 */
	@Deprecated
	public static PortletResponse getPortletResponse() {
		return getContext().response;
	}

	/**
	 * Returns the original HTTP request object.
	 * 
	 * @return
	 *   the original HTTP request object.
	 */
	@Deprecated
	public static HttpServletRequest getHttpServletRequest() {
		if(getContext().portal != null) {
			return getContext().portal.getHTTPServletRequest(getContext().request);
		}
		return null;
	}
	
	
//	/**
//	 * Returns the underlying HTTP server request object, which is common to all
//	 * portlets on the same page. Note that portlet-specific parameters in the 
//	 * URL are encoded with a portlet-specific namespace.
//	 * 
//	 * @return
//	 *   the underlying HTTP server request object.
//	 */
//	public static HttpServletRequest getHttpServletRequest() {
//		Enumeration<String> names = getContext().request.getAttributeNames();
//		while(names.hasMoreElements()) {
//			String name = names.nextElement();
//			Object value = getContext().request.getAttribute(name);
//			logger.trace("request attribute '{}' = '{}'", name, value);
//		}
//		
//		return (HttpServletRequest)getContext().request.getAttribute("javax.servlet.request");  
//	}	
		
	/**
	 * Returns the underlying portlet session object.
	 * 
	 * @return
	 *   the underlying portlet session object.
	 */
	@Deprecated
	public static PortletSession getPortletSession() {
		return getContext().request.getPortletSession();
	}
	
	/**
	 * Returns the event, if this invocation was due to an inter-portlet communication
	 * even being fired ("event phase").
	 *  
	 * @return
	 *   the event object.
	 * @throws InvalidPhaseException 
	 */
	@Deprecated
	public static Event getEvent() throws InvalidPhaseException {
		if(isEventPhase()) {
			return ((EventRequest)getContext().request).getEvent();
		} else {
			logger.error("trying to get event out of event phase");
			throw new InvalidPhaseException("Events are not available out of event phase");
		}
	}

	/**
	 * Sets interceptor-specific data into the action context; this information 
	 * is available through different calls and can be used to keep track of 
	 * system status, such as number of calls for target, or number of accesses
	 * by the same user etc. This method should only be used by interceptors, 
	 * and the associated data should not be tampered with, to avoid unpredictable 
	 * behaviour.
	 * 
	 * @param interceptorId
	 *   the namepaced id of the interceptor this data belongs to (see 
	 *   {@link Interceptor#getId()} for details).
	 * @param data
	 *   the data object.
	 */
	public static void setInterceptorData(String interceptorId, Object data) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)getPortletAttribute(INTERCEPTOR_DATA_KEY);
		if(map == null) {
			map = Collections.synchronizedMap(new HashMap<String, Object>());
			setPortletAttribute(INTERCEPTOR_DATA_KEY, map);
		}
		map.put(interceptorId, data);
	}	
	
	/**
	 * Retrieves interceptor-specific data stored by the given interceptor. This 
	 * method should only be used by interceptors, and the associated data should 
	 * not be tampered with, to avoid unpredictable behaviour.
	 *   
	 * @param interceptorId
	 *   the namespaced id of the interceptor owning the stored data.
	 * @return
	 *   the data, or null if none found.
	 */
	public static Object getInterceptorData(String interceptorId) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)getPortletAttribute(INTERCEPTOR_DATA_KEY);
		Object data = null;
		if(map != null) {
			data = map.get(interceptorId);
		}
		return data;
	}	
	
	/**
	 * Returns the data stored by the given interceptor. This method should only 
	 * be used by interceptors, and the associated data should not be tampered
	 * with, to avoid unpredictable behaviour.
	 * 
	 * @param interceptorId
	 *   the namespaced id of the interceptor requesting the data.
	 * @param clazz
	 *   the type of the data to be retrieved, so it can be automatically cast.
	 * @return
	 *   the data, already cast to the given type, or null if nothing found.
	 */
	public static <T> T getInterceptorData(String interceptorId, Class<? extends T> clazz) {
		Object data = getInterceptorData(interceptorId);
		return data != null ? clazz.cast(data) : null;
	}	
	
	/**
	 * This method returns a portlet-specific key for request-scoped attributes
	 * for the current portlet.
	 * 
	 * @return
	 *   a portlet-specific key for request-scoped attributes.
	 */
	public static String getRequestScopedAttributesKey() {
		return getRequestScopedAttributesKey(getPortletName());
	}
	
	/**
	 * This method returns the portlet-specific key for request-scoped attributes
	 * for the given portlet name.
	 * 
	 * @param portletName
	 *   the name of the portlet whose request-scoped attributes key is being
	 *   asked for.
	 * @return
	 *   the portlet-specific key for request-scoped attributes.
	 */
	public static String getRequestScopedAttributesKey(String portletName) {
		return ActionContext.REQUEST_SCOPED_ATTRIBUTES_KEY + "[" + portletName.toUpperCase() + ":" + ActionContext.getPortletWindowId() + "]";
	}
	
	/**
	 * This method checks if during the invocation the render parameters have been 
	 * changed, e g. by setting their values. This method is reliable if and only
	 * if the only way to set the render parameters is through the appropriate 
	 * <code>ActionContext</code> method: if you use the portlet response method
	 * directly the framework has no way of detecting changes to the render 
	 * parameters.
	 * Tracking this information is relevant because you cannot redirect to another 
	 * page (in the action phase) if any render parameter has been changed; if 
	 * you help the framework keep track of what you do with the parameters, it
	 * might help you spot errors by giving a warning instead of simply letting 
	 * the portal fail with an illegal state exception.
	 * 
	 * @return
	 *   whether the render parameters have been set during this invocation.
	 */
	public static boolean hasChangedRenderParameters() {
		return getContext().renderParametersChanged;
	}
	
	/**
	 * Protected constructor, so this object cannot be instantiated by anyone 
	 * except extending classes, which are supposed to provide a phase-related 
	 * filter on available portlat functionalities. 
	 */
	protected ActionContext() {		
	}
}
