package server.core.controller.process;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.properties.KafkaTopicAdmin;
import server.core.properties.KafkaPropertiesFileManager;

/**
 * @author Patrick This Class merges bascily the ObservationTopic with the
 *         FeatureOfInterested topic to an Output topic. It's needed for other
 *         Processing Classes
 *
 */
public class MergeObsToFoiProcess implements ProcessInterface, Runnable {

	private String observationTopic;
	private String featureOfIntresssTopic;
	private String outputTopic;
	private String keyEqual;
	private Properties props;
	private KafkaStreams kafkaStreams;
	private static final String THREAD_NAME = "MergeProcess";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean threadBoolean = true;
	private Thread thread;
	private CountDownLatch countdownLatch = null;

	/**
	 * This is the custom Constructor for the Merge Class if you want to merge other
	 * Topics
	 * 
	 * @param topic1
	 * @param topic2
	 * @param outputTopic
	 * @param key         is the where you want to merge the topics
	 */
	public MergeObsToFoiProcess(String topic1, String topic2, String outputTopic, String key) {
		KafkaTopicAdmin kAdmin = KafkaTopicAdmin.getInstance();
		
		if (!kAdmin.existsTopic(topic1, topic2)) {
			kAdmin.createTopic(topic1);
			kAdmin.createTopic(topic2);
		}
		
		this.observationTopic = topic1;
		this.featureOfIntresssTopic = topic2;
		this.outputTopic = outputTopic;
		this.keyEqual = key;

		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		this.props = propManager.getMergeStreamProperties();
		logger.info("Creating thread: {}", THREAD_NAME);
	}

	/**
	 * Default Constructer
	 */
	public MergeObsToFoiProcess() {
		this("Observations", "FeaturesOfInterest", "ObservationsMergesGeneric", "Observations");
	}

	/**
	 * This Starts the process with String Serializer
	 * @return 
	 */
	public boolean startDifferent() {
		final Serde<String> stringSerde = Serdes.String();

		StreamsBuilder builder = new StreamsBuilder();
		final KStream<String, GenericRecord> foIT = builder.stream(featureOfIntresssTopic);
		final KTable<String, GenericRecord> obsT = builder.table(observationTopic);
		final KStream<String, GenericRecord> transformfoIT = foIT
				.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

		final KStream<String, String> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {

			if (value != null) {
				GenericRecord obj = (GenericRecord) location.get("feature");
				if (obj != null) {
					value.put("FeatureOfInterest", obj.get("coordinates").toString());
				} else {
					// Observation ohne Location ?
					return value.toString();
				}

				return value.toString();
			}
			return null;

		});

		transformfoITTable.to(outputTopic, Produced.with(stringSerde, stringSerde));

		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();
		
		stringSerde.close();
		return true;
	}

	/**
	 * This Starts the process with Generic Avro Serialiser
	 */

	public boolean kafkaStreamStart() {

		logger.info("Starting thread: {}", THREAD_NAME);
		if (thread == null) {
			thread = new Thread(this, THREAD_NAME);

			countdownLatch = new CountDownLatch(1);
			thread.start();

			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see server.core.controller.ProcessInterface#apply(org.apache.kafka.streams.
	 * StreamsBuilder)
	 */
	public void apply(StreamsBuilder builder) {
		final KTable<String, GenericRecord> obsT = builder.table(observationTopic);
		final KStream<String, GenericRecord> foIT = builder.stream(featureOfIntresssTopic);
		
		final KStream<String, GenericRecord> transformfoIT = foIT
				.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

		final KStream<String, GenericRecord> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {

			if (value != null) {
				GenericRecord obj = (GenericRecord) location.get("feature");
				if (obj != null) {
					value.put("FeatureOfInterest", obj.get("coordinates").toString());
				} else {
					// TODO ? Observation ohne Location ?
					return value;
				}

				return value;
			}
			return null;

		});

		transformfoITTable.to(outputTopic);
	}

	/**
	 * This closes the process
	 */
	public boolean kafkaStreamClose() {
		logger.info("Closing thread: {}", THREAD_NAME);
		if(countdownLatch != null) {
			countdownLatch.countDown();
		}
		
		if(thread != null) {
			this.threadBoolean = false;
			try {
				thread.join();
				if (kafkaStreams == null) {
					logger.info("Applikation 'Export' is not Running");
					return false;
				}
				Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
			} catch (InterruptedException e) {
				thread.interrupt();
				logger.warn("Interruption of thread: {}", THREAD_NAME);
			}
			logger.info("Stopped thread successfully: {}", THREAD_NAME);
			return true;
			
		}
		return false;
	}

	@Override
	public void run() {
		logger.info("Starting thread: {}", THREAD_NAME);
		StreamsBuilder builder = new StreamsBuilder();

		apply(builder);
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();

	}

	@Override
	public void apply() throws InterruptedException {
		apply(new StreamsBuilder());
	}

}
