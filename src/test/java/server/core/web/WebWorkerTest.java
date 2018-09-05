package server.core.web;

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

import server.core.grid.GeoGrid;
import server.core.grid.GeoGridManager;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class WebWorkerTest {
	
	private static volatile WebServer server;
	private static ExecutorService service = Executors.newSingleThreadExecutor();
	
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
	
	@Test
	public void testBadRequest() {
		List<String> answer = askServer("bla");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 400"));
	}
	
	@Test
	public void testGetGeoJsonCluster_Live() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property=pM_10");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	@Test
	public void testGetGeoJsonCluster_Memcached_SingleTimestamp() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property=pM_10&time=2018-05-19T13:52:15Z&steps=2");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	@Test
	public void testGetGeoJsonCluster_Memcached_TwoTimestamps() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGeoJsonCluster?&clusterID=recursiveRectangleGrid-2_2_3:1_0,"
				+ "recursiveRectangleGrid-2_2_3:0_0&property=pM_10&time=2018-05-19T13:52:15Z,2000-05-19T13:52:15Z&steps=2");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	@Test
	public void testGetGeoJsonSensor() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		String sensorID = "sensor54321";
		String property = "pM_10";
		data.sensorID = sensorID ;
		data.observations.put(property, "14.7");
		grid.addObservation(new Point2D.Double(148.0, 59.2), data);
		
		List<String> answer = askServer("getGeoJsonSensor?gridID=" + grid.id
				+ "&sensorID=" + sensorID + "&property=" + property);
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	@Test
	public void testReportSensor() {
		List<String> answer = askServer("reportSensor?sensorID=sensor54321&reason=blubb");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
	}
	
	@Test
	public void testGetObservationTypes() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		String sensorID = "sensor54321";
		String property = "pM_10";
		data.sensorID = sensorID ;
		data.observations.put(property, "14.7");
		grid.addObservation(new Point2D.Double(148.0, 59.2), data);
		
		List<String> answer = askServer("getObservationTypes?gridID=" + grid.id);
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	@Test
	public void testGetGradient() {
		List<String> answer = askServer("getGradient?name=temperature");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
	}
	
	@Test
	public void testGetAllGradients() {
		List<String> answer = askServer("getAllGradients?");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
	}
	
	@Test
	public void testGetGridID() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		List<String> answer = askServer("getGridID?");
		assertTrue(answer.get(0).startsWith("HTTP/1.1 200"));
		
		GeoGridManager.getInstance().removeGeoGrid(grid);
	}
	
	@Test
	public void testGetGridBounds() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, 
				- WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
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
			while((currentLine = in.readLine()) != null) {
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
