package server.core.web;

import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer implements Runnable {

	private static final int PORT = 7700;
	private static final int BACKLOG = 10000;
	private volatile boolean shutdown = false;
	private static Logger logger = LoggerFactory.getLogger(WebServer.class);
	
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
			logger.info(String.format("WebServer for GeoGrid-informations has started. IP=%s PORT=%s", serverSocket.getInetAddress(), PORT));
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
