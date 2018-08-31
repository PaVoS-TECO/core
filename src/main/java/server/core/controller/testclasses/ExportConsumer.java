package server.core.controller.testclasses;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.controller.Main;

public class ExportConsumer {
	
	private static final String JSON_PARSE_EXCEPTION = "Could not parse to JSONObject.";
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
    public static void main(String[] args) {

        //Client Props
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "192.168.56.3:9092");
        props.put(GROUP_ID_CONFIG, "i");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    		  "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("schema.registry.url", "http://192.168.56.3:8081");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("AvroExport11"));

        System.out.println("Consumer A gestartet!");

        Thread t = new Thread(() -> {

            final ConsumerRecords<String, String> foi =
                    consumer.poll(100);
            System.out.println(foi.count());
            foi.forEach(record1 -> {
               try {
					JSONObject obs = (JSONObject) new JSONParser().parse(record1.value());


//					JSONObject foiI =  (JSONObject) new JSONParser().parse((String) obs.get("FeatureOfInterest"));
					JSONObject dataS =  (JSONObject) new JSONParser().parse((String) obs.get("Datastream"));
					
					System.out.println(dataS.get("iotId"));
				} catch (ParseException e) {
					logger.warn(JSON_PARSE_EXCEPTION);
				}
            });
        });
        t.start();
        try {
			t.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warn("Interrupted while joining threads.");
		}
        consumer.close();
}
    
    public static String toJson(GenericRecord record,String Stream, String key) {
    	JSONObject ds;
    	try {
			ds = (JSONObject) new JSONParser().parse(record.get(Stream).toString());
			return (String) ds.get( key);
		} catch (ParseException e) {
			logger.warn(JSON_PARSE_EXCEPTION);
		}
    	return null;
    	
	}

}
