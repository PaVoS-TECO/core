package server.transfer.sender.connection;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketManager {
	
	private Logger logger = LoggerFactory.getLogger(SocketManager.class);
	private String host;
	private int port;
	
	public void connect(Socket socket, String host, int port) {
		try {
			socket = new Socket(host, port);
		} catch (IOException e) {
			logger.error("Could not initialize socket.", e);
		}
	}
	
	public void reconnect(Socket socket) {
		if (socket != null) {
			connect(socket, this.host, this.port);
		} else {
			logger.error("Could not reconnect to socket. Socket is not initialized.");
		}
	}
	
}
