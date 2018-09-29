package edu.teco.pavos.core.grid.geojson.data;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.teco.pavos.core.grid.geojson.GeoJsonBuilder;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;

/**
 * The GeoJson format for a {@link GeoPolygon} / Cluster.
 */
public class SensorGeoJson {
	
	private static final String COMMA = ",";
	private static final String COLON = ":";
	private String geoJson;
	
	private ArrayList<Double> value = new ArrayList<>();
	private final String sensorID;
	private final String geometry;
	
	/**
	 * Creates a new {@link SensorGeoJson}.
	 * @param value The value of the sensor at the current point of time
	 * @param sensorID The identifier of the sensor
	 * @param point The point defining the form of the sensor
	 */
	public SensorGeoJson(ArrayList<Double> value, String sensorID, Point2D.Double point) {
		this.value = value;
		this.sensorID = sensorID;
		this.geometry = buildGeometry(point);
	}
	
	/**
	 * A utility method.
	 * Returns the current cluster as GeoJson with the specified value
	 * instead of using the live value.
	 * @param value The value of the observationType
	 * @return geoJson {@link String}
	 */
	public String getArchivedGeoJson(ArrayList<Double> value) {
		return getGeoJsonDirectly(value);
	}
	
	private void update() {
		this.geoJson = getGeoJsonDirectly(this.value);
	}
	
	private String getGeoJsonDirectly(ArrayList<Double> value2) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(COMMA, GeoJsonBuilder.toSProperty("type", "Feature"),
				String.join(COLON, GeoJsonBuilder.toEntry("properties"), buildProperties(value2)),
				String.join(COLON, GeoJsonBuilder.toEntry("geometry"), geometry)
				));
		builder.append("}");
		return builder.toString();
	}
	
	private String buildGeometry(Point2D.Double point) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(COMMA, GeoJsonBuilder.toSProperty("type", "Point"),
				String.join(COLON, GeoJsonBuilder.toEntry("coordinates"), buildCoordinate(point))
				));
		builder.append("}");
		return builder.toString();
	}
	
	private String buildCoordinate(Point2D.Double point) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(String.join(COMMA, String.valueOf(point.getX()), String.valueOf(point.getY())));
		builder.append("]");
		return builder.toString();
	}
	
	private String buildProperties(ArrayList<Double> value2) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(COMMA, GeoJsonBuilder.toNProperty("value", value2.toString()),
				GeoJsonBuilder.toSProperty("sensorID", sensorID)));
		builder.append("}");
		return builder.toString();
	}
	
	/**
	 * @return the geoJson
	 */
	public String getGeoJson() {
		return geoJson;
	}

	/**
	 * @return the value
	 */
	public ArrayList<Double> getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(ArrayList<Double> value) {
		this.value = value;
		update();
	}
	
}
