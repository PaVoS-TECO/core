package edu.teco.pavos.core.grid.geojson;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import edu.teco.pavos.core.grid.GeoGrid;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;
import edu.teco.pavos.transfer.data.ObservationData;

/**
 * The {@link GeoJsonConverter} provides simple methods to create
 * GeoJson {@link String}s with the {@link GeoJsonBuilder}.
 */
public final class GeoJsonConverter {
	
	private GeoJsonConverter() {
		
	}
	
	/**
	 * Converts a {@link GeoPolygon} to a GeoJson {@link String}.
	 * Only stores the specified observation-type.
	 * @param geoPolygon {@link GeoPolygon}
	 * @param observationType {@link String}
	 * @return geoJson {@link String}
	 */
	public static String convert(GeoPolygon geoPolygon, String observationType) {
		Collection<GeoPolygon> col = new ArrayList<>();
		col.add(geoPolygon);
		return convertPolygons(col, observationType);
	}
	
	/**
	 * Converts a {@link Collection} of {@link ObservationData} that resemble multiple {@link GeoPolygon}s
	 * to a GeoJson {@link String}.
	 * Only stores the specified observation-type.
	 * @param observations {@link Collection} of {@link ObservationData}
	 * @param observationType {@link String}
	 * @param grid {@link GeoGrid}
	 * @return geoJson {@link String}
	 */
	public static String convertPolygonObservations(Collection<ObservationData> observations,
			String observationType, GeoGrid grid) {
		GeoJsonBuilder builder = new GeoJsonBuilder(observationType, "polygon");
		builder.addDBClusterObservations(observations, grid);
		return builder.toString();
	}
	
	/**
	 * Converts a single {@link ObservationData} that resembles a sensor to a GeoJson {@link String}.
	 * Only stores the specified observation-type.
	 * @param observation {@link ObservationData}
	 * @param observationType {@link String}
	 * @param location {@link Point2D.Double}
	 * @return geoJson {@link String}
	 */
	public static String convertSensorObservations(ObservationData observation,
			String observationType, Point2D.Double location) {
		GeoJsonBuilder builder = new GeoJsonBuilder(observationType, "sensor");
		builder.addDBSensorObservation(observation, location);
		return builder.toString();
	}
	
	/**
	 * Converts multiple {@link GeoPolygon}s to a GeoJson {@link String}.
	 * Only stores the specified observation-type.
	 * @param geoPolygons {@link Collection} of {@link GeoPolygon}s
	 * @param observationType {@link String}
	 * @return geoJson {@link String}
	 */
	public static String convertPolygons(Collection<GeoPolygon> geoPolygons, String observationType) {
		GeoJsonBuilder builder = new GeoJsonBuilder(observationType, "polygon");
		builder.addGeoPolygons(geoPolygons);
		return builder.toString();
	}
	
}
