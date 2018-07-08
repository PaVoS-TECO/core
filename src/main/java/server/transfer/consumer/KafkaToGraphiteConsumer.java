package server.transfer.consumer;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import server.transfer.config.GraphiteConfig;
import server.transfer.config.KafkaConfig;
import server.transfer.send.Sender;
import server.transfer.serialization.KafkaObservationData;
import server.transfer.serialization.ObservationDataDeserializer;

/**
 * Receives the data from Kafka and sends it to Graphite
 */
public class KafkaToGraphiteConsumer extends Consumer {
	
	private boolean sendLoop = true;

    /**
     * Default constructor
     */
	public KafkaToGraphiteConsumer(List<String> topics, Sender sender) {
    	this.topics = topics;
    	this.sender = sender;
    }

    /**
     * Starts the process of consumation and readying the sender object
     */
    public void run() {
    	Properties consumerProperties = getConsumerProperties();
        consumer = new KafkaConsumer<String, KafkaObservationData>(consumerProperties);
        consumer.subscribe(topics);

        if (GraphiteConfig.getStartFromBeginning()) {
            consumer.poll(100);
            consumer.seekToBeginning(Collections.<TopicPartition>emptyList());
        }

        try {
            while (sendLoop) {
                ConsumerRecords<String, KafkaObservationData> records = consumer.poll(100);

                if (!records.isEmpty()) {
                    sender.send(records);
                }
            }
        }
        catch(WakeupException ex) {
            logger.info("Consumer has received instruction to wake up");
        }
        finally {
            logger.info("Consumer closing...");
            consumer.close();
            shutdownLatch.countDown();
            logger.info("Consumer has closed successfully");
        }
    }

    /**
     * Stops the process
     */
    public void stop() {
    	logger.info("Waking up consumer...");
        consumer.wakeup();

        try {
            logger.info("Waiting for consumer to shutdown...");
            shutdownLatch.await();
        } catch (InterruptedException e) {
            logger.error("Exception thrown waiting for shutdown", e);
        }
    }

    /**
     * Gathers the nessecary properties, that are required for data-reception and data-processing
     * @return The nessecary properties, that are required for data-reception and data-processing
     */
    private Properties getConsumerProperties() {
    	Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.getKafkaHostName());
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ObservationDataDeserializer.class.getName());
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "GraphiteConsumers");
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "GraphiteConsumer");
        configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configProperties;
    }

}