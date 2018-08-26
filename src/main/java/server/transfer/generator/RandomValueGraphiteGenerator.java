package server.transfer.generator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;
import server.transfer.sender.GraphiteSender;
import server.transfer.sender.util.TimeUtil;

/**
 * Generates random values and sends them to Graphite.
 * Use to quickly check the appearance of data in Graphite and Grafana.
 */
public final class RandomValueGraphiteGenerator {

	private RandomValueGraphiteGenerator() {
		
	}
	
	private static final int MAX_VALUE = 30;
	private static final String TOPIC = "RandomGraphiteSenderTest";
	private static boolean loop = true;

	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {

			public void run() {
				
				GraphiteSender sender = new GraphiteSender();
				
					while (loop) {
						ObservationData data = null;
						data = generateRandomData(data, MAX_VALUE);
						
						try {
							TimeUnit.MILLISECONDS.sleep(9900);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						syncDateTime();
						sender.send(TOPIC, data);
					}
			}
			
			private void syncDateTime() {
				boolean sync = true;
				while (sync) {
					LocalDateTime dateTime = LocalDateTime.now();
					String time = String.valueOf(dateTime.toEpochSecond(ZoneOffset.UTC));
					char c = time.charAt(time.length() - 1);
					if (c=='0') sync = false;
				}
			}
			
			private ObservationData generateRandomData(ObservationData data, int maxValue) {
				int value_PM10 = (int) (Math.random() * maxValue);
				int value_PM2p5 = (int) (Math.random() * maxValue);
				data = new ObservationData();
				data.sensorID = "exampleSensor";
				data.observationDate = TimeUtil.getUTCDateTimeNowString();
				data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), String.valueOf(value_PM10));
				data.observations.put(ObservationType.PARTICULATE_MATTER_PM2P5.toString(), String.valueOf(value_PM2p5));
				return data;
			}
			
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
