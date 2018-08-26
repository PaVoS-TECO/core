package server.core.grid.exceptions;

/**
 * This {@link Exception} is thrown when a sensor is currently not registrated.
 * This can be due to cleanup of old data.
 */
public class SensorNotFoundException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private String sensorID;
	
	public SensorNotFoundException(String sensorID) {
		this.sensorID = sensorID;
	}
	
	public String getSensor() {
		return this.sensorID;
	}
	
}
