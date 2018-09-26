package edu.teco.pavos.core.grid.geojson.data;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.teco.pavos.core.grid.geojson.GeoJsonBuilder;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;

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
	 * @param subPolygons The sub-{@link GeoPolygon}s of the cluster
	 * @param points The points defining the form of the cluster
	 */
	public ClusterGeoJson(String value, String clusterID, List<GeoPolygon> subPolygons,
			Collection<Point2D.Double> points) {
		this.value = value;
		this.clusterID = clusterID;
		this.content = buildContent(subPolygons);
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
	
	private String buildContent(List<GeoPolygon> subPolygons) {
		Collection<String> dqColl = new ArrayList<>();
		subPolygons.forEach(subPolygon -> dqColl.add(GeoJsonBuilder.toEntry(subPolygon.getID())));
		return dqColl.toString();
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
		builder.append("[");
		Collection<String> coordinates = new ArrayList<>();
		points.forEach(point -> coordinates.add(buildCoordinate(point)));
		coordinates.add(buildCoordinate(points.iterator().next()));
		builder.append(coordinates.toString());
		builder.append("]");
		return builder.toString();
	}
	
	private String buildCoordinate(Point2D.Double point) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(String.join(COMMA, String.valueOf(point.getX()), String.valueOf(point.getY())));
		builder.append("]");
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
