package server.transfer.serialization;

import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Deserializes KafkaObservationData objects
 */
public class ObservationDataDeserializer<T extends KafkaObservationData> implements org.apache.kafka.common.serialization.Deserializer {

    /**
     * Default constructor
     */
    public ObservationDataDeserializer() {
    	
    }

    /**
     * Documents the deserialization of our objects
     */
    private static Logger logger = LoggerFactory.getLogger(ObservationDataDeserializer.class);

    /**
     * Closes this object
     */
    public void close() {
        // TODO implement here
    }

    /**
     * Configures the deserializer
     * @param configs The Configuration
     * @param isKey A variable, telling us whether we want to configure the key or the value
     */
	@Override
	public void configure(Map configs, boolean isKey) {
		// TODO Auto-generated method stub
	}

	/**
     * Deserializes an object
     * @param topic Kafka-Topic
     * @param data These are our serialized bytes
     * @return A serializable object that contains the observed data from kafka
     */
	@Override
	public KafkaObservationData deserialize(String topic, byte[] data) {
		KafkaObservationData observationData = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            observationData = mapper.readValue(data, KafkaObservationData.class);
        } catch (IOException e) {
        	logger.error("Failed to deserialize object: " + data.toString(), e);
        }
        return observationData;
	}

}