package server.core.controller.Process;

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

import server.core.properties.KafkaTopicAdmin;
import server.core.properties.PropertiesFileManager;

/**
 * @author Patrick This Class merges bascily the ObservationTopic with the
 *         FeatureOfInterested topic to an Output topic. It's needed for other
 *         Processing Classes
 *
 */
public class MergeObsToFoiProcess implements ProcessInterface, Runnable {

	private String ObservationTopic;
	private String FeatureOfIntresssTopic;
	private String outputTopic;
	private String keyEqual;
	private Properties props;
	private KafkaStreams kafkaStreams;
	private final String threadName = "MergeProcess";

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

		this.ObservationTopic = topic1;
		this.FeatureOfIntresssTopic = topic2;
		this.outputTopic = outputTopic;
		this.keyEqual = key;

		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		this.props = propManager.getMergeStreamProperties();
		System.out.println("Creating " + threadName);
	}

	/**
	 * Default Constructer
	 */
	public MergeObsToFoiProcess() {
		this("Observations", "FeaturesOfInterest", "ObservationsMergesGeneric", "Observations");
	}

	/**
	 * This Starts the process with String Serializer
	 */

	public boolean startDifferent() {
		final Serde<String> stringSerde = Serdes.String();

		StreamsBuilder builder = new StreamsBuilder();
		final KStream<String, GenericRecord> foIT = builder.stream(FeatureOfIntresssTopic);
		final KTable<String, GenericRecord> obsT = builder.table(ObservationTopic);
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

		return true;
	}

	/**
	 * This Starts the process with Generic Avro Serialiser
	 */

	public boolean kafkaStreamStart() {

		System.out.println("Starting " + threadName);
		if (thread == null) {
			thread = new Thread(this, threadName);

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
		final KTable<String, GenericRecord> obsT = builder.table(ObservationTopic);
		final KStream<String, GenericRecord> foIT = builder.stream(FeatureOfIntresssTopic);
		
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
		

		
		
		System.out.println("Closing " + threadName);
		if(countdownLatch != null) {
			countdownLatch.countDown();
		}
		
		if(thread != null) {
			this.threadBoolean = false;
			try {
				thread.join();
				if (kafkaStreams == null) {
					System.out.println("Applikation 'Merge' is not Running");
					return false;
				}
				Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			System.out.println(threadName + "successfully stopped.");
			return true;
			
		}
		return false;
	}

	@Override
	public void run() {
		System.out.println("Running " +  threadName );
		StreamsBuilder builder = new StreamsBuilder();

		apply(builder);
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();

	}

}
