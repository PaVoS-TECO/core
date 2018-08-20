package server.core.command;

import java.sql.Connection;

/**
 * This Class handels the Requests and Replies to them
 */
public class Replier {

	/**
	 * Default constructor
	 */
	public Replier() {
	}

	/**
	 * This is the Session Object, its used to store IP Informations
	 */
	private Session session;

	/**
	 * This the message producer Object
	 */
	private MessageProducer messageProducer;

	/**
	 * This is the initialisation Method for the Replier to connect to different
	 * Requestors
	 * 
	 * @param connection       This is the Connection parameter, so taht the replier
	 *                         knows where he answers
	 * @param requestQueueName This a Simple name for the request Queue
	 */
	public void initialize(Connection connection, String requestQueueName) {
		// TODO implement here
	}

	/**
	 * This Methode triggers something in the System waht has to be done
	 * 
	 * @param message This is a simple Message parameter
	 * @param request This is the RequestCommand Object wich Contains the Real
	 *                request.
	 */
	public void onMessage(Message message, RequestCommand request) {
		// TODO implement here
	}

}