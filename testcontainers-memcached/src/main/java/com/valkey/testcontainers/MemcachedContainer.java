package com.valkey.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class MemcachedContainer extends GenericContainer<MemcachedContainer> implements MemcachedServer {

	public static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("memcached");

	public static final String DEFAULT_TAG = "1.6-alpine";

	private static int MEMCACHED_PORT = 11211;

	public MemcachedContainer(String dockerImageName) {
		this(DockerImageName.parse(dockerImageName));
	}

	public MemcachedContainer(final DockerImageName dockerImageName) {
		super(dockerImageName);
		withExposedPorts(MEMCACHED_PORT);
	}

	@Override
	public String getMemcachedHost() {
		return getHost();
	}

	@Override
	public int getMemcachedPort() {
		return getFirstMappedPort();
	}

}