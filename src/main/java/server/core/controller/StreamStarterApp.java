package server.core.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.kafka.support.serializer.JsonSerde;

public class StreamStarterApp {

	public static void main(String[] args) {

		Map<String, String> streamProperties = new HashMap<>();
		streamProperties.put("bootstrap.servers", "localhost:9092");
		streamProperties.put("key.serde", "org.apache.kafka.common.serialization.Serdes$StringSerde");
		streamProperties.put("value.serde", "org.apache.kafka.common.serialization.Serdes$StringSerde");

		Map<String, String> streamProperties1 = new HashMap<>(streamProperties);
		streamProperties1.put("application.id", "temperature");
		Map<String, String> streamProperties2 = new HashMap<>(streamProperties);
		streamProperties2.put("application.id", "pressure");

		streamProperties1.put("default.deserialization.exception.handler",
				"org.apache.kafka.streams.errors.LogAndContinueExceptionHandler");
		streamProperties2.put("default.deserialization.exception.handler",
				"org.apache.kafka.streams.errors.LogAndContinueExceptionHandler");

		Class<Map<String, Object>> genericMapClass = (Class) Map.class;
		Produced<String, Map<String, Object>> produced = Produced.with(Serdes.String(),
				new JsonSerde<>(genericMapClass));

		StreamsBuilder streamBuilder1 = new StreamsBuilder();

		Consumed<String, String> consumed = Consumed.with(Serdes.String(), Serdes.String());
		KStream<String, String> temperatureKStream = streamBuilder1.stream("temperatureTopic3", consumed);
		temperatureKStream.mapValues((generalDetailsString) -> {
			String updatedMessage = generalDetailsString.replaceAll("\\n", "").replaceAll("\\s+", "");
			return updatedMessage;
		});

		temperatureKStream.mapValues(value -> {
			try {
				JSONObject obj = (JSONObject) new JSONParser().parse(value.toString());
				Object result = obj.get("result");
				System.out.println(result);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return value;
		});

		temperatureKStream.to("temperatureTopic4");

		StreamsConfig streamsConfig1 = new StreamsConfig(streamProperties1);
		KafkaStreams kafkaStreams1 = new KafkaStreams(streamBuilder1.build(), streamsConfig1);
		kafkaStreams1.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			kafkaStreams1.close();

		}));

	}

	private static Properties getProperties() {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "ObservationAndLocation"); // Applikation Name
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // => Da Wo sich Kafka befindet
																						// 127.0.0.1
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass()); // => Seriilasation
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass()); // => DeSerilasation
		return properties;
	}

}
