package server.core.controller;

import server.core.command.StreamProcessingStrategy;

/**
 * This Class is for The Processing of the Data for Graphite, to represente the
 * Sensors. It Generates a Graphite Output Stream
 */
public class GraphiteProcessStrategy implements StreamProcessingStrategy {

	/**
	 * Default constructor
	 */
	public GraphiteProcessStrategy() {
	}

	/**
	 * This Method is used to explicitly start the Kafka Stream thread. So that
	 * theProcessing need to get started.
	 * 
	 * @return true if the Kafka Stream Started false otherwise
	 */
	public boolean kafkaStreamStart() {
		// TODO implement here
		return false;
	}

	/**
	 * This Method is used to explicitly close the Kafka Stream thread. So that the
	 * Processing stops.
	 * 
	 * @return true if the Kafka Stream closed, false otherwise
	 */
	public boolean kafkaStreamClose() {
		// TODO implement here
		return false;
	}

	/**
	 * This Methode definite the Process of the Application. What Application does
	 * specificly.
	 * 
	 * @return true if the Graphite Process got Successfully worked
	 */
	public boolean apply() {
		// TODO implement here
		return false;
	}

}