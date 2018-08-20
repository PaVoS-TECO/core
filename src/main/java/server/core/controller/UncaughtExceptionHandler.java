package server.core.controller;

/**
 * To catch any unexpected exceptions, you can set before you start the
 * application. This handler is called whenever a stream thread is terminated by
 * an unexpected exception.
 */
public class UncaughtExceptionHandler {

	/**
	 * Default constructor
	 */
	public UncaughtExceptionHandler() {
	}

	/**
	 * Returns the detail message string of this throwable.
	 * 
	 * @return String with the error Message
	 */
	public String getMessage() {
		// TODO implement here
		return "";
	}

}