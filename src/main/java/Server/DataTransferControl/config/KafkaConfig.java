package DataTransferControl.config;

/**
 * The specified configuration-object that stores all needed configurations for the connection from Kafka to another specified component
 */
public class KafkaConfig {
	
    /**
     * Gets the Kafka-host-name
     * @return name The host-name of Kafka
     */
    public static String getKafkaHostName() {
    	return ConfigUtil.getEnvironmentVariable("WM_KAFKA_HOST", "localhost:9092");
    }

}