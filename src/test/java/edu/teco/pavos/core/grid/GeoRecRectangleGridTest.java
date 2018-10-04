package edu.teco.pavos.core.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;

import edu.teco.pavos.core.grid.config.Seperators;
import edu.teco.pavos.core.grid.config.WorldMapData;
import edu.teco.pavos.core.grid.exceptions.ClusterNotFoundException;
import edu.teco.pavos.core.grid.exceptions.PointNotOnMapException;
import edu.teco.pavos.core.grid.exceptions.SensorNotFoundException;
import edu.teco.pavos.core.grid.geojson.data.ObservationGeoJson;
import edu.teco.pavos.core.grid.geojson.data.SensorGeoJson;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link GeoRecRectangleGrid}
 */
public class GeoRecRectangleGridTest {
	
	/**
	 * Tests the getSensorObservations() method.
	 */
	@Test
	public void testGetSensorObservations() {
		GeoGrid grid = createBasicGrid();
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID = "testSensorID";
		data.setSensorID(sensorID);
		String observationType = "temperature_celsius";
		data.addSingleObservation(observationType, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		
		try {
			assertFalse(grid.getSensorObservation(sensorID, location1).equals(data));
		} catch (PointNotOnMapException | SensorNotFoundException e) { }
		
		grid.addObservation(location1, data);
		
		try {
			assertTrue(grid.getSensorObservation(sensorID, location1).equals(data));
		} catch (PointNotOnMapException | SensorNotFoundException e) {
			fail("Sensor, was not found on the GeoGrid!");
		}
	}
	
	/**
	 * Tests the getSensorLocation() method.
	 */
	@Test
	public void testGetSensorLocation() {
		GeoGrid grid = createBasicGrid();
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID = "testSensorID";
		data.setSensorID(sensorID);
		String observationType = "temperature_celsius";
		data.addSingleObservation(observationType, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		grid.addObservation(location1, data);
		
		try {
			assertTrue(grid.getSensorLocation(sensorID).equals(location1));
		} catch (SensorNotFoundException e) {
			fail("Latest location of added Sensor, was not found on the GeoGrid!");
		}
	}
	
	/**
	 * Tests the getOutputTopic() method.
	 */
	@Test
	public void testGetOutputTopic() {
		GeoGrid grid = createBasicGrid();
		
		assertTrue(grid.getOutputTopic().contains(grid.getID()));
	}
	
	/**
	 * Tests the updateObservations(), transferSensorDataDirectly(), updateDatabase() and resetObservations() method.
	 */
	@Test
	public void testUpdateCycle() {
		GeoGrid grid = createBasicGrid();
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.setSensorID("testSensorID");
		String observationType = "temperature_celsius";
		data.addSingleObservation(observationType, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		grid.addObservation(location1, data);
		
		grid.updateObservations();
		grid.transferSensorDataDirectly();
		grid.updateDatabase();
		grid.resetObservations();
	}
	
	/**
	 * Tests the getClusterID() method.
	 */
	@Test
	public void testGetClusterID() {
		GeoGrid grid = createBasicGrid();
		Rectangle2D.Double bounds = grid.getMapBounds();
		Point2D.Double goodPoint = new Point2D.Double((bounds.getMaxX() - bounds.getMinX()) / 4,
				(bounds.getMaxY() - bounds.getMinY()) / 4);
		
		try {
			grid.getClusterID(goodPoint, 1);
		} catch (PointNotOnMapException e) {
			fail("Grid could not locate existing cluster!");
		}
		
		try {
			grid.getClusterID(new Point2D.Double(bounds.getMaxX() * 2,
					bounds.getMaxY() * 2), 1);
			fail("Grid located nonexisting cluster outside of the maps boundaries!");
		} catch (PointNotOnMapException e) { }
	}
	
	/**
	 * Tests the closeGrid() method.
	 */
	@Test
	public void testCloseGrid() {
		GeoGrid grid = createBasicGrid();
		grid.close();
		assertEquals(GeoGridManager.getInstance().getGrid(grid.getID()), null);
	}
	
	/**
	 * Tests the getLiveClusterGeoJson() method.
	 */
	@Test
	public void testGetLiveClusterGeoJson() {
		GeoGrid grid = createBasicGrid();
		
		try {
			System.out.println(grid.getLiveClusterGeoJson(grid.getID() + ":0_0-0_0", "pM_10"));
			assertTrue(grid.getLiveClusterGeoJson(grid.getID() + ":0_0-0_0", "pM_10").matches(
					"(\\{\"type\":\"Feature\",\"properties\":\\{\"value\":\\[\\],\"clusterID\":"
					+ "\"recursiveRectangleGrid-2_2_3:0_0-0_0\",\"content\":"
					+ "\\[\"recursiveRectangleGrid-2_2_3:0_0-0_0-0_0\","
					+ " \"recursiveRectangleGrid-2_2_3:0_0-0_0-0_1\","
					+ " \"recursiveRectangleGrid-2_2_3:0_0-0_0-1_0\","
					+ " \"recursiveRectangleGrid-2_2_3:0_0-0_0-1_1\"\\]\\},\"geometry\":\\{\"type\":\"Polygon\","
					+ "\"coordinates\":\\[\\[\\[-180.0,-85.0\\], \\[-90.0,-85.0\\],"
					+ " \\[-90.0,-42.5\\], \\[-180.0,-42.5\\], \\[-180.0,-85.0\\]\\]\\]\\}\\})"));
		} catch (ClusterNotFoundException e) {
			fail("Existing cluster could not be interpreted as ClusterGeoJson!");
		}
		
		try {
			grid.getLiveClusterGeoJson("aNonExistingCluster", "pM_10");
			fail("Nonexisting cluster was interpreted as ClusterGeoJson but must throw ClusterNotFoundException!");
		} catch (ClusterNotFoundException e) { }
		
	}
	
	/**
	 * Tests the getArchivedClusterGeoJson() method.
	 */
	@Test
	public void testGetArchivedClusterGeoJson() {
		GeoGrid grid = createBasicGrid();
		ArrayList<Double> values = new ArrayList<>();
		values.add(1.0);
		values.add(0.0);
		try {
			assertTrue(grid.getArchivedClusterGeoJson(grid.getID() + ":0_0", "pM_10", values).matches(
					"(\\{\"type\":\"Feature\",\"properties\":\\{\"value\":\\[1.0, 0.0\\],\"clusterID\":"
					+ "\"recursiveRectangleGrid-2_2_3:0_0\",\"content\":\\[\"recursiveRectangleGrid-2_2_3:0_0-0_0\","
					+ " \"recursiveRectangleGrid-2_2_3:0_0-0_1\", \"recursiveRectangleGrid-2_2_3:0_0-1_0\","
					+ " \"recursiveRectangleGrid-2_2_3:0_0-1_1\"\\]\\},\"geometry\":\\{\"type\":\"Polygon\","
					+ "\"coordinates\":\\[\\[\\[-180.0,-85.0\\], \\[0.0,-85.0\\], \\[0.0,0.0\\],"
					+ " \\[-180.0,0.0\\], \\[-180.0,-85.0\\]\\]\\]\\}\\})"));
		} catch (ClusterNotFoundException e) {
			fail("Existing cluster could not be interpreted as ClusterGeoJson!");
		}
		
		try {
			grid.getArchivedClusterGeoJson("aNonExistingCluster", "pM_10", values);
			fail("Nonexisting cluster was interpreted as ClusterGeoJson but must throw ClusterNotFoundException!");
		} catch (ClusterNotFoundException e) { }
		
	}
	
	/**
	 * Tests the addObservation() method.
	 */
	@Test
	public void testAddObservation() {
		GeoGrid grid = createBasicGrid();
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.setSensorID("testSensorID");
		String observationType = "temperature_celsius";
		data.addSingleObservation(observationType, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		grid.addObservation(location1, data);
		grid.addObservation(new Point2D.Double(99999.0, -99999.0), data);
	}
	
	/**
	 * Tests the {@link GeoGrid} for observationTypes.
	 */
	@Test
	public void testObservationTypes() {
		GeoGrid grid = createBasicGrid();
		
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
	public void testSendToGraphite() {
		GeoGrid grid = createBasicGrid();
		
		grid.updateObservations();
		grid.resetObservations();
		
		grid.transferSensorDataDirectly();
	}
	
	/**
	 * Tests if two objects are equal.
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		GeoGrid grid1 = createBasicGrid();
		GeoGrid grid2 = createBasicGrid();
		String gridWrongClass = "notAGrid";
		GeoGrid gridOtherSubClass = new GeoGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),
				1, 1, 1, "") {
			@Override
			protected void generateGeoPolygons() {
				// TODO Auto-generated method stub
				
			}
		};
		
		ObservationData startData = new ObservationData();
		startData.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		startData.setSensorID("testSensorID");
		String property = "temperature_celsius";
		startData.addSingleObservation(property, 14.0);
		
		Point2D.Double location1 = new  Point2D.Double(-150.0, 40.0);
		grid1.addObservation(location1, startData);
		
		assertTrue(grid1.equals(grid2));
		assertFalse(grid1.equals(null));
		assertFalse(grid1.equals(gridWrongClass));
		assertFalse(grid1.equals(gridOtherSubClass));
	}
	
	/**
	 * Tests if sensors can be added to the {@link GeoGrid}.
	 */
	@Ignore
	public void testSensorAddedToGrid() {
		GeoGrid grid = createBasicGrid();
		
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
		
		SensorGeoJson sensorGeoJson = new SensorGeoJson(data.getAnonObservation(property),
				"testSensorID2", new Point2D.Double(260.0, 80.0));
		ObservationGeoJson obsGeoJson = new ObservationGeoJson(data.getObservationDate(),
				property, sensorGeoJson.getGeoJson());
		assertTrue(obsGeoJson.getGeoJson().matches(
				"(\\{\"type\":\"FeatureCollection\",\"timestamp\":\"" + TimeUtil.getDateTimeRegex() + "\","
				+ "\"observationType\":\"temperature_celsius\",\"features\":\\[\\{\"type\":\"Feature\","
				+ "\"properties\":\\{\"value\":\\[28.0\\],\"sensorID\":\"testSensorID2\"\\},\"geometry\":"
				+ "\\{\"type\":\"Point\",\"coordinates\":\\[260.0,80.0\\]\\}\\}\\]\\})"));
		
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
		
		grid.updateObservations();
		
		GeoPolygon jsonPoly = null;
		try {
			jsonPoly = grid.getPolygon(grid.getID()
					+ Seperators.GRID_CLUSTER_SEPERATOR + "0_1"
					+ Seperators.GRID_CLUSTER_SEPERATOR + "0_1");
		} catch (ClusterNotFoundException e) {
			fail(e.getMessage());
		}
		
		ObservationData dataClone = jsonPoly.cloneObservation();
		dataClone.addSingleObservation(property, 10.799999999999998);
		LocalDateTime ldt = TimeUtil.getUTCDateTime(dataClone.getObservationDate());
		LocalDateTime subtracted = ldt.minusYears(20);
		dataClone.setObservationDate(TimeUtil.getUTCDateTimeString(subtracted));
		Collection<ObservationData> observations = new HashSet<>();
		observations.add(dataClone);
		
		grid.getGridObservations();
		
	}
	
	private GeoGrid createBasicGrid() {
		return new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
	}
	
}
