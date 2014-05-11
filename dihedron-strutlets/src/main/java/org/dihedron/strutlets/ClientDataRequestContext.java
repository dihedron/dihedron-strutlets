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
