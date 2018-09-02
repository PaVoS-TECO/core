package server.core.properties;

import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import server.transfer.config.util.EnvironmentUtil;
import server.transfer.data.ObservationDataDeserializer;
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
	
	private static final String BOOTSTRAP_SERVERS_CONFIG = "PAVOS_BOOTSTRAP_SERVERS_CONFIG";
	private static final String M_APPLICATION_ID_CONFIG = "PAVOS_M_APPLICATION_ID_CONFIG";
	private static final String M_CLIENT_ID_CONFIG = "PAVOS_M_CLIENT_ID_CONFIG";
	private static final String SCHEMA_REGISTRY_URL_CONFIG = "PAVOS_SCHEMA_REGISTRY_URL_CONFIG";
	private static final String M_AUTO_OFFSET_RESET_CONFIG = "PAVOS_M_AUTO_OFFSET_RESET_CONFIG";
	private static final String E_APPLICATION_ID_CONFIG = "PAVOS_E_APPLICATION_ID_CONFIG";
	private static final String E_CLIENT_ID_CONFIG = "PAVOS_E_CLIENT_ID_CONFIG";
	private static final String E_AUTO_OFFSET_RESET_CONFIG = "PAVOS_E_AUTO_OFFSET_RESET_CONFIG";
	private static final String P_APPLICATION_ID_CONFIG = "PAVOS_P_APPLICATION_ID_CONFIG";
	private static final String P_CLIENT_ID_CONFIG = "PAVOS_P_CLIENT_ID_CONFIG";
	private static final String P_AUTO_OFFSET_RESET_CONFIG = "PAVOS_P_AUTO_OFFSET_RESET_CONFIG";
	private static final String C_CLIENT_ID_CONFIG = "PAVOS_C_CLIENT_ID_CONFIG";
	private static KafkaPropertiesFileManager instance;
	private Properties properties = new Properties();
	
	private void loadKafkaCoreProperties() {
		properties.put(BOOTSTRAP_SERVERS_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_BOOTSTRAP_SERVERS_CONFIG", "localhost:9092"));
		properties.put(M_APPLICATION_ID_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_M_APPLICATION_ID_CONFIG", "m_application"));
		properties.put(M_CLIENT_ID_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_M_CLIENT_ID_CONFIG", "m_client"));
		properties.put(SCHEMA_REGISTRY_URL_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_SCHEMA_REGISTRY_URL_CONFIG", "http://localhost:8081"));
		properties.put(M_AUTO_OFFSET_RESET_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_M_AUTO_OFFSET_RESET_CONFIG", "earliest"));
		properties.put(E_APPLICATION_ID_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_E_APPLICATION_ID_CONFIG", "Export_application2"));
		properties.put(E_CLIENT_ID_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_E_CLIENT_ID_CONFIG", "Export_client2"));
		properties.put(E_AUTO_OFFSET_RESET_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_E_AUTO_OFFSET_RESET_CONFIG", "earliest"));
		properties.put(P_APPLICATION_ID_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_P_APPLICATION_ID_CONFIG", "p_application"));
		properties.put(P_CLIENT_ID_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_P_CLIENT_ID_CONFIG", "p_client"));
		properties.put(P_AUTO_OFFSET_RESET_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_P_AUTO_OFFSET_RESET_CONFIG", "earliest"));
		properties.put(C_CLIENT_ID_CONFIG, EnvironmentUtil.getEnvironmentVariable("PAVOS_C_CLIENT_ID_CONFIG", "c_client"));
	}
	
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

	
	public Properties getGraphiteConnectorProperties() {
		Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
        		"org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
        		ObservationDataDeserializer.class.getName());
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "GraphiteConsumers");
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "GraphiteConsumer");
        configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configProperties;
	}
	
	public Properties getGraphiteProducerProperties() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
		properties.put(ProducerConfig.ACKS_CONFIG, "all");
		properties.put(ProducerConfig.RETRIES_CONFIG, 0);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		return properties;
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
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(BOOTSTRAP_SERVERS_CONFIG));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "i");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
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
	 * @param key 
	 * @return Prpoerty from selected key
	 */

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
}
