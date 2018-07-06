package server.databaseConnection;

import java.util.*;

/**
 * A facade to simplify access to a StorageSolution, such as a database. Through the methods, data can be inserted into the StorageSolution and certain information about its content requested.
 */
public class Facade {

    /**
     * Default constructor
     */
    public Facade() {
    }





    /**
     * Subscribes to the given KafkaStream, which contains ZoomLevel-specific data and initiates processing of its records.
     * @param stream The stream to subscribe to.
     */
    public void subscribeToZoomLevelStream(KStream stream) {
        // TODO implement here
    }

    /**
     * Fetches all sensors from the given cluster that observe the given ObservedProperty and returns an array of sensors.
     * @param type The ObservationType of the requested sensors.
     * @param id The ID of the cluster.
     * @return An array of sensors.
     */
    public Set<Sensor> getSensors(ObservationType type, ClusterID id) {
        // TODO implement here
        return null;
    }

    /**
     * Returns an appropriate grid of clusters in the requested grid section for the specified ZoomLevel and time. The (first) two values of the ClusterID array define the grid section from which to get the data.
     * @param clusters An array of ClusterIDs from which the first two entries are taken to compute the section of the Grid to get the data from.
     * @param zoom The ZoomLevel from which to get the data.
     * @param time The point in time.
     * @return A grid with the computed data.
     */
    public Grid getGrid(ClusterID[] clusters, ZoomLevel zoom, Time time) {
        // TODO implement here
        return null;
    }

}