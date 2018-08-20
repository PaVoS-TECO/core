package server.core.command;

import java.sql.Connection;

/**
 * The Implemente this class and request something to the System and a Replier
 * answer to it.
 */
public class Requestor {

	/**
	 * Default constructor
	 */
	public Requestor() {
	}

	/**
	 * This is the Session Object, its used to store IP Informations
	 */
	private Session session;

	/**
	 * This is a List of the RequestCommands
	 */
	private MessageProducer requestProducer;

	/**
	 * This is a List of the RequestCommands which got worked off
	 */
	private MessageConsumer replyConsumer;

	/**
	 * @param connection This is the Connection parameter, so taht the repuestor
	 *                   knows where he requests something
	 */
	public void initialize(Connection connection) {
		// TODO implement here
	}

	/**
	 * @param request This is the RequestCommand Object wich Conntains the Real
	 *                request.
	 * @return true if the RequestCommand got send and false otherwise
	 */
	public boolean send(RequestCommand request) {
		// TODO implement here
		return false;
	}

	/**
	 * This Methode is there to got the Request again when it get lost or something
	 * 
	 * @param request It Returns the Requested RequestCommand
	 * @return A RequestCommand which contains a Request for a RequestCommand
	 */
	public RequestCommand receiveSync(RequestCommand request) {
		// TODO implement here
		return null;
	}

}