package edu.teco.pavos.core.grid.geojson;

/**
 * A utility class that lets you create a {@link String}
 * in GeoJson format which holds information about the
 * form, the values and the observation.
 */
public final class GeoJsonBuilder {
	
	private static final String SLASH = "\"";
	private static final String COLON = ":";
	
	private GeoJsonBuilder() {
		
	}
	
	/**
	 * name -> "name"
	 * @param name {@link String}
	 * @return entry {@link String}
	 */
	public static String toEntry(String name) {
		return String.join(name, SLASH, SLASH);
	}
	
	/**
	 * (key, value) -> "key":value
	 * @param key {@link String}
	 * @param value {@link String}
	 * @return nProperty {@link String}
	 */
	public static String toNProperty(String key, String value) {
		return String.join(COLON, toEntry(key), value);
	}
	
	/**
	 * (key, value) -> "key":"value"
	 * @param key {@link String}
	 * @param value {@link String}
	 * @return sProperty {@link String}
	 */
	public static String toSProperty(String key, String value) {
		return String.join(COLON, toEntry(key), toEntry(value));
	}
	
}
