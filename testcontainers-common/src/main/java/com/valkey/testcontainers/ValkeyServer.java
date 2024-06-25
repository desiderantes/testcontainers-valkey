package com.valkey.testcontainers;

public interface ValkeyServer {

	/**
	 * Returns URI of Valkey server
	 *
	 * @return Valkey URI.
	 */
	default String getValkeyURI() {
		return "redis://" + getValkeyHost() + ":" + getValkeyPort();
	}

	String getValkeyHost();

	int getValkeyPort();

	/**
	 * 
	 * @return true if this is a Valkey Cluster
	 */
	boolean isCluster();

}
