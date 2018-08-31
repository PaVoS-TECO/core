package edu.teco.pavos.pim;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the FileReaderStrategy interface to generate continuous data
 */
public class DummyReaderStrategy implements FileReaderStrategy {

	private int counter = 0;
	private int maxCounter = 100000;
	private int maxThings = 100;
	private HashMap<String, String> desc;
	private String url = "http://pavos-01.teco.edu/8080/v1.0/";
	
	/**
	 * Constructor
	 */
	public DummyReaderStrategy() {
		
		this.desc = new HashMap<String, String>();
		desc.put("Acceleration", "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#Acceleration");
		desc.put("Weight", "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#Weight");
		desc.put("Luminance", "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#Luminance");
		desc.put("MagnetomotiveForce", "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#MagnetomotiveForce");
		
	}

	/**
     * Reads from a File as specified by the FilePath and sends the information in
     * it to the FROST-Server using the FrostSender that was provided.
     * @param file Is the File to Import.
     */
	public void sendFileData(File file) {
		
		this.createObservedProperties();
		this.createSensors();
		this.createThings();
		this.createDataStreams();
		this.createObservations();
		
	}
	
	private void createSensors() {
		
		for (String key : desc.keySet()) {
			
			String id = "pavos.teco.edu/Sensors/" + key;
			String sensor = "{"
					+ "\"name\": \"JR21" + key.charAt(0) + "\","
					+ "\"description\": \"Jelumar 21 " + key + " Sensor\","
					+ "\"encodingType\": \"application/pdf\","
					+ "\"metadata\": \"https://cdn-shop.adafruit.com/datasheets/DHT22.pdf\","
					+ "\"@iot.id\": \"" + id + "\""
					+ "}";
			FrostSender.sendToFrostServer(url + "Sensors", sensor);
			
		}
		
	}
	
	private void createObservedProperties() {
		
		for (String key : desc.keySet()) {
			
			String id = "pavos.teco.edu/ObservedProperties/" + key;
			String observedProperty = "{\"name\": \"" + key + "\", \"description\":  \"" + key
					+ "\", \"definition\": \"" + desc.get(key) + "\", \"@iot.id\": \"" + id + "\"}";
			FrostSender.sendToFrostServer(url + "ObservedProperties", observedProperty);
			
		}
		
	}
	
	private void createThings() {
		
		for (int i = 0; i < maxThings; i++) {
			
			String id = "pavos.teco.edu/Things/Thing" + i + "J";
			int lon = (int) (Math.random() * 360 - 180);
			int lat = (int) (Math.random() * 180 - 90);
			String thing = "{\"name\": \"Thing Number " + i + "\","
					+ "\"description\": \"Thing Number " + i + " containing multiple Sensors.\","
					+ "\"@iot.id\": \"" + id + "\","
					+ "\"Locations\": [{"
					+ "\"name\": \"T" + i + " Loc\","
					+ "\"description\": \"Location of Thing Number " + i + ".\","
					+ "\"encodingType\": \"application/vnd.geo+json\","
					+ "\"location\": {"
					+ "\"type\": \"Point\","
					+ "\"coordinates\": [" + lon + ".0, " + lat + ".0]"
					+ "}"
					+ "}]}";
			FrostSender.sendToFrostServer(url + "Things", thing);
			
		}
		
	}
	
	private void createDataStreams() {
		
		for (int i = 0; i < maxThings; i++) {
			
			String id = "pavos.teco.edu/Things/Thing" + i + "J";
			for (String key : desc.keySet()) {
				String sid = "pavos.teco.edu/Datastreams/" + key + i + "DS";
				String stream = "{"
						+ "  \"name\": \"" + key + " DS " + i + "\","
						+ "  \"description\": \"Datastream for recording " + key + "\","
						+ "  \"@iot.id\": \"" + sid + "\","
						+ "  \"observationType\": "
						+ "\"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement\","
						+ "  \"unitOfMeasurement\": {"
						+ "    \"name\": \"Degree Celsius\","
						+ "    \"symbol\": \"degC\","
						+ "    \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#DegreeCelsius\""
						+ "  },"
						+ "  \"Thing\":{\"@iot.id\": \"" + id + "\"},"
						+ "  \"ObservedProperty\":{\"@iot.id\":\"pavos.teco.edu/ObservedProperties/" + key + "\"},"
						+ "  \"Sensor\":{\"@iot.id\":\"pavos.teco.edu/Sensors/" + key + "\"}"
						+ "}";
				FrostSender.sendToFrostServer(url + "Datastreams", stream);
			}
			
		}
		
	}
	
	private void createObservations() {
		
		while (maxCounter > counter++) {
			
			for (int i = 0; i < maxThings; i++) {
				
				LocalDateTime now = LocalDateTime.now();
				int year = now.getYear();
				int month = now.getMonthValue();
				int day = now.getDayOfMonth();
				int hour = now.getHour();
				int minute = now.getMinute();
				int second = now.getSecond();
				for (String key : this.desc.keySet()) {
					
					String sid = "pavos.teco.edu/Datastreams/" + key + i + "DS";
					String obs = "{"
							+ "  \"phenomenonTime\": \"" + year + "-" + t(month) + "-" + t(day) + "T"
							+ t(hour) + ":" + t(minute) + ":" + t(second) + ".000Z\","
							+ "  \"resultTime\": \"" + year + "-" + t(month) + "-" + t(day) + "T"
							+ t(hour) + ":" + t(minute) + ":" + t(second) + ".500Z\","
							+ "  \"result\" : " + (Math.random() * 200 - 100) + ","
							+ "\"Datastream\":{\"@iot.id\":\"" + sid + "\"}"
							+ "}";
					FrostSender.sendToFrostServer(url + "Observations", obs);
					
					if (i == 0) {
						System.out.println(obs);
					}
					
				}
				
			}
			
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				System.out.println(e.getLocalizedMessage());
			}
			
		}
		
	}
	
	private String t(int number) {
		
		if (number == 0) {
			return "00";
		}
		
		if (number < 10) {
			return "0" + number;
		}
		
		return "" + number;
		
	}

}
