package edu.teco.pavos.transfer.connector;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.Sender;

/**
 * Connects Kafka to a specified Component.
 */
public abstract class Connector {

    /**
     * Documents the activity of the Consumer
     */
    Logger logger = LoggerFactory.getLogger(this.getClass());

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
     * @param sender The {@link Sender} to use in this process.
     * @return operationSuccessful {@link Boolean}
     */
    public abstract boolean run(Sender sender);

    /**
     * Stops the transferring-process
     */
    public abstract void stop();

}