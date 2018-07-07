package server.transfer.consumer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.transfer.send.Sender;
import server.transfer.serialization.KafkaObservationData;

/**
 * Consumes data from Kafka
 */
public abstract class Consumer {

    /**
     * Documents the activity of the Consumer
     */
    Logger logger = LoggerFactory.getLogger(this.getClass());;

    /**
     * Kafka-Topics that should be subscribed
     */
    List<String> topics;

    /**
     * Monitors the correct shutdown of the Consumer
     */
    CountDownLatch shutdownLatch = new CountDownLatch(1);

    /**
     * The KafkaConsumer that consumes the data from Kafka
     */
    KafkaConsumer<String, KafkaObservationData> consumer;

    /**
     * An Object to send data with
     */
    Sender sender;

    /**
     * Starts the transferring-process
     */
    public abstract void run();

    /**
     * Stops the transferring-process
     */
    public abstract void stop();

    /**
     * Gathers the nessecary properties, that are required for data-reception and data-processing
     * @return The nessecary properties, that are required for data-reception and data-processing
     */
    @SuppressWarnings("unused")
	private Properties getConsumerProperties() { return null; }

}