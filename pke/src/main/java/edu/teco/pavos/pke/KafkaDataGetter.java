package edu.teco.pavos.pke;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Polls the data from Kafka to use it for the export
 * @author Jean Baumgarten
 */
public class KafkaDataGetter {
	
	KafkaConsumer<String, String> consumer;
	HashSet<JSONObject> jsons;
	
	/**
	 * Default Constructor
	 */
	public KafkaDataGetter() {
		
		PropertyGetter p = new PropertyGetter();
		String bootstrapServersConfig = p.getProperty("BOOTSTRAP_SERVERS_CONFIG");
		Properties props = new Properties();
		String deserializer = "org.apache.kafka.common.serialization.StringDeserializer";
		
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        props.put(GROUP_ID_CONFIG, "i");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
        
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Arrays.asList("ObservationExport"));
        
        this.jsons = new HashSet<JSONObject>();
		
	}
	
	/**
	 * Checks if there are more data to poll and does so
	 * @return true if more data have been polled
	 */
	public boolean doMoreDataExist() {
		
		this.jsons = new HashSet<JSONObject>();
		final ConsumerRecords<String, String> obs = consumer.poll(100);
        
        obs.forEach(record -> {
        	
			try {
				
				JSONParser jsonParser = new JSONParser();
				JSONObject json = (JSONObject) jsonParser.parse(record.value());
                this.jsons.add(json);
                
			} catch (ParseException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
        });
        
        return !this.jsons.isEmpty();
		
	}
	
	/**
	 * Gives the next data from Kafka
	 * @return a HashSet of JSONObject
	 */
	public HashSet<JSONObject> getNextData() {
		
		return this.jsons;
		
	}
	
	/**
	 * Closes the consumer
	 */
	public void close() {
		
		this.consumer.close();
		
	}

}
