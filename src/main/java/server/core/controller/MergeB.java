package server.core.controller;

import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Reducer;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

public class MergeB {

	private static final String ObservationTopic = "Observations";
	private static final String FeatureOfIntresssTopic = "FeaturesOfInterest";
	private static final String outputTopic = "ObservationsMerges";

	public static void main(String[] args) throws InterruptedException {

		Properties props = new Properties();
		String test = "FINAL1";
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Merge" + test);
		props.put(StreamsConfig.CLIENT_ID_CONFIG, "example-client-" + test);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		final Serde<String> stringSerde = Serdes.String();

		StreamsBuilder builder = new StreamsBuilder();
		final KStream<String, GenericRecord> foIT = builder.stream(FeatureOfIntresssTopic);
		final KTable<String, GenericRecord> obsT = builder.table(ObservationTopic);
		final KStream<String, GenericRecord> transformfoIT = foIT
				.map((key, value) -> KeyValue.pair(value.get("Observations").toString(), value));

		final KStream<String, String> transformfoITTable = transformfoIT.leftJoin(obsT, (location, value) -> {
			;

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

		obsT.mapValues(value -> {

			String obsIot = value.get("FeatureOfInterest").toString();
			System.out.println(obsIot);

			foIT.mapValues(value1 -> {
				String foiIot = value.get("iotId").toString();
				Boolean testIot = obsIot.equals(foiIot);
				System.out.println(obsIot + " " + foiIot + " " + testIot);

				return value1;
			});

			return value;
		}

		);
		
		final KStream<String, String> Location = foIT.mapValues(value -> {

			GenericRecord obj = (GenericRecord) value.get("feature");

			String iotFoi = value.get("iotId").toString();
			obsT.mapValues(value1 -> {
				if (value1.get("FeatureOfInterest").toString().equals(iotFoi)) {
					value1.put("FeatureOfInterest", obj.get("coordinates").toString());
				}
				return value1;

			});

			return obj.get("coordinates").toString();

		}

		);

		final KStream<String, String> viewsByRegionForConsole = foIT.mapValues(value -> value.toString());

		transformfoITTable.to("bs" + test, Produced.with(stringSerde, stringSerde));

		KafkaStreams kafkaStreams1 = new KafkaStreams(builder.build(), props);
		kafkaStreams1.start();

	}

}
