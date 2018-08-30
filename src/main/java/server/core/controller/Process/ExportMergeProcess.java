package server.core.controller.Process;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.core.properties.KafkaPropertiesFileManager;

/**
 * @author Patrick
 * 
 * This Class generates a Merged Topic from Observations,Thing,Datastream,ObservedProperty and FeatureOfInterest
 *
 */
public class ExportMergeProcess implements ProcessInterface, Runnable {

	private Properties props;
	private KafkaStreams kafkaStreams;
	private final String threadName = "ExportProcess";
	private boolean obsPro = false;


	private Thread thread;
	private CountDownLatch countdownLatch = null;

	/**
	 * Default Constructor
	 */
	public ExportMergeProcess(Boolean obsPro) {
		this.obsPro = obsPro;
		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		this.props = propManager.getExportStreamProperties();
		System.out.println("Creating " + threadName);
	}

	/* (non-Javadoc)
	 * @see server.core.controller.ProcessInterface#kafkaStreamStart()
	 */
	@Override
	public boolean kafkaStreamStart() {
		
		
		
		System.out.println("Starting " + threadName);
		if (thread == null) {
			thread = new Thread(this, threadName);

			countdownLatch = new CountDownLatch(1);
			thread.start();

			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see server.core.controller.ProcessInterface#kafkaStreamClose()
	 */
	@Override
	public boolean kafkaStreamClose() {

		System.out.println("Closing " + threadName);
		if(countdownLatch != null) {
			countdownLatch.countDown();
		}
		
		if(thread != null) {
			try {
				thread.join();
				if (kafkaStreams == null) {
					System.out.println("Applikation 'Export' is not Running");
					return false;
				}
				Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			System.out.println(threadName + "successfully stopped.");
			return true;
			
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see server.core.controller.ProcessInterface#apply(org.apache.kafka.streams.StreamsBuilder)
	 */
	@Override
	public void apply(StreamsBuilder builder) {
		final KStream<String, GenericRecord> observationStream = builder.stream("Observations");
		final KStream<String, GenericRecord> foIStream = builder.stream("FeaturesOfInterest");

		final KStream<String, GenericRecord> foIStreamKey = foIStream
				.map((key, value) -> KeyValue.pair(value.get("Observations").toString(), value));

		final KStream<String, GenericRecord> mergedFoIObs = observationStream.join(foIStreamKey, (value, location) -> {
			value.put("FeatureOfInterest", location.toString());
//			JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
			return value;
			// return null;

		}, JoinWindows.of(100000));

		// get DataStream
		final KStream<String, GenericRecord> DataStream = builder.stream("Datastreams");
		// Transform merged to Equals Keys to DataStream.Iot
		final KStream<String, GenericRecord> mergedKey = mergedFoIObs
				.map((key, value) -> KeyValue.pair(value.get("Datastream").toString(), value));
		// Join the DataStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsData = mergedKey.join(DataStream, (value, data) -> {

			value.put("Datastream", data.toString());
			// JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
			return value;

		},JoinWindows.of(100000));

		// get ThingsStream
		final KTable<String, GenericRecord> ThingStream = builder.table("Things");
		// Tranfrom mergedFoIObsData to Equals Key to Things
		final KStream<String, GenericRecord> mergedKeyThing = mergedFoIObsData
				.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "Thing"), value));
		
		
		 //Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThing = mergedKeyThing.join(ThingStream,
				(value, thing) -> {
					JSONObject ds = toJson(value, "Datastream");
					ds.put("Thing", thing.toString());

					value.put("Datastream", ds.toString());
					
					return value;

				});

		// get SensorStream
		final KTable<String, GenericRecord> SensorStream = builder.table("Sensors");
		// Tranfrom mergedFoIObsData to Equals Key to Sensor
		final KStream<String, GenericRecord> mergedFoIObsDataThingKey = mergedFoIObsDataThing
				.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "Sensor"), value));

		// Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThingSensor = mergedFoIObsDataThingKey.leftJoin(SensorStream,
				(value, thing) -> {

					JSONObject ds = toJson(value, "Datastream");
					if(thing!= null) {
						ds.put("Sensor", thing.toString());
						value.put("Datastream", ds.toString());
					}else {
						value.put("Datastream", "NO Sensor VALUE FOUND");
					}
					

					
					// JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
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

			finalStream.to("AvroExport",Produced.with(stringSerde, stringSerde));
		
		



		
	}

	/**
	 * This Methode generates a key String from a GenericRecord
	 * @param record 
	 * @param Stream Name
	 * @param key Name
	 * @return
	 */
	public String toJson(GenericRecord record, String Stream, String key) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser().parse(record.get(Stream).toString());

			return (String) ds.get(key);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * This Method generation a JSONOject from a Generic Record
	 * @param record
	 * @param Stream
	 * @return
	 */
	public JSONObject toJson(GenericRecord record, String Stream) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser().parse(record.get(Stream).toString());

			return ds;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Transform GenericRecord to Json
	 * @param record
	 * @return
	 */
	public JSONObject toJson(GenericRecord record ){
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser().parse(record.toString());

			return ds;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void run() {
		System.out.println("Running " +  threadName );
		StreamsBuilder builder = new StreamsBuilder();

		apply(builder);
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();
		
		
	}

}
