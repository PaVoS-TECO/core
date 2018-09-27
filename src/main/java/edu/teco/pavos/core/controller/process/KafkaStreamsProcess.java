package edu.teco.pavos.core.controller.process;

import java.util.concurrent.CountDownLatch;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;

/**
 * The {@link KafkaStreamsProcess} provides simple implementations of the {@link ProcessInterface}
 * as it extends {@link BasicProcess}.
 * For starting and stopping, this class uses {@link CountDownLatch}es.
 * Uses Apache-Kafkas streams, which will be closed upon closing this process.
 */
public abstract class KafkaStreamsProcess extends BasicProcess {
	protected KafkaStreams kafkaStreams;
	
	@Override
	public void run() {
		logger.info("Running thread: {}", threadName);
		StreamsBuilder builder = new StreamsBuilder();

		try {
			execute(builder);
		} catch (InterruptedException e) {
			logger.error("Thread: {} was interrupted!", threadName);
			Thread.currentThread().interrupt();
		}
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();

	}
	
	@Override
	public boolean stop() {
		logger.info("Closing thread: {}", threadName);
		if (countdownLatch != null) countdownLatch.countDown();
		if (thread != null) {
			try {
				thread.join();
				if (kafkaStreams == null) {
					logger.info("Applikation 'Export' is not Running");
					return false;
				}
				Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
			} catch (InterruptedException e) {
				thread.interrupt();
				logger.warn("Interruption of thread: {}", threadName);
			}
			logger.info("Stopped thread successfully: {}", threadName);
			return true;

		}
		return false;
	}
	
}
