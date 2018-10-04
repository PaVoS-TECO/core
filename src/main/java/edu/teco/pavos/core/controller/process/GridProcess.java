package edu.teco.pavos.core.controller.process;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.streams.StreamsBuilder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.teco.pavos.core.grid.GeoGrid;
import edu.teco.pavos.core.grid.GeoGridManager;
import edu.teco.pavos.core.properties.GridPropertiesFileManager;
import edu.teco.pavos.core.properties.KafkaPropertiesFileManager;
import edu.teco.pavos.core.properties.KafkaTopicAdmin;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * The {@link GridProcess} preprocesses the incoming data and adds the sensors
 * to the grid.
 */
public class GridProcess extends BasicProcess {
	private static final String KEY = "FeatureOfInterest";
	private final String inputTopic;
	private boolean doProcessing = true;
	private volatile GeoGrid grid;

	/**
	 * Creates a new {@link GridProcess}.
	 * 
	 * @param inputTopic The name of the topic which provides the data to feed to
	 *                   the grid
	 */
	public GridProcess(String inputTopic) {
		KafkaTopicAdmin admin = KafkaTopicAdmin.getInstance();
		admin.createTopic(inputTopic);

		this.inputTopic = inputTopic;

		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		this.props = propManager.getGridStreamProperties();
		logger.info("Creating thread: {}", threadName);
	}

	/**
	 * Creates a new {@link GridProcess}. <br>
	 * Sets the input topic name to "ObservationsMergesGeneric".
	 */
	public GridProcess() {
		this("ObservationsMergesGeneric");
	}

	/**
	 * 
	 * Convert a {@link JSONObject} to a {@link Map} with both key and value as
	 * {@link String}.
	 * 
	 * @param json {@link JSONObject}
	 * @return map {@link Map}
	 */
	private Map<String, Double> setPropetiesSensoring(JSONObject json) {
		Map<String, Double> map = new HashMap<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			map = mapper.readValue(json.toJSONString(), new TypeReference<Map<String, Double>>() {
			});
			return map;
		} catch (JsonGenerationException e) {
			logger.warn("Failed to generate json.", e);
		} catch (JsonMappingException e) {
			logger.warn("Failed to map json.", e);
		} catch (IOException e) {
			logger.warn("Could not read values of json.", e);
		}
		return map;
	}

	@Override
	public boolean stop() {
		this.doProcessing = false;
		boolean result = super.stop();
		if (grid != null)
			grid.close();
		return result;
	}

	// TODO - FIX Tempeture
	@Override
	public void execute() throws InterruptedException {
		try (KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList(inputTopic));
			logger.info("Started consumer grid.");
			GridPropertiesFileManager.getInstance();
			GeoGridManager gridManager = GeoGridManager.getInstance();
			grid = gridManager.getNewestGrid();

			TimeUnit.SECONDS.sleep(1);

			while (this.doProcessing) {
				final ConsumerRecords<String, GenericRecord> observations = consumer.poll(100);
				observations.forEach(record1 -> {
					GenericRecord value = (record1.value());

					String time = TimeUtil.removeMillis((String) value.get("phenomenonTime").toString());
					String resultValue = value.get("result").toString();

					JSONParser parser = new JSONParser();
					try {
						JSONObject json = (JSONObject) parser.parse(resultValue);
						String sensorID = record1.value().get("Datastream").toString();
						ObservationData data = new ObservationData();

						data.setObservationDate(time);
						data.setSensorID(sensorID);
						data.addSingleObservations(setPropetiesSensoring(json));

						double coord1 = Double.parseDouble(value.get(KEY).toString().split(",")[0]);
						double coord2 = Double.parseDouble(value.get(KEY).toString().split(",")[1]);

						Point2D.Double location = new Point2D.Double(coord1, coord2);
						grid.addObservation(location, data);
					} catch (ParseException e) {
						logger.warn("Could not parse FeatureOfInterest value to double.", e);
					} catch (ClassCastException e) {
						logger.warn("Could not cast value to JSONObject.", e);
					}
				});
			}
		}
	}

	@Override
	public void run() {
		logger.info("Running thread: {}", threadName);
		try {
			execute();
		} catch (InterruptedException e) {
			logger.warn("Interruption of thread while execution: {}", threadName);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void execute(StreamsBuilder builder) throws InterruptedException {
		execute();
	}

}
