package webGUI.grid;

import java.util.*;

/**
 * Encapsulates multiple Clusters into a single object.
 */
public class Grid {

    /**
     * Default constructor
     */
    public Grid() {
    }

    /**
     * 
     */
    private MapLayer grid;


    /**
     * Set the Clusters contained in this Grid.
     * @param clusters
     */
    public void setClusters(Set<Cluster> clusters) {
        // TODO implement here
    }

    /**
     * Get all Clusters contained in this Grid.
     * @return all Clusters contained in this Grid.
     */
    public Set<Cluster> getClusters() {
        // TODO implement here
        return null;
    }

    /**
     * Select Clusters contained in this Grid by using their specific ClusterIDs.
     * @param ids 
     * @return selected Clusters contained in this Grid identified by their specific ClusterIDs.
     */
    public Set<Cluster> selectClusters(Set<ClusterID> ids) {
        // TODO implement here
        return null;
    }

}