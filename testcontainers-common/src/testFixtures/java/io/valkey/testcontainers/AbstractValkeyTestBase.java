package io.valkey.testcontainers;


import io.valkey.*;
import io.valkey.search.SearchResult;
import io.valkey.search.schemafields.TagField;
import io.valkey.search.schemafields.TextField;
import io.valkey.timeseries.TSCreateParams;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.lifecycle.Startable;

import java.util.HashMap;
import java.util.Map;

@TestInstance(Lifecycle.PER_CLASS)
@SuppressWarnings("unchecked")
public abstract class AbstractValkeyTestBase {

	private ValkeyServer valkey;
	private UnifiedJedis client;

	protected abstract ValkeyServer getValkeyServer();

	@BeforeAll
	public void setup() {
		valkey = getValkeyServer();
		if (valkey instanceof Startable startable) {
			startable.start();
		}
		client = client(valkey);
	}

	private UnifiedJedis client(ValkeyServer redis) {
		if (redis.isCluster()) {
			return new JedisCluster(new HostAndPort(redis.getValkeyHost(), redis.getValkeyPort()));
		}
		return new JedisPooled(redis.getValkeyURI());
	}

	@AfterAll
	public void teardown() {
		if (client != null) {
			client.close();
		}
		if (valkey instanceof Startable startable) {
			startable.stop();
		}
	}

	@BeforeEach
	void flushall() {
		client.flushAll();
	}

	@Test
	void ping() {
		Assertions.assertEquals("PONG", client.ping());
	}

	@Test
	void search() {
		client.ftCreate("test", TextField.of("name"), TagField.of("id"));
		int count = 10;
		for (int index = 0; index < count; index++) {
			client.hset("hash:" + index,
					Map.of(
							"name", "name " + index,
							"id", String.valueOf(index + 1)
					)
			);
		}
		SearchResult results = client.ftSearch("test", "*");
		Assertions.assertEquals(count, results.getTotalResults());
	}

	@Test
	void timeseries() {
		// TimeSeries tests
		client.tsCreate("temperature:3:11", TSCreateParams.createParams()
				.retention(6000)
				.labels(Map.of("sensor_id", "2", "area_id", "32"))
		);
		// TS.ADD temperature:3:11 1548149181 30
		Long add1 = client.tsAdd("temperature:3:11",  1548149181L, 30);
		Assertions.assertEquals(1548149181L, add1);
	}

	@Test
	void writeHash() {
		// Write test
		Map<String, String> map = new HashMap<>();
		map.put("field1", "value1");
		String key = "testhash";
		client.hset(key, map);
		Assertions.assertEquals(map, client.hgetAll(key));
	}

}
