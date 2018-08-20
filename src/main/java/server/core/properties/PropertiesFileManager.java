package server.core.properties;

import java.security.InvalidParameterException;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

public final class PropertiesFileManager {

	private Properties properties;
	private String kafkaPropertyFilePath = "src/main/resources/KafkaCore.properties";
	private static PropertiesFileManager instance;
	
	private PropertiesFileManager() {
		loadKafkaCoreProperties();
	}
	
	public static PropertiesFileManager getInstance() {
		if (instance == null) {
			instance = new PropertiesFileManager();
		}
		return instance;
	}

	public Properties getMergeStreamProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty("M_APPLICATION_ID_CONFIG"));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty("M_CLIENT_ID_CONFIG"));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, getProperty("SCHEMA_REGISTRY_URL_CONFIG"));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty("M_AUTO_OFFSET_RESET_CONFIG"));
		return props;
	}

	public Properties getGraphiteStreamProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty("P_APPLICATION_ID_CONFIG"));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty("P_CLIENT_ID_CONFIG"));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, getProperty("SCHEMA_REGISTRY_URL_CONFIG"));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty("P_AUTO_OFFSET_RESET_CONFIG"));
		return props;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	private void loadKafkaCoreProperties() {
		try {
			properties = PropertyFileReader.readPropertyFile(kafkaPropertyFilePath);

			// check if properties file is missing keys

			if (!properties.containsKey("BOOTSTRAP_SERVERS_CONFIG")
					|| !properties.containsKey("SCHEMA_REGISTRY_URL_CONFIG")
					|| !properties.containsKey("M_AUTO_OFFSET_RESET_CONFIG")
					|| !properties.containsKey("M_APPLICATION_ID_CONFIG")
					|| !properties.containsKey("M_CLIENT_ID_CONFIG")) {
				throw new InvalidParameterException();
			}
		}  catch (InvalidParameterException e) {
			e.printStackTrace();
			System.err.println("The configuration file is missing at least one of the following required arguments:\n"
					+ "\t- BOOTSTRAP_SERVERS_CONFIG\n" + "\t- SCHEMA_REGISTRY_URL_CONFIG\n"
					+ "\t- M_AUTO_OFFSET_RESET_CONFIG\n" + "\t- M_APPLICATION_ID_CONFIG\n"
					+ "\t- M_CLIENT_ID_CONFIG\n");
			System.exit(-1);
		}
	}
	
}
