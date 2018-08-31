package server.core.properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;

import java.security.InvalidParameterException;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import server.transfer.data.ObservationDataSerializer;

/**
 * @author Patrick
 *
 */
/**
 * The PropertyFile is a special form of associative memory in which
 * key-value pairs are always of type string. Since the entries can be stored in
 * a file and read out again, hardwired character strings can be externalized
 * from the program text so that the values ​​can be easily changed without
 * retranslation.
 */
public final class KafkaPropertiesFileManager {
	
	private static final String BOOTSTRAP_SERVERS_CONFIG = "BOOTSTRAP_SERVERS_CONFIG";
	private static final String M_APPLICATION_ID_CONFIG = "M_APPLICATION_ID_CONFIG";
	private static final String M_CLIENT_ID_CONFIG = "M_CLIENT_ID_CONFIG";
	private static final String SCHEMA_REGISTRY_URL_CONFIG = "SCHEMA_REGISTRY_URL_CONFIG";
	private static final String M_AUTO_OFFSET_RESET_CONFIG = "M_AUTO_OFFSET_RESET_CONFIG";
	private static final String E_APPLICATION_ID_CONFIG = "E_APPLICATION_ID_CONFIG";
	private static final String E_CLIENT_ID_CONFIG = "E_CLIENT_ID_CONFIG";
	private static final String E_AUTO_OFFSET_RESET_CONFIG = "E_AUTO_OFFSET_RESET_CONFIG";
	private static final String P_APPLICATION_ID_CONFIG = "P_APPLICATION_ID_CONFIG";
	private static final String P_CLIENT_ID_CONFIG = "P_CLIENT_ID_CONFIG";
	private static final String P_AUTO_OFFSET_RESET_CONFIG = "P_AUTO_OFFSET_RESET_CONFIG";
	private static final String C_CLIENT_ID_CONFIG = "C_CLIENT_ID_CONFIG";
	private static KafkaPropertiesFileManager instance;
	private Properties properties;
	private String kafkaPropertyFilePath = "src/main/resources/KafkaCore.properties";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Default Constructor
	 */
	private KafkaPropertiesFileManager() {
		loadKafkaCoreProperties();
	}
	
	/**
	 * 
	 * @return it Self
	 */
	public static KafkaPropertiesFileManager getInstance() {
		if (instance == null) {
			instance = new KafkaPropertiesFileManager();
		}
		return instance;
	}

	/**
	 * @return Merge Stream Properties
	 */
	public Properties getMergeStreamProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty(M_APPLICATION_ID_CONFIG));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty(M_CLIENT_ID_CONFIG));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, getProperty(SCHEMA_REGISTRY_URL_CONFIG));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty(M_AUTO_OFFSET_RESET_CONFIG));
		return props;
	}
	
	/**
	 * @return Export Stream Properties
	 */
	public Properties getExportStreamProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty(E_APPLICATION_ID_CONFIG));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty(E_CLIENT_ID_CONFIG));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, getProperty(SCHEMA_REGISTRY_URL_CONFIG));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty(E_AUTO_OFFSET_RESET_CONFIG));
		return props;
	}
	
	/**
	 * @return Default/Test Stream Properties
	 */
	
	public Properties getDummyStreamProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty(M_APPLICATION_ID_CONFIG));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty(M_CLIENT_ID_CONFIG));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty(M_AUTO_OFFSET_RESET_CONFIG));
		return props;
	}

	/**
	 * @return Graphite Stream Properties
	 */

	public Properties getGraphiteStreamProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty(P_APPLICATION_ID_CONFIG));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty(P_CLIENT_ID_CONFIG));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, getProperty(SCHEMA_REGISTRY_URL_CONFIG));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty(P_AUTO_OFFSET_RESET_CONFIG));
		return props;
	}
	
	/**
	 * @return Grid Stream Properties
	 */
	
	public Properties getGridStreamProperties() {
		Properties props = new Properties();
		props.put(BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
		props.put(GROUP_ID_CONFIG, "i");
		props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, getProperty(C_CLIENT_ID_CONFIG));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put("schema.registry.url", getProperty(SCHEMA_REGISTRY_URL_CONFIG));

		return props;
	}
	
	/**
	 * @return Simple Producer Properties
	 */
	
	public Properties getProducerGridProperties() {
    	Properties configProperties = new Properties();
    	configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
        configProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        configProperties.put(ProducerConfig.RETRIES_CONFIG, 0);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ObservationDataSerializer.class.getName());
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return configProperties;
    }
	
	/**
	 * @return Prpoerty from selected key
	 */

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	/**
	 * Load from File Properties
	 */
	
	private void loadKafkaCoreProperties() {
		try {
			properties = PropertyFileReader.readPropertyFile(kafkaPropertyFilePath);

			// check if properties file is missing keys

			if (!properties.containsKey(BOOTSTRAP_SERVERS_CONFIG)
					|| !properties.containsKey(SCHEMA_REGISTRY_URL_CONFIG)
					|| !properties.containsKey(M_AUTO_OFFSET_RESET_CONFIG)
					|| !properties.containsKey(M_APPLICATION_ID_CONFIG)
					|| !properties.containsKey(C_CLIENT_ID_CONFIG)
					|| !properties.containsKey(M_CLIENT_ID_CONFIG)) {
				throw new InvalidParameterException();
			}
		}  catch (InvalidParameterException e) {
			logger.error(String.format("The configuration file is missing at least one of the following required arguments:\n"
					+ "\t- %s\n\t- %s\n\t- %s\n\t- %s\n\t- %s\n", BOOTSTRAP_SERVERS_CONFIG, SCHEMA_REGISTRY_URL_CONFIG,
					M_AUTO_OFFSET_RESET_CONFIG, M_APPLICATION_ID_CONFIG, M_CLIENT_ID_CONFIG), e);
			System.exit(-1);
		}
	}
	
}
