package edu.teco.pavos.core.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import edu.teco.pavos.core.grid.config.Seperators;
import edu.teco.pavos.core.grid.config.WorldMapData;
import edu.teco.pavos.core.grid.exceptions.ClusterNotFoundException;
import edu.teco.pavos.core.grid.exceptions.PointNotOnMapException;
import edu.teco.pavos.core.grid.exceptions.SensorNotFoundException;
import edu.teco.pavos.core.grid.geojson.GeoJsonConverter;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link GeoRecRectangleGrid}
 */
public class GeoRecRectangleGridTest {
	
	/**
	 * Tests the {@link GeoGrid} for observationTypes.
	 */
	@Test
	public void checkObservationTypes() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		ObservationData startData = new ObservationData();
		startData.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		startData.setSensorID("testSensorID");
		String observationType = "temperature_celsius";
		startData.addSingleObservation(observationType, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		grid.addObservation(location1, startData);
		
		Collection<ObservationData> observationCheck = grid.getGridObservations();
		observationCheck.forEach((data) -> {
			assertTrue(data.getSingleObservations().isEmpty());
		});
		
		grid.updateObservations();
		
		boolean isPropSaved = false;
		observationCheck = grid.getGridObservations();
		for (ObservationData data : observationCheck) {
			if (!data.getSingleObservations().isEmpty()) isPropSaved = true;
		}
		assertTrue(isPropSaved);
		
		grid.resetObservations();
		
		observationCheck = grid.getGridSensorObservations();
		System.out.println(observationCheck);
		observationCheck.forEach((data) -> {
			assertTrue(data.getSingleObservations().isEmpty());
		});
		
		assertTrue(grid.getGridObservationTypes().contains("temperature_celsius"));
	}
	
	/**
	 * Tests sending data to Graphite.
	 */
	@Test
	public void sendToGraphite() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		grid.updateObservations();
		grid.transferSensorDataDirectly();
	}
	
	/**
	 * Tests if two objects are equal.
	 */
	@Test
	public void testEquals() {
		GeoGrid grid1 = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		GeoGrid grid2 = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		ObservationData startData = new ObservationData();
		startData.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		startData.setSensorID("testSensorID");
		String property = "temperature_celsius";
		startData.addSingleObservation(property, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		grid1.addObservation(location1, startData);
		
		assertTrue(grid1.equals(grid2));
	}
	
	/**
	 * Tests if sensors can be added to the {@link GeoGrid}.
	 */
	@Test
	public void testSensorAddedToGrid() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.setSensorID("testSensorID1");
		String property = "temperature_celsius";
		data.addSingleObservation(property, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		grid.addObservation(location1, data);
		
		try {
			assertTrue(grid.getSensorLocation(data.getSensorID()).equals(location1));
		} catch (SensorNotFoundException e1) {
			fail("Sensor not on map.");
		}
		
		data = new ObservationData();
		data.setSensorID("testSensorID2");
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.addSingleObservation(property, 28.0);
		
		Point2D.Double location2 = new  Point2D.Double(130.0, 40.0);
		grid.addObservation(location2, data);
		
		data = new ObservationData();
		data.setSensorID("testSensorID3");
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.addSingleObservation(property, 28.0);
		
		Point2D.Double location3 = new  Point2D.Double(130.0, 40.0);
		grid.addObservation(location3, data);
		
		try {
			data = grid.getSensorObservation("testSensorID2", location2);
		} catch (PointNotOnMapException | SensorNotFoundException e) {
			fail(e.getMessage());
		}
		assertTrue(data.getObservationDate().matches(TimeUtil.getDateTimeRegex()));
		System.out.println(GeoJsonConverter.convertSensorObservations(
				data, property, new  Point2D.Double(260.0, 80.0)));
		assertTrue(GeoJsonConverter.convertSensorObservations(data, property, new  Point2D.Double(260.0, 80.0)).matches(
				"\\{ \"type\":\"FeatureCollection\", \"timestamp\":\"" + TimeUtil.getDateTimeRegex() + "\", "
				+ "\"observationType\":\"temperature_celsius\", \"features\": \\[ \\{ \"type\":\"Feature\", "
				+ "\"properties\": \\{ \"value\":28.0, \"sensorID\":\"testSensorID2\"\\}, \"geometry\": "
				+ "\\{ \"type\":\"Point\", \"coordinates\": \\[ 260.0, 80.0\\]\\} \\}\\] \\}"));
		
		String clusterID = null;
		GeoPolygon poly = null;
		try {
			poly = grid.getPolygonContaining(location1, grid.getMaxLevel());
			clusterID = poly.getID();
		} catch (PointNotOnMapException e) {
			fail("Location out of map bounds.");
		}
		assertEquals("recursiveRectangleGrid-2_2_3:1_0-0_0-1_0", clusterID);
		assertEquals(1, poly.getNumberOfSensors());
		assertEquals(1, poly.getNumberOfSensors(property));
		System.out.println(observationsToString(poly.getSensorDataList()));
		
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons0 = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons0) {
				Collection<GeoPolygon> subPolygons1 = poly1.getSubPolygons();
				System.out.println(observationToString(poly1.cloneObservation()));
				for (GeoPolygon poly2 : subPolygons1) {
					System.out.println(observationToString(poly2.cloneObservation()));
				}
			}
		}
		
		System.out.println();
		grid.updateObservations();
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons0 = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons0) {
				Collection<GeoPolygon> subPolygons1 = poly1.getSubPolygons();
				System.out.println(observationToString(poly1.cloneObservation()));
				for (GeoPolygon poly2 : subPolygons1) {
					System.out.println(observationToString(poly2.cloneObservation()));
				}
			}
		}
		
		GeoPolygon jsonPoly = null;
		try {
			jsonPoly = grid.getPolygon(grid.getID() + Seperators.GRID_CLUSTER_SEPERATOR + "0_1");
		} catch (ClusterNotFoundException e) {
			fail(e.getMessage());
		}
		System.out.println(jsonPoly.getGeoJson(property));
		
		ObservationData dataClone = jsonPoly.cloneObservation();
		dataClone.addSingleObservation(property, 10.799999999999998);
		LocalDateTime ldt = TimeUtil.getUTCDateTime(dataClone.getObservationDate());
		LocalDateTime subtracted = ldt.minusYears(20);
		dataClone.setObservationDate(TimeUtil.getUTCDateTimeString(subtracted));
		Collection<ObservationData> observations = new HashSet<>();
		observations.add(dataClone);
		System.out.println(GeoJsonConverter.convertPolygonObservations(observations, property, grid));
		
		Collection<ObservationData> observations2 = grid.getGridObservations();
		for (ObservationData data2 : observations2) {
			System.out.println(data2.getClusterID());
			System.out.println(data2.getSingleObservations().get(property));
		}
		
	}
	
	private String observationToString(ObservationData data) {
		return "ObservationData: " + data.getObservationDate() + ", " 
				+ data.getSensorID() + ", " + data.getClusterID() + ", " + data.getSingleObservations();
	}
	
	private String observationsToString(Collection<ObservationData> collection) {
		String result = "";
		for (ObservationData data : collection) {
			result = result + observationToString(data) + "\n";
		}
		return result;
	}

}
