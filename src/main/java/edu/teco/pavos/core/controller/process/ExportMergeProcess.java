package edu.teco.pavos.core.controller.process;

import java.awt.geom.Point2D;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.teco.pavos.core.grid.GeoGrid;
import edu.teco.pavos.core.grid.GeoGridManager;
import edu.teco.pavos.core.grid.exceptions.PointNotOnMapException;
import edu.teco.pavos.core.properties.KafkaPropertiesFileManager;
import edu.teco.pavos.core.properties.KafkaTopicAdmin;

/**
 * @author Patrick
 * 
 *         This Class generates a Merged Topic from
 *         Observations,Thing,Datastream,ObservedProperty and FeatureOfInterest
 *
 */
public class ExportMergeProcess extends KafkaStreamsProcess {
	private static final String FOI_DOUBLE_PARSE_EXCEPTION = "Could not parse FeatureOfInterest value to double.";
	private static final String DATASTREAM = "Datastream";
	private final String observationsTopic;
	private final String featureOfInterestTopic;
	private final String thingsTopic;
	private final String datastreamsTopic;
	private final String sensorsTopic;
	private final String observedPropertiesTopic;
	private final String outputTopic;

	/**
	 * Creates a new {@link ExportMergeProcess} with the specified topics.
	 * @param observationsTopic Kafka Topic of observations
	 * @param featureOfInterestTopic Kafka Topic of featuresOfInterest
	 * @param thingsTopic Kafka Topic of things
	 * @param datastreamsTopic Kafka Topic of datastreams
	 * @param sensorsTopic Kafka Topic of sensors
	 * @param observedPropertiesTopic Kafka Topic of observed properties
	 * @param outputTopic Kafka Topic of this processes results
	 */
	public ExportMergeProcess(String observationsTopic, String featureOfInterestTopic, String thingsTopic,
			String datastreamsTopic, String sensorsTopic, String observedPropertiesTopic, String outputTopic) {
		KafkaTopicAdmin admin = KafkaTopicAdmin.getInstance();
		if (!admin.existsTopic(observationsTopic)) admin.createTopic(observationsTopic);
		if (!admin.existsTopic(featureOfInterestTopic)) admin.createTopic(featureOfInterestTopic);
		if (!admin.existsTopic(thingsTopic)) admin.createTopic(thingsTopic);
		if (!admin.existsTopic(datastreamsTopic)) admin.createTopic(datastreamsTopic);
		if (!admin.existsTopic(sensorsTopic)) admin.createTopic(sensorsTopic);
		if (!admin.existsTopic(observedPropertiesTopic)) admin.createTopic(observedPropertiesTopic);
		this.observationsTopic = observationsTopic;
		this.featureOfInterestTopic = featureOfInterestTopic;
		this.thingsTopic = thingsTopic;
		this.datastreamsTopic = datastreamsTopic;
		this.sensorsTopic = sensorsTopic;
		this.observedPropertiesTopic = observedPropertiesTopic;
		this.outputTopic = outputTopic;
		
		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		this.props = propManager.getExportStreamProperties();
		logger.info("Creating thread: {}", threadName);
	}
	
	/**
	 * Creates a new {@link ExportMergeProcess}.<p>
	 * Sets {@code observationsTopic} to {@code "Observations"}<br>,
	 * {@code featureOfInterestTopic} to {@code "FeaturesOfInterest"}<br>,
	 * {@code thingsTopic} to {@code "Things"}<br>,
	 * {@code datastreamsTopic} to {@code "Datastreams"}<br>,
	 * {@code sensorsTopic} to {@code "Sensors"}<br>
	 * {@code observedPropertiesTopic} to {@code "ObservedProperties"}<br>
	 * and {@code outputTopic} to {@code "AvroExport"}
	 */
	public ExportMergeProcess() {
		this("Observations", "FeaturesOfInterest", "Things", "Datastreams",
				"Sensors", "ObservedProperties", "AvroExport");
	}

