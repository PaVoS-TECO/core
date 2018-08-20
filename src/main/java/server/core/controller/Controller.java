package server.core.controller;

import server.core.command.RequestCommand;
import server.core.command.StreamProcessingStrategy;
import server.core.properties.PropertiesFileInterface;

/**
 * This Class is the ControllerClass which manages the Requests and start new
 * TopologyBuilders to start new Processing Application.
 */
public class Controller {

	/**
	 * Default constructor
	 */
	public Controller() {
	}

	/**
	 * This Method process the single Reuqest form the View
	 * 
	 * @param command command is Instance of the RequestCommand Interface which
	 *                contains a Job Request
	 */
	public void workRequest(RequestCommand command) {
		// TODO implement here
	}

	/**
	 * This Method initialise the Controler
	 * 
	 * @return true when the initialise was successful and false otherwise
	 */
	public boolean init() {
		// TODO implement here
		return false;
	}

	/**
	 * Thsi Method starts a TopolgyBuilder to start a Kafka Stream Process.
	 * 
	 * @param process     process name of the Process Application
	 * @param inputTopic  inputTopic of the Kafka Topic
	 * @param outputTopic outputTopic of the Kafka Topic
	 */
	public void setTopolgyBuilder(StreamProcessingStrategy process, String inputTopic, String outputTopic) {
		// TODO implement here
	}

	/**
	 * This Method generates a Output Topic, which uses a ProcessApplikation as
	 * OutputSink. This will use Apache Avro Format.
	 * 
	 * @param topic topic name of the new Topic in Kafka
	 * @return true when the Output Topic got successful generated
	 */
	public boolean generateOutputtopic(String topic) {
		// TODO implement here
		return false;
	}

	/**
	 * This Method sets the Properties File
	 * 
	 * @param props props is the Propertyfile form where the controller reads his
	 *              Settings
	 */
	public void setProperties(PropertiesFileInterface props) {
		// TODO implement here
	}

	/**
	 * This method subscribe the controller to the Input Kafka Stream
	 * 
	 * @param topic The Name of the Topic which you want to subscribe
	 */
	public void subscribe(String topic) {
		// TODO implement here
	}

}