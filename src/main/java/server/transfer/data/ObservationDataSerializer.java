package server.transfer.data;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObservationDataSerializer implements Serializer<ObservationData> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		
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
	
	public String convertToJson(ObservationData data) {
		ObjectMapper mapper = new ObjectMapper();

		String json = null;
		try {
			json = mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			logger.error("Could not convert object to JSON " + data.getClass(), e);
		}
		return json;
	}

	@Override
	public void close() {
		
	}

}
