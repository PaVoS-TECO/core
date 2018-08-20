package server.core.controller;

import server.core.command.StreamProcessingStrategy;
import server.export.ExportProperties;

/**
 * This Class is for The Processing of the Export Stream and it generates a
 * Output Stream
 */
public class ExportProcessStrategy implements StreamProcessingStrategy {

	/**
	 * Default constructor
	 */
	public ExportProcessStrategy() {
	}

	/**
	 * This is the default Contructer for the Export Process
	 * 
	 * @param props ExportProperties is the Properties Object for the Application
	 */
	public void ExportApplication(ExportProperties props) {
		// TODO implement here
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
	 * @return true if the Kafka Stream Started false otherwise
	 */
	public boolean kafkaStreamClose() {
		// TODO implement here
		return false;
	}

	/**
	 * This Methode definite the Process of the Application. What Application does
	 * specificly.
	 * 
	 * @return true if the Export Process got Successfully worked.
	 */
	public boolean apply() {
		// TODO implement here
		return false;
	}

}