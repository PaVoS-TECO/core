package edu.teco.pavos.transfer.data;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serializes {@link ObservationData}
 */
public class ObservationDataSerializer implements Serializer<ObservationData> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// class is unaffected by this method
	}

	@Override
	public byte[] serialize(String topic, ObservationData data) {
		ObjectMapper mapper = new ObjectMapper();

		byte[] bData = null;
		try {
			bData = mapper.writeValueAsBytes(data);
		} catch (JsonProcessingException e) {
			logger.error("Could not serialize object " + data.getClass(), e);
		}
		return bData;
	}

	@Override
	public void close() {
		// resource leak is impossible with an ObjectMapper
	}

}
