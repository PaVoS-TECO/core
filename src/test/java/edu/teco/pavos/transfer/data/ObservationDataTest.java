package edu.teco.pavos.transfer.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link ObservationData}
 */
public class ObservationDataTest {

	/**
	 * Tests the conversion to a Json {@link String} with toString().
	 */
	@Test
	public void toStringTest() {
		ObservationData data = new ObservationData();
		data.setClusterID("clusterID");
		data.setSensorID("sensorID");
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.addSingleObservation("temperature_celsius", 28.6);
		data.addSingleObservation("pM_10", 14.2);
		System.out.println(data.toString());
		assertTrue(data.toString().matches(
				"\\{clusterID=clusterID, sensorID=sensorID, "
				+ "observationDate=[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z, "
				+ "observations=\\{single-double=\\{temperature_celsius=28\\.6, pM_10=14\\.2\\}\\}\\}"));
	}

}
