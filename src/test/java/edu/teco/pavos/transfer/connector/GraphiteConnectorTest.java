package edu.teco.pavos.transfer.connector;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.data.ObservationType;
import edu.teco.pavos.transfer.sender.ConsoleSender;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * Tests {@link GraphiteConnector}
 */
public class GraphiteConnectorTest {
	
	/**
	 * Tests setting up needed prerequisites and sending
	 * data to the console.
	 * @throws InterruptedException The {@link Thread} was interrupted while trying to sleep or join.
	 */
	@Test
	public void sendConvertedResultToConsole() throws InterruptedException {
		ObservationData data = new ObservationData();
		setupCorrectData(data);
		
		Collection<ObservationData> records = new ArrayList<>();
		records.add(data);
		
		final GraphiteConnector connector = new GraphiteConnector(records);
		
		Thread t = new Thread(new Runnable() {
	        public void run() {
	        	connector.run(new ConsoleSender());
	        }
	    });
	    t.start();
	    TimeUnit.SECONDS.sleep(2);
		connector.stop();
		t.join();
	}
	
	/**
	 * Tests sending an empty record.
	 * @throws InterruptedException The {@link Thread} was interrupted while trying to sleep or join.
	 */
	@Test
	public void sendEmptyRecord() throws InterruptedException {
		ObservationData data = new ObservationData();
		setupCorrectData(data);
		
		final GraphiteConnector connector = new GraphiteConnector(new ArrayList<>());
		
		Thread t = new Thread(new Runnable() {
	        public void run() {
	        	assertFalse(connector.run(new ConsoleSender()));
	        }
	    });
	    t.start();
	    TimeUnit.SECONDS.sleep(2);
		connector.stop();
		t.join();
	}
	
	private ObservationData setupCorrectData(ObservationData data) {
		return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest",
				TimeUtil.getUTCDateTimeNowString(), 0.0, 0.0);
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation,
			String locationID, String locationName, String date, Double pM10, Double pM2p5) {
		data.setSensorID("testSensor");
		data.setClusterID("testGrid-1_1_1:1_1");
		data.setObservationDate(date);
		data.addSingleObservation(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		data.addSingleObservation(ObservationType.PARTICULATE_MATTER_PM2P5.toString(), pM2p5);
		return data;
	}
	
}
