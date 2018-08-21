package server.core.grid.converter;

import java.awt.geom.Point2D;
import java.util.Collection;

public final class GeoJsonConverter {
	
	private GeoJsonConverter() {
		
	}
	
	public static String convert(Collection<Point2D.Double> points) {
		String result = "{ \"type\": \"FeatureCollection\", \"features\": [ { \"type\": \"Feature\", \"properties\": {},"
				+ " \"geometry\": { \"type\": \"Polygon\", \"coordinates\": [ [ ";
		int index = 1;
		for (Point2D.Double point : points) {
			result = result + "[ " + point.getX() + ", " + point.getY() + "]";
			if (index < points.size()) {
				result = result + ",";
			}
			result = result + " ";
			index++;
		}
		result = result + "] ] } } ] }";
		return result;
	}
	
}
