package server.transfer.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import server.transfer.config.KafkaConfig;
import server.transfer.data.ObservationData;
import server.transfer.data.ObservationDataSerializer;

public class GraphiteProducer {
	
	private KafkaProducer<String, ObservationData> producer;
	
	public void produceMessage(String topic, ObservationData data) {
		producer = new KafkaProducer<>(getProducerProperties());
		
		producer.send(new ProducerRecord<String, ObservationData>(topic, data));
		producer.close();
	}
	
	private Properties getProducerProperties() {
    	Properties configProperties = new Properties();
    	configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.getKafkaHostName());
        configProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        configProperties.put(ProducerConfig.RETRIES_CONFIG, 0);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ObservationDataSerializer.class.getName());
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return configProperties;
    }
	
}
