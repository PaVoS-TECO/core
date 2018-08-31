package server.transfer.send;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;
import server.transfer.sender.GraphiteSender;

public class GraphiteSenderTests {
	
	private static boolean print = true;
	private static final String topic = "GraphiteSenderTest";
	
	@Test
	public void connectAndSendData_multipleRecords_sendDataToGraphite() {
		Map<TopicPartition, List<ConsumerRecord<String, ObservationData>>> recordsMap 
		= new HashMap<TopicPartition, List<ConsumerRecord<String, ObservationData>>>();
		List<ConsumerRecord<String, ObservationData>> recordList = new ArrayList<ConsumerRecord<String, ObservationData>>();
		
		ObservationData data = setupCorrectData(new ObservationData());
		ConsumerRecord<String, ObservationData> record = new ConsumerRecord<String, ObservationData>(topic, 0, 0, null, data);
		
		recordList.add(record);
		recordsMap.put(new TopicPartition(topic, 0), recordList);
		ConsumerRecords<String, ObservationData> records = new ConsumerRecords<String, ObservationData>(recordsMap);
		
		if (print) {
			ObjectMapper mapper = new ObjectMapper();
			String sData = null;
			try {
				sData = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			System.out.println("Serialized data as String: " + sData);
		}
		
		GraphiteSender sender = new GraphiteSender();
		sender.send(records);
	}
	
	@Test
	public void connectAndSendData_singleRecord_sendDataToGraphite() {
		ObservationData data = setupCorrectData(new ObservationData());
		
		if (print) {
			ObjectMapper mapper = new ObjectMapper();
			String sData = null;
			try {
				sData = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			System.out.println("Serialized data as String: " + sData);
		}
		
		GraphiteSender sender = new GraphiteSender();
		sender.send(topic, data);
	}
	
	private ObservationData setupCorrectData(ObservationData data) {
		int i = (int) (Math.random() * 4);
		if (print) System.out.println(i);
		switch (i) {
		case 0:
			return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest", getDateString(), "0");
		case 1:
			return setupData(data, "96", "Mannheim_49-29-15_8-27-58", "Mannheim", getDateString(), "5");
		case 2:
			return setupData(data, "115", "Karlsruhe_49-0-25_8-24-13", "Karlsruhe", getDateString(), "6");
		case 3:
			return setupData(data, "96", "Berlin_52-31-12_13-24-18", "Berlin", getDateString(), "15");
		case 4:
			return setupData(data, "56", "Hannover_52-22-33_9-43-55", "Hannover", getDateString(), "3");
		default:
			return null;
		}
	}
	
	private String getDateString() {
		return LocalDateTime.now(Clock.systemUTC()).toString();
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation, String locationID, String locationName, String date, String pM10) {
		data.observationDate = date;
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		return data;
	}

}
