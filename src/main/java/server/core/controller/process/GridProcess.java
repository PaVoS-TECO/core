package server.core.controller.process;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.core.properties.KafkaPropertiesFileManager;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

/**
 * This Class is for The Processing of the Data for Graphite and the View , to
 * represente the Sensors
 */

public class GridProcess implements ProcessInterface, Runnable {

	private static final String THREAD_NAME = "GridProcess";
	private boolean threadBoolean = true;
	private int roundsCounter;
	private int timeIntervall;	
	private String inputTopic;
	private Properties props;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Thread thread;
	private CountDownLatch countdownLatch = null;
	private volatile GeoGrid grid;

	
	
	/**
	 * 
	 * This initialize the Grid Process with Different Parameters as it should be
	 * @param inputTopic
	 * @param timeIntervall
	 */
	
	public GridProcess(String inputTopic, int timeIntervall) {
		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		this.props = propManager.getGridStreamProperties();
		this.timeIntervall = timeIntervall;
		this.inputTopic = inputTopic;
		logger.info("Creating thread: {}", THREAD_NAME);
	}

	/**
	 * Default Constructor
	 */
	
	public GridProcess() {
		this("ObservationsMergesGeneric", 10000);
	}
	
	

	/**
	 * 
	 * This Methode generates a Map out of a Json
	 * @param json 
	 * @return Map<String,String>
	 */
	
	private Map<String, String> setPropetysSensoring(JSONObject json) {
		Map<String, String> map = new HashMap<>();
		try {

			ObjectMapper mapper = new ObjectMapper();

			// convert JSON string to Map
			map = mapper.readValue(json.toJSONString(), new TypeReference<Map<String, String>>() {
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

	/**
	 * This Method is used to explicitly start the Kafka Stream thread. So that
	 * theProcessing need to get started.
	 * 
	 * @return true if the Kafka Stream Started false otherwise
	 */
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
	/**
	 * This Method is used to explicitly close the Kafka Stream thread. So that the
	 * Processing stops.
	 * 
	 * @return true if the Kafka Stream closed, false otherwise
	 */

	public boolean kafkaStreamClose() {
		logger.info("Closing thread: {}", THREAD_NAME);
		if (countdownLatch != null) {
			countdownLatch.countDown();
		}
		if (grid != null) {
			grid.close();
		}

		if (thread != null) {
			this.threadBoolean = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				thread.interrupt();
				logger.warn("Interruption of thread: {}", THREAD_NAME);
			}

			logger.info("Stopped thread successfully: {}", THREAD_NAME);
			return true;

		}
		return false;
	}
	
	/**
	 * This Methode definite the Process of the Application. What Application does
	 * specificly.
	 * 
	 * @return true if the Graphite Process got Successfully worked
	 * @throws InterruptedException 
	 */

	public void apply() throws InterruptedException {
		try (KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList(inputTopic));
			logger.info("Started consumer grid.");
			grid = new GeoRecRectangleGrid(new Rectangle2D.Double(-WorldMapData.lngRange, -WorldMapData.latRange,
					WorldMapData.lngRange * 2, WorldMapData.latRange * 2), 2, 2, 3);

			TimeUnit.SECONDS.sleep(1);

			while (this.threadBoolean) {

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

						data.observationDate = time;
						data.sensorID = sensorID;
						data.observations = setPropetysSensoring(json);

						double coord1 = Double.parseDouble(value.get("FeatureOfInterest").toString().split(",")[0]);
						double coord2 = Double.parseDouble(value.get("FeatureOfInterest").toString().split(",")[1]);

						Point2D.Double location = new Point2D.Double(coord1, coord2);
						grid.addObservation(location, data);
					} catch (ParseException e) {
						logger.warn("Could not parse FeatureOfInterest value to double.", e);
					}

				});
			}
		}

	}

	@Override
	public void run() {
		logger.info("Starting thread: {}", THREAD_NAME);
		Thread t = new Thread(() -> {
			try {
				apply();
			} catch (InterruptedException e) {
				logger.warn("Interruption of thread while execution: {}", THREAD_NAME);
				Thread.currentThread().interrupt();
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			thread.interrupt();
			logger.warn("Interruption of thread while joining threads: {}", THREAD_NAME);
		}
	}

	@Override
	public void apply(StreamsBuilder builder) throws InterruptedException {
		apply();
	}
	
}
