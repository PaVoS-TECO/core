package server.transfer.send;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;
import server.transfer.sender.GraphiteSender;
import server.transfer.sender.util.TimeUtil;

/**
 * Tests {@link GraphiteSender}
 */
public class GraphiteSenderTest {
	
	/**
	 * Tests the connection and sending of data to Graphite.
	 */
	@Test
	public void connectAndSendDataRecordsSendDataToGraphite() {
		ObservationData data = setupData(new ObservationData(),
				"8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest", getDateString(), "0");
		
		Collection<ObservationData> records = new ArrayList<>();
		records.add(data);
		
		GraphiteSender sender = new GraphiteSender();
		sender.send(records);
	}
	
	private String getDateString() {
		return TimeUtil.getUTCDateTimeNowString();
	}
	
	private ObservationData setupData(ObservationData data,
			String locationElevation, String locationID, String locationName, String date, String pM10) {
		data.observationDate = date;
		data.clusterID = "testGrid-1_1_1:1_0";
		data.sensorID = "testSensor";
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		return data;
	}

}
