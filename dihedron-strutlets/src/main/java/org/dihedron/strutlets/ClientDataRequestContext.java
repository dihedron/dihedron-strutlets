/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets;

import java.io.IOException;
import java.io.InputStream;

import javax.portlet.ClientDataRequest;

import org.apache.commons.fileupload.RequestContext;

/**
 * @author Andrea Funto'
 */
public class ClientDataRequestContext implements RequestContext {

	/**
	 * The underlying {@code ClientDataRequest} (whether an {@code ActionRequest} 
	 * or a {@code ResourceRequest}: this context supports both flavours, thus 
	 * enabling support for file uploads in both action and resource phases).
	 */
	private ClientDataRequest request;
	
	public ClientDataRequestContext(ClientDataRequest request) {
		this.request = request;
	}
	
	/**
	 * @see org.apache.commons.fileupload.RequestContext#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	/**
	 * @see org.apache.commons.fileupload.RequestContext#getContentLength()
	 */
	@Override
	public int getContentLength() {
		return request.getContentLength();
	}

	/**
	 * @see org.apache.commons.fileupload.RequestContext#getContentType()
	 */
	@Override
	public String getContentType() {
		return request.getContentType();
	}

	/**
	 * @see org.apache.commons.fileupload.RequestContext#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return request.getPortletInputStream();
	}
}
