package server.core.web;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoGrid;
import server.core.grid.GeoGridManager;
import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.exceptions.SensorNotFoundException;
import server.core.grid.geojson.GeoJsonConverter;
import server.core.grid.geojson.data.ObservationGeoJson;
import server.core.grid.polygon.GeoPolygon;
import server.core.visualization.GradientManager;
import server.core.visualization.gradients.MultiGradient;
import server.database.Facade;
import server.transfer.sender.util.TimeUtil;

/**
 * The {@link WebWorker} handles http-requests and sends a http-answer.
 */
public class WebWorker implements Runnable {
	
	Socket clientSocket;
	private int statusCode = HttpStatus.SC_OK;
	private String[] req;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Creates a new {@link WebWorker} that handles a single request of the client-socket.
	 * @param socket {@link Socket} of the client
	 */
    public WebWorker(Socket socket) {
        clientSocket = socket;
    }
	
	@Override
	public void run() {
		try (
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			) {
			
	        String request = in.readLine();
	        if (request.startsWith("GET /") && request.endsWith(" HTTP/1.1")) {
	        	request = request.replaceFirst("GET /", "").replaceFirst(" HTTP/1.1", "");
	        } else {
	        	statusCode = HttpStatus.SC_FORBIDDEN;
				printOut(null, out);
	        	throw new IOException();
	        }
	        
	        req = request.split("\\?", 2);
	        String type = req[0];
	        if (req.length == 2) {
	        	handleRequest(type, in, out);
	        } else {
	        	logger.info("Bad-Request: " + type);
				statusCode = HttpStatus.SC_BAD_REQUEST;
				printOut(null, out);
				return;
	        }
        } catch (IOException | NullPointerException e) {
            logger.error("Processing socket request was interrupted. Attempting to close socket now.", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error("Could not close socket!", e);
            }
        }
	}
	
