package DatabaseConnection;

import java.util.*;

/**
 * This class maintains the list of sensors saved in the StorageSolution.
 */
public class SensorMaintainer extends Maintainer {

    /**
     * Default constructor
     */
    public SensorMaintainer() {
    }

    /**
     * This method checks if the sensors registered to the given cluster are up to date. A sensor is up to date if data has been received from it in the last 24 hours. If this requirement is not met, the sensor is deleted from the database.
     * @param cluster The cluster to check.
     */
    public void checkSensorsOfCluster(ClusterID cluster) {
        // TODO implement here
    }

}