package server.transfer.generator;

import java.awt.geom.Point2D;
import java.util.concurrent.TimeUnit;

import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class RuntimeGenerator {
	
	private static GeoGrid grid = new GeoRecRectangleGrid(new Point2D.Double(WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
	
	public static void main(String[] args) {
		
		Thread t = new Thread(new Runnable() {

			public void run() {
				
					while (true) {
						grid.addObservation(randomLocation(), generateRandom(randomSensor(), "temperature_celsius", 40.0));
						sleep(2);
						grid.addObservation(randomLocation(), generateRandom(randomSensor(), "pM_10", 40.0));
						sleep(2);
					}
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	private static void sleep(long timeoutSeconds) {
		try {
			TimeUnit.SECONDS.sleep(timeoutSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static String randomSensor() {
		int num = (int) (Math.random() * 99999);
		return "sensor" + num;
	}
	
	private static Point2D.Double randomLocation() {
		double x = Math.random() * grid.MAP_BOUNDS.getX();
		double y = Math.random() * grid.MAP_BOUNDS.getY();
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
