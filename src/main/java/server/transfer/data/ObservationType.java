package server.transfer.data;

/**
 * This {@link Enum} provides different properties and their types (temperature in Celsius OR Fahrenheit),
 * while encapsulating the path name that will be used for internal data-management.
 */
public enum ObservationType {
	
	/**
	 * The amount of particulate matter.
	 * Measured in the pM10 standard.
	 */
	PARTICULATE_MATTER_PM10("pm_10"),
	
	/**
	 * The amount of particulate matter.
	 * Measured in the pM2.5 standard.
	 */
	PARTICULATE_MATTER_PM2P5("pm_2p5"),
	
	/**
	 * The temperature.
	 * Measured in celsius.
	 */
	TEMPERATURE_C("temperature_celsius"),
	
	/**
	 * The temperature.
	 * Measured in fahrenheit.
	 */
	TEMPERATURE_F("temperature_fahrenheit"),
	
	/**
	 * The UV radiation.
	 * Measured by index.
	 */
	UV_INDEX("uv_index"),
	
	/**
	 * The wind.
	 * Measured in kilometers per hour.
	 */
	WIND_SPEED("windspeed_kmh"),
	
	/**
	 * The rainfall.
	 * Measured in millimeters.
	 */
	RAINFALL("rainfall_mm"),
	
	/**
	 * The amount of lightning.
	 * Measured by count.
	 */
	LIGHTNING("lightning_count");
	
	private final String value;
	
	/**
	 * Sets a value to the enum.
	 * @param value {@link String}
	 */
    ObservationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
