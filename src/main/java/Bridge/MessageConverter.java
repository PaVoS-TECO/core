package Bridge;

import java.util.*;

/**
 * This convenience class provides static methods to convert a given message to another format.
 */
public class MessageConverter {

    /**
     * Default constructor
     */
    public MessageConverter() {
    }


    /**
     * This method converts a given MqttMessage, which contains information in the JSON format, to an Avro message in a byte array.
     * @param message The message to convert.
     * @return The message in Avro format.
     */
    public static byte[] mqttMessageToAvro(MqttMessage message) {
        // TODO implement here
        return null;
    }

    /**
     * This method returns the sensor ID that has supplied the information in the message. In detail, this method searches for the key 'iot.id' in the message and returns the value associated with the key.
     * @param message The message from which to extract the sensor ID.
     * @return The sensor ID.
     */
    public static String getSensorIdFromMessage(byte[] message) {
        // TODO implement here
        return "";
    }

}