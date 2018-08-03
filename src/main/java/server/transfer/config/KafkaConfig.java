package server.transfer.config;

import server.transfer.config.util.EnvironmentUtil;

/**
 * The specified configuration-object that stores all needed configurations
 *  for the connection from Kafka to another specified component
 */
public final class KafkaConfig {
	
	private KafkaConfig() {
		
	}
	
    /**
     * Gets the Kafka-host-name
     * @return name The host-name of Kafka
     */
    public static String getKafkaHostName() {
    	return EnvironmentUtil.getEnvironmentVariable("WM_KAFKA_HOST", "localhost:9092");
    }

}