package server.core.command;

/**
 * This Class is a Interface for the Stream Builder Applications which
 * genereates an Output topic to provides data transformations. The
 * ProcessingApplication will use Kafka DSL API to process the data.
 */
public interface StreamProcessingStrategy {

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
	 * 
	 * @return true if the Process got Successfully worked
	 */
	public boolean apply();

}