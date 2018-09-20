package edu.teco.pavos.core.grid.exceptions;

/**
 * This {@link Exception} is thrown when a sensor is currently not registrated.
 * This can be due to cleanup of old data.
 */
public class SensorNotFoundException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private final String sensorID;
	
	/**
	 * Creates a new {@link SensorNotFoundException}.
	 * @param sensorID {@link String}
	 */
	public SensorNotFoundException(String sensorID) {
		this.sensorID = sensorID;
	}
	
	/**
	 * Returns the identifier of the sensor that caused this {@link Exception}.
	 * @return sensorID {@link String}
	 */
	public String getSensor() {
		return this.sensorID;
	}
	
}
