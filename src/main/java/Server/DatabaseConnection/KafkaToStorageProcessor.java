package DatabaseConnection;

import java.util.*;

/**
 * This class converts KafkaStream records to data that can be inserted into the StorageSolution.
 */
public class KafkaToStorageProcessor {

    /**
     * Default constructor
     */
    public KafkaToStorageProcessor() {
    }



    /**
     * Subscribes to the given KafkaStream and converts the data to the appropriate format for the StorageSolution.
     * If a stream is already subscribed to, unsubscribes from the old stream and subscribes to the new one.
     * @param stream The KStream to subscribe to.
     */
    public void subscribe(KStream stream) {
        // TODO implement here
    }

}