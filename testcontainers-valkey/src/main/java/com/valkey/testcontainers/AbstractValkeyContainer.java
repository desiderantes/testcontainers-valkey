package com.valkey.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.lang3.ClassUtils;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

abstract class AbstractValkeyContainer<C extends AbstractValkeyContainer<C>> extends GenericContainer<C>
		implements ValkeyServer {

	public static final int VALKEY_PORT = 6379;

	protected AbstractValkeyContainer(String dockerImageName) {
		this(DockerImageName.parse(dockerImageName));
	}

	protected AbstractValkeyContainer(final DockerImageName dockerImageName) {
		super(dockerImageName);
		addExposedPorts(VALKEY_PORT);
		waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1));
	}

	@SuppressWarnings("unchecked")
	public C withKeyspaceNotifications() {
		withCopyFileToContainer(MountableFile.forClasspathResource("valkey-keyspace-notifications.conf"),
				"/data/valkey.conf");
		withCommand("valkey-server", "/data/valkey.conf");
		return (C) this;
	}

	@Override
	public String toString() {
		return ClassUtils.getShortClassName(getClass());
	}

	@Override
	public String getValkeyHost() {
		return getHost();
	}

	@Override
	public int getValkeyPort() {
		return getFirstMappedPort();
	}

	@Override
	public boolean isCluster() {
		return false;
	}

}
