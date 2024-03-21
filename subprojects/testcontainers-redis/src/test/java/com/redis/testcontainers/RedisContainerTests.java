package com.redis.testcontainers;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

@Testcontainers
class RedisContainerTests {

	@SuppressWarnings("resource")
	@Test
	void emitsKeyspaceNotifications() throws InterruptedException {
		try (RedisContainer redis = new RedisContainer(
				RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG))
				.withKeyspaceNotifications()) {
			redis.start();
			RedisClient client = RedisClient.create(redis.getRedisURI());
			List<String> messages = new ArrayList<>();
			try (StatefulRedisConnection<String, String> connection = client.connect();
					StatefulRedisPubSubConnection<String, String> pubSubConnection = client.connectPubSub()) {
				pubSubConnection.addListener(new PubSubListener(messages));
				String channel = "__keyspace@0__:*";
				pubSubConnection.sync().psubscribe(channel);
				connection.sync().set("key1", "value");
				connection.sync().set("key2", "value");
				Awaitility.await().until(() -> messages.size() == 2);
			} finally {
				client.shutdown();
				client.getResources().shutdown();
			}
			Assertions.assertEquals(2, messages.size());
		}
	}

	private static class PubSubListener extends RedisPubSubAdapter<String, String> {

		private final List<String> messages;

		PubSubListener(List<String> messages) {
			this.messages = messages;
		}

		@Override
		public void message(String channel, String message) {
			messages.add(message);
		}

		@Override
		public void message(String pattern, String channel, String message) {
			messages.add(message);
		}
	}

}
