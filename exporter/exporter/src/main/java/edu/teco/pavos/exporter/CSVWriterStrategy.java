package edu.teco.pavos.exporter;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

/**
 * Implementation of the FileWriterStrategy interface for CSV files.
 */
public class CSVWriterStrategy implements FileWriterStrategy {

    private ArrayList<JSONObject> observations = new ArrayList<JSONObject>();
    private HashMap<String, JSONObject> features = new HashMap<String, JSONObject>();
    private HashMap<String, JSONObject> dataStreams = new HashMap<String, JSONObject>();
    private HashMap<String, JSONObject> locations = new HashMap<String, JSONObject>();
    private HashMap<String, JSONObject> things = new HashMap<String, JSONObject>();
    private HashMap<String, JSONObject> sensors = new HashMap<String, JSONObject>();
    private HashMap<String, JSONObject> observedProperties = new HashMap<String, JSONObject>();
	private DateTime startTime;
	private DateTime endTime;
	private DateTimeFormatter timeParser;
	private Set<String> obsProps;
	private JSONParser jsonParser;
	private Set<String> clusters;

	/**
     * Default constructor
     * @param props are the properties of the data, that should be exported to a File.
     */
    public CSVWriterStrategy(ExportProperties props) {
    	this.jsonParser = new JSONParser();
    	TimeIntervall interval = props.getTimeFrame();
		this.startTime = interval.getStartDate();
		this.endTime = interval.getEndDate();
		this.timeParser = ISODateTimeFormat.dateTimeNoMillis();
		this.obsProps = props.getObservedProperties();
		this.clusters = props.getClusters();
    }
    
    /**
     * Creates a File as specified by the FilePath and saves the Data from the provided KafkaStream into it.
     * @param file Is the FilePath, where the new File should be created.
     */
	public void saveToFile(File file) {
		//Client Props
        Properties kprops = new Properties();
        kprops.put(BOOTSTRAP_SERVERS_CONFIG, "192.168.56.3:9092");
        kprops.put(GROUP_ID_CONFIG, "i");
        kprops.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        kprops.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        kprops.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        kprops.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kprops.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        kprops.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        kprops.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
        kprops.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        kprops.put("schema.registry.url", "http://192.168.56.3:8081");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kprops);

        consumer.subscribe(Arrays.asList("Observations"));
        
        boolean continueGettingRecords = true;

        while (continueGettingRecords) {
            final ConsumerRecords<String, String> obs = consumer.poll(100);
            obs.forEach(record -> {
				try {
					JSONObject json = (JSONObject) this.jsonParser.parse(record.value());
	                processRecord(json);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            });
            // change ending condition
            continueGettingRecords = false;
        }
        
        consumer.close();
        
        saveToFile();
		
	}
	
	private void processRecord(JSONObject record) {
		
		JSONObject observation = (JSONObject) record.get("Observation");
		DateTime time = this.timeParser.parseDateTime("" + observation.get("phenomenonTime"));
		
		if (time.isAfter(this.startTime) && time.isBefore(this.endTime)) {

			JSONObject observedProperty = (JSONObject) record.get("ObservedProperty");
			String o = "" + observedProperty.get("name");
			
			if (this.obsProps.contains(o)) {
				
				JSONObject featureOfInterest = (JSONObject) record.get("FeatureOfInterest");
				JSONObject loc = (JSONObject) featureOfInterest.get("feature");
				
				if (this.isContainedInClusters(loc)) {
					
					JSONObject dataStream = (JSONObject) record.get("Datastream");
					JSONObject thing = (JSONObject) record.get("Thing");
					JSONObject location = (JSONObject) record.get("Location");
					JSONObject sensor = (JSONObject) record.get("Sensor");
					
					this.observations.add(observation);
					this.features.put(featureOfInterest.get("iot.id").toString(), featureOfInterest);
					this.dataStreams.put(dataStream.get("iot.id").toString(), dataStream);
					this.locations.put(location.get("iot.id").toString(), location);
					this.sensors.put(sensor.get("iot.id").toString(), sensor);
					this.things.put(thing.get("iot.id").toString(), thing);
					this.observedProperties.put(observedProperty.get("iot.id").toString(), observedProperty);
					
				}
				
			}
			
		}
		
	}
	
	private boolean isContainedInClusters(JSONObject location) {
		// TODO
		// uses this.clusters to check
		return true;
	}
	
