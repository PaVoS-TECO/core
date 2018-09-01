package server.core.web;

import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerFactory;

public class WebServer implements Runnable {

	private static final int PORT = 7700;
	private static final int BACKLOG = 10000;
	private boolean shutdown = false;
	private static Logger logger = new Log4jLoggerFactory().getLogger(WebServer.class.toString());
	
	public WebServer() {
		
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.run();
	}
	
	@Override
	public void run() {
		shutdown = false;
		try (ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG)) {
			while (!shutdown) {
				processClients(serverSocket);
			}
		} catch (Exception e) {
			logger.error("Server-socket closed with an exception.", e);
		}
	}
	
	private void processClients(ServerSocket serverSocket) {
		try {
			Thread t = new Thread(new WebWorker(serverSocket.accept()));
			t.start();
		} catch (Exception e) {
			logger.error("Client-socket closed with an exception.", e);
		}
	}
	
	public void close() {
		shutdown = true;
	}

}
