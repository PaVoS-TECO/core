package server.transfer.data;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Deserializes KafkaObservationData objects
 */
public class ObservationDataDeserializer implements Deserializer<ObservationData> {

	private Logger logger = null;
	
    /**
     * Default constructor
     */
    public ObservationDataDeserializer() {
    	this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
    public void close() {
        // TODO implement here
    }
    
	/**
     * Configures the deserializer
     * @param configs The Configuration
     * @param isKey A variable, telling us whether we want to configure the key or the value
     */
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub
	}
	
	/**
     * Deserializes an object
     * @param topic Kafka-Topic
     * @param data These are our serialized bytes
     * @return A serializable object that contains the observed data from kafka
     */
	public ObservationData deserialize(String topic, byte[] data) {
		ObservationData observationData = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            observationData = mapper.readValue(data, ObservationData.class);
        } catch (IOException e) {
        	logger.error("Failed to deserialize object: " + data.toString(), e);
        }
        return observationData;
	}

}