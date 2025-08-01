package io.valkey.testcontainers;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Container;

@TestInstance(Lifecycle.PER_CLASS)
class MemcachedContainerTests extends AbstractMemcachedTestBase {

	@Container
	private static final MemcachedContainer server = new MemcachedContainer(
			MemcachedContainer.DEFAULT_IMAGE_NAME.withTag(MemcachedContainer.DEFAULT_TAG));

	@Override
	protected MemcachedContainer getMemcachedServer() {
		return server;
	}

}