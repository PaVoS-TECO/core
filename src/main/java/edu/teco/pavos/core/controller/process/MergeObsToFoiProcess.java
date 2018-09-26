package edu.teco.pavos.core.controller.process;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import edu.teco.pavos.core.properties.KafkaPropertiesFileManager;
import edu.teco.pavos.core.properties.KafkaTopicAdmin;

/**
 * The {@link MergeObsToFoiProcess} combines the Observations with the FeatureOfInterests
 * and outputs the result in a new topic.
 */
public class MergeObsToFoiProcess extends KafkaStreamsProcess {
	private static final String FOI = "FeatureOfInterest";
	private final String observationTopic;
	private final String featureOfInterestTopic;
	private final String outputTopic;
	private final String keyEqual;

	/**
	 * Creates a new {@link MergeObsToFoiProcess}.
	 * @param observationTopic {@link String}
	 * @param featureOfInterestTopic {@link String}
	 * @param outputTopic {@link String}
	 * @param key The {@link String} key defines the reference point for merging
	 */
	public MergeObsToFoiProcess(String observationTopic, String featureOfInterestTopic,
			String outputTopic, String key) {
		KafkaTopicAdmin kAdmin = KafkaTopicAdmin.getInstance();
		
		if (!kAdmin.existsTopic(observationTopic, featureOfInterestTopic, outputTopic)) {
			kAdmin.createTopic(observationTopic);
			kAdmin.createTopic(featureOfInterestTopic);
			kAdmin.createTopic(outputTopic);
		}
		
		this.observationTopic = observationTopic;
		this.featureOfInterestTopic = featureOfInterestTopic;
		this.outputTopic = outputTopic;
		this.keyEqual = key;

		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		this.props = propManager.getMergeStreamProperties();
		logger.info("Creating thread: {}", threadName);
	}

	/**
	 * Creates a new {@link MergeObsToFoiProcess}.<p>
	 * Sets {@code observationTopic} to {@code "Observations"},<br>
	 * {@code featureOfInterestTopic} to {@code "FeaturesOfInterest"},<br>
	 * {@code outputTopic} to {@code "ObservationsMergeGeneric"} and <br>
	 * {@code key} to {@code "FeatureOfInterest"}.
	 */
	public MergeObsToFoiProcess() {
		this("Observations", "FeaturesOfInterest", "ObservationsMergeGeneric", FOI);
	}

	/**
	 * Starts the process with {@link String}-Serializer.
	 * @return operationSuccessful {@link Boolean}
	 */
	public boolean startWithStringSerializer() {
		final Serde<String> stringSerde = Serdes.String();

		StreamsBuilder builder = new StreamsBuilder();
		final KStream<String, GenericRecord> foIT = builder.stream(featureOfInterestTopic);
		final KTable<String, GenericRecord> obsT = builder.table(observationTopic);
		final KStream<String, GenericRecord> transformfoIT = foIT
				.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

		final KStream<String, String> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {

			if (value != null) {
				GenericRecord obj = (GenericRecord) location.get("feature");
				if (obj != null) {
					value.put(FOI, obj.get("coordinates").toString());
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

	@Override
	public void execute(StreamsBuilder builder) {
		final KStream<String, GenericRecord> obsT = builder.stream(observationTopic);
		final KTable<String, GenericRecord> foIT = builder.table(featureOfInterestTopic);
		
		final KStream<String, GenericRecord> tranformObsT = obsT
				.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));
		
		//final KStream<String, GenericRecord> transformfoIT = foIT
		//		.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

		final KStream<String, GenericRecord> transformfoITTable = tranformObsT.join(foIT, (value, location) -> {

			if (value != null) {
				GenericRecord obj = (GenericRecord) location.get("feature");
				if (obj != null) {
					value.put(FOI, obj.get("coordinates").toString());
				} else {
					// TODO - Observation ohne Location ?
					return value;
				}

				return value;
			}
			return null;

		});

		transformfoITTable.to(outputTopic);
	}

	@Override
	public void execute() throws InterruptedException {
		execute(new StreamsBuilder());
	}

}
