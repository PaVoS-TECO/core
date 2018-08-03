package server.transfer.data;

/**
 * This {@link Enum} provides different properties and their types (temperature in Celsius OR Fahrenheit),
 * while encapsulating the path name that will be used for internal data-management.
 */
public enum ObservationType {
	PARTICULATE_MATTER_PM10("particulateMatter_PM10"),
	PARTICULATE_MATTER_PM2P5("particulateMatter_PM2p5"),
	TEMPERATURE_C("temperature_C"),
	TEMPERATURE_F("temperature_F"),
	UV_INDEX("uvIndex"),
	WIND_SPEED("wind_speed"),
	RAINFALL("rainfall"),
	LIGHTNING("lightning");
	
	private final String val;

    private ObservationType(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
