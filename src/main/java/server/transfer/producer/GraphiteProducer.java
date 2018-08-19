package server.transfer.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.transfer.config.KafkaConfig;
import server.transfer.data.ObservationData;

public class GraphiteProducer {
	
	private KafkaProducer<String, String> producer;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void produceMessage(String topic, ObservationData data) {
		producer = new KafkaProducer<>(getProducerProperties());
		
		ObjectMapper mapper = new ObjectMapper();

		String json = null;
		try {
			json = mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			logger.error("Could not convert object to String " + data.getClass(), e);
		}
		
		producer.send(new ProducerRecord<String, String>(topic, json));
		producer.close();
	}
	
	private Properties getProducerProperties() {
    	Properties configProperties = new Properties();
    	configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.getKafkaHostName());
        configProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        configProperties.put(ProducerConfig.RETRIES_CONFIG, 0);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return configProperties;
    }
	
}
