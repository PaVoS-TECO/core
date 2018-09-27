package edu.teco.pavos.core.controller.process;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BasicProcess} provides simple implementations of the {@link ProcessInterface}.
 * For starting and stopping, this class uses {@link CountDownLatch}es.
 */
public abstract class BasicProcess implements ProcessInterface, Runnable {
	protected final String threadName = this.getClass().getName();
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected CountDownLatch countdownLatch = null;
	protected Thread thread;
	protected Properties props;
	
	@Override
	public boolean start() {
		logger.info("Starting thread: {}", threadName);
		if (thread == null) {
			thread = new Thread(this, threadName);

			countdownLatch = new CountDownLatch(1);
			thread.start();

			return true;
		}
		return false;
	}

	@Override
	public boolean stop() {
		logger.info("Closing thread: {}", threadName);
		if (countdownLatch != null) countdownLatch.countDown();
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				logger.warn("Interruption of thread: {}", threadName);
				thread.interrupt();
			}
			logger.info("Stopped thread successfully: {}", threadName);
			return true;
		}
		return false;
	}

}
