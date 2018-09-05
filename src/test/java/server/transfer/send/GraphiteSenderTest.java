package server.transfer.send;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;
import server.transfer.sender.GraphiteSender;
import server.transfer.sender.util.TimeUtil;

public class GraphiteSenderTest {
	
	private static final String topic = "GraphiteSenderTest";
	
	@Test
	public void connectAndSendData_records_sendDataToGraphite() {
		Map<TopicPartition, List<ConsumerRecord<String, ObservationData>>> recordsMap 
		= new HashMap<TopicPartition, List<ConsumerRecord<String, ObservationData>>>();
		List<ConsumerRecord<String, ObservationData>> recordList = new ArrayList<ConsumerRecord<String, ObservationData>>();
		
		ObservationData data = setupData(new ObservationData(), "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest", getDateString(), "0");
		ConsumerRecord<String, ObservationData> record = new ConsumerRecord<String, ObservationData>(topic, 0, 0, null, data);
		
		recordList.add(record);
		recordsMap.put(new TopicPartition(topic, 0), recordList);
		ConsumerRecords<String, ObservationData> records = new ConsumerRecords<String, ObservationData>(recordsMap);
		
		GraphiteSender sender = new GraphiteSender();
		sender.send(records);
	}
	
	@Test
	public void connectAndSendData_collection_sendDataToGraphite() {
		ObservationData data = setupData(new ObservationData(), "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest", getDateString(), "0");
		GraphiteSender sender = new GraphiteSender();
		
		Collection<ObservationData> coll = new HashSet<>();
		coll.add(data);
		sender.send(coll);
	}
	
	@Test
	public void connectAndSendData_singleRecord_sendDataToGraphite() {
		ObservationData data = setupData(new ObservationData(), "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest", getDateString(), "0");
		
		GraphiteSender sender = new GraphiteSender();
		sender.send(topic, data);
	}
	
	private String getDateString() {
		return TimeUtil.getUTCDateTimeNowString();
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation, String locationID, String locationName, String date, String pM10) {
		data.observationDate = date;
		data.clusterID = "testGrid-1_1_1:1_0";
		data.sensorID = "testSensor";
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		return data;
	}

}
