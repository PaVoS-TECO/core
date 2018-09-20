package edu.teco.pavos.core.controller.process;

import org.apache.kafka.streams.StreamsBuilder;

import scala.collection.immutable.Stream.StreamBuilder;

/**
 * The {@link ProcessInterface} should be implemented by every process
 * that needs Apache-Kafka and its streams.
 */
public interface ProcessInterface {
	
	
	/**
	 * Starts the Kafka stream thread.
	 * @return true if the Kafka Stream Started false otherwise
	 */
	boolean kafkaStreamStart();

	/**
	 * Closes the Kafka Stream thread.
	 * @return true if the Kafka Stream closed, false otherwise
	 */
	boolean kafkaStreamClose();

	
	/**
	 * Runs the code of the process.
	 * @param builder The {@link StreamBuilder} to use
	 * @throws InterruptedException The process failed
	 */
	void execute(StreamsBuilder builder) throws InterruptedException;
	
	/**
	 * Runs the code of the process.
	 * @throws InterruptedException The process failed
	 */
	void execute() throws InterruptedException;
}
