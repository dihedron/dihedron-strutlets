/**
 * Copyright (c) 2013, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Crypto library ("Crypto").
 *
 * Crypto is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Crypto is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Crypto. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.strutlets.runtimes;

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
 * A class representing the JBossInitialiser runtime environment.
 * 
 * @author Andrea Funto'
 */
public class JBossInitialiser extends RuntimeInitialiser {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(JBossInitialiser.class);

	/**
	 * Performs JBossInitialiser-specific initialisation tasks, such as registering a URL 
	 * type for classpath-related resources, needed by Strutlets to be able to
	 * scan the classpath for packages and for resources contained therein. 
	 * 
	 * @see org.dihedron.strutlets.runtimes.RuntimeInitialiser#initialise()
	 */
	public void initialise() {
		logger.debug("initialising JBossInitialiser runtime environment...");
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
					return file.exists() && file.canRead() ? file.isDirectory() ? new SystemDir(file) : new ZipDir(
							new JarFile(file)) : null;
				} catch (IOException e) {
					logger.error("I/O exception caught", e);
				}
				return null;
			}
		});
	}
}
