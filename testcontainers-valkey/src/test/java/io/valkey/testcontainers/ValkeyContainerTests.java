package io.valkey.testcontainers;

import io.valkey.Jedis;
import io.valkey.JedisPool;
import io.valkey.JedisPubSub;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class ValkeyContainerTests {

    @Container
    private static final ValkeyContainer valkey = new ValkeyContainer()
            .withKeyspaceNotifications();

    private static final Logger log = LoggerFactory.getLogger(ValkeyContainerTests.class);

    @Test
    void emitsKeyspaceNotifications() {
        assertTrue(valkey.isRunning());
        ArrayList<String> messages = new ArrayList<>();
        try (final JedisPool pool = new JedisPool(valkey.getValkeyURI())) {
            log.atInfo().log("Subscribing to keyspace notifications");
            var listener = new PubSubListener(messages);
            var thread = new Thread(() -> {
                try {
                    var threadJedis = pool.getResource();
                    threadJedis.psubscribe(listener, "__keyspace@*__:*");
                } catch (Exception e) {
                    log.atError().setCause(e).log("Error subscribing to keyspace notifications");
                }
            });
            thread.start();
            thread.join(5000);
            Jedis client = pool.getResource();
            client.set("key1", "value1");
            client.set("key2", "value2");
            log.atInfo().addKeyValue("messages", messages.size()).log("Waiting for messages");
            Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> messages.size() == 2);
            pool.returnResource(client);
            listener.punsubscribe();


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(2, messages.size());

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
