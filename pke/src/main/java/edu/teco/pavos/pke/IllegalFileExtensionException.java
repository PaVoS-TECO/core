package edu.teco.pavos.pke;

/**
 * Exception for unknown file extensions
 * @author Jean Baumgarten
 */
public class IllegalFileExtensionException extends Exception {

	private static final long serialVersionUID = 9074699396552789496L;

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