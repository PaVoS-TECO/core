package server.core.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import server.core.grid.config.WorldMapData;
import server.core.grid.exceptions.GridNotFoundException;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.exceptions.SensorNotFoundException;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class GeoGridManagerTest {

	private static GeoGridManager manager;
	
	@Before
	public void beforeTest() {
		manager = GeoGridManager.getInstance();
	}
	
	@Test
	public void testGetInstance_multiThreading() {
		Thread t = new Thread(() -> {
			GeoGridManager manager2 = GeoGridManager.getInstance();
			assertEquals(manager, manager2);
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGridManagement() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		assertEquals(grid, manager.getNewestGrid());
		assertEquals(grid, manager.getGrid(grid.id));
		assertTrue(manager.isGridActive(grid.id));
		assertTrue(manager.isGridActive(grid));
		assertTrue(manager.removeGeoGrid(grid));
	}
	
	@Test
	public void testGridConnectivity() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		ObservationData data = new ObservationData();
		String sensorID = "testSensor";
		String property = "temperature_celsius";
		data.sensorID = sensorID;
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		data.observations.put(property, "28.6");
		
		grid.addObservation(new Point2D.Double(160.0, -47.0), data);
		
		Collection<ObservationData> sensorColl = manager.getAllSensorObservations();
		grid.updateObservations();
		
		assertTrue(manager.getAllProperties().contains(property));
		
		boolean isPropertySet = false;
		for (ObservationData d : sensorColl) {
			if (isPropertySet) break;
			isPropertySet = d.observations.containsKey(property);
		}
		assertTrue(isPropertySet);
		
		try {
			ObservationData gridData = manager.getSensorObservation(sensorID, grid.id);
			assertTrue(gridData.observationDate.matches(TimeUtil.getDateTimeRegex()));
			assertTrue(gridData.sensorID.equals(sensorID));
			assertTrue(gridData.observations.containsKey(property));
		} catch (GridNotFoundException | SensorNotFoundException | PointNotOnMapException e) {
			fail(e.getMessage());
		}
	}

}
