package server.transfer.connector;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import server.core.properties.KafkaPropertiesFileManager;
import server.transfer.config.GraphiteConfig;
import server.transfer.data.ObservationData;
import server.transfer.sender.GraphiteSender;
import server.transfer.sender.Sender;

/**
 * Connects Kafka to Graphite.
 * Consumes data from Kafka and sends it to Graphite.
 */
public class GraphiteConnector extends Connector {

    /**
     * Default constructor
     * @param topics The topics that the consumer should subscribe to
     * @param graphTopic The Graphite / Grafana topic name, where all data will be sent to
     * @param sender Sends the data to a specified component, normally a {@link GraphiteSender}
     */
	public GraphiteConnector(List<String> topics) {
    	this.topics = topics;
    }

    /**
     * Starts the process of consumption and prepares the {@link Sender}, then starts to send data to Graphite.
     */
    public boolean run(Sender sender) {
    	this.sender = sender;
    	
    	Properties consumerProperties = KafkaPropertiesFileManager.getInstance().getGraphiteConnectorProperties();
        consumer = new KafkaConsumer<String, ObservationData>(consumerProperties);
        consumer.subscribe(topics);

        if (GraphiteConfig.getStartFromBeginning()) {
            consumer.poll(100);
            consumer.seekToBeginning(Collections.<TopicPartition>emptyList());
        }

        try {
			ConsumerRecords<String, ObservationData> records = consumer.poll(10000);

			if (!records.isEmpty()) {
				return sender.send(records);
			}
        } catch (WakeupException ex) {
            logger.info("Connector has received instruction to wake up");
        } finally {
            logger.info("Connector closing...");
            consumer.close();
            shutdownLatch.countDown();
            logger.info("Connector has closed successfully");
        }
        return false;
    }

    /**
     * Stops the process of consumption and sending.
     */
    public void stop() {
    	
    	this.sender.close();
    	logger.info("Waking up consumer...");
        consumer.wakeup();

        try {
            logger.info("Waiting for consumer to shutdown...");
            shutdownLatch.await();
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
            logger.error("Exception thrown waiting for shutdown", e);
        }
    }

}