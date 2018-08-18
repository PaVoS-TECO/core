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
     */
    public void startDataTransfer(List<String> topics, String graphTopic, Destination dest) {
    	if (connector != null) stopDataTransfer();
        if (dest.equals(Destination.GRAPHITE)) startGraphiteTransfer(topics, graphTopic);
    }
    
    /**
     * Starts data-transfer
     * @param topic Kafka-Topic that should be subscribed
     * @param graphTopic The Graphite / Grafana topic name, where all data will be sent to
     * @param dest The destination the data should be sent to
     */
    public void startDataTransfer(String topic, String graphTopic, Destination dest) {
    	List<String> topics = new ArrayList<>();
    	topics.add(topic);
    	startDataTransfer(topics, graphTopic, dest);
    }
    
    /**
     * Stops the data-transfer.
     */
    public void stopDataTransfer() {
    	if (connector == null) return;
    	connector.stop();
    }

	private void startGraphiteTransfer(List<String> topics, String graphTopic) {
    	connector = new GraphiteConnector(topics, graphTopic);
    	connector.run(new GraphiteSender());
    }
    
}