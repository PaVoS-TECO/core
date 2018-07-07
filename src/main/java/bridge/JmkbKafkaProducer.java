package bridge;

import org.apache.kafka.clients.producer.KafkaProducer;

/**
 * This class creates a Kafka producer using defined settings and publishes records to the Kafka Cluster.
 */
public class JmkbKafkaProducer {

    /**
     * Default constructor
     */
    public JmkbKafkaProducer() {
    }

    /**
     * The Kafka producer.
     */
    private KafkaProducer producer;



    /**
     * Asynchronously sends a record to the topic.
     * @param topic The topic.
     * @param avroMessage The message to send.
     */
    public void send(String topic, byte[] avroMessage) {
        // TODO implement here
    }

    /**
     * Disconnects this Kafka producer from the Kafka Cluster and closes the producer.
     */
    public void disconnect() {
        // TODO implement here
    }

}