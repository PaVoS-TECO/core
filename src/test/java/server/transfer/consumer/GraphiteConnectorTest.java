package server.transfer.consumer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.core.properties.KafkaPropertiesFileManager;
import server.transfer.connector.GraphiteConnector;
import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;
import server.transfer.sender.ConsoleSender;
import server.transfer.sender.util.TimeUtil;

public class GraphiteConnectorTest {

	private static final String topic = "GraphiteConsumerTest";
	
	@Test
	public void consume_preproducedMessage_sendConvertedResultToConsole() throws InterruptedException {
		ObservationData data = new ObservationData();
		setupCorrectData(data);
		
		ObjectMapper mapper = new ObjectMapper();
		boolean canSerialize = mapper.canSerialize(ObservationData.class);
		
		assertTrue(canSerialize);
		
		String sData = null;
		try {
			sData = mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			fail("JsonProcessingException thrown");
		}
		
		
		ArrayList<String> topics = new ArrayList<String>();
		topics.add(topic);
		final GraphiteConnector connector = new GraphiteConnector(topics);
		
		KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaPropertiesFileManager.getInstance().getGraphiteProducerProperties());
		producer.send(new ProducerRecord<String, String>(topic, sData));
		producer.close();
		
		Thread t = new Thread(new Runnable() {
	        public void run() {
	        	connector.run(new ConsoleSender());
	        }
	    });
	    t.start();
	    TimeUnit.SECONDS.sleep(2);
		connector.stop();
		t.join();
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
