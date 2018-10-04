package edu.teco.pavos.transfer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link ObservationData}
 */
public class ObservationDataTest {
	
	/**
	 * Tests the setting and getting of mapped data.
	 */
	@Test
	public void testSetGetObservations() {
		ObservationData data = new ObservationData();
		Map<String, Double> singleObservations = new HashMap<>();
		Map<String, ArrayList<? extends Number>> vectorObservations = new HashMap<>();
		ArrayList<Double> vectorData = new ArrayList<>();
		singleObservations.put("pM_10", 1.8);
		vectorData.add(1.0);
		vectorData.add(0.0);
		vectorData.add(0.0);
		String vectorObservationType = "wind_direction";
		vectorObservations.put(vectorObservationType, vectorData);
		
		data.setSingleObservations(singleObservations);
		assertEquals(singleObservations, data.getSingleObservations());
		
		data.setVectorObservations(vectorObservations);
		assertEquals(vectorObservations, data.getVectorObservations());
		
		assertEquals(vectorObservations.get(vectorObservationType), data.getAnonObservation(vectorObservationType));
	}
	
	/**
	 * Tests the addition of a {@link Collection} of data.
	 */
	@Test
	public void testAddObservations() {
		ObservationData data = new ObservationData();
		Map<String, Double> singleObservations = new HashMap<>();
		Map<String, ArrayList<? extends Number>> vectorObservations = new HashMap<>();
		ArrayList<Double> vectorData = new ArrayList<>();
		singleObservations.put("pM_10", 1.8);
		vectorData.add(1.0);
		vectorData.add(0.0);
		vectorData.add(0.0);
		vectorObservations.put("wind_direction", vectorData);
		
		data.addSingleObservations(singleObservations);
		assertEquals(singleObservations, data.getSingleObservations());
		
		data.addVectorObservations(vectorObservations);
		assertEquals(vectorObservations, data.getVectorObservations());
	}
	
	/**
	 * Tests the conversion to a Json {@link String} with toString().
	 */
	@Test
	public void testToString() {
		ObservationData data = new ObservationData();
		data.setClusterID("clusterID");
		data.setSensorID("testSensorID");
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.addSingleObservation("temperature_celsius", 28.6);
		data.addSingleObservation("pM_10", 14.2);
		System.out.println(data.toString());
		assertTrue(data.toString().matches(
				"(\\{clusterID=clusterID,(\\s)*"
				+ "sensorID=testSensorID,(\\s)*"
				+ "observationDate=" + TimeUtil.getDateTimeRegex() + ",(\\s)*"
				+ "singleObservations=\\{temperature_celsius=28\\.6, pM_10=14\\.2\\},(\\s)*"
				+ "vectorObservations=\\{\\}\\})"));
	}

}
