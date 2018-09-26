package edu.teco.pavos.core.web;

import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link WebServer}
 */
public class WebServerTest {
	
	private static volatile WebServer server;
	
	/**
	 * Sets everything up before every method.
	 */
	@Before
	public void beforeTest() {
		server = new WebServer();
	}
	
	/**
	 * Tests the servers reaction to a forced shutdown outside of its {@link Thread}.
	 */
	@Test
	public void testForcedShutdown() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(server);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		server.close();
	}

}
