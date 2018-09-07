package server.transfer;

import server.transfer.data.ObservationData;

/**
 * This {@link Enum} defines possible destinations to send {@link ObservationData} objects to.
 */
public enum Destination {
	/**
	 * Graphite - See {@code https://graphiteapp.org/}
	 */
	GRAPHITE
}
