package server.core.web;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.ServerSocket;
import java.net.Socket;

import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.core.grid.exceptions.SensorNotFoundException;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class WebServer {

	private static final int PORT = 7700;
	private static final int BACKLOG = 10000;
	private static boolean SHUTDOWN = false;
	private static WebServer instance;
	
	private WebServer() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, - WorldMapData.latRange, WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		ObservationData result = new ObservationData();
		result.observationDate = TimeUtil.getUTCDateTimeNowString();
		result.sensorID = "sensor12345";
		result.observations.put("temperature_celsius", "40.0");
		grid.addObservation(new Point2D.Double(-50, -70), result);
		
		result = new ObservationData();
		result.observationDate = TimeUtil.getUTCDateTimeNowString();
		result.sensorID = "sensor54321";
		result.observations.put("pM_10", "40.0");
		grid.addObservation(new Point2D.Double(150, 40), result);
		try {
			System.out.println(grid.getSensorLocation("sensor54321"));
		} catch (SensorNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static WebServer getInstance() {
		if (instance == null) {
			instance = new WebServer();
		}
		return instance;
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			getInstance();
			SHUTDOWN = false;
			ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG);
			while (!SHUTDOWN) {
				Socket clientSocket = serverSocket.accept();
				Thread t = new Thread(new WebWorker(clientSocket));
				t.start();
			}
			serverSocket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		SHUTDOWN = true;
	}

}
