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
     */
    public abstract void send(ConsumerRecords<String, ObservationData> records);

}