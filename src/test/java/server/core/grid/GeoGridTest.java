package server.core.grid;

import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import server.core.grid.config.WorldMapData;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.polygon.GeoPolygon;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class GeoGridTest {

	@Test
	public void test() {
		GeoGrid grid = new GeoRectangleGrid(new Point2D.Double(WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 1, "testGrid");
		
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeString();
		data.sensorID = "testSensorID";
		String property = "temperature_celsius";
		data.observations.put(property, "28.0");
		
		Point2D.Double location = new  Point2D.Double(300.0, 80.0);
		grid.addObservation(location, data);
		String clusterID = null;
		GeoPolygon poly = null;
		try {
			poly = grid.getPolygonContaining(location, grid.MAX_LEVEL);
			clusterID = poly.ID;
		} catch (PointNotOnMapException e) {
			fail("Location out of map bounds.");
		}
		System.out.println("ClusterID: " + clusterID);
		System.out.println("Total sensors: " + poly.getNumberOfSensors());
		System.out.println("Sensors with property '" + property + "': " + poly.getNumberOfSensors(property));
		System.out.println(observationsToString(poly.getSensorDataList()));
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons) {
				System.out.println(observationToString(poly1.cloneObservation()));
			}
		}
		
		grid.updateObservations();
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons) {
				System.out.println(observationToString(poly1.cloneObservation()));
			}
		}
	}
	
	private String observationToString(ObservationData data) {
		return "ObservationData: " + data.observationDate + ", " + data.sensorID + ", " + data.clusterID + ", " + data.observations;
	}
	
	private String observationsToString(List<ObservationData> list) {
		String result = "";
		for (ObservationData data : list) {
			result = result + observationToString(data) + "\n";
		}
		return result;
	}

}
