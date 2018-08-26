package server.database;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import server.transfer.data.ObservationData;

/**
 * This class provides methods to add or get ObservationData objects to or from the storage solution.
 */
public class ObservationDataToStorageProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private MemcachedClient cli;

    /**
     * Default constructor
     */
    public ObservationDataToStorageProcessor(String host) {
        try {
            cli = new XMemcachedClient(host, 11211);
        } catch (IOException e) {
            logger.error("Could not connect to memcached client!", e);
        }
    }

    /**
     * Saves the ObservationData object into the database for 24 hours.
     * @param observationData
     */
	public void add(ObservationData observationData) {
		// A singular pipe character is used as delimiter, so it is an illegal character for clusterID.
		if (observationData.clusterID.contains("|")) {
			logger.warn("ClusterID of ObservationData object contains illegal character '|': "
					+ observationData.clusterID);
			return;
		}
		
		Long counter = null;
		
		try {
			counter = cli.get(observationData.clusterID);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.warn("Could not get counter to clusterID " + observationData.clusterID, e);
			return;
		}
		
		if (counter == null) {
			// first entry of key, set counter
			counter = 0L;
		} else {
			// increment counter
			counter += 1;
		}
		
		String dataKey = String.join("|", observationData.clusterID, String.valueOf(counter));
		
		// data expiration time of one day in seconds, can safely cast
		int dataExp = (int) TimeUnit.SECONDS.convert(1, TimeUnit.DAYS);
		
		// counter expiration time of two days in seconds, can safely cast
		int counterExp = (int) TimeUnit.SECONDS.convert(2, TimeUnit.DAYS);
		
		try {
			// set observationData entry
			cli.set(dataKey, dataExp, observationData);
			// set observationData counter entry
			cli.set(observationData.clusterID, counterExp, counter);
		} catch (TimeoutException e) {
			logger.warn("Timeout when saving ObservationData to memcached!", e);
		} catch (InterruptedException | MemcachedException e) {
			logger.warn("Memcached error: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Converts a String timestamp of the format {@code YYYY-MM-DDTHH:MM:SSZ} into a LocalDateTime object.
	 * This enables comparison of timestamps.
	 * @param timestamp The timestamp to convert
	 * @return A LocalDateTime object representing the time in the timestamp
	 */
	private LocalDateTime getTime(String timestamp) {
		// Remove Z
		String obsTime = timestamp;
		if (obsTime != null && obsTime.length() >= 1 && obsTime.charAt(obsTime.length() - 1) == 'Z') {
			obsTime = obsTime.substring(0, obsTime.length() - 1);
		} else {
			logger.warn("Given timestamp is invalid: " + timestamp);
			return null;
		}
		
		// Parse time to LocalDateTime format and check validity
		LocalDateTime time = null;
		try {
			time = LocalDateTime.parse(obsTime);
		} catch (DateTimeParseException e) {
			logger.warn("Could not parse given time " + timestamp);
			return null;
		}
		
		return time;
	}

	/**
     * Get the value of an observedProperty from a clusterID at or before the given timestamp.
     * The returned value is guaranteed to come from an observation in the given cluster at or before
     * the given timestamp (i.e. no values from the future).
     * @param clusterID The cluster from which to get the value
     * @param timestamp The time to check
     * @param observedProperty The observedProperty needed
     * @return The value to the observedProperty key. Returns {@code null} in any of the following cases:<br>
     * - There is no entry for the cluster<br>
     * - There is no entry for the cluster before or at the given timestamp<br>
     * - There is no {@code observedProperty} key in the observations Map<br>
     * - The value to the {@code observedProperty} key is literally {@code null}<br>
     * - Any of the parameters is badly formatted (see logs)
     */
	public String get(String clusterID, String timestamp, String observedProperty) {
		// get counter for clusterID
		Long counter = null;
		
		try {
			counter = cli.get(clusterID);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.warn("Could not get counter to clusterID " + clusterID, e);
			return null;
		}
		
		// no entry exists, return null
		if (counter == null) {
			return null;
		}
		
		LocalDateTime givenTime = getTime(timestamp);
		// given timestamp is invalid
		if (givenTime == null) {
			return null;
		}
		
		ObservationData od = null;
		
		try {
			for (long i = counter; i >= 0; i--) {
				String dataKey = String.join("|", clusterID, String.valueOf(i));
				od = cli.get(dataKey);
				// entry expired, return null
				if (od == null) {
					return null;
				}
				LocalDateTime time = getTime(od.observationDate);
				// some error occurred when getting time
				if (time == null) {
					return null;
				}
				// check if returned time is less or equal to given time
				// in other words, check if this observation has occurred before or at the given time
				if (time.compareTo(givenTime) <= 0) {
					// correct ObservationData was found
					break;
				}
			}
		} catch (TimeoutException e) {
			logger.warn("Timeout when saving ObservationData to memcached!", e);
			return null;
		} catch (InterruptedException | MemcachedException e) {
			logger.warn("Memcached error: " + e.getMessage(), e);
			return null;
		} catch (ClassCastException e) {
			logger.warn("Memcached returned an item that cannot be parsed as an ObservationData object", e);
			return null;
		}
		
		// get value to given observedProperty
		Map<String, String> obs = od.observations;
		if (obs.containsKey(observedProperty)) {
			return obs.get(observedProperty);
		} else {
			return null;
		}
	}

	/**
	 * Add a memcached server to the server cluster.
	 * @param address The address of the server
	 * @param port The port for memcached
	 */
	public void addServer(String address, int port) {
		try {
			cli.addServer(address, port);
		} catch (IOException e) {
			logger.warn("Could not add Memcached server", e);
		}
	}

}