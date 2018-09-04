package server.database;

import java.util.Set;

import server.transfer.config.util.EnvironmentUtil;
import server.transfer.data.ObservationData;
import server.transfer.data.ObservationDataDeserializer;

/**
 * A facade to simplify access to a StorageSolution, such as a database. Through the methods, data can be inserted into the StorageSolution and certain information about its content requested.
 */
public class Facade {

	private static volatile ObservationDataToStorageProcessor storageProcessor;
	private static volatile Facade instance;
	
    /**
     * Default constructor
     */
    private Facade() {
    	String host = EnvironmentUtil.getEnvironmentVariable("PAVOS_MEMCACHED_LOCATION", "pavos.oliver.pw");
    	int port;
    	try {
    		port = Integer.parseInt(EnvironmentUtil.getEnvironmentVariable("PAVOS_MEMCACHED_PORT", "11211"));
    	} catch (NumberFormatException e) {
    		port = 11211;
    	}
    	storageProcessor = new ObservationDataToStorageProcessor(host, port);
    }
    
    /**
     * Add an ObservationData object to the storage solution.
     * @param observationData The ObservationData object.
     */
    public void addObservationData(ObservationData observationData) {
    	storageProcessor.add(observationData);
    }
    
    /**
     * Add a byte array (which represents a serialized ObservationData object)
     * to the storage solution.
     * @param observationData The serialized ObservationData object.
     */
    public void addObservationData(byte[] observationData) {
    	ObservationDataDeserializer deserializer = new ObservationDataDeserializer();
    	ObservationData obsDataObject = deserializer.deserialize(null, observationData);
    	deserializer.close();
    	if (obsDataObject != null) {
    		addObservationData(obsDataObject);
    	}
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
    public String getObservationData(String clusterID, String timestamp, String observedProperty) {
    	return storageProcessor.get(clusterID, timestamp, observedProperty);
    }
    
    /**
	 * Add a memcached server to the server cluster.
	 * @param address The address of the server
	 * @param port The port for memcached
	 */
    public void addMemcachedServer(String address, int port) {
    	storageProcessor.addServer(address, port);
    }
    
    /**
     * Get a HashSet containing all observed properties in a grid with ID {@code gridID}.
     * @param gridID The gridID from which to get the observed properties
     * @return A HashSet containing the observed properties
     */
    public Set<String> getObservedProperties(String gridID) {
    	return storageProcessor.getObservedProperties(gridID);
    }
    
    public synchronized static Facade getInstance() {
    	if (storageProcessor == null || !storageProcessor.isConnected()) {
    		instance = new Facade();
    	}
    	return instance;
    }

}