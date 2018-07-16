package gui.importer;

/**
 * Exception for unknown file extensions
 */
public class IllegalFileExtensionException extends Exception {

	/**
	 * Constructor
	 * @param message
	 */
	public IllegalFileExtensionException(String message) {
        super(message);
    }

	/**
	 * Other constructor
	 * @param message
	 * @param throwable
	 */
    public IllegalFileExtensionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
