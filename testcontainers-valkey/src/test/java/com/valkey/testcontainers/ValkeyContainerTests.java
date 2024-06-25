package com.valkey.testcontainers;

import io.jackey.Jedis;
import io.jackey.JedisPubSub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Testcontainers
class ValkeyContainerTests {

	private static final Logger log = LoggerFactory.getLogger(ValkeyContainerTests.class);

	@Test
	@Disabled
	void emitsKeyspaceNotifications() {
		try (ValkeyContainer valkey = new ValkeyContainer(
				ValkeyContainer.DEFAULT_IMAGE_NAME.withTag(ValkeyContainer.DEFAULT_TAG))
				.withKeyspaceNotifications()) {
			valkey.start();

			ArrayList<String> messages = new ArrayList<>();
			try (Jedis subclient = new Jedis(valkey.getValkeyURI()); Jedis client = new Jedis(valkey.getValkeyURI())) {
				log.atInfo().log("Subscribing to keyspace notifications");
				var listener = new PubSubListener(messages);
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(() -> {
					try {
                        subclient.psubscribe(listener, "__keyspace@*__:*");
                    } catch (Exception e) {
                        log.atError().setCause(e).log("Error subscribing to keyspace notifications");
                    }
                });
				client.set("key1", "value");
				client.set("key2", "value");
				listener.punsubscribe();
				executor.shutdown();
				log.atInfo().addKeyValue("messages", messages.size()).log("Waiting for messages");
				Awaitility.await().atMost(Duration.ofSeconds(20)).pollInSameThread().until(() -> messages.size() == 2);
			}
			Assertions.assertEquals(2, messages.size());
		}
	}

	private static class PubSubListener extends JedisPubSub {

		private final ArrayList<String> messages;

		PubSubListener(ArrayList<String> messages) {
			this.messages = messages;
		}

		@Override
		public void onMessage(String channel, String message) {
			messages.add(message);
		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {
			messages.add(message);
		}
	}

}
