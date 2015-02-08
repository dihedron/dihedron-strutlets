/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.upload;

import static org.dihedron.strutlets.Constants.KILOBYTE;
import static org.dihedron.strutlets.Constants.MEGABYTE;

import java.io.File;

/**
 * A class containing all the information needed to configure the file upload
 * handling.
 * 
 * @author Andrea Funto'
 */
public class FileUploadConfiguration {

	/**
	 * The default uploaded files repository if neither the server not the user 
	 * via a configuration parameter (see {@link Parameter#UPLOADED_FILES_DIRECTORY} 
	 * provided one.
	 */
	public static final String DEFAULT_UPLOAD_DIRECTORY = "/tmp/${portlet}-${server}-${port}";
	
	/**
	 * The default maximum size for an individual uploaded file, if not provided
	 * via configuration parameter {@link Parameter#UPLOADED_FILES_MAX_FILE_SIZE}.
	 */
	public static final long DEFAULT_MAX_UPLOADABLE_FILE_SIZE_SINGLE = 20 * MEGABYTE;
	
	/**
	 * The default maximum total size for a batch of uploaded files (in a single 
	 * request), if not provided by the user via configuration parameter 
	 * {@link Parameter#UPLOADED_FILES_MAX_SIZE_TOTAL}.
	 */
	public static final long DEFAULT_MAX_UPLOADABLE_FILE_SIZE_TOTAL = 100 * MEGABYTE;
	
	/**
	 * The default size for small files; all files below this size will be kept 
	 * in memory; all larger files will be written out to disk. This value will
	 * be used unless provided via {@link Parameter#UPLOADED_SMALL_FILE_SIZE_THRESHOLD}.
	 */
	public static final int DEFAULT_SMALL_FILE_SIZE_THRESHOLD = 10 * KILOBYTE;
	
	/**
	 * The directory into which uploaded files exceeding the {@link inMemoryThreshold}. 
	 * will be stored.
	 */
	private File repository;
	
	/**
	 * The maximum size for individual uploaded files.
	 */
	private long maxUploadableFileSize;
	
	/**
	 * The maximum size for the whole request.
	 */
	private long maxUploadableTotalSize;
	
	/**
	 * The size above discriminating files that will be held in memory from those
	 * that are written out to disk.
	 */
	private int inMemorySizeThreshold;

	/**
	 * Returns the File object representing the temporary uploaded files repository.
	 *
	 * @return 
	 *   the File object representing the temporary uploaded files repository.
	 */
	public File getRepository() {
		return repository;
	}

	/**
	 * Sets the File object representing the temporary uploaded files repository
	 *
	 * @param repository 
	 *   the new value for the File object representing the temporary uploaded 
	 *   files repository.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public FileUploadConfiguration setRepository(File repository) {
		this.repository = repository;
		return this;
	}

	/**
	 * Returns the value of the maximum uploadable file size, for each individual
	 * uploaded file.
	 *
	 * @return 
	 *   the value of the maximum uploadable file size.
	 */
	public long getMaxUploadableFileSize() {
		return maxUploadableFileSize;
	}
	
	/**
	 * Sets the value of the maximum uploadable file size.
	 *
	 * @param maxUploadableFileSize 
	 *   the new value for the maximum uploadable file size.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public FileUploadConfiguration setMaxUploadFileSize(long maxUploadableFileSize) {
		this.maxUploadableFileSize = maxUploadableFileSize;
		return this;
	}

	/**
	 * Returns the value of the maximum uploadable size, as the sum of the sizes
	 * of all file uploaded with a single request.
	 *
	 * @return 
	 *   the value of the maximum total uploadable file size.
	 */
	public long getMaxUploadableTotalSize() {
		return maxUploadableTotalSize;
	}
	
	/**
	 * Sets the value of the maximum uploadable size with a single request.
	 *
	 * @param maxUploadableTotalSize 
	 *   the new value for the maximum uploadable size with a single request.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public FileUploadConfiguration setMaxUploadTotalSize(long maxUploadableTotalSize) {
		this.maxUploadableTotalSize = maxUploadableTotalSize;
		return this;
	}

	/**
	 * Returns the value of the maximum file size that will be kept in memory;
	 * file larger than this size will be written out to disk.
	 *
	 * @return 
	 *   the maximum size of files that will be kept in memory.
	 */
	public int getInMemorySizeThreshold() {
		return inMemorySizeThreshold;
	}

	/**
	 * Sets the value of the maximum size for files that will be kept in memory;
	 * files larger than this size will be written out to disk as temporary files
	 * inside the configured repository directory.
	 *
	 * @param inMemorySizeThreshold 
	 *   the new value for the maximum size of uploaded files for these to be
	 *   kept in memory.
	 * @return
	 *   the object itself, for method chaining. 
	 */
	public FileUploadConfiguration setInMemorySizeThreshold(int inMemorySizeThreshold) {
		this.inMemorySizeThreshold = inMemorySizeThreshold;
		return this;
	}
}

