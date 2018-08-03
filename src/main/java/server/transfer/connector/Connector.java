package server.transfer.connector;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.transfer.data.ObservationData;
import server.transfer.sender.Sender;

/**
 * Consumes data from Kafka
 */
public abstract class Connector {

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
    KafkaConsumer<String, ObservationData> consumer;

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

}