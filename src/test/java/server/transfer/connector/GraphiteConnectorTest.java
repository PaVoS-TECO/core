package server.transfer.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;
import server.transfer.sender.ConsoleSender;
import server.transfer.sender.util.TimeUtil;

public class GraphiteConnectorTest {
	
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
	
	private ObservationData setupCorrectData(ObservationData data) {
		return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest"
				, TimeUtil.getUTCDateTimeNowString(), "0", "0");
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation
			, String locationID, String locationName, String date, String pM10, String pM2p5) {
		data.observationDate = date;
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM2P5.toString(), pM2p5);
		return data;
	}
	
}
