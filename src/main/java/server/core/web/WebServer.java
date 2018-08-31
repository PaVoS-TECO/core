package server.core.web;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class WebServer {

	private static final int PORT = 7700;
	private static final int BACKLOG = 10000;
	private static boolean shutdown = false;
	private static WebServer instance;
	private static Logger logger = LoggerFactory.getLogger(WebServer.class);
	
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
	}
	
	public static WebServer getInstance() {
		if (instance == null) {
			instance = new WebServer();
		}
		return instance;
	}
	
	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG)) {
			getInstance();
			shutdown = false;
			while (!shutdown) {
				processClients(serverSocket);
			}
			
		} catch (Exception e) {
			logger.error("Server-socket closed with an exception.", e);
		}
	}
	
	private static void processClients(ServerSocket serverSocket) {
		try (Socket clientSocket = serverSocket.accept()) {
			Thread t = new Thread(new WebWorker(clientSocket));
			t.start();
		} catch (Exception e) {
			logger.error("Client-socket closed with an exception.", e);
		}
	}
	
	public static void shutdown() {
		shutdown = true;
	}

}