	private void handleRequest(String type, BufferedReader in, PrintWriter out) {
		try {
			req = req[1].split("&");
			switch (type) {
			case "getGeoJsonCluster":
				getGeoJsonCluster(out);
				break;
			case "getGeoJsonSensor":
				getGeoJsonSensor(out);
				break;
			case "reportSensor":
				reportSensor(out);
				break;
			case "getObservationTypes":
				getObservationTypes(out);
				break;
			case "getGradient":
				getGradient(out);
				break;
			case "getAllGradients":
				getAllGradients(out);
				break;
			case "getGridID":
				getGridID(out);
				break;
			case "getGridBounds":
				getGridBounds(out);
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | NullPointerException e) {
			logger.info("Bad-Request");
			statusCode = HttpStatus.SC_BAD_REQUEST;
			printOut(null, out);
		}
	}
	
	private void getGridBounds(PrintWriter out) {
		GeoGridManager manager = GeoGridManager.getInstance();
		GeoGrid grid = manager.getNewestGrid();
		printOut(rectangleToString(grid.getMapBounds()), out);
	}
	
	private String rectangleToString(Rectangle2D.Double rect) {
		StringBuilder builder = new StringBuilder();
		String comma = ", ";
		builder.append("[[");
		builder.append(rect.getX());
		builder.append(comma);
		builder.append(rect.getY());
		builder.append("], [");
		builder.append(rect.getMaxX());
		builder.append(comma);
		builder.append(rect.getMaxY());
		builder.append("]]");
		return builder.toString();
	}

	private void getGridID(PrintWriter out) {
		GeoGridManager manager = GeoGridManager.getInstance();
		GeoGrid grid = manager.getNewestGrid();
		printOut(grid.getID(), out);
	}

	private void getAllGradients(PrintWriter out) {
		GradientManager manager = GradientManager.getInstance();
		List<MultiGradient> gradients = manager.getAllGradients();
		StringBuilder builder = new StringBuilder();
		builder.append("{ ");
		for (MultiGradient gradient : gradients) {
			builder.append(gradient.toString());
			if (gradients.indexOf(gradient) != gradients.size() - 1) {
				builder.append(",");
			}
			builder.append(" ");
		}
		builder.append("}");
		printOut(builder.toString(), out);
	}

	private void getGradient(PrintWriter out) {
		String name = getParameter("name");
		GradientManager manager = GradientManager.getInstance();
		MultiGradient gradient = manager.getGradient(name);
		StringBuilder builder = new StringBuilder();
		builder.append("{ ");
		builder.append(gradient.toString());
		builder.append(" }");
		printOut(builder.toString(), out);
	}
	
	private void getObservationTypes(PrintWriter out) {
		String gridID = getParameter("gridID");
		Facade f = Facade.getInstance();
		Set<String> properties = f.getObservedProperties(gridID);
		StringBuilder builder = new StringBuilder();
		builder.append("[ ");
		Iterator<String> it = properties.iterator();
		it.forEachRemaining((property) -> {
			builder.append("\"" + property + "\"");
			if (it.hasNext()) builder.append(", ");
		});
		builder.append(" ]");
	    printOut(builder.toString(), out);
	}

	private void reportSensor(PrintWriter out) {
		String sensor = getParameter("sensorID");
		String reason = getParameter("reason");
		InetAddress ip = clientSocket.getInetAddress();
		String message = String.format("sensor = %s, reason = %s, ip = %s", sensor, reason, ip.getHostAddress());
		logger.info("A sensor was reported: {}", message);
	    printOut("Sensor reported successfully!", out);
	}

	private void getGeoJsonSensor(PrintWriter out) {
		String result = null;
		
		String gridID = getParameter("gridID");
		String sensorID = getParameter("sensorID");
		String keyProperty = getParameter("property");
		
		result = getLiveDataSensor(sensorID, gridID, keyProperty);
		
	    printOut(result, out);
	}

	private void getGeoJsonCluster(PrintWriter out) {
		
		String fusedClusterIDs = getParameter("clusterID");
		String keyProperty = getParameter("property");
		String[] clusterIDs = fusedClusterIDs.split(",");
		String gridID = null;
		for (int i = 0; i < clusterIDs.length; i++) {
			if (i == 0) {
				gridID = clusterIDs[i].split(":")[0];
			} else {
				String gridID2 = clusterIDs[i].split(":")[0];
				if (!gridID.equals(gridID2)) {
					throw new IllegalArgumentException();
				}
			}
		}
		
		String result = null;
		try {
			String fusedTime = getParameter("time");
			
			String[] time = fusedTime.split(",");
			
			int steps = 1;
			try {
				String stepsString = getParameter("steps");
				steps = Integer.valueOf(stepsString);
			} catch (IllegalArgumentException e) {
				if (time.length != 1) throw new IllegalArgumentException();
			}
			
			result = getDatabaseDataCluster(gridID, keyProperty, clusterIDs, time, steps);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().equals("time")) {
				result = getLiveDataCluster(gridID, keyProperty, clusterIDs);
			} else {
				statusCode = HttpStatus.SC_BAD_REQUEST;
				
			}
		}
		if (result == null) {
			
			statusCode = HttpStatus.SC_BAD_REQUEST;
			throw new IllegalArgumentException();
		} else {
			printOut(result, out);
		}
	}
	
	private String getLiveDataSensor(String sensorID, String gridID, String keyProperty) {
		
		final GeoGrid grid = getGrid(gridID);
		Point2D.Double point = null;
		try {
			point = grid.getSensorLocation(sensorID);
			return GeoJsonConverter.convertSensorObservations(
					grid.getSensorObservation(sensorID, point), keyProperty, point);
		} catch (PointNotOnMapException | SensorNotFoundException | NullPointerException e) {
			statusCode = HttpStatus.SC_BAD_REQUEST;
		}
		return null;
	}
	
	private String getLiveDataCluster(String gridID, String observationType, String[] clusterIDs) {
		Collection<String> features = new HashSet<>();
		final GeoGrid grid = getGrid(gridID);
		for (int i = 0; i < clusterIDs.length; i++) {
			GeoPolygon polygon = null;
			try {
				polygon = grid.getPolygon(clusterIDs[i]);
			} catch (ClusterNotFoundException e) {
				statusCode = HttpStatus.SC_BAD_REQUEST;
				throw new IllegalArgumentException();
			}
			if (polygon != null) {
				features.add(polygon.getLiveClusterGeoJson(observationType));
			}
		}
		return new ObservationGeoJson(TimeUtil.getUTCDateTimeNowString(), observationType, features).getGeoJson();
	}
	
	private String getDatabaseDataCluster(String gridID, String observationType, 
			String[] clusterIDs, String[] time, int steps) {
		
		if (time.length == 1) {
			Collection<String> features = new ArrayList<>();
			features.addAll(getDBFeaturesSingleDateTime(gridID, clusterIDs, time[0], observationType));
			
			return new ObservationGeoJson(time[0], observationType, features).getGeoJson();
			
		} else if (time.length == 2) {
			DateTime dt1 = TimeUtil.getUTCDateTime(time[0]).toDateTime(DateTimeZone.UTC);
			DateTime dt2 = TimeUtil.getUTCDateTime(time[1]).toDateTime(DateTimeZone.UTC);
			long dt1Millis = dt1.getMillis();
			long dt2Millis = dt2.getMillis();
			long minMillis = Math.min(dt1Millis, dt2Millis);
			long diff = Math.abs(dt1Millis - dt2Millis) / steps;
			
			Collection<String> featuresCollections = new ArrayList<>();
			
			for (int i = 0; i < steps; i++) {
				Collection<String> features = new ArrayList<>();
				
				long currentMillis = minMillis + (long) i * diff;
				DateTime dtCurrent = new DateTime(currentMillis, DateTimeZone.UTC);
				String currentTimestamp = TimeUtil.getUTCDateTimeString(dtCurrent.toLocalDateTime());
				
				features.addAll(getDBFeaturesSingleDateTime(gridID, clusterIDs, currentTimestamp, observationType));
				featuresCollections.add(new ObservationGeoJson(
						currentTimestamp, observationType, features).getGeoJson());
			}
			return featuresCollections.toString();
		} else {
			throw new IllegalArgumentException("Time format unacceptable.");
		}
	}
	
	private Collection<String> getDBFeaturesSingleDateTime(String gridID, String[] clusterIDs,
			String currentTimestamp, String observationType) {
		Facade database = Facade.getInstance();
		
		Collection<String> features = new ArrayList<>();
		GeoGridManager manager = GeoGridManager.getInstance();
		GeoGrid grid = manager.getGrid(gridID);
		logger.debug("getDBFeaturesSingleDateTime");
		
		for (String clusterID : clusterIDs) {
			String value = database.getObservationData(clusterID, currentTimestamp, observationType);
			features.add(grid.getArchivedClusterGeoJson(clusterID, observationType, value));
		}
		return features;
	}
	
	private GeoGrid getGrid(String gridID) {
		GeoGridManager gridManager = GeoGridManager.getInstance();
		if (!gridManager.isGridActive(gridID)) {
			throw new IllegalArgumentException("Grid is not active and therefore can not be fetched.");
		}
		return gridManager.getGrid(gridID);
	}
	
	private String getParameter(String parameter) {
		for (int i = 0; i < req.length; i++) {
			if (req[i].startsWith(parameter + "=")) {
				return req[i].replaceFirst(parameter + "=", "");
			}
		}
		throw new IllegalArgumentException(parameter);
	}
	
	private void printOut(String result, PrintWriter out) {
        // Start sending our reply, using the HTTP 1.1 protocol
        out.print("HTTP/1.1 " + statusCode + " \r\n"); 			// Version & status code
        out.print("Access-Control-Allow-Origin: * \r\n"); // CORS
        out.print("Content-Type: text/plain\r\n"); 	// The type of data
        out.print("Connection: close\r\n"); 		// Will close stream
        out.print("\r\n"); 							// End of headers
        if (result != null) {
        	out.write(result);
        	out.close();
        }
        if (statusCode != 200) {
        	printErr(out);
        }
	}

	private void printErr(PrintWriter out) {
		switch (statusCode) {
		case HttpStatus.SC_BAD_REQUEST:
			out.write("Error " + statusCode + " - Requested parameters do not match internal data.");
			break;
		case HttpStatus.SC_FORBIDDEN:
			out.write("Error " + statusCode + " - Forbidden.");
			break;
		default:
			out.write("Error" + HttpStatus.SC_NOT_IMPLEMENTED + " - Not implemented.");
			break;
		}
		out.close();
	}
	
}
