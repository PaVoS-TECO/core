package edu.teco.pavos.core.web;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import org.junit.Before;
import org.junit.Test;

import edu.teco.pavos.core.grid.GeoGrid;
import edu.teco.pavos.core.grid.GeoGridManager;
import edu.teco.pavos.core.grid.GeoRecRectangleGrid;
import edu.teco.pavos.core.grid.config.WorldMapData;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;
import edu.teco.pavos.core.visualization.gradients.MultiGradient;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link WebWorker}
 */
public class WebWorkerTest {
	
	private static volatile WebServer server;
	private static ExecutorService service = Executors.newSingleThreadExecutor();
	
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
		server = new WebServer();
		service.execute(server);
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
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
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests the conversion of database-saved {@link GeoPolygon}s to GeoJson {@link String}s.
	 * Uses a single timestamp.
	 */
	@Test
	public void testGetGeoJsonClusterMemcachedSingleTimestamp() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.LNG_RANGE, 
				-WorldMapData.LAT_RANGE, WorldMapData.LNG_RANGE * 2, WorldMapData.LAT_RANGE * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property=pM_10&time=2018-05-19T13:52:15Z&steps=2");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	/**
	 * Tests the conversion of database-saved {@link GeoPolygon}s to GeoJson {@link String}s.
	 * Uses a two timestamps.
	 */
	@Test
	public void testGetGeoJsonClusterMemcachedTwoTimestamps() {
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
		data.addDoubleObservation(property, 14.7);
		grid.addObservation(new Point2D.Double(148.0, 59.2), data);
		
		List<String> answer = askServer("getGeoJsonSensor?gridID=" + grid.getID()
				+ "&sensorID=" + sensorID + "&property=" + property);
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
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
		
		ObservationData data = new ObservationData();
		data.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		String sensorID = "sensor54321";
		String property = "pM_10";
		data.setSensorID(sensorID);
		data.addDoubleObservation(property, 14.7);
		grid.addObservation(new Point2D.Double(148.0, 59.2), data);
		
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
	
	private void doHttpRequest(PrintWriter out, String req) {
		out.println("GET /" + req + " HTTP/1.1");
		out.println("Host: localhost:7700");
		out.println();
		out.flush();
	}

}
