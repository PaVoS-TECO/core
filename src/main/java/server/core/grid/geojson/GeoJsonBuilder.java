package server.core.grid.geojson;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoGrid;
import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.polygon.GeoPolygon;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

/**
 * A utility class that lets you create a {@link String}
 * in GeoJson format which holds information about the
 * form, the values and the observation.
 */
public final class GeoJsonBuilder {
	
	private static final String COMMA = ", ";
	private final String observationType;
	private final String format;
	private String ldtString;
	private StringBuilder builder;
	private StringBuilder polygonsBuilder;
	private StringBuilder sensorsBuilder;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Creates a new {@link GeoJsonBuilder}.
	 * @param observationType {@link String}
	 * @param format {@link String}
	 */
	public GeoJsonBuilder(String observationType, String format) {
		this.observationType = observationType;
		this.format = format;
		
		this.builder = new StringBuilder();
		this.polygonsBuilder = new StringBuilder();
		this.sensorsBuilder = new StringBuilder();
	}
	
	/**
	 * Adds {@link GeoPolygon}s in form of a {@link Collection} of {@link ObservationData}.
	 * @param observations {@link Collection} of {@link ObservationData}
	 * @param grid {@link GeoGrid}
	 */
	public void addDBClusterObservations(Collection<ObservationData> observations, GeoGrid grid) {
		Collection<GeoPolygon> geoPolygons = new HashSet<>();
		StringBuilder polyBuilder = new StringBuilder();
		
		int countFeature = 1;
		for (ObservationData data : observations) {
			GeoPolygon geoPolygon;
			try {
				geoPolygon = grid.getPolygon(data.clusterID);
				polyBuilder.append(geoPolygonToStringQuick(data, geoPolygon.getSubPolygons(), geoPolygon.getPoints()));
				if (countFeature < geoPolygons.size()) {
					polyBuilder.append(COMMA);
				}
			} catch (ClusterNotFoundException e) {
				logger.warn("Could not find cluster: " + e.getCluster() 
				+ ". Decided to skip the cluster and continue the json-building process.", e);
			}
		}
			
		this.polygonsBuilder.append(polyBuilder.toString());
	}
	
	/**
	 * Adds a sensor in form of a single {@link ObservationData}.
	 * @param observation {@link ObservationData}
	 * @param location {@link Point2D.Double}
	 */
	public void addDBSensorObservation(ObservationData observation, Point2D.Double location) {
		StringBuilder sensorBuilder = new StringBuilder();
		
		sensorBuilder.append(geoSensorToStringQuick(observation, location));
			
		this.sensorsBuilder.append(sensorBuilder.toString());
	}
	
	private String geoSensorToStringQuick(ObservationData data, Double point) {
		LocalDateTime ldt = TimeUtil.getUTCDateTime(data.observationDate);
		if (ldtString == null || ldt.isAfter(TimeUtil.getUTCDateTime(ldtString))) {
			ldtString = data.observationDate;
		}
		
		StringBuilder polyBuilder = new StringBuilder();
		polyBuilder.append("{ " + toSProperty("type", "Feature") + COMMA);
		polyBuilder.append(toEntry("properties") + ": { ");
		polyBuilder.append(toNProperty("value", data.observations.get(observationType)) + COMMA);
		polyBuilder.append(toSProperty("sensorID", data.sensorID));
		
		polyBuilder.append("}" + COMMA);
		polyBuilder.append(toEntry("geometry") + ": { ");
		polyBuilder.append(toSProperty("type", "Point") + COMMA);
		polyBuilder.append(toEntry("coordinates") + ": ");
		polyBuilder.append("[ " + point.getX() + COMMA + point.getY() + "]");
		polyBuilder.append("} }");
		return polyBuilder.toString();
	}