	private void saveToFile() {
		String path = null;
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			ArrayList<String> lines;
			
			lines = getObservedPropertyLines();
			for (String line : lines) {
				writer.println(line);
			}
			lines = getSensorLines();
			for (String line : lines) {
				writer.println(line);
			}
			lines = getLocationLines();
			for (String line : lines) {
				writer.println(line);
			}
			lines = getFeatureLines();
			for (String line : lines) {
				writer.println(line);
			}
			lines = getThingLines();
			for (String line : lines) {
				writer.println(line);
			}
			lines = getDataStreamLines();
			for (String line : lines) {
				writer.println(line);
			}
			lines = getObservationLines();
			for (String line : lines) {
				writer.println(line);
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> getObservedPropertyLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String id : this.observedProperties.keySet()) {
			JSONObject value = this.observedProperties.get(id);
			String line = "observedPropertyϢ" + value.get("@iot.id") + "Ϣ";
			line += value.get("name") + "Ϣ";
			line += value.get("description") + "Ϣ";
			line += value.get("definition");
			lines.add(line);
		}
		return lines;
	}
	
	private ArrayList<String> getSensorLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String id : this.sensors.keySet()) {
			JSONObject value = this.sensors.get(id);
			String line = "sensorϢ" + value.get("@iot.id") + "Ϣ";
			line += value.get("name") + "Ϣ";
			line += value.get("description") + "Ϣ";
			line += value.get("encodingType") + "Ϣ";
			line += value.get("metadata");
			lines.add(line);
		}
		return lines;
	}
	
	private ArrayList<String> getLocationLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String id : this.locations.keySet()) {
			JSONObject value = this.locations.get(id);
			String line = "locationϢ" + value.get("@iot.id") + "Ϣ";
			line += value.get("name") + "Ϣ";
			line += value.get("description") + "Ϣ";
			line += value.get("encodingType") + "Ϣ";
			line += ((JSONObject) value.get("location")).toJSONString();
			lines.add(line);
		}
		return lines;
	}
	
	private ArrayList<String> getFeatureLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String id : this.features.keySet()) {
			JSONObject value = this.features.get(id);
			String line = "featureOfInterestϢ" + value.get("@iot.id") + "Ϣ";
			line += value.get("name") + "Ϣ";
			line += value.get("description") + "Ϣ";
			line += value.get("encodingType") + "Ϣ";
			line += ((JSONObject) value.get("feature")).toJSONString();
			lines.add(line);
		}
		return lines;
	}

	private ArrayList<String> getThingLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String id : this.things.keySet()) {
			JSONObject value = this.things.get(id);
			String line = "thingϢ" + value.get("@iot.id") + "Ϣ";
			line += value.get("name") + "Ϣ";
			line += value.get("description") + "Ϣ";
			line += ((JSONObject) value.get("properties")).toJSONString() + "Ϣ";
			lines.add(line);
		}
		return lines;
	}

	private ArrayList<String> getDataStreamLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String id : this.dataStreams.keySet()) {
			JSONObject value = this.dataStreams.get(id);
			String line = "dataStreamϢ" + value.get("@iot.id") + "Ϣ";
			line += value.get("name") + "Ϣ";
			line += value.get("description") + "Ϣ";
			line += value.get("observationType") + "Ϣ";
			line += ((JSONObject) value.get("unitOfMeasurement")).toJSONString() + "Ϣ";
			
			line += value.get("Thing@iot.id") + "Ϣ";
			line += value.get("ObservedProperty@iot.id") + "Ϣ";
			line += value.get("Sensor@iot.id") + "Ϣ";
			
			String opt = "";
			Object optional = value.get("observedArea");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "Ϣ";
			opt = "";
			optional = value.get("phenomenonTime");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "Ϣ";
			opt = "";
			optional = value.get("resultTime");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "Ϣ";
			lines.add(line);
		}
		return lines;
	}

	private ArrayList<String> getObservationLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (JSONObject value : this.observations) {
			String line = "observationϢ" + value.get("@iot.id") + "Ϣ";
			line += value.get("phenomenonTime") + "Ϣ";
			line += value.get("result") + "Ϣ";
			line += value.get("resultTime") + "Ϣ";
			
			line += value.get("Datastream@iot.id") + "Ϣ";
			line += value.get("FeatureOfInterest@iot.id") + "Ϣ";
			
			String opt = "";
			Object optional = value.get("resultQuality");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "Ϣ";
			opt = "";
			optional = value.get("validTime");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "Ϣ";
			opt = "";
			optional = value.get("parameters");
			if (optional != null) {
				JSONObject op = (JSONObject) optional;
				opt = "" + op.toJSONString();
			}
			line += opt + "Ϣ";
			lines.add(line);
		}
		return lines;
	}
}
