package edu.teco.pavos.database;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * This class provides methods to add or get ObservationData objects to or from the storage solution.
 */
public class ObservationDataToStorageProcessor {
	
	@Autowired
	@Qualifier("xmemcachedClient")
	private MemcachedClient memcachedClient;
	
	private static final String TEST_CONNECTION = "testConnection";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String host;
    private final int port;
    private final boolean useDummy;
    private boolean isConnected = false;

    /**
     * Default constructor
     * @param host {@link String}
     * @param port {@link Integer}
     */
    public ObservationDataToStorageProcessor(String host, int port) {
    	this.host = host;
    	this.port = port;
    	this.useDummy = false;
    	setIsConnected(connect(host, port));
    }
    
    /**
     * DUMMY memcached constructor
     * @param memcachedClient {@link MemcachedClient}
     */
    public ObservationDataToStorageProcessor(MemcachedClient memcachedClient) {
    	this.host = "dummyHost";
    	this.port = 11211;
    	this.useDummy = true;
    	this.memcachedClient = memcachedClient;
    	this.isConnected = true;
    }
    
	private boolean connect(String host, int port) {
    	if (useDummy) {
			return true;
    	} else {
			try {
				XMemcachedClientBuilder builder = new XMemcachedClientBuilder(
						String.join(":", String.valueOf(host), String.valueOf(port)));
				builder.setEnableHealSession(false);
				builder.setConnectTimeout(1000);
				builder.setOpTimeout(1000);
				memcachedClient = builder.build();

				memcachedClient.set(TEST_CONNECTION, 1000, "TEST");
				if (memcachedClient.get(TEST_CONNECTION) == null) {
					return false;
				} else {
					memcachedClient.delete(TEST_CONNECTION);
					return true;
				}
			} catch (IOException | TimeoutException | InterruptedException | MemcachedException e) {
				isConnected = false;
				logger.error("Could not connect to memcached client!", e);
				if (e.getClass().equals(InterruptedException.class))
					Thread.currentThread().interrupt();
				return false;
			}
    	}
    }
    
    private void setIsConnected(boolean status) {
    	isConnected = status;
    }
    
    /**
     * Check if this instance is connected to Memcached.
     * @return {@code true} if connected, {@code false} if not.
     */
    public boolean isConnected() {
    	return isConnected;
    }
    
    /**
     * Tries to reconnect to Memcached.
     * @return {@code true} if connection could be established, {@code false} if not.
     */
    public boolean reconnect() {
    	if (!isConnected()) {
    		setIsConnected(connect(this.host, this.port));
    	}
    	return isConnected();
    }
    
    /**
     * Shutdown this processor and disconnect from Memcached.
     */
    public void shutdown() {
    	try {
			memcachedClient.shutdown();
		} catch (IOException e) {
			logger.warn("Shutdown failed", e);
		}
    }

    /**
     * Saves the ObservationData object into the database for 24 hours.
     * @param observationData The ObservationData object
     */
	public void add(ObservationData observationData) {
		
		if (!isConnected()) {
			logger.warn("Memcached is not connected! ObservationData could not be added to database!");
			return;
		}
		
		if (observationData == null) {
			return;
		}
		
		// A singular pipe character is used as delimiter, so it is an illegal character for clusterID.
		if (observationData.getClusterID().contains("|")) {
			logger.warn("ClusterID of ObservationData object contains illegal character '|': {}", 
					observationData.getClusterID());
			return;
		}
		
		Long counter = null;
		
		try {
			counter = memcachedClient.get(observationData.getClusterID());
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			if (e.getClass().equals(InterruptedException.class)) Thread.currentThread().interrupt();
			logger.warn("Could not get counter to clusterID " + observationData.getClusterID(), e);
			return;
		}
		
		if (counter == null) {
			// first entry of key, set counter
			counter = 0L;
		} else {
			// increment counter
			counter += 1;
		}
		
		String dataKey = String.join("|", observationData.getClusterID(), String.valueOf(counter));
		
		// data expiration time of one day in seconds, can safely cast
		int dataExp = (int) TimeUnit.SECONDS.convert(1, TimeUnit.DAYS);
		
		// counter expiration time of two days in seconds, can safely cast
		int counterExp = (int) TimeUnit.SECONDS.convert(2, TimeUnit.DAYS);
		
		// get gridID from clusterID
		String gridID = observationData.getClusterID().split(":")[0];
		
		try {
			// set observationData entry
			memcachedClient.set(dataKey, dataExp, observationData);
			// set observationData counter entry
			memcachedClient.set(observationData.getClusterID(), counterExp, counter);
			// update observedProperties list
			HashSet<String> properties = memcachedClient.get(gridID);
			if (properties == null) {
				properties = new HashSet<>();
			}
			for (String property : observationData.getSingleObservations().keySet()) {
				properties.add(property);
			}
			for (String property : observationData.getVectorObservations().keySet()) {
				if (!properties.contains(property)) properties.add(property);
			}
			
			memcachedClient.set(gridID, dataExp, properties);
			logger.debug("Successfully added item with key {}", dataKey);
			String keySetSingle = String.join(", ", observationData.getSingleObservations().keySet());
			String keySetVector = String.join(",", observationData.getVectorObservations().keySet());
			String keySet = String.join(",", keySetSingle, keySetVector);
			logger.debug("\tTimestamp {} and properties {}", 
					observationData.getObservationDate(), keySet);
		} catch (TimeoutException e) {
			logger.warn("Timeout when saving ObservationData to memcached!", e);
		} catch (InterruptedException | MemcachedException e) {
			if (e.getClass().equals(InterruptedException.class)) Thread.currentThread().interrupt();
			logger.warn("Memcached error: {}", e.getMessage(), e);
		}
	}
	