	public void addGeoPolygons(Collection<GeoPolygon> geoPolygons) {
		StringBuilder polyBuilder = new StringBuilder();
		
		int countFeature = 1;
		for (GeoPolygon geoPolygon : geoPolygons) {
			polyBuilder.append(geoPolygonToString(geoPolygon));
			if (countFeature < geoPolygons.size()) {
				polyBuilder.append(COMMA);
			}
			countFeature++;
		}
		this.polygonsBuilder.append(polyBuilder.toString());
	}
	
	@Override
	public String toString() {
		if (format.equals("polygon")) {
			return buildPolygon();
		} else if (format.equals("sensor")) {
			return buildSensor();
		}
		throw new NullPointerException("No building type selected for GeoJsonBuilder.");
	}
	
	private String buildPolygon() {
	builder.append("{ " + toSProperty("type", "FeatureCollection") + COMMA);
	builder.append(toSProperty("timestamp", ldtString) + COMMA);
	builder.append(toSProperty("observationType", observationType) + COMMA);
	builder.append(toEntry("features") + ": [ ");
	builder.append(polygonsBuilder.toString());
	builder.append("] }");
	return builder.toString();
	}
	
	private String buildSensor() {
		builder.append("{ " + toSProperty("type", "FeatureCollection") + COMMA);
		builder.append(toSProperty("timestamp", ldtString) + COMMA);
		builder.append(toSProperty("observationType", observationType) + COMMA);
		builder.append(toEntry("features") + ": [ ");
		builder.append(sensorsBuilder.toString());
		builder.append("] }");
		return builder.toString();
		}
	
	private String geoPolygonToString(GeoPolygon geoPolygon) {
		return geoPolygonToStringQuick(geoPolygon.cloneObservation(), geoPolygon.getSubPolygons(), geoPolygon.getPoints());
	}
	
	private String geoPolygonToStringQuick(ObservationData data, List<GeoPolygon> subPolygons, List<Point2D.Double> points) {
		LocalDateTime ldt = TimeUtil.getUTCDateTime(data.observationDate);
		if (ldtString == null || ldt.isAfter(TimeUtil.getUTCDateTime(ldtString))) {
			ldtString = data.observationDate;
		}
		
		StringBuilder polyBuilder = new StringBuilder();
		polyBuilder.append("{ " + toSProperty("type", "Feature") + COMMA);
		polyBuilder.append(toEntry("properties") + ": { ");
		polyBuilder.append(toNProperty("value", data.observations.get(observationType)) + COMMA);
		polyBuilder.append(toSProperty("clusterID", data.clusterID) + COMMA);
		polyBuilder.append(toEntry("content") + ": [ ");
		int count = 1;
		for (GeoPolygon sub2Polygon : subPolygons) {
			polyBuilder.append(toEntry(sub2Polygon.id));
			if (count < subPolygons.size()) {
				polyBuilder.append(COMMA);
			}
			count++;
		}
		polyBuilder.append("] }" + COMMA);
		polyBuilder.append(toEntry("geometry") + ": { ");
		polyBuilder.append(toSProperty("type", "Polygon") + COMMA);
		polyBuilder.append(toEntry("coordinates") + ": [ [ ");
		count = 1;
		Point2D.Double tempPoint = null;
		for (Point2D.Double point : points) {
			polyBuilder.append("[ " + point.getX() + COMMA + point.getY() + "]");
			if (tempPoint == null) {
				tempPoint = point;
			}
			polyBuilder.append(COMMA);
			count++;
		}
		if (tempPoint != null) polyBuilder.append("[ " + tempPoint.getX() + COMMA + tempPoint.getY() + "]");
		polyBuilder.append("] ] } }");
		return polyBuilder.toString();
	}
	
	private String toEntry(String name) {
		return "\"" + name + "\"";
	}
	
	private String toNProperty(String key, String value) {
		return toEntry(key) + ": " + value;
	}

	private String toSProperty(String key, String value) {
		return toEntry(key) + ": " + toEntry(value);
	}
	
}
