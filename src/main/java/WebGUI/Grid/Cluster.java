package Grid;

import java.util.*;

/**
 * Encapsulates multiple sensors into a single object by using their specific SensorIDs and provides a graphical representation of their values average by using a Tile.
 */
public class Cluster {

    /**
     * Default constructor
     */
    public Cluster() {
    }

    /**
     * 
     */
    private ClusterID clusterId;

    /**
     * 
     */
    private Set<SensorID> sensorIds;

    /**
     * 
     */
    private ObservedProperty observedProperty;



    /**
     * Get the ClusterID of this Cluster.
     * @return the ClusterID of this Cluster.
     */
    public ClusterID getClusterId() {
        // TODO implement here
        return null;
    }

    /**
     * Set the SensorIDs of the sensors contained in this cluster.
     * @param sensorIds
     */
    public void setSensorIds(Set<SensorID> sensorIds) {
        // TODO implement here
    }

    /**
     * Get all  SensorIDs of the sensors contained in this cluster.
     * @return all SensorIDs of the sensors contained in this cluster.
     */
    public Set<SensorID> getSensorIds() {
        // TODO implement here
        return null;
    }

    /**
     * Set the ObservedProperty of this Cluster.
     * @param observedProperty
     */
    public void setObservedProperty(ObservedProperty observedProperty) {
        // TODO implement here
    }

    /**
     * Get the ObservedProperty of this Cluster.
     * @return the ObservedProperty of this Cluster.
     */
    public ObservedProperty getObservedProperty() {
        // TODO implement here
        return null;
    }

    /**
     * Get the Tile of this Cluster.
     * @return the Tile of this Cluster.
     */
    public Tile getTile() {
        // TODO implement here
        return null;
    }

}