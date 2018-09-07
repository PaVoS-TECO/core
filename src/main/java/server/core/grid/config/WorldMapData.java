package server.core.grid.config;

/**
 * A utility class that provides general data about the world-map.
 * In this case, the map bounds of the Leaflet-map are stored here as a fail-save.
 */
public final class WorldMapData {
	
	/**
	 * The map range (longitude)
	 */
	public static final double LNG_RANGE = 180.0;
	
	/**
	 * The map range (latitude)
	 */
	public static final double LAT_RANGE = 85.0;
	
	private WorldMapData() {
		
	}

}
