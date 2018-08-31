package server.core.grid;

import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import server.core.grid.config.Seperators;
import server.core.grid.config.WorldMapData;
import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.exceptions.SensorNotFoundException;
import server.core.grid.geojson.GeoJsonConverter;
import server.core.grid.polygon.GeoPolygon;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class GeoRectangleGridTest {

	@Test
	public void test() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(- WorldMapData.lngRange, - WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);
		
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		data.sensorID = "testSensorID1";
		String property = "temperature_celsius";
		data.observations.put(property, "14.0");
		
		Point2D.Double location1 = new  Point2D.Double(300.0, 80.0);
		grid.addObservation(location1, data);
		
		data = new ObservationData();
		data.sensorID = "testSensorID2";
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		data.observations.put(property, "28.0");
		
		Point2D.Double location2 = new  Point2D.Double(260.0, 80.0);
		grid.addObservation(location2, data);
		
		data = new ObservationData();
		data.sensorID = "testSensorID3";
		data.observations.put(property, "28.0");
		
		Point2D.Double location3 = new  Point2D.Double(260.0, 80.0);
		grid.addObservation(location3, data);
		
		try {
			data = grid.getSensorObservation("testSensorID2", location2);
		} catch (PointNotOnMapException | SensorNotFoundException e) {
			fail(e.getMessage());
		}
		System.out.println(data.observationDate);
		System.out.println(GeoJsonConverter.convertSensorObservations(data, property, new  Point2D.Double(260.0, 80.0)));
		
		String clusterID = null;
		GeoPolygon poly = null;
		try {
			poly = grid.getPolygonContaining(location1, grid.maxLevel);
			clusterID = poly.id;
		} catch (PointNotOnMapException e) {
			fail("Location out of map bounds.");
		}
		System.out.println("ClusterID: " + clusterID);
		System.out.println("Total sensors: " + poly.getNumberOfSensors());
		System.out.println("Sensors with property '" + property + "': " + poly.getNumberOfSensors(property));
		System.out.println(observationsToString(poly.getSensorDataList()));
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons0 = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons0) {
				Collection<GeoPolygon> subPolygons1 = poly1.getSubPolygons();
				System.out.println(observationToString(poly1.cloneObservation()));
				for (GeoPolygon poly2 : subPolygons1) {
					System.out.println(observationToString(poly2.cloneObservation()));
				}
			}
		}
		
		System.out.println();
		grid.updateObservations();
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons0 = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons0) {
				Collection<GeoPolygon> subPolygons1 = poly1.getSubPolygons();
				System.out.println(observationToString(poly1.cloneObservation()));
				for (GeoPolygon poly2 : subPolygons1) {
					System.out.println(observationToString(poly2.cloneObservation()));
				}
			}
		}
		
		GeoPolygon jsonPoly = null;
		try {
			jsonPoly = grid.getPolygon(grid.id + Seperators.GRID_CLUSTER_SEPERATOR + "0_1");
		} catch (ClusterNotFoundException e) {
			fail(e.getMessage());
		}
		System.out.println(jsonPoly.getJson(property));
		
		ObservationData dataClone = jsonPoly.cloneObservation();
		dataClone.observations.put(property, "10.799999999999998");
		LocalDateTime ldt = TimeUtil.getUTCDateTime(dataClone.observationDate);
		LocalDateTime subtracted = ldt.minusYears(20);
		dataClone.observationDate = TimeUtil.getUTCDateTimeString(subtracted);
		Collection<ObservationData> observations = new HashSet<>();
		observations.add(dataClone);
		System.out.println(GeoJsonConverter.convertPolygonObservations(observations, property, grid));
		
		Collection<ObservationData> observations2 = grid.getGridObservations();
		for (ObservationData data2 : observations2) {
			System.out.println(data2.clusterID);
			System.out.println(data2.observations.get(property));
		}
		
	}
	
	private String observationToString(ObservationData data) {
		return "ObservationData: " + data.observationDate + ", " + data.sensorID + ", " + data.clusterID + ", " + data.observations;
	}
	
	private String observationsToString(Collection<ObservationData> collection) {
		String result = "";
		for (ObservationData data : collection) {
			result = result + observationToString(data) + "\n";
		}
		return result;
	}

}
