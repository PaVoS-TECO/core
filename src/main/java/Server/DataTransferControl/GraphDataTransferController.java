package DataTransferControl;

import java.util.*;

/**
 * The Control-Unit in charge of creating and destroying KafkaToGraphiteConsumer as well as passing on the users request.
 */
public class GraphDataTransferController {

    /**
     * Default constructor
     */
    public GraphDataTransferController() {
    }


    /**
     * Starts data-transfer
     * @param topics Kafka-Topics that should be subscribed
     * @param UIDS The unique identifiers, that tell us which data should be transfered. Everything else will be ignored.
     */
    public void startDataTransfer(Collection<String> topics, Collection<String> UIDS) {
        // TODO implement here
    }

    /**
     * Stoppt den Datentransfer.
     * @param UIDS The unique identifiers, that tell us which data should no longer be transfered. Everything else will be ignored.
     */
    public void stopDataTransfer(Collection<String> UIDS) {
        // TODO implement here
    }

}