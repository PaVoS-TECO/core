package edu.teco.pavos.core.grid.polygon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import edu.teco.pavos.core.grid.exceptions.ClusterNotFoundException;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link GeoRectangle}
 */
public class GeoRectangleTest {
	
	/**
	 * Tests the getProperties() method.
	 */
	@Test
	public void testProperties() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		
		ObservationData data = new ObservationData();
		data.setSensorID("sensorA");
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String singleType = "temperature_celsius";
		data.addSingleObservation(singleType, 27.0);
		
		ArrayList<Double> windDirection = new ArrayList<>();
		windDirection.add(1.0);
		windDirection.add(0.0);
		windDirection.add(0.0);
		String vectorType = "wind_direction";
		data.addVectorObservation(vectorType, windDirection);
		
		rect.addObservation(data);
		try {
			GeoPolygon sub = rect.getSubPolygon("test-1_0");
			sub.addObservation(data);
		} catch (ClusterNotFoundException e) {
			fail("Cluster test-1_0 is missing!");
		}
		
		rect.updateObservations();
		
		Collection<String> observationTypes = rect.getObservationTypes();
		System.out.println(observationTypes);
		assertTrue(observationTypes.contains(singleType));
		assertTrue(observationTypes.contains(vectorType));
	}
	
	/**
	 * Tests the getSubPolygon() method.
	 */
	@Test
	public void testGetSubPolygon() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		
		try {
			rect.getSubPolygon("invalidClusterID");
			fail("Invalid cluster must throw an exception!");
		} catch (ClusterNotFoundException e) {
			
		}
	}
	
	/**
	 * Tests the getNumberOfSensors() method.
	 */
	@Test
	public void testGetNumberOfSensors() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		
		String observationType = "pM_10";
		
		assertTrue(rect.getNumberOfSensors() == 0);
		assertTrue(rect.getNumberOfSensors(observationType) == 0);
		assertTrue(rect.getNumberOfSensors(getCollectionFromString(observationType)) == 0);
		
		ObservationData data = new ObservationData();
		data.setSensorID("sensorA");
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.addSingleObservation(observationType, 1.7);
		rect.addObservation(data);
		
		assertTrue(rect.getNumberOfSensors() == 1);
		assertTrue(rect.getNumberOfSensors(observationType) == 1);
		assertTrue(rect.getNumberOfSensors(getCollectionFromString(observationType)) == 1);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void testToString() {
		String gridID = "testGrid";
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, gridID);
		
		assertTrue(rect.toString().equals(gridID));
	}
	
	/**
	 * Tests generation of geoJson {@link String}s.
	 */
	@Test
	public void testGenerateGeoJson() {
		Point2D.Double start = new Point2D.Double(2.3, 1.7);
		Point2D.Double dim = new Point2D.Double(10.0, 5.0);
		List<Point2D.Double> points = new ArrayList<>();
		points.add(start);
		points.add(new Point2D.Double(start.getX() + dim.getX(), start.getY()));
		points.add(new Point2D.Double(start.getX() + dim.getX(), start.getY() + dim.getY()));
		points.add(new Point2D.Double(start.getX(), start.getY() + dim.getY()));
		
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(
				start.getX(), start.getY(), dim.getX(), dim.getY()), 1, 1, 0, "test");
		assertEquals(points, rect.getPoints());
		
		assertTrue(rect.getLiveClusterGeoJson("pM10").equals(
				"{\"type\":\"Feature\",\"properties\":"
				+ "{\"value\":[],\"clusterID\":\"test\",\"content\":[]},\"geometry\":"
				+ "{\"type\":\"Polygon\",\"coordinates\":[[[2.3,1.7], [12.3,1.7], [12.3,6.7],"
				+ " [2.3,6.7], [2.3,1.7]]]}}"));
		
		assertTrue(rect.getArchivedClusterGeoJson("pM10", getArrayListFromValue(20.0)).equals(
				"{\"type\":\"Feature\",\"properties\":"
				+ "{\"value\":[20.0],\"clusterID\":\"test\",\"content\":[]},\"geometry\":"
				+ "{\"type\":\"Polygon\",\"coordinates\":[[[2.3,1.7], [12.3,1.7], [12.3,6.7],"
				+ " [2.3,6.7], [2.3,1.7]]]}}"));
	}
	
	/**
	 * Tests information management about sensors. (Number of Sensors)
	 */
	@Test
	public void testAddValueAndGetNumberOfSensors() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 1, 1, 0, "test");
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.setSensorID("testSensorID");
		String property = "temperature_celsius";
		data.addSingleObservation(property, 28.0);
		rect.addObservation(data);
		Collection<String> properties = new HashSet<>();
		int numTotal = rect.getNumberOfSensors();
		int numBla = rect.getNumberOfSensors("bla");
		properties.add(property);
		properties.add("bla");
		int numPropAndBla = rect.getNumberOfSensors(properties);
		assertEquals(1, numTotal);
		assertEquals(0, numBla);
		assertEquals(0, numPropAndBla);
		if (numTotal != 1 || numBla != 0) fail("Numbers of Sensors not requested correctly.");
		
		rect.resetObservations();
		numTotal = rect.getNumberOfSensors();
		assertEquals(0, numTotal);
	}
	
	/**
	 * Tests the generation of sub-{@link GeoPolygon}s.
	 */
	@Test
	public void generateSubPolygons() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		Collection<GeoPolygon> subPolygons = rect.getSubPolygons();
		Iterator<GeoPolygon> it = subPolygons.iterator();
		assertEquals("[Point2D.Double[0.0, 0.0], Point2D.Double[5.0, 0.0], "
				+ "Point2D.Double[5.0, 2.5], Point2D.Double[0.0, 2.5]]",
				it.next().getPoints().toString());
		assertEquals("[Point2D.Double[5.0, 0.0], Point2D.Double[10.0, 0.0], "
				+ "Point2D.Double[10.0, 2.5], Point2D.Double[5.0, 2.5]]",
				it.next().getPoints().toString());
		assertEquals("[Point2D.Double[0.0, 2.5], Point2D.Double[5.0, 2.5], "
				+ "Point2D.Double[5.0, 5.0], Point2D.Double[0.0, 5.0]]",
				it.next().getPoints().toString());
		assertEquals("[Point2D.Double[5.0, 2.5], Point2D.Double[10.0, 2.5], "
				+ "Point2D.Double[10.0, 5.0], Point2D.Double[5.0, 5.0]]",
				it.next().getPoints().toString());
		try {
			rect.getSubPolygon("test-0_0");
		} catch (ClusterNotFoundException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the localization of a {@link Point2D.Double}
	 */
	@Test
	public void searchForPoint() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		rect.getSubPolygons();
		
		boolean contains = rect.contains(new Point2D.Double(1.4, 3.3), false);
		assertTrue(contains);
		if (!contains) fail("Incorrect Shape created.");
		
		contains = rect.contains(new Point2D.Double(1.4, 3.3), true);
		assertTrue(contains);
		if (!contains) fail("Incorrect Shape created.");
	}
	
	/**
	 * Tests information management about sensors. (SensorIDs)
	 */
	@Test
	public void getAllSensorIDs() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID = "testSensorID";
		data.setSensorID(sensorID);
		String property = "temperature_celsius";
		data.addSingleObservation(property, 28.0);
		rect.addObservation(data);
		
		Collection<String> check = new HashSet<>();
		check.add(sensorID);
		assertEquals(check, rect.getAllSensorIDs());
		
		ObservationData data2 = new ObservationData();
		data2.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID2 = "testSensorID2";
		data2.setSensorID(sensorID2);
		String property2 = "temperature_fahrenheit";
		data2.addSingleObservation(property2, 61.0);
		rect.addObservation(data2);
		
		Collection<String> properties = new HashSet<>();
		properties.add(property);
		properties.add(property2);
		assertEquals(properties, rect.getAllObservationTypes());
	}
	
	/**
	 * Tests the generation of median values from sensor data input.
	 */
	@Test
	public void getClusterObservations() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 1, 1, 0, "test");
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID = "testSensorID";
		data.setSensorID(sensorID);
		String property = "temperature_celsius";
		data.addSingleObservation(property, 28.0);
		
		rect.addObservation(data);
		
		ObservationData data2 = new ObservationData();
		data2.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID2 = "testSensorID2";
		data2.setSensorID(sensorID2);
		data2.addSingleObservation(property, 20.0);
		
		rect.addObservation(data2);
		
		rect.updateObservations();
		rect.resetObservations();
		
		Collection<ObservationData> check = rect.getClusterObservations();
		System.out.println(check);
		check.forEach((d) -> {
			assertFalse(d.getSingleObservations().isEmpty());
			assertTrue(d.getSingleObservations().containsKey(property));
			assertTrue(d.getSingleObservations().get(property).equals(24.0));
		});
	}
	
	private Collection<String> getCollectionFromString(String s) {
		ArrayList<String> result = new ArrayList<>();
		result.add(s);
		return result;
	}
	
	private ArrayList<Double> getArrayListFromValue(Double value) {
		ArrayList<Double> result = new ArrayList<>();
		result.add(value);
		return result;
	}
	
}
