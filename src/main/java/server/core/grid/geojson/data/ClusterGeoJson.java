package server.core.grid.geojson.data;

import java.awt.geom.Point2D;
import java.util.Collection;

import server.core.grid.geojson.GeoJsonBuilder;
import server.core.grid.polygon.GeoPolygon;

/**
 * The GeoJson format for a {@link GeoPolygon} / Cluster.
 */
public class ClusterGeoJson {
	
	private static final String COMMA = ",";
	private static final String COLON = ":";
	private String geoJson;
	
	private String value;
	private final String clusterID;
	private final String content;
	private final String geometry;
	
	/**
	 * Creates a new {@link ClusterGeoJson}.
	 * @param value The value of the cluster at the current point of time
	 * @param clusterID The identifier of the cluster
	 * @param content The sub-{@link GeoPolygon}s of the cluster
	 * @param points The points defining the form of the cluster
	 */
	public ClusterGeoJson(String value, String clusterID, String content,
			Collection<Point2D.Double> points) {
		this.value = value;
		this.clusterID = clusterID;
		this.content = content;
		this.geometry = buildGeometry(points);
	}
	
	/**
	 * A utility method.
	 * Returns the current cluster as GeoJson with the specified value
	 * instead of using the live value.
	 * @param value The value of the observationType
	 * @return geoJson {@link String}
	 */
	public String getArchivedGeoJson(String value) {
		return getGeoJsonDirectly(value);
	}
	
	private void update() {
		this.geoJson = getGeoJsonDirectly(this.value);
	}
	
	private String getGeoJsonDirectly(String value) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(COMMA, GeoJsonBuilder.toSProperty("type", "Feature"),
				String.join(COLON, GeoJsonBuilder.toEntry("properties"), buildProperties(value)),
				String.join(COLON, GeoJsonBuilder.toEntry("geometry"), geometry)
				));
		builder.append("}");
		return builder.toString();
	}
	
	private String buildGeometry(Collection<Point2D.Double> points) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(COMMA, GeoJsonBuilder.toSProperty("type", "Polygon"),
				String.join(COLON, GeoJsonBuilder.toEntry("coordinates"), buildCoordinates(points))
				));
		builder.append("}");
		return builder.toString();
	}

	private String buildCoordinates(Collection<Point2D.Double> points) {
		StringBuilder builder = new StringBuilder();
		builder.append("[[");
		points.forEach((point) -> {
			builder.append("[");
			builder.append(String.join(COMMA, String.valueOf(point.getX()), String.valueOf(point.getY())));
			builder.append("]");
		});
		builder.append("]]");
		return builder.toString();
	}

	private String buildProperties(String value) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(COMMA, GeoJsonBuilder.toNProperty("value", value),
				GeoJsonBuilder.toSProperty("clusterID", clusterID),
				String.join(COLON, GeoJsonBuilder.toEntry("content"), content)
				));
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
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
		update();
	}
	
}
