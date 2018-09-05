package server.core.grid.exceptions;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import org.junit.Test;

public class ExceptionsTest {

	@Test
	public void testClusterNotFoundException() {
		ClusterNotFoundException e = new ClusterNotFoundException("testCluster");
		assertNotNull(e);
		assertEquals("testCluster", e.getCluster());
	}
	
	@Test
	public void testGridNotFoundException() {
		GridNotFoundException e = new GridNotFoundException("testGrid");
		assertNotNull(e);
		assertEquals("testGrid", e.getGrid());
	}
	
	@Test
	public void testPointNotOnMapException() {
		Point2D.Double point = new Point2D.Double(13.37d, 42.42d);
		PointNotOnMapException e = new PointNotOnMapException(point);
		assertNotNull(e);
		assertEquals(point, e.getPoint());
	}
	
	@Test
	public void testSensorNotFoundException() {
		SensorNotFoundException e = new SensorNotFoundException("testSensor");
		assertNotNull(e);
		assertEquals("testSensor", e.getSensor());
	}

}
