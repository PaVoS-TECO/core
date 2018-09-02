package server.core.controller.testclasses;




import io.confluent.kafka.serializers.KafkaAvroDeserializer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

public class Exporttester2 {

public static  int counter = 0;

    public static void main(String[] args) throws InterruptedException {


    	
        //Client Props
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "pavos.oliver.pw:9092");
        props.put(GROUP_ID_CONFIG, "i");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        //props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        //        KafkaAvroDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("schema.registry.url", "http://pavos.oliver.pw:8081");






        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("AvroExport"));

        System.out.println("Consumer A gestartet!");


        JSONObject obj;
        JsonEncoder jsonEncoder;
        int i = 0;
        ArrayList<String> list = new ArrayList<>();

        while (true) {

            final ConsumerRecords<String, String> foi =
                    consumer.poll(100);
            foi.forEach(record1 -> {

            	try {
					JSONObject obs = (JSONObject) new JSONParser().parse(record1.value());
					JSONObject foiI =  (JSONObject) new JSONParser().parse((String) obs.get("FeatureOfInterest"));
                    JSONObject dataS =  (JSONObject) new JSONParser().parse((String) obs.get("Datastream"));
                    JSONObject Thing =  (JSONObject) new JSONParser().parse((String) dataS.get("Thing"));
                    JSONObject Sensor =  (JSONObject) new JSONParser().parse((String) dataS.get("Sensor"));
                    JSONObject ObsPro =  (JSONObject) new JSONParser().parse((String) dataS.get("ObservedProperty"));
                    
                   System.out.println(obs.get("iotId"));
					
					
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                





            });

    }
}}