package edu.teco.pavos.core.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import edu.teco.pavos.core.grid.config.WorldMapData;
import edu.teco.pavos.core.grid.exceptions.GridNotFoundException;
import edu.teco.pavos.core.grid.exceptions.PointNotOnMapException;
import edu.teco.pavos.core.grid.exceptions.SensorNotFoundException;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link GeoGridManager}
 */
public class GeoGridManagerTest {

	private static GeoGridManager manager;
	
	/**
	 * Sets everything up before every method.
	 */
	@Before
	public void beforeTest() {
		manager = GeoGridManager.getInstance();
	}
	
	/**
	 * Tests the getInstance method with multi-threading.
	 */
	@Test
	public void testGetInstanceWithMultiThreading() {
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
	
	/**
	 * Tests the management of {@link GeoGrid}s.
	 */
	@Test
	public void testGridManagement() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		assertEquals(grid, manager.getNewestGrid());
		assertEquals(grid, manager.getGrid(grid.getID()));
		assertTrue(manager.isGridActive(grid.getID()));
		assertTrue(manager.isGridActive(grid));
		assertTrue(manager.removeGeoGrid(grid));
	}
	
	/**
	 * Tests if {@link GeoGrid}s are accessible.
	 */
	@Test
	public void testGridConnectivity() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		ObservationData data = new ObservationData();
		String sensorID = "testSensor";
		String property = "temperature_celsius";
		data.setSensorID(sensorID);
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		data.addSingleObservation(property, 28.6);
		
		grid.addObservation(new Point2D.Double(160.0, -47.0), data);
		
		Collection<ObservationData> sensorColl = manager.getAllSensorObservations();
		grid.updateObservations();
		
		assertTrue(manager.getAllObservationTypes().contains(property));
		
		boolean isPropertySet = false;
		for (ObservationData d : sensorColl) {
			if (isPropertySet) break;
			isPropertySet = d.getSingleObservations().containsKey(property);
		}
		assertTrue(isPropertySet);
		
		try {
			ObservationData gridData = manager.getSensorObservation(sensorID, grid.getID());
			System.out.println(gridData.getObservationDate());
			assertTrue(gridData.getObservationDate().matches(TimeUtil.getDateTimeRegex()));
			assertTrue(gridData.getSensorID().equals(sensorID));
			assertTrue(gridData.getSingleObservations().containsKey(property));
		} catch (GridNotFoundException | SensorNotFoundException | PointNotOnMapException e) {
			fail(e.getMessage());
		}
	}

}
