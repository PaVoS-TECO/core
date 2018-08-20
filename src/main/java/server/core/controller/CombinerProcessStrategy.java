package server.core.controller;

import server.core.command.StreamProcessingStrategy;
import server.database.ZoomLevel;

/**
 * This Class does combinate the Clusters to bigger Cluster for the Different
 * Zoom Levels
 */
public class CombinerProcessStrategy implements StreamProcessingStrategy {

	/**
	 * Default constructor
	 */
	public CombinerProcessStrategy() {
	}

	/**
	 * This Attribute definietes the Zoomlevel of the Cluster
	 */
	private ZoomLevel zoom;

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
	 * @return true if the Combiner Process got Successfully worked
	 */
	public boolean apply() {
		// TODO implement here
		return false;
	}

}