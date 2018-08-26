package server.core.controller.Process;

import org.apache.kafka.streams.StreamsBuilder;

public interface ProcessInterface {
	
	
	/**
	 * This Method is used to explicitly start the Kafka Stream thread. So that
	 * theProcessing need to get started.
	 * 
	 * @return true if the Kafka Stream Started false otherwise
	 */
	public boolean kafkaStreamStart();

	/**
	 * This Method is used to explicitly close the Kafka Stream thread. So that the
	 * Processing stops.
	 * 
	 * @return true if the Kafka Stream closed, false otherwise
	 */
	public boolean kafkaStreamClose();

	
	/**
	 * This Methode definite the Process of the Application. What Application does
	 * specificly.
	 * @param builder is the used Streambuilder
	 * 
	 * @return true if the Process got Successfully worked
	 */
	public void apply(StreamsBuilder builder);
}
