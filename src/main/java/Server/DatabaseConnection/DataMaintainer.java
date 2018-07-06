package DatabaseConnection;

import java.util.*;

/**
 * This class maintains the sensordata in the StorageSolution.
 */
public class DataMaintainer extends Maintainer {

    /**
     * Default constructor
     */
    public DataMaintainer() {
    }

    /**
     * This method takes data of a certain TimeUnit and summarizes it into the next higher TimeUnit. The summarized data is then saved back into the StorageSolution. The original data of the lower TimeUnit is then deleted from the database.
     * @param timeUnit The TimeUnit to summarize.
     */
    public void summarize(TimeUnit timeUnit) {
        // TODO implement here
    }

}