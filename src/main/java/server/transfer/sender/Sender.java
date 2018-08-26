package server.transfer.sender;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.transfer.data.ObservationData;

/**
 * Sends data to the specified component
 */
public abstract class Sender {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
    /**
     * Sends the recorded data
     * @param records Multiple records of data from Kafka
     * @param graphTopic The Graphite / Grafana topic name, where all data will be sent to
     * @return 
     */
    public abstract boolean send(ConsumerRecords<String, ObservationData> records);
    
    /**
     * Closes the sender and the connection
     */
    public abstract void close();

}