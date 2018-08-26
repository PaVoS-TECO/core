package server.core.web;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import server.core.grid.polygon.GeoPolygon;
import server.database.Facade;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class DataServlet  extends HttpServlet {
	
	private static final long serialVersionUID = 4505621561403961545L;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String type = req.getParameter("requestType");
		if (type.equals("getGeoJsonCluster")) {
			getGeoJsonCluster(req, res);
		} else if (type.equals("getGeoJsonSensor")) {
			getGeoJsonSensor(req, res);
		} else if (type.equals("reportSensor")) {
			reportSensor(req, res);
		} else if (type.equals("getObservationTypes")) {
			getObservationTypes(req, res);
		}
	}
	
	private void getGeoJsonSensor(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String gridID = req.getParameter("gridID");
		String sensorID = req.getParameter("sensorID");
		String keyProperty = req.getParameter("property");
		
		String result = getLiveDataSensor(sensorID, gridID, keyProperty, req, res);
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
	    res.getWriter().write(result);
	}

	private String getLiveDataSensor(String sensorID, String gridID, String keyProperty, HttpServletRequest req, HttpServletResponse res) throws ServletException {
		final GeoGrid grid = getGrid(gridID);
		Point2D.Double point;
		try {
			point = grid.getSensorLocation(sensorID);
		} catch (SensorNotFoundException e) {
			throw new ServletException("Sensor is not registrated in this grid. " + e.getSensor());
		}
		try {
			return GeoJsonConverter.convertSensorObservations(grid.getSensorObservation(sensorID, point), keyProperty, point);
		} catch (PointNotOnMapException e) {
			throw new ServletException("Point is not on map. " + e.getPoint() );
		} catch (SensorNotFoundException e) {
			throw new ServletException("Sensor was removed from grid during operation. " + e.getSensor());
		}
	}

	private void getObservationTypes(HttpServletRequest req, HttpServletResponse res) throws IOException {
		GeoGridManager manager = GeoGridManager.getInstance();
		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");
	    res.getWriter().write(manager.getAllProperties().toString());
	}

	private void reportSensor(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String sensor = req.getParameter("sensor");
		String reason = req.getParameter("reason");
		String ip = req.getRemoteAddr();
		logger.info("[Webinterface][Sensor-Reported] Sensor = " + sensor + ", reason = " + reason + ", ip = " + ip);
		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");
	    res.getWriter().write("Sensor reported successfully!");
	}

	private String getDatabaseDataCluster(String gridID, String keyProperty, String[] clusterIDs, String[] time, 
			String stepsString, HttpServletRequest req, HttpServletResponse res) throws ServletException {
		if (time.length == 1) {
			Facade database = new Facade();
			Collection<ObservationData> observations = new HashSet<>();
			for (String clusterID : clusterIDs) {
				String val = database.getObservationData(clusterID, time[0], keyProperty);
				ObservationData data = new ObservationData();
				data.clusterID = clusterID;
				data.observationDate = time[0];
				data.observations.put(keyProperty, val);
				observations.add(data);
			}
			GeoGridManager manager = GeoGridManager.getInstance();
			return GeoJsonConverter.convertPolygonObservations(observations, keyProperty, manager.getGrid(gridID));
		} else if (time.length == 2) {
			DateTime dt1 = TimeUtil.getUTCDateTime(time[0]).toDateTime(DateTimeZone.UTC);
			DateTime dt2 = TimeUtil.getUTCDateTime(time[1]).toDateTime(DateTimeZone.UTC);
			long dt1Millis = dt1.getMillis();
			long dt2Millis = dt2.getMillis();
			long minMillis = Math.min(dt1Millis, dt2Millis);
			int steps = Integer.parseInt(stepsString);
			long diff = Math.abs(dt1Millis - dt2Millis) / steps;
			
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < steps; i++) {
				long currentMillis = minMillis + (long) steps * diff;
				DateTime dtCurrent = new DateTime(currentMillis, DateTimeZone.UTC);
				String[] currentTimestamp = new String[1];
				currentTimestamp[0] = TimeUtil.getUTCDateTimeString(dtCurrent.toLocalDateTime());
				builder.append(getDatabaseDataCluster(gridID, keyProperty, clusterIDs, time, stepsString, req, res));
				if (i < steps - 1) {
					builder.append(", ");
				}
			}
			
		}
		throw new ServletException("Time format unacceptable.");
	}
	
	private void getGeoJsonCluster(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String gridID = req.getParameter("gridID");
		String fusedClusterIDs = req.getParameter("clusterID");
		String keyProperty = req.getParameter("property");
		String fusedTime = req.getParameter("time");
		String[] clusterIDs = fusedClusterIDs.split(",");
		String[] time = fusedTime.split(",");
		String stepsString = req.getParameter("steps");
		
		String result = null;
		if (time[0] == null) {
			result = getLiveDataCluster(gridID, keyProperty, clusterIDs, req, res);
		} else {
			result = getDatabaseDataCluster(gridID, keyProperty, clusterIDs, time, stepsString, req, res);
		}
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
	    res.getWriter().write(result);
	}
	
	private GeoGrid getGrid(String gridID) throws ServletException {
		GeoGridManager gridManager = GeoGridManager.getInstance();
		if (!gridManager.isGridActive(gridID)) throw new ServletException("Grid is not active and therefore can not be fetched.");
		return gridManager.getGrid(gridID);
	}
	
	private String getLiveDataCluster(String gridID, String keyProperty, String[] clusterIDs, 
			HttpServletRequest req, HttpServletResponse res) throws ServletException {
		Collection<GeoPolygon> polygons = new HashSet<>();
		final GeoGrid grid = getGrid(gridID);
		for (int i = 0; i < clusterIDs.length; i++) {
			GeoPolygon polygon = null;
			try {
				polygon = grid.getPolygon(clusterIDs[i]);
			} catch (ClusterNotFoundException e) {
				throw new ServletException("Cluster is not registrated in this grid. " + e.getCluster());
			}
			if (polygon != null) {
				polygons.add(polygon);
			}
		}
		return GeoJsonConverter.convertPolygons(polygons, keyProperty);
	}
	
}
