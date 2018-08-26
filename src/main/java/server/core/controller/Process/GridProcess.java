package server.core.controller.Process;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

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

import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.core.properties.PropertiesFileManager;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

/**
 * This Class is for The Processing of the Data for Graphite and the View , to
 * represente the Sensors
 */

public class GridProcess implements ProcessInterface, Runnable {

	private Properties props;
	private int timeIntervall;
	private String inputTopic;
	private int roundsCounter;
	private final String threadName = "GridProcess";

	private boolean threadBoolean = true;
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
		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		this.props = propManager.getGridStreamProperties();
		this.timeIntervall = timeIntervall;
		this.inputTopic = inputTopic;
		System.out.println("Creating " + threadName);
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
		Map<String, String> map = new HashMap<String, String>();
		try {

			ObjectMapper mapper = new ObjectMapper();

			// convert JSON string to Map
			map = mapper.readValue(json.toJSONString(), new TypeReference<Map<String, String>>() {
			});
			return map;

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		System.out.println("Starting " + threadName);
		if (thread == null) {
			thread = new Thread(this, threadName);

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
		System.out.println("Closing " + threadName);
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

				e.printStackTrace();
			}

			System.out.println(threadName + "successfully stopped.");
			return true;

		}
		return false;
	}
	
	/**
	 * This Methode definite the Process of the Application. What Application does
	 * specificly.
	 * 
	 * @param streambuilder
	 * @return true if the Graphite Process got Successfully worked
	 */

	public void apply(StreamsBuilder builder) {

		KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(inputTopic));
		System.out.println("Consumer Grid gestartet!");
		grid = new GeoRecRectangleGrid(new Point2D.Double(WorldMapData.lngRange * 2, WorldMapData.latRange * 2), 2, 2,
				3);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (this.threadBoolean) {

			final ConsumerRecords<String, GenericRecord> observations = consumer.poll(100);
			// System.out.println(observations.count());
			observations.forEach(record1 -> {
				GenericRecord value = (record1.value());

				String time = TimeUtil.removeMillis((String) value.get("phenomenonTime").toString());
				;
				String resultValue = (String) value.get("result").toString();
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
					System.out.println(data);
					grid.addObservation(location, data);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		}

		//grid.produceSensorDataMessages();

	}

	@Override
	public void run() {
		System.out.println("Running " + threadName);
		// updateGrid();

		apply(null);

	}
	
//	public void updateGrid() {
//	Thread t = new Thread(new Runnable() {
//		
//
//
//		@Override
//		public void run() {
//			while(threadBoolean) {
//
//				
//				if(grid != null) {
//					System.out.println("rounds " +roundsCounter ++);
//					grid.updateObservations();
//					grid.produceSensorDataMessages();
//					
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//			
//		}
//	});
//	t.start();
//	System.out.println("GridUpdate started");
//	
//}


}
