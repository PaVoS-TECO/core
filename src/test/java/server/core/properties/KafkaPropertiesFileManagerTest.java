package server.core.properties;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.Test;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;

/**
 * Tests {@link KafkaPropertiesFileManager}
 */
public class KafkaPropertiesFileManagerTest {
	
	/**
	 * Tests if the GraphiteConnector {@link Properties} are complete.
	 */
	@Test
	public void testGetGraphiteConnectorProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getObservationDataConsumerProperties();
		assertTrue(props.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.CLIENT_ID_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
	}
	
	/**
	 * Tests if the GraphiteProducer {@link Properties} are complete.
	 */
	@Test
	public void testGetGraphiteProducerProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getObservationDataProducerProperties();
		assertTrue(props.containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.ACKS_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.RETRIES_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
	}
	
	/**
	 * Tests if the MergeStream {@link Properties} are complete.
	 */
	@Test
	public void testGetMergeStreamProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getMergeStreamProperties();
		assertBACDDA(props);
		assertTrue(props.containsKey(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG));
	}
	

	/**
	 * Tests if the ExportStream {@link Properties} are complete.
	 */
	@Test
	public void testGetExportStreamProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getExportStreamProperties();
		assertBACDDA(props);
		assertTrue(props.containsKey(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG));
	}
	

	/**
	 * Tests if the DummyStream {@link Properties} are complete.
	 */
	@Test
	public void testGetDummyStreamProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getDummyStreamProperties();
		assertBACDDA(props);
	}
	

	/**
	 * Tests if the GraphiteStream {@link Properties} are complete.
	 */
	@Test
	public void testGetGraphiteStreamProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getAvroStreamProperties();
		assertBACDDA(props);
	}
	

	/**
	 * Tests if the GridStream {@link Properties} are complete.
	 */
	@Test
	public void testGetGridStreamProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getGridStreamProperties();
		assertTrue(props.containsKey(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.CLIENT_ID_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
		assertTrue(props.containsKey("schema.registry.url"));
	}

	/**
	 * Tests if the ProducerGrid {@link Properties} are complete.
	 */
	@Test
	public void testGetProducerGridProperties() {
		KafkaPropertiesFileManager manager = KafkaPropertiesFileManager.getInstance();
		Properties props = manager.getGridProducerProperties();
		assertTrue(props.containsKey(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.ACKS_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.RETRIES_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
		assertTrue(props.containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
	}
	
	private void assertBACDDA(Properties props) {
		assertTrue(props.containsKey(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG));
		assertTrue(props.containsKey(StreamsConfig.APPLICATION_ID_CONFIG));
		assertTrue(props.containsKey(StreamsConfig.CLIENT_ID_CONFIG));
		assertTrue(props.containsKey(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG));
		assertTrue(props.containsKey(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG));
		assertTrue(props.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
	}
	
}
