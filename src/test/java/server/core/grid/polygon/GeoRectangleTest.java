package server.core.grid.polygon;

import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.util.Collection;

import org.junit.Test;

import server.core.grid.polygon.GeoPolygon;
import server.core.grid.polygon.GeoRectangle;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class GeoRectangleTest {

	@Test
	public void generateJson() {
		System.out.println("Starting Test [generateJson]:");
		GeoRectangle rect = new GeoRectangle(2.3, 1.7, 10.0, 5.0, 1, 1, 0, "test1");
		System.out.println(rect.getPoints());
		System.out.println(rect.getJson());
		System.out.println();
	}
	
	@Test
	public void addValueAndGetNumberOfSensors() {
		System.out.println("Starting Test [addValue & getNumberOfSensors]:");
		GeoRectangle rect = new GeoRectangle(0.0, 0.0, 10.0, 5.0, 1, 1, 0, "test1");
		System.out.println(rect.getPoints());
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeString();
		data.sensorID = "testSensorID";
		String property = "temperature_celsius";
		data.observations.put(property, "28.0");
		rect.addObservation(data);
		int numTotal = rect.getNumberOfSensors();
		int numBla = rect.getNumberOfSensors("bla");
		System.out.println("Total Number of sensors: " + numTotal);
		System.out.println("Number of sensors with property: " + numBla);
		if (numTotal != 1 || numBla != 0) fail("Numbers of Sensors not requested correctly.");
		System.out.println();
	}
	
	@Test
	public void generateSubPolygons() {
		System.out.println("Starting Test [generateSubPolygons]:");
		GeoRectangle rect = new GeoRectangle(0.0, 0.0, 10.0, 5.0, 2, 2, 1, "test1");
		Collection<GeoPolygon> subPolygons = rect.getSubPolygons();
		for (GeoPolygon polygon : subPolygons) {
			System.out.println(polygon.getPoints());
		}
		System.out.println();
	}
	
	@Test
	public void searchForPoint() {
		System.out.println("Starting Test [searchForPoint]:");
		GeoRectangle rect = new GeoRectangle(0.0, 0.0, 10.0, 5.0, 2, 2, 1, "test1");
		rect.getSubPolygons();
		boolean contains = rect.contains(new Point2D.Double(1.4, 3.3), false);
		System.out.println(contains);
		if (!contains) fail("Incorrect Shape created.");
		System.out.println();
	}
	
}
