package io.valkey.testcontainers;

import io.jackey.Jedis;
import io.jackey.JedisPool;
import io.jackey.JedisPubSub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.ArrayList;

@Testcontainers
class ValkeyContainerTests {

	private static final Logger log = LoggerFactory.getLogger(ValkeyContainerTests.class);

	@Test
	void emitsKeyspaceNotifications() {
		try (ValkeyContainer valkey = new ValkeyContainer()
				.withKeyspaceNotifications()) {
			valkey.start();

			ArrayList<String> messages = new ArrayList<>();


			try (final JedisPool pool = new JedisPool(valkey.getValkeyURI())) {
				log.atInfo().log("Subscribing to keyspace notifications");
				var listener = new PubSubListener(messages);
				var thread = new Thread(() -> {
					try {
                        pool.getResource().psubscribe(listener, "__keyspace@*__:*");
                    } catch (Exception e) {
                        log.atError().setCause(e).log("Error subscribing to keyspace notifications");
                    }
                });
				thread.start();
				Jedis client = pool.getResource();
				client.set("key1", "value");
				client.set("key2", "value");
				listener.punsubscribe();
				thread.join(2000);
				log.atInfo().addKeyValue("messages", messages.size()).log("Waiting for messages");
				Awaitility.await().atMost(Duration.ofSeconds(2)).until(() -> messages.size() == 2);
			} catch (InterruptedException e) {
                throw new RuntimeException(e);
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
