package server.transfer.sender.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code SocketManager} class manages basic operations that require a {@link Socket}.
 * It encapsulates management of a connection and stores data, so you can reconnect at any given time,
 * as well as providing information about the socket.
 */
public class SocketManager {
	
	private Logger logger = LoggerFactory.getLogger(SocketManager.class);
	private String host;
	private int port;
	private Socket socket;
	
	/**
	 * Connects to the address that is described via the host and port.
	 * @param host {@link String}
	 * @param port {@link int}
	 */
	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		
		try {
			socket = new Socket(host, port);
		} catch (IOException e) {
			logger.error("Could not initialize socket to Graphite. Using internal Socket to prevent failure", e);
			socket = new Socket();
		}
	}
	
	/**
	 * Reconnects to the previously connected address.
	 */
	public void reconnect() {
		connect(this.host, this.port);
	}
	
	/**
	 * Closes the socket.
	 */
	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			logger.error("Could not close socket.", e);
		}
	}
	
	/**
	 * Returns {@code true}, if the connetion is closed.
	 * @return isConnectionClosed {@link boolean}
	 */
	public boolean isConnectionClosed() {
		return socket.isClosed();
	}
	
	/**
	 * Returns the {@link OutputStream} of the {@link Socket}.
	 * @return outputStream {@link OutputStream}
	 */
	public OutputStream getOutputStream() {
		try {
			return socket.getOutputStream();
		} catch (IOException e) {
			logger.error("Failed creating OutputStream.", e);
			return null;
		}
	}
	
}
