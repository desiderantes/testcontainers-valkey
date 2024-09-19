package io.valkey.testcontainers;

import io.jackey.Jedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class ValkeyExampleTest {

    @Container
    private static ValkeyContainer container = new ValkeyContainer(
            ValkeyContainer.DEFAULT_IMAGE_NAME.withTag(ValkeyContainer.DEFAULT_TAG));

    @Test
    void testSomethingUsingJackey() {
        // Retrieve the Valkey URI from the container
        String valkeyURI = container.getValkeyURI();

        try (Jedis client = new Jedis(valkeyURI)) {
            Assertions.assertEquals("PONG", client.ping());
        }
    }

}
