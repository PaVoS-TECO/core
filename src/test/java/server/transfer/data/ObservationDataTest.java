package server.transfer.data;

import static org.junit.Assert.*;

import org.junit.Test;

import server.transfer.sender.util.TimeUtil;

public class ObservationDataTest {

	@Test
	public void toStringTest() {
		ObservationData data = new ObservationData();
		data.clusterID = "clusterID";
		data.sensorID = "sensorID";
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		data.observations.put("temperature_celsius", "28.6");
		data.observations.put("pM_10", "14.2");
		assertTrue(data.toString().matches(
				"\\{clusterID=clusterID, sensorID=sensorID, "
				+ "observationDate=[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z, "
				+ "observations=\\{temperature_celsius=28\\.6, pM_10=14\\.2\\}\\}"));
	}

}
