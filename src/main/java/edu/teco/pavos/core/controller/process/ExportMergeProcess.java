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
 * The {@link ExportMergeProcess} merges the contents of multiple Kafka-Topics
 * into a single one from Observations, Thing,Datastream, ObservedProperty and
 * FeatureOfInterest.
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
	private StreamsBuilder builder;

	/**
	 * Creates a new {@link ExportMergeProcess} with the specified topics.
	 * 
	 * @param observationsTopic       Kafka Topic of observations
	 * @param featureOfInterestTopic  Kafka Topic of featuresOfInterest
	 * @param thingsTopic             Kafka Topic of things
	 * @param datastreamsTopic        Kafka Topic of datastreams
	 * @param sensorsTopic            Kafka Topic of sensors
	 * @param observedPropertiesTopic Kafka Topic of observed properties
	 * @param outputTopic             Kafka Topic of this processes results
	 */
	public ExportMergeProcess(String observationsTopic, String featureOfInterestTopic, String thingsTopic,
			String datastreamsTopic, String sensorsTopic, String observedPropertiesTopic, String outputTopic) {
		KafkaTopicAdmin admin = KafkaTopicAdmin.getInstance();
		admin.createTopic(observationsTopic);
		admin.createTopic(featureOfInterestTopic);
		admin.createTopic(thingsTopic);
		admin.createTopic(datastreamsTopic);
		admin.createTopic(sensorsTopic);
		admin.createTopic(observedPropertiesTopic);
		
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
	 * Creates a new {@link ExportMergeProcess}.
	 * <p>
	 * Sets {@code observationsTopic} to {@code "Observations"}<br>
	 * , {@code featureOfInterestTopic} to {@code "FeaturesOfInterest"}<br>
	 * , {@code thingsTopic} to {@code "Things"}<br>
	 * , {@code datastreamsTopic} to {@code "Datastreams"}<br>
	 * , {@code sensorsTopic} to {@code "Sensors"}<br>
	 * {@code observedPropertiesTopic} to {@code "ObservedProperties"}<br>
	 * and {@code outputTopic} to {@code "AvroExport"}
	 */
	public ExportMergeProcess() {
		this("Observations", "FeaturesOfInterest", "Things", "Datastreams", "Sensors", "ObservedProperties",
				"AvroExport");
	}

	@Override
	public void execute(StreamsBuilder builder) {
		this.builder = builder;
		final Serde<String> stringSerde = Serdes.String();
		
		generateOutputStream(stringSerde);

		stringSerde.close();
		this.builder = null;
	}
	
	private void generateOutputStream(Serde<String> stringSerde) {

		// *********************************************
		// LEITFADEN : MINDERHEIT = TABLE
		// KStream neu mappen auf key von minderheit
		// dann joinen
		// ********************************************

		final KStream<String, GenericRecord> mergedFoIObsDataThingSensorKey =
				getMergedFoIObsDataThingSensorKey(
						getThingTableJson(getStream(observationsTopic)),
						getTable(sensorsTopic));
		
		final KStream<String, String> outputStream =
				tableJoinThingsWithMerged(getTable(observedPropertiesTopic), mergedFoIObsDataThingSensorKey);
		outputStream.to(outputTopic, Produced.with(stringSerde, stringSerde));
	}
	
	private KStream<String, GenericRecord> getThingTableJson(KStream<String, GenericRecord> observationStream) {
		KStream<String, GenericRecord> tmpStream = streamMapSimple(observationStream, "FeatureOfInterest");
		tmpStream = streamJoinObsFoiCheckLocation(tmpStream, getTable(featureOfInterestTopic));
		tmpStream = streamMapSimple(tmpStream, DATASTREAM);
		tmpStream = streamJoinTableSimple(tmpStream, getTable(datastreamsTopic));
		tmpStream = streamMapToJsonAndValue(tmpStream, "Thing");
		return streamJoinTableJson(tmpStream, getTable(thingsTopic), "Thing");
	}
	
	private KStream<String, GenericRecord> getMergedFoIObsDataThingSensorKey(KStream<String, GenericRecord> thingStream,
			KTable<String, GenericRecord> sensorStream) {
		KStream<String, GenericRecord> tmpStream = streamMapToJsonAndValue(thingStream, "Sensor");
		tmpStream = streamLeftJoinTableJson(tmpStream, sensorStream, "Sensor");
		return streamMapToJsonAndValue(tmpStream, "ObservedProperty");
	}
	
	private KStream<String, GenericRecord> streamJoinObsFoiCheckLocation(KStream<String, GenericRecord> obsStreamKey,
			KTable<String, GenericRecord> foITable) {
		return obsStreamKey.join(foITable, (value, location) -> {
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
	}
	
	private KStream<String, GenericRecord> streamJoinTableJson(KStream<String, GenericRecord> stream,
			KTable<String, GenericRecord> table, String jsonKey) {
		return stream.join(table, (value, thing) -> streamAnyJoinTableMapping(value, thing, jsonKey));
	}
	
	private KStream<String, GenericRecord> streamLeftJoinTableJson(KStream<String, GenericRecord> stream,
			KTable<String, GenericRecord> table, String jsonKey) {
		return stream.leftJoin(table, (value, thing) -> streamAnyJoinTableMapping(value, thing, jsonKey));
	}
	
	private KStream<String, GenericRecord> streamJoinTableSimple(KStream<String, GenericRecord> stream,
			KTable<String, GenericRecord> table) {
		return stream.join(table, (value, data) -> {
			value.put(DATASTREAM, data.toString());
			return value;
		});
	}
	
	private GenericRecord streamAnyJoinTableMapping(GenericRecord record1, GenericRecord record2, String jsonKey) {
		JSONObject ds = toJson(record1, DATASTREAM);
		if (record2 != null) {
			jsonPut(ds, jsonKey, record2.toString());
			record1.put(DATASTREAM, ds.toString());
		} else {
			record1.put(DATASTREAM, "NO VALUE FOUND");
		}
		return record1;
	}
	
	private KStream<String, GenericRecord> getStream(String topic) {
		return builder.stream(topic);
	}
	
	private KTable<String, GenericRecord> getTable(String topic) {
		return builder.table(topic);
	}
	
	private KStream<String, GenericRecord> streamMapSimple(KStream<String, GenericRecord> stream, String mappingKey) {
		return stream.map((key, value) -> KeyValue.pair(value.get(mappingKey).toString(), value));
	}
	
	private KStream<String, GenericRecord> streamMapToJsonAndValue(KStream<String, GenericRecord> stream,
			String jsonKey) {
		return stream.map((key, value) -> KeyValue.pair(toJson(value, DATASTREAM, jsonKey), value));
	}

	private KStream<String, String> tableJoinThingsWithMerged(KTable<String, GenericRecord> thingsTable,
			KStream<String, GenericRecord> mergedStream) {
		return mergedStream.join(thingsTable, (value, thing) -> {

			if (thing != null) {
				JSONObject ds = toJson(value, DATASTREAM);
				jsonPut(ds, "ObservedProperty", thing.toString());
				value.put(DATASTREAM, ds.toString());
			} else {
				value.put(DATASTREAM, "NO ObservedProperty VALUE FOUND");
			}
			return toJson(value).toJSONString();

		});
	}

	@SuppressWarnings("unchecked")
	private void jsonPut(JSONObject json, String key, String value) {
		json.put(key, value);
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
