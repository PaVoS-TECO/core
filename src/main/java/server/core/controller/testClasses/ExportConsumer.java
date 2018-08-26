package server.core.controller.testClasses;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

public class ExportConsumer {




    public static void main(String[] args) throws InterruptedException {


        //Client Props
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "192.168.56.3:9092");
        props.put(GROUP_ID_CONFIG, "i");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
       // props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
       // 		 KafkaAvroDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    		  "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("schema.registry.url", "http://192.168.56.3:8081");






        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//19
        consumer.subscribe(Arrays.asList("AvroExport11"));

        System.out.println("Consumer A gestartet!");


        JSONObject obj;

        while (true) {

            final ConsumerRecords<String, String> foi =
                    consumer.poll(100);
            System.out.println(foi.count());
            foi.forEach(record1 -> {
//            	GenericRecord rc= record1.value();
//            	JSONObject ds;
//				
//            	try {
//					JSONObject thing = (JSONObject) new JSONParser().parse(toJson(rc, "Datastream", "Thing"));
//					System.out.println(thing.get("name").toString());
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
 
                
               try {
					JSONObject obs = (JSONObject) new JSONParser().parse(record1.value());


					JSONObject foiI =  (JSONObject) new JSONParser().parse((String) obs.get("FeatureOfInterest"));
					JSONObject dataS =  (JSONObject) new JSONParser().parse((String) obs.get("Datastream"));
					
					System.out.println(dataS.get("iotId"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
				}


            });
    }
}
    
    public static String toJson(GenericRecord record,String Stream, String key) {
    	JSONObject ds;
    	try {
			ds = (JSONObject) new JSONParser().parse(record.get(Stream).toString());
			return (String) ds.get( key);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    	
	}

}
