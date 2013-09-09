package org.dihedron.strutlets.containers.web.jbossas7x;

import org.dihedron.strutlets.containers.Container;
import org.dihedron.strutlets.containers.ContainerPlugin;
import org.dihedron.strutlets.containers.ContainerProbe;

public class JBossAS7xPlugin implements ContainerPlugin {

	@Override
	public Container makeContainer() {
		return new JBossAS7x();
	}

	@Override
	public ContainerProbe makeContainerProbe() {
		return new JBossAS7xProbe();
	}
}
