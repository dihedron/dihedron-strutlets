/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.containers.web.jbossas7x;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

import org.dihedron.strutlets.containers.web.ApplicationServer;
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
public class JBossAS7x implements ApplicationServer {
	
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(JBossAS7x.class);
	
	/**
	 * Constructor has package visibility to prevent construction by anyone except 
	 * its plugin.
	 */
	JBossAS7x() {		
	}
	
	/**
	 * Returns the name of the JBossAS server.
	 */
	public String getName() {
		return "JBossAS ver. 7.x";
	}
	
	/**
	 * Performs JBoss 7.x-specific initialisation tasks, such as registering a URL 
	 * type for classpath-related resources, needed by Strutlets to be able to
	 * scan the classpath for packages and for resources contained therein. 
	 * 
	 * @see org.dihedron.strutlets.containers.web.ApplicationServer#initialise()
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
				} catch (IOException e) { 
					// continue
				}
				if (dir == null) {
					try {
						dir = createDir(content.getPhysicalFile());
					} catch (IOException e) { 
						// continue
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
