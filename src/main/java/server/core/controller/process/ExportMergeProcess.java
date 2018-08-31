package server.core.controller.process;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.properties.KafkaPropertiesFileManager;

/**
 * @author Patrick
 * 
 * This Class generates a Merged Topic from Observations,Thing,Datastream,ObservedProperty and FeatureOfInterest
 *
 */
public class ExportMergeProcess implements ProcessInterface, Runnable {

	private static final String FOI_DOUBLE_PARSE_EXCEPTION = "Could not parse FeatureOfInterest value to double.";
	private Properties props;
	private KafkaStreams kafkaStreams;
	private static final String THREAD_NAME = "ExportProcess";
	private boolean obsPro = false;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Thread thread;
	private CountDownLatch countdownLatch = null;
	
	
	public ExportMergeProcess(Boolean obsPro) {
		this.obsPro = obsPro;
		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		this.props = propManager.getExportStreamProperties();
		logger.info("Creating thread: {}", THREAD_NAME);
	}
	
	@Override
	public boolean kafkaStreamStart() {
		logger.info("Starting thread: {}", THREAD_NAME);
		if (thread == null) {
			thread = new Thread(this, THREAD_NAME);

			countdownLatch = new CountDownLatch(1);
			thread.start();

			return true;
		}
		return false;
	}
	
	@Override
	public boolean kafkaStreamClose() {
		logger.info("Closing thread: {}", THREAD_NAME);
		
		if(countdownLatch != null) {
			countdownLatch.countDown();
		}
		
		if(thread != null) {
			try {
				thread.join();
				if (kafkaStreams == null) {
					logger.info("Applikation 'Export' is not Running");
					return false;
				}
				Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
			} catch (InterruptedException e) {
				thread.interrupt();
				logger.warn("Interruption of thread: {}", THREAD_NAME);
			}
			logger.info("Stopped thread successfully: {}", THREAD_NAME);
			return true;
			
		}
		return false;
	}
	
	@Override
	public void apply(StreamsBuilder builder) {
		final KStream<String, GenericRecord> observationStream = builder.stream("Observations");
		final KStream<String, GenericRecord> foIStream = builder.stream("FeaturesOfInterest");

		final KStream<String, GenericRecord> foIStreamKey = foIStream
				.map((key, value) -> KeyValue.pair(value.get("Observations").toString(), value));

		final KStream<String, GenericRecord> mergedFoIObs = observationStream.join(foIStreamKey, (value, location) -> {
			value.put("FeatureOfInterest", location.toString());
			
			return value;
		}, JoinWindows.of(100000));
		
		final KStream<String, GenericRecord> dataStream = builder.stream("Datastreams");
		
		// Transform merged to Equals Keys to DataStream.Iot
		final KStream<String, GenericRecord> mergedKey = mergedFoIObs
				.map((key, value) -> KeyValue.pair(value.get("Datastream").toString(), value));
		
		// Join the DataStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsData = mergedKey.join(dataStream, (value, data) -> {

			value.put("Datastream", data.toString());
			return value;

		},JoinWindows.of(100000));
		
		final KTable<String, GenericRecord> thingStream = builder.table("Things");
		
		// Tranfrom mergedFoIObsData to Equals Key to Things
		final KStream<String, GenericRecord> mergedKeyThing = mergedFoIObsData
				.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "Thing"), value));
		
		 //Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThing = mergedKeyThing.join(thingStream,
				(value, thing) -> {
					JSONObject ds = toJson(value, "Datastream");
					ds.put("Thing", thing.toString());

					value.put("Datastream", ds.toString());
					
					return value;

				});

		// get SensorStream
		final KTable<String, GenericRecord> sensorStream = builder.table("Sensors");
		// Tranfrom mergedFoIObsData to Equals Key to Sensor
		final KStream<String, GenericRecord> mergedFoIObsDataThingKey = mergedFoIObsDataThing
				.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "Sensor"), value));

		// Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThingSensor = mergedFoIObsDataThingKey.leftJoin(sensorStream,
				(value, thing) -> {

					JSONObject ds = toJson(value, "Datastream");
					if (thing!= null) {
						ds.put("Sensor", thing.toString());
						value.put("Datastream", ds.toString());
					} else {
						value.put("Datastream", "NO Sensor VALUE FOUND");
					}
					
					return value;

				});
		
			// get ObsPro
			final KStream<String, GenericRecord> propertyStream = builder.stream("ObservedProperties");
			// Tranfrom mergedFoIObsData to Equals Key to Sensor
			final KStream<String, GenericRecord> mergedFoIObsDataThingSensorKey = mergedFoIObsDataThingSensor
					.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "ObservedProperty"), value));

			// Join ThingsStream with MergedStream
			final KStream<String, String> finalStream = mergedFoIObsDataThingSensorKey.join(propertyStream,
					(value, thing) -> {
						
						if(thing!= null) {
							JSONObject ds = toJson(value, "Datastream");
							ds.put("ObservedProperty", thing.toString());
							value.put("Datastream", ds.toString());
						}else {
							value.put("Datastream", "NO ObservedProperty VALUE FOUND");
						}
						return toJson(value).toJSONString();

					},JoinWindows.of(100000));
	
		
		final Serde<String> stringSerde = Serdes.String();
		
		finalStream.to("AvroExport", Produced.with(stringSerde, stringSerde));
		
		stringSerde.close();
	}

	/**
	 * This Methode generates a key String from a GenericRecord
	 * @param record 
	 * @param stream Name
	 * @param key Name
	 * @return
	 */
	public String toJson(GenericRecord record, String stream, String key) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser().parse((stream == null) ? record.toString() : record.get(stream).toString());
			return (String) ds.get(key);
		} catch (ParseException e) {
			logger.warn(FOI_DOUBLE_PARSE_EXCEPTION, e);
		}
		return null;
	}

	/**
	 * 
	 * This Method generation a JSONOject from a Generic Record
	 * @param record
	 * @param stream
	 * @return
	 */
	public JSONObject toJson(GenericRecord record, String stream) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser().parse((stream == null) ? record.toString() : record.get(stream).toString());
			return ds;
		} catch (ParseException e) {
			logger.warn(FOI_DOUBLE_PARSE_EXCEPTION, e);
		}
		return null;

	}
	
	/**
	 * Transform GenericRecord to Json
	 * @param record
	 * @return
	 */
	public JSONObject toJson(GenericRecord record){
		return toJson(record, null);
	}

	@Override
	public void run() {
		logger.info("Starting thread: {}", THREAD_NAME);
		StreamsBuilder builder = new StreamsBuilder();

		apply(builder);
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();
		
		
	}

	@Override
	public void apply() throws InterruptedException {
		apply(new StreamsBuilder());
	}

}
