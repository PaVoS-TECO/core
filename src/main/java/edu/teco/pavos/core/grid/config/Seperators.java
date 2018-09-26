package edu.teco.pavos.core.grid.config;

import edu.teco.pavos.core.grid.GeoGrid;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;

/**
 * A utility class that provides seperators for different purposes.
 */
public final class Seperators {
	
	/**
	 * Seperates a {@link GeoGrid} from a {@link GeoPolygon}
	 */
	public static final String GRID_CLUSTER_SEPERATOR = ":";
	
	/**
	 * Seperates multiple {@link GeoPolygon}s
	 */
	public static final String CLUSTER_SEPERATOR = "-";
	
	/**
	 * Seperates the {@link GeoGrid} name from its properties
	 */
	public static final String GRIDID_GRIDPROPERTIES_SEPERATOR = "-";
	
	/**
	 * Seperates the {@link GeoGrid}s properties
	 */
	public static final String GRIDPROPERTIES_SEPERATOR = "_";
	
	/**
	 * Seperates the {@link GeoPolygon}s rows from its columns
	 */
	public static final String ROW_COLUMN_SEPERATOR = "_";
	
	/**
	 * Seperates the {@link GeoPolygon} from the sensor
	 */
	public static final String CLUSTER_SENSOR_SEPERATOR = ".";
	
	private Seperators() {
		
	}
	
}
