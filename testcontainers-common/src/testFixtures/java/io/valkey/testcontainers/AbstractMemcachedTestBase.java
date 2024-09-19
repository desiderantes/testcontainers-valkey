package io.valkey.testcontainers;

import net.spy.memcached.MemcachedClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.lifecycle.Startable;

import java.io.IOException;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractMemcachedTestBase {

	private MemcachedServer server;
	private MemcachedClient client;

	protected abstract MemcachedServer getMemcachedServer();

	@BeforeAll
	public void setup() throws IOException {
		server = getMemcachedServer();
		if (server instanceof Startable startable) {
			startable.start();
		}
		client = new MemcachedClient(server.getMemcachedAddresses());
	}

	@AfterAll
	public void teardown() {
		if (client != null) {
			client.shutdown();
		}
		if (server instanceof Startable startable) {
			startable.stop();
		}
	}

	@BeforeEach
	void flushall() {
		client.flush();
	}

	@Test
	void testSet() {
		String key = "testkey";
		String value = "value";
		client.set(key, 30, value);
		Assertions.assertEquals(value, client.get(key));
	}

}
