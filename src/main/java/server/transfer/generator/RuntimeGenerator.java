package server.transfer.generator;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class RuntimeGenerator {
	
	private static GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, - WorldMapData.latRange, WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
	
	public static void main(String[] args) {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			grid.addObservation(randomLocation(), generateRandom(randomSensor(), "temperature_celsius", 40.0));
			grid.addObservation(randomLocation(), generateRandom(randomSensor(), "pM_10", 40.0));
		}, 0, 8, TimeUnit.SECONDS);
	}
	
	private static String randomSensor() {
		int num = new Random().nextInt(99999);
		return "sensor" + num;
	}
	
	private static Point2D.Double randomLocation() {
		double x = Math.random() * grid.mapBounds.getX();
		double y = Math.random() * grid.mapBounds.getY();
		return new Point2D.Double(x, y);
	}
	
	private static ObservationData generateRandom(String sensorID, String property, double max) {
		double value = Math.random() * max;
		return generate(sensorID, property, String.valueOf(value));
	}
	
	private static ObservationData generate(String sensorID, String property, String value) {
		ObservationData result = new ObservationData();
		result.observationDate = TimeUtil.getUTCDateTimeNowString();
		result.sensorID = sensorID;
		result.observations.put(property, value);
		return result;
	}
	
}
