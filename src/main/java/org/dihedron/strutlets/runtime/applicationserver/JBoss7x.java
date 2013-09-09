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
package org.dihedron.strutlets.runtime.applicationserver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

import org.jboss.vfs.VirtualFile;
import org.reflections.ReflectionsException;
import org.reflections.vfs.SystemDir;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing the JBoss 7.x runtime environment.
 * 
 * @author Andrea Funto'
 */
public abstract class JBoss7x implements ApplicationServer {
	
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(JBoss7x.class);

	/**
	 * Returns whether the actual application server the portlet container is running on 
	 * is JBoss 7.x, by trying to detect the existence of some classes.
	 */
	@Override
	public boolean isAppropriate() {
		try {
			Class.forName("org.reflections.vfs.Vfs");
			logger.info("runtime environment is JBoss 7.x+");
			return true;		
		} catch (ClassNotFoundException e) {
			logger.info("runtime environment is not JBoss 7.x+");
		}
		return false;
	}
	
	
	/**
	 * Performs JBoss 7.x-specific initialisation tasks, such as registering a URL 
	 * type for classpath-related resources, needed by Strutlets to be able to
	 * scan the classpath for packages and for resources contained therein. 
	 * 
	 * @see org.dihedron.strutlets.runtime.applicationserver.ApplicationServer#initialise()
	 */
	@Override
	public boolean initialise() {
		logger.info("initialising JBoss 7.x runtime environment...");
		Vfs.addDefaultURLTypes(new Vfs.UrlType() {
			
			public boolean matches(URL url) {
				return url.getProtocol().equals("vfs");
			}

			public Vfs.Dir createDir(URL url) {
				VirtualFile content;
				try {
					content = (VirtualFile) url.openConnection().getContent();
				} catch (IOException e) {
					logger.error("could not open url connection as VirtualFile [{}]", url);
					throw new ReflectionsException("could not open url connection as VirtualFile [" + url + "]", e);
				}

				Vfs.Dir dir = null;
				try {
					dir = createDir(new File(content.getPhysicalFile().getParentFile(), content.getName()));
				} catch (IOException e) { /* continue */
				}
				if (dir == null) {
					try {
						dir = createDir(content.getPhysicalFile());
					} catch (IOException e) { /* continue */
					}
				}
				return dir;
			}

			Vfs.Dir createDir(File file) {
				try {
					return file.exists() && file.canRead() ? file.isDirectory() ? new SystemDir(file) : new ZipDir(new JarFile(file)) : null;
				} catch (IOException e) {
					logger.error("I/O exception caught", e);
				}
				return null;
			}
		});
		return true;
	}

	/**
	 * No actual JBoss 7.x specific cleanup tasks.
	 */
	@Override
	public void cleanup() {
	}
}
