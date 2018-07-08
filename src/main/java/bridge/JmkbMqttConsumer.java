package bridge;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * This class serves as an MqttClient that consumes messages from the specified FROST-Server address. On message arrival, it will initiate the conversion of the message to a desired format via MqttMessageConverter and supply the converted message to a JmkbKafkaProducer.
 * 
 * An instance of this class should be destroyed with a call to the disconnect() method.
 */
public class JmkbMqttConsumer implements MqttCallback {

    /**
     * Default constructor
     */
    public JmkbMqttConsumer() {
    }

    /**
     * The MqttClient.
     */
    private MqttClient client;

    /**
     * The Kafka Producer.
     */
    private JmkbKafkaProducer producer;



    /**
     * This constructor for this class. Creates a new MqttClient and subscribes to the topics specified in the SensorThings API standard.
     * A unique identifier and a JmkbKafkaProducer should be supplied.
     * @param clientId The unique identifier for the MqttClient.
     * @param producer A JmkbKafkaProducer.
     */
    public void JmkbMqttConsumer(String clientId, JmkbKafkaProducer producer) {
        // TODO implement here
    }

    /**
     * This method is called when the connection to the server is lost.
     * @param cause the reason behind the loss of connection.
     */
    public void connectionLost(Throwable cause) {
        // TODO implement here
    }

    /**
     * This method is called when a message arrives from the server.
     * This method is invoked synchronously by the MQTT client. An acknowledgment is not sent back to the server until this method returns cleanly.
     * Any additional messages which arrive while this method is running will build up in memory, and will then back up on the network.
     * When this method is called, the supplied message will be converted to an Avro message and forwarded to an instance of JmkbKafkaProducer, which will then send the message to the Kafka Cluster.
     * @param topic name of the topic on the message was published to
     * @param message the actual message.
     */
    public void messageArrived(String topic, MqttMessage message) {
        // TODO implement here
    }

    /**
     * Called when delivery for a message has been completed, and all acknowledgments have been received. In this implementation of this method, nothing happens.
     * @param token the delivery token associated with the message.
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO implement here
    }

    /**
     * Disconnects client from MQTT and closes the client.
     */
    public void disconnect() {
        // TODO implement here
    }

}