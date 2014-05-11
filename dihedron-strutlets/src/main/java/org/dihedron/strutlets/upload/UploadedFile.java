/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Zephyr framework ("Zephyr").
 *
 * Zephyr is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Zephyr is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Zephyr. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.strutlets.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.dihedron.commons.strings.Strings;
import org.dihedron.strutlets.ActionContext;

/**
 * A class representing an uploaded file, and providing a few operations on it.
 * 
 * @author Andrea Funto'
 */
public class UploadedFile {

	/**
	 * The Apache FileUpload object containing information about the uploaded file.
	 */
	private FileItem info;
	
	/**
	 * Constructor.
	 *
	 * @param info
	 *   the Apache FileUpload item containing information about the uploaded 
	 *   file. 
	 */
	public UploadedFile(FileItem info) {
		this.info = info;
	}
	
	/**
	 * Returns the name of the file.
	 * 
	 * @return
	 *  the name of the file.
	 */
	public String getName() {
		return info.getName();
	}
	
	/**
	 * Returns the name of the form field containing the file, after the portlet 
	 * namespace has been removed.
	 * 
	 * @return
	 *   the name of the form field containing the file without the portlet 
	 *   namespace.
	 */
	public String getFieldName() {
		String name = info.getFieldName();
		String namespace = ActionContext.getPortletNamespace();
		if(Strings.isValid(namespace)) {
			name = name.replaceFirst(namespace, "");
		}
		return name;
	}
	
	/**
	 * Returns the original name of the field, including the portlet namespace.
	 * 
	 * @return
	 *   the original name of the field, including the portlet namespace.
	 */
	public String getOriginalFieldName() {
		return info.getFieldName();
	}
	
	/**
	 * Returns whether the File's size is such (small) that it is stored in memory.
	 * 
	 * @return
	 *   whether the File's size is such that it is stored in memory.
	 */
	public boolean isInMemory() {
		return info.isInMemory();
	}
	
	/**
	 * Returns the headers associated with the given uploaded file.
	 *
	 * @return
	 *   the headers associated with the given uploaded file.
	 */
	public Map<String, String[]> getHeaders() {		
		Map<String, String[]> result = new HashMap<String, String[]>();
		FileItemHeaders headers =  info.getHeaders();		
		Iterator<String> names = headers.getHeaderNames();
		while(names.hasNext()) {
			String name = names.next();
			Iterator<String> values = headers.getHeaders(name);
			List<String> list = new ArrayList<String>();
			while(values.hasNext()) {
				list.add(values.next());
			}
			result.put(name, list.toArray(new String[list.size()]));
		}
		return result;
	}
	
	/**
	 * Returns the size of the uploaded file.
	 * 
	 * @return
	 *   the size of the uploaded file.
	 */
	public long getSize() {
		return info.getSize();
	}
	
	/**
	 * Returns the character set associated with the mutipart/form-data request.
	 * 
	 * @return
	 *   the character set associated with the mutipart/form-data request.
	 */
	public String getCharacterSet() {
		if(info instanceof DiskFileItem) {
			return ((DiskFileItem)info).getCharSet();
		}
		return null;
	}
	
	/**
	 * Returns the content type of the file.
	 * 
	 * @return
	 *   the content type of the file.
	 */
	public String getContentType() {
		return info.getContentType();
	}
	
	/**
	 * Returns the uploaded file as a byte array.
	 * 
	 * @return
	 *   the uploaded file as a byte array.
	 */
	public byte[] getAsByteArray() {
		return info.get();
	}
	
	/**
	 * Returns the uploaded file as a Java File object; note that this method may 
	 * not return a valid File object is the uploaded file was kept in memory 
	 * (its size was below the in-memory threshold).
	 * 
	 * @return
	 *   the uploaded file as a Java File object, or {@code null} if stored in 
	 *   memory.
	 */
	public File getAsFile() {
		if(info instanceof DiskFileItem) {
			return ((DiskFileItem)info).getStoreLocation();
		}
		return null;
	}
	
	/**
	 * Returns the uploaded file as an input stream
	 * 
	 * @return
	 * 
	 *  the uploaded file as an input stream.
	 * @throws IOException
	 *   if the input stream cannot be acquired. 
	 */
	public InputStream getAsInputStream() throws IOException {
		return info.getInputStream();
	}
	
	/**
	 * Returns the uploaded file as a string, using the default character set.
	 * 
	 * @return
	 *   the uploaded file as a  string, using the default character set.
	 */
	public String getAsString() {
		return info.getString();
	}
	
	/**
	 * Returns the uploaded file as a string, using the specified character set.
	 * 
	 * @param charset
	 *   the character set to use for decoding the file content.
	 * @return
	 *   the uploaded file as a  string, using the specified character set.
	 * @throws UnsupportedEncodingException
	 *   if the specified character set is not supported on this platform. 
	 */
	public String getAsString(String charset) throws UnsupportedEncodingException {
		return info.getString(charset);
	}
	
	/**
	 * A convenience method to store the uploaded file to a file on disk.
	 * 
	 * @param file
	 *   the file to write to.
	 * @throws Exception
	 *   if the file cannot be written. 
	 */
	public void writeTo(File file) throws Exception {
		info.write(file);
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{ ")
			.append("name: '").append(getName())
			.append("', field : '").append(getOriginalFieldName())
			.append("', size  : ").append(getSize())
			.append(", storage: '").append(getAsFile() != null ? getAsFile().getAbsolutePath() : "memory")
			.append("' }");
		
		return buffer.toString();
	}
}
