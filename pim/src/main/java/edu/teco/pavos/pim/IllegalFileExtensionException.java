package edu.teco.pavos.pim;

/**
 * Exception for unknown file extensions
 */
public class IllegalFileExtensionException extends Exception {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7953608563433481548L;

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