	/**
	 * Converts a String timestamp of the format {@code YYYY-MM-DDTHH:MM:SSZ} into a LocalDateTime object.
	 * This enables comparison of timestamps.
	 * @param timestamp The timestamp to convert
	 * @return A LocalDateTime object representing the time in the timestamp
	 */
	private LocalDateTime getTime(String timestamp) {
		try {
			if (timestamp.matches(TimeUtil.getDateTimeRegex())) {
				return TimeUtil.getUTCDateTime(timestamp);
			}
		} catch (IllegalFieldValueException | NullPointerException e) {
			logger.warn("Could not parse given time {}. Invalid Parameters.", timestamp);
		}
		logger.warn("Could not parse given time {}. " + "The specified format does not match the internal format.",
				timestamp);
		return null;
	}

	/**
     * Get the value of an observedProperty from a clusterID at or before the given timestamp.
     * The returned value is guaranteed to come from an observation in the given cluster at or before
     * the given timestamp (i.e. no values from the future).
     * @param clusterID The cluster from which to get the value
     * @param timestamp The time to check
     * @param observedProperty The observedProperty needed
     * @return The value to the observedProperty key. Returns {@code null} in case of an error. See logs for details.
     */
	public String get(String clusterID, String timestamp, String observedProperty) {
		
		logger.debug("Entered get() with {} at {} and {}", clusterID, timestamp, observedProperty);
		long start = System.currentTimeMillis();
		
		if (!isConnected()) {
			logger.warn("Memcached is not connected! ObservationData could not be fetched from database!");
			return null;
		}
		
		if (clusterID == null || timestamp == null || observedProperty == null) {
			logger.warn("Parameters may not be null.");
			return null;
		}
		
		// get counter for clusterID
		Long counter = null;
		
		try {
			counter = memcachedClient.get(clusterID);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			if (e.getClass().equals(InterruptedException.class)) Thread.currentThread().interrupt();
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
		
		logger.debug("get() took {} and started at {}", (System.currentTimeMillis() - start), start);
		return getObservationValue(counter, clusterID, givenTime, observedProperty);
	}
	
	private String getObservationValue(
			long counter, String clusterID, LocalDateTime givenTime, Object observedProperty) {
		
		ObservationData od = null;
		
		try {
			for (long i = counter; i >= 0; i--) {
				String dataKey = String.join("|", clusterID, String.valueOf(i));
				od = memcachedClient.get(dataKey);
				// entry expired, return null
				if (od == null) {
					return null;
				}
				LocalDateTime time = getTime(od.getObservationDate());
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
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			if (e.getClass().equals(InterruptedException.class)) Thread.currentThread().interrupt();
			logger.warn("Memcached error: {}", e.getMessage(), e);
			return null;
		} catch (ClassCastException e) {
			logger.warn("Memcached returned an item that cannot be parsed as an ObservationData object", e);
			return null;
		}
		
		// get value to given observedProperty
		if (od != null) {
			String property = String.valueOf(observedProperty);
			return od.getAnonObservation(property).toString();
		}
		return null;
	}
	
	/**
	 * Add a memcached server to the server cluster.
	 * @param address The address of the server
	 * @param port The port for memcached
	 */
	public void addServer(String address, int port) {
		try {
			memcachedClient.addServer(address, port);
		} catch (IOException | IllegalArgumentException e) {
			logger.warn("Could not add Memcached server", e);
		}
	}

	/**
     * Get a HashSet containing all observed properties in a grid with ID {@code gridID}.
     * @param gridID The gridID from which to get the observed properties
     * @return A HashSet containing the observed properties
     */
	public Set<String> getObservedProperties(String gridID) {
		
		if (!isConnected()) {
			logger.warn("Memcached is not connected! ObservedProperties could not be fetched from database!");
			return new HashSet<>();
		}
		
		try {
			Set<String> set = memcachedClient.get(gridID);
			if (set != null) {
				return set;
			}
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			if (e.getClass().equals(InterruptedException.class)) Thread.currentThread().interrupt();
			logger.warn("Could not get observedProperties list", e);
		}
		return new HashSet<>();
	}

}
