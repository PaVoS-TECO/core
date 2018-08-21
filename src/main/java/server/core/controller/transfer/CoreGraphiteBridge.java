package server.core.controller.transfer;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoGrid;
import server.transfer.Destination;
import server.transfer.TransferManager;
import server.transfer.data.ObservationData;

/**
 * Connects the core to graphite.
 */
public class CoreGraphiteBridge {
	
	private final String KAFKA_TOPIC;
	private final long UPDATE_MILLIS;
	private final long SEND_MILLIS;
	private GeoGrid geoGrid;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private TransferManager tm;
	
	/**
	 * Creates a basic {@link CoreGraphiteBridge} Instance.
	 * @param kafkaTopic {@link String}
	 * @param geoGrid {@link GeoGrid}
	 * @param updateMillis {@link long}
	 * @param sendMillis {@link long}
	 */
	public CoreGraphiteBridge(String kafkaTopic, GeoGrid geoGrid, long updateMillis, long sendMillis) {
		this.KAFKA_TOPIC = kafkaTopic;
		this.UPDATE_MILLIS = updateMillis;
		this.SEND_MILLIS = sendMillis;
		this.geoGrid = geoGrid;
		tm = new TransferManager();
	}
	
	/**
	 * Produces messages from the {@link GeoGrid} and sends it to the {@link GeoGrid}s output-topic.
	 * Messages produced are {@link ObservationData} objects in JSON form.
	 */
	public void produce() {
		geoGrid.produceSensorDataMessages();
		try {
			TimeUnit.MILLISECONDS.sleep(UPDATE_MILLIS);
		} catch (InterruptedException e) {
			logger.warn("ClusterDataRequest sleep-timer was interrupted. Attepting to update now." + e);
		}
	}
	
	/**
	 * Consumes messages from the {@link GeoGrid}s output-topic and sends the {@link ObservationData} objects to graphite.
	 */
	public void consumeAndSend() {
	    tm.startDataTransfer(KAFKA_TOPIC, geoGrid.getOutputTopic(), Destination.GRAPHITE);
	}
	
	public void close() {
		tm.stopDataTransfer();
	}
	
}
