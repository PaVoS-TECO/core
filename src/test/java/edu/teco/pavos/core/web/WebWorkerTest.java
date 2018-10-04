package edu.teco.pavos.core.web;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.teco.pavos.core.grid.GeoGrid;
import edu.teco.pavos.core.grid.GeoGridManager;
import edu.teco.pavos.core.grid.GeoRecRectangleGrid;
import edu.teco.pavos.core.grid.config.WorldMapData;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;
import edu.teco.pavos.core.visualization.GradientManager;
import edu.teco.pavos.core.visualization.GradientRange;
import edu.teco.pavos.core.visualization.gradients.MultiGradient;
import edu.teco.pavos.database.ObservationDataToStorageProcessor;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * Tests {@link WebWorker}.
 */
public class WebWorkerTest {
	
	@Autowired
	private static ObservationDataToStorageProcessor database;
	private static MemcachedClient memcachedClient;
	
	private static volatile WebServer server;
	private static ExecutorService service = Executors.newSingleThreadExecutor();
	
	/**
	 * Checks for available memcached connections.
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@BeforeClass
	public static void beforeClass() throws NoSuchFieldException, SecurityException {
		memcachedClient = mock(MemcachedClient.class);
		
		database = new ObservationDataToStorageProcessor(memcachedClient);
	}
	
	/**
	 * Sets everything up before every method.
	 */
	@Before
	public void beforeTest() {
		try {
			service.awaitTermination(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		server = new WebServer(database);
		service.execute(server);
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the reaction to a forbidden, non http-conform request.
	 * Assumes a http-answer with statuscode 403 - forbidden.
	 */
	@Test
	public void testForbidden() {
		List<String> answer = askServerWithoutHttp("bla");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 403"));
	}
	
	/**
	 * Tests the reaction to a bad request.
	 * Assumes a http-answer with statuscode 400 - bad request.
	 */
	@Test
	public void testBadRequest() {
		List<String> answer = askServer("bla");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 400"));
	}
	
	/**
	 * Tests the conversion of live {@link GeoPolygon}s to GeoJson {@link String}s.
	 */
	@Test
	public void testGetGeoJsonClusterLive() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property=pM_10");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_000&property=pM_10");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 400"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests the conversion of live {@link GeoPolygon}s to GeoJson {@link String}s.
	 * The request is missing the steps parameter.
	 */
	@Test
	public void testGetGeoJsonClusterDBNoSteps() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property=pM_10&time=2018-05-19T13:52:15Z,2000-05-19T13:52:15Z");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 400"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests the conversion of database-saved {@link GeoPolygon}s to GeoJson {@link String}s.
	 * Uses a single timestamp.
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testGetGeoJsonClusterMemcachedSingleTimestamp()
			throws TimeoutException, InterruptedException, MemcachedException {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		String clusterID1 = "recursiveRectangleGrid-2_2_3:1_0";
		String clusterID2 = "recursiveRectangleGrid-2_2_3:0_0";
		String timestamp = TimeUtil.getUTCDateTimeNowString();
		String observedType = "pM_10";
		String sensorID = "testSensor";
		
		ObservationData d1 = new ObservationData();
		d1.addSingleObservation(observedType, 1.0);
		d1.setClusterID(clusterID1);
		d1.setObservationDate(timestamp);
		d1.setSensorID(sensorID);
		
		ObservationData d2 = new ObservationData();
		d2.addSingleObservation(observedType, 2.0);
		d2.setClusterID(clusterID2);
		d2.setObservationDate(timestamp);
		d2.setSensorID(sensorID);
		
		when(memcachedClient.get(clusterID1)).thenReturn(0L);
		when(memcachedClient.get(clusterID2)).thenReturn(0L);
		when(memcachedClient.get(clusterID1 + "|0")).thenReturn(d1);
		when(memcachedClient.get(clusterID2 + "|0")).thenReturn(d2);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property="
				+ observedType + "&time=2018-05-19T13:52:15Z&steps=2");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests the conversion of database-saved {@link GeoPolygon}s to GeoJson {@link String}s.
	 * Uses a two timestamps.
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testGetGeoJsonClusterMemcachedTwoTimestamps()
			throws TimeoutException, InterruptedException, MemcachedException {
		
		String clusterID1 = "recursiveRectangleGrid-2_2_3:1_0";
		String clusterID2 = "recursiveRectangleGrid-2_2_3:0_0";
		String timestamp = TimeUtil.getUTCDateTimeNowString();
		String observedType = "pM_10";
		String sensorID = "testSensor";
		
		ObservationData d1 = new ObservationData();
		d1.addSingleObservation(observedType, 1.0);
		d1.setClusterID(clusterID1);
		d1.setObservationDate(timestamp);
		d1.setSensorID(sensorID);
		
		ObservationData d2 = new ObservationData();
		d2.addSingleObservation(observedType, 2.0);
		d2.setClusterID(clusterID2);
		d2.setObservationDate(timestamp);
		d2.setSensorID(sensorID);
		
		when(memcachedClient.get(clusterID1)).thenReturn(0L);
		when(memcachedClient.get(clusterID2)).thenReturn(0L);
		when(memcachedClient.get(clusterID1 + "|0")).thenReturn(d1);
		when(memcachedClient.get(clusterID2 + "|0")).thenReturn(d2);
		
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property=pM_10&"
				+ "time=2018-05-19T13:52:15Z,2000-05-19T13:52:15Z&steps=2");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests the conversion of a sensors {@link ObservationData} to a GeoJson {@link String}.
	 */
	@Test
	public void testGetGeoJsonSensor() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID = "sensor54321";
		String property = "pM_10";
		data.setSensorID(sensorID);
		data.addSingleObservation(property, 14.7);
		grid.addObservation(new Point2D.Double(148.0, 59.2), data);
		
		// normal
		List<String> answer = askServer("getGeoJsonSensor?gridID=" + grid.getID()
				+ "&sensorID=" + sensorID + "&property=" + property);
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		// nonexisting grid
		answer = askServer("getGeoJsonSensor?gridID=" + "aNonExistingGrid"
		+ "&sensorID=" + sensorID + "&property=" + property);
		assertTrue(answer.get(0).startsWith("HTTP/1.1 400"));
		
		// nonexisting sensor
		answer = askServer("getGeoJsonSensor?gridID=" + grid.getID()
		+ "&sensorID=" + "aNonExistingSensor" + "&property=" + property);
		assertTrue(answer.get(0).startsWith("HTTP/1.1 400"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests whether it is possible to report a sensor or not.
	 */
	@Test
	public void testReportSensor() {
		List<String> answer = askServer("reportSensor?sensorID=sensor54321&reason=blubb");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
	}
	
	/**
	 * Tests requesting all saved observationTypes from the Database.
	 */
	@Test
	public void testGetObservationTypes() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		List<String> answer = askServer("getObservationTypes?gridID=" + grid.getID());
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests requesting a single {@link MultiGradient}.
	 */
	@Test
	public void testGetGradient() {
		List<String> answer = askServer("getGradient?name=temperature");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
	}
	
	/**
	 * Tests requesting all existing {@link MultiGradient}s.
	 */
	@Test
	public void testGetAllGradients() {
		List<String> answer = askServer("getAllGradients?");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		MultiGradient grad = new MultiGradient("newGradient001", Color.BLUE, Color.GREEN);
		GradientManager.getInstance().addGradient(grad, new GradientRange("newGradient001-range001", -1.2, 5.3));
		
		answer = askServer("getAllGradients?");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
	}
	
	/**
	 * Tests requesting the identifier of a {@link GeoGrid}.
	 */
	@Test
	public void testGetGridID() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGridID?");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests requesting the map-boundaries of the specified {@link GeoGrid}.
	 */
	@Test
	public void testGetGridBounds() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGridBounds?");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	private List<String> askServer(String request) {
		try {
			Socket client = new Socket("localhost", 7700);
			PrintWriter out = new PrintWriter(client.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			System.out.println("-----------------------");
			System.out.println("Request was: " + request);
			doHttpRequest(out, request);
			
			List<String> answer = new ArrayList<>();
			String currentLine;
			while ((currentLine = in.readLine()) != null) {
				System.out.println(currentLine);
				answer.add(currentLine);
			}
			in.close();
			out.close();
			client.close();
			return answer;
		} catch (IOException e) {
			fail(e.getMessage());
		}
		fail("Server did not answer request.");
		return null;
	}
	
	private List<String> askServerWithoutHttp(String request) {
		try {
			Socket client = new Socket("localhost", 7700);
			PrintWriter out = new PrintWriter(client.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			System.out.println("-----------------------");
			System.out.println("NON-HTTP-Request was: " + request);
			out.println(request);
			out.println();
			out.flush();
			
			List<String> answer = new ArrayList<>();
			String currentLine;
			while ((currentLine = in.readLine()) != null) {
				System.out.println(currentLine);
				answer.add(currentLine);
			}
			in.close();
			out.close();
			client.close();
			return answer;
		} catch (IOException e) {
			fail(e.getMessage());
		}
		fail("Server did not answer request.");
		return null;
	}
	
	private void doHttpRequest(PrintWriter out, String req) {
		out.println("GET /" + req + " HTTP/1.1");
		out.println("Host: localhost:7700");
		out.println();
		out.flush();
	}

}
