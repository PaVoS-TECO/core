package edu.teco.pavos.exporter;

/**
 * Exception for unknown file extensions
 */
public class IllegalFileExtensionException extends Exception {

	/**
	 * Constructor
	 * @param message for the Exception
	 */
	public IllegalFileExtensionException(String message) {
        super(message);
    }
	
	/**
	 * Other constructor
	 * @param message for the Exception
	 * @param throwable for the Exception
	 */
    public IllegalFileExtensionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}