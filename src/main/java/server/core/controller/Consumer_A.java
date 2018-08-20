package server.core.controller;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer_A {

	private Logger logger = LoggerFactory.getLogger(Consumer_A.class);
	private boolean loop = true;
	private KafkaConsumer<String, String> consumer;
	private CountDownLatch shutdownLatch = new CountDownLatch(1);

	public void init() {
		
		// Client Props
		Properties props = new Properties();
		props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(GROUP_ID_CONFIG, "i");
		props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer.class");
		props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer.class");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		consumer = new KafkaConsumer<>(props);
	}

	public void start() {
		consumer.subscribe(Arrays.asList("Observations"));

		System.out.println("Consumer A started!");

		try {
			while (loop) {

				ConsumerRecords<String, String> records = consumer.poll(1000);
				if (records.count() == 0)
					continue;

				for (ConsumerRecord<String, String> record : records) {

					String result = record.value();
					int i = result.indexOf("{");
					result = result.substring(i);
					try {
						JSONObject jo = (JSONObject) new JSONParser().parse(result.toString());
						System.out.println(jo.get("@iot.id"));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					System.out.println(result);

				}

			}
		} catch (WakeupException ex) {
			logger.info("Consumer has received instruction to wake up");
		} finally {
			logger.info("Consumer closing...");
			consumer.close();
			shutdownLatch.countDown();
			logger.info("Consumer has closed successfully");
		}
	}

	public void stop() {
		logger.info("Waking up consumer...");
		consumer.wakeup();

		try {
			logger.info("Waiting for consumer to shutdown...");
			shutdownLatch.await();
		} catch (InterruptedException e) {
			logger.error("Exception thrown waiting for shutdown", e);
		}
	}

}