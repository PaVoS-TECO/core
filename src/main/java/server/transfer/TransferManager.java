package server.transfer;

import java.util.ArrayList;
import java.util.List;

import server.transfer.connector.Connector;
import server.transfer.connector.GraphiteConnector;
import server.transfer.sender.GraphiteSender;

/**
 * The Control-Unit in charge of creating and destroying KafkaToGraphiteConsumer
 *  as well as passing on the users request.
 */
public class TransferManager {
	
	private Connector connector;
	
    /**
     * Default constructor
     */
    public TransferManager() {
    }

    /**
     * Starts data-transfer
     * @param topics Kafka-Topics that should be subscribed
     * @param graphTopic The Graphite / Grafana topic name, where all data will be sent to
     * @param dest The destination the data should be sent to
     * @return 
     */
    public boolean startDataTransfer(List<String> topics, Destination dest) {
    	if (connector != null) stopDataTransfer();
        if (dest.equals(Destination.GRAPHITE)) return transferToGraphite(topics);
        return false;
    }
    
    /**
     * Starts data-transfer
     * @param topic Kafka-Topic that should be subscribed
     * @param graphTopic The Graphite / Grafana topic name, where all data will be sent to
     * @param dest The destination the data should be sent to
     * @return 
     */
    public boolean startDataTransfer(String topic, Destination dest) {
    	List<String> topics = new ArrayList<>();
    	topics.add(topic);
    	return startDataTransfer(topics, dest);
    }
    
    /**
     * Stops the data-transfer.
     */
    public void stopDataTransfer() {
    	if (connector == null) return;
    	connector.stop();
    }

	private boolean transferToGraphite(List<String> topics) {
    	connector = new GraphiteConnector(topics);
    	return connector.run(new GraphiteSender());
    }
    
}