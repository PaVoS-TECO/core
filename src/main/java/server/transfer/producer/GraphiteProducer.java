package server.transfer.producer;

import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import server.core.properties.KafkaTopicAdmin;
import server.core.properties.PropertiesFileManager;
import server.transfer.data.ObservationData;

public class GraphiteProducer {
	
	private KafkaProducer<String, ObservationData> producer;
	
	public GraphiteProducer() {
		producer = new KafkaProducer<>(getProducerProperties());
	}
	
	public void produceMessage(String topic, ObservationData data) {
		KafkaTopicAdmin kAdmin = KafkaTopicAdmin.getInstance();
		if (!kAdmin.existsTopic(topic)) {
			kAdmin.createTopic(topic);
		}
		producer.send(new ProducerRecord<String, ObservationData>(topic, data));
	}
	
	public void produceMessages(String topic, Collection<ObservationData> dataSet) {
		KafkaTopicAdmin kAdmin = KafkaTopicAdmin.getInstance();
		if (!kAdmin.existsTopic(topic)) {
			kAdmin.createTopic(topic);
		}
		for (ObservationData data : dataSet) {
			producer.send(new ProducerRecord<String, ObservationData>(topic, data));
		}
	}
	
	private Properties getProducerProperties() {
		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		Properties props = propManager.getProducerGridProperties();
		return props;
    }
	
	public void close() {
		producer.close();
	}
	
}
