package server.transfer.consumer;

import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.transfer.config.KafkaConfig;
import server.transfer.connector.GraphiteConnector;
import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;
import server.transfer.sender.ConsoleSender;
import server.transfer.sender.util.TimeUtil;

public class GraphiteConnectorTests {

	private static boolean print = false;
	private static final String topic = "GraphiteConsumerTest";
	
	@Test(timeout = 10000)
	public void consume_preproducedMessage_sendConvertedResultToConsole() throws InterruptedException {
		if (print) System.out.println("Running test: 'consume a preproduced Kafka-message"
				+ ", convert it and output the result in the console'");
		ObservationData data = new ObservationData();
		setupCorrectData(data);
		
		ObjectMapper mapper = new ObjectMapper();
		boolean canSerialize = mapper.canSerialize(ObservationData.class);
		if (print) System.out.println("Mapper can serialize object: " + canSerialize);
		assert(canSerialize == true);
		
		String sData = null;
		try {
			sData = mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			fail("JsonProcessingException thrown");
		}
		if (print) System.out.println("Serialized data as String: " + sData);
		
		ArrayList<String> topics = new ArrayList<String>();
		topics.add(topic);
		final GraphiteConnector consumer = new GraphiteConnector(topics);
		
		KafkaProducer<String, String> producer = new KafkaProducer<>(getProducerProperties());
		producer.send(new ProducerRecord<String, String>(topic, sData));
		producer.close();
		
		Thread t = new Thread(new Runnable() {
	        public void run() {
	        	consumer.run(new ConsoleSender());
	        }
	    });
	    t.start();
	    TimeUnit.SECONDS.sleep(2);
		consumer.stop();
		t.join();
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
	
	private ObservationData setupCorrectData(ObservationData data) {
		return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest"
				, TimeUtil.getUTCDateTimeNowString(), "0", "0");
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation
			, String locationID, String locationName, String date, String pM10, String pM2p5) {
		data.observationDate = date;
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM2P5.toString(), pM2p5);
		return data;
	}
	
}