	@Override
	public void execute(StreamsBuilder builder) {
		final KStream<String, GenericRecord> observationStream = builder.stream(observationsTopic);
		final KTable<String, GenericRecord> foITable = builder.table(featureOfInterestTopic);

		final KStream<String, GenericRecord> obsStreamKey = observationStream
				.map((key, value) -> KeyValue.pair(value.get("FeatureOfInterest").toString(), value));

		final KStream<String, GenericRecord> mergedFoIObs = obsStreamKey.join(foITable, (value, location) -> {

			GenericRecord obj = (GenericRecord) location.get("feature");
			String point = obj.get("coordinates").toString();
			double coord1 = Double.parseDouble(point.split(",")[0]);
			double coord2 = Double.parseDouble(point.split(",")[1]);

			Point2D.Double location2 = new Point2D.Double(coord1, coord2);
			GeoGrid grid = GeoGridManager.getInstance().getNewestGrid();
			try {
				location.put("description", grid.getClusterID(location2, grid.getMaxLevel()));
			} catch (PointNotOnMapException e) {
				logger.warn("Point: {} is not inside the map-bounds!", e.getPoint());
			}
			value.put("FeatureOfInterest", location.toString());
			return value;
		});

		// *********************************************
		// LEITFADEN : MINDERHEIT = TABLE
		// KStream neu mappen auf key von minderheit
		// dann joinnen
		// ********************************************

		final KTable<String, GenericRecord> dataTable = builder.table(datastreamsTopic);

//		   Transform merged to Equals Keys to DataStream.Iot
		final KStream<String, GenericRecord> mergedKey = mergedFoIObs
				.map((key, value) -> KeyValue.pair(value.get(DATASTREAM).toString(), value));

//		   Join the DataStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsData = mergedKey.join(dataTable, (value, data) -> {

			value.put(DATASTREAM, data.toString());
			return value;

		});

		final KTable<String, GenericRecord> thingStream = builder.table(thingsTopic);

//		   Tranfrom mergedFoIObsData to Equals Key to Things
		final KStream<String, GenericRecord> mergedKeyThing = mergedFoIObsData
				.map((key, value) -> KeyValue.pair(toJson(value, DATASTREAM, "Thing"), value));

//		   Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThing = mergedKeyThing.join(thingStream,
				(value, thing) -> {
					JSONObject ds = toJson(value, DATASTREAM);
					ds.put("Thing", thing.toString());

					value.put(DATASTREAM, ds.toString());

					return value;

				});

//		get SensorStream
		final KTable<String, GenericRecord> sensorStream = builder.table(sensorsTopic);
//		Tranfrom mergedFoIObsData to Equals Key to Sensor
		final KStream<String, GenericRecord> mergedFoIObsDataThingKey = mergedFoIObsDataThing
				.map((key, value) -> KeyValue.pair(toJson(value, DATASTREAM, "Sensor"), value));

//		Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThingSensor = mergedFoIObsDataThingKey
				.leftJoin(sensorStream, (value, thing) -> {
					JSONObject ds = toJson(value, DATASTREAM);
					if (thing != null) {
						ds.put("Sensor", thing.toString());
						value.put(DATASTREAM, ds.toString());
					} else {
						value.put(DATASTREAM, "NO Sensor VALUE FOUND");
					}

					return value;
				});

//			get ObsPro
		final KTable<String, GenericRecord> propertyStream = builder.table(observedPropertiesTopic);
//			Tranfrom mergedFoIObsData to Equals Key to Sensor
		final KStream<String, GenericRecord> mergedFoIObsDataThingSensorKey = mergedFoIObsDataThingSensor
				.map((key, value) -> KeyValue.pair(toJson(value, DATASTREAM, "ObservedProperty"), value));

//			Join ThingsStream with MergedStream
		final KStream<String, String> finalStream = mergedFoIObsDataThingSensorKey.join(propertyStream,
				(value, thing) -> {

					if (thing != null) {
						JSONObject ds = toJson(value, DATASTREAM);
						ds.put("ObservedProperty", thing.toString());
						value.put(DATASTREAM, ds.toString());
					} else {
						value.put(DATASTREAM, "NO ObservedProperty VALUE FOUND");
					}
					return toJson(value).toJSONString();

				});

		final Serde<String> stringSerde = Serdes.String();

		finalStream.to(outputTopic, Produced.with(stringSerde, stringSerde));

		stringSerde.close();
	}

	/**
	 * This Methode generates a key String from a GenericRecord
	 * 
	 * @param record The stream record
	 * @param stream The name of the stream
	 * @param key    The property that will be returned with its values
	 * @return json Json-String
	 */
	public String toJson(GenericRecord record, String stream, String key) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser()
					.parse((stream == null) ? record.toString() : record.get(stream).toString());
			return (String) ds.get(key);
		} catch (ParseException e) {
			logger.warn(FOI_DOUBLE_PARSE_EXCEPTION, e);
		}
		return null;
	}

	/**
	 * 
	 * This Method generation a JSONOject from a Generic Record
	 * 
	 * @param record The stream record
	 * @param stream The name of the stream
	 * @return json Json-String
	 */
	public JSONObject toJson(GenericRecord record, String stream) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser()
					.parse((stream == null) ? record.toString() : record.get(stream).toString());
			return ds;
		} catch (ParseException e) {
			logger.warn(FOI_DOUBLE_PARSE_EXCEPTION, e);
		}
		return null;

	}

	/**
	 * Transform GenericRecord to Json
	 * 
	 * @param record The stream record
	 * @return json Json-String
	 */
	public JSONObject toJson(GenericRecord record) {
		return toJson(record, null);
	}

	@Override
	public void execute() throws InterruptedException {
		execute(new StreamsBuilder());
	}

}
