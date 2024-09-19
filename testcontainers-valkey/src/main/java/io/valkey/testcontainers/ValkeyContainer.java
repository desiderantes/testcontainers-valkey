package io.valkey.testcontainers;

import org.testcontainers.utility.DockerImageName;

public class ValkeyContainer extends AbstractValkeyContainer<ValkeyContainer> {

	public static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("docker.io/valkey/valkey");

	public static final String DEFAULT_TAG = "latest";

	public ValkeyContainer(String dockerImageName) {
		super(dockerImageName);
	}

	public ValkeyContainer(final DockerImageName dockerImageName) {
		super(dockerImageName);
	}

	public ValkeyContainer() {
		super(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
	}

}
