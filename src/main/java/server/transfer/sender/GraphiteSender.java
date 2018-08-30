package server.transfer.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;

import server.transfer.config.GraphiteConfig;
import server.transfer.converter.GraphiteConverter;
import server.transfer.data.ObservationData;
import server.transfer.sender.connection.SocketManager;

/**
 * Sends data to Graphite
 */
public class GraphiteSender extends Sender {


	private SocketManager som;

	/**
	 * Default constructor
	 */
	public GraphiteSender() {
		this.som = new SocketManager(); 
		som.connect(GraphiteConfig.getGraphiteHostName(), GraphiteConfig.getGraphitePort());
	}
	
	/**
	 * Sends the recorded data to Graphite.
	 * Uses a record of multiple data objects.
	 * <p>
	 * @param records {@link Map}<{@link String}, {@link ObservationData}> records
	 * @return 
	 */
	public boolean send(Collection<ObservationData> records) {
		PyList list = new PyList();
		
		for (ObservationData record : records) {
			GraphiteConverter.addObservations(record, list);
		}
		
		PyString payload = cPickle.dumps(list);
		byte[] header = ByteBuffer.allocate(4).putInt(payload.__len__()).array();
		
		if (som.isConnectionClosed()) {
			som.reconnect();
		}
		
		try {
			OutputStream outputStream = som.getOutputStream();
			outputStream.write(header);
			outputStream.write(payload.toBytes());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			logger.error("Failed writing to Graphite.", e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Sends the recorded data to Graphite.
	 * Uses a record of multiple data objects.
	 * <p>
	 * @param records {@link ConsumerRecords}<{@link String}, {@link ObservationData}> records
	 * @return 
	 */
	@Override
	public boolean send(ConsumerRecords<String, ObservationData> records) {
		if (som.isConnectionClosed()) {
			som.reconnect();
		}
		
		PyList list = new PyList();

		records.forEach(record -> {
			GraphiteConverter.addObservations(record, list);
		});

		PyString payload = cPickle.dumps(list);
		byte[] header = ByteBuffer.allocate(4).putInt(payload.__len__()).array();

		try {
			OutputStream outputStream = som.getOutputStream();
			outputStream.write(header);
			outputStream.write(payload.toBytes());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			logger.error("Failed writing to Graphite.", e);
			return false;
		}
		
		return true;
	}

	/**
	 * Sends the recorded data to Graphite.
	 * Uses a single data object.
	 * <p>
	 * {@link String} topic, {@link ObservationData} data
	 * @param singleTopic The {@link String} topic. A KafkaTopic representing the source of the data.
	 * @param topic The name of the topic that this data belongs to
	 * @param data  The data that will be sent to Graphite
	 * @return 
	 */
	public boolean send(String singleTopic, ObservationData data) {
		HashMap<TopicPartition, List<ConsumerRecord<String, ObservationData>>> recordsMap 
		= new HashMap<TopicPartition, List<ConsumerRecord<String, ObservationData>>>();
		ArrayList<ConsumerRecord<String, ObservationData>> recordList 
		= new ArrayList<ConsumerRecord<String, ObservationData>>();
		ConsumerRecord<String, ObservationData> record 
		= new ConsumerRecord<String, ObservationData>(singleTopic, 0, 0, null, data);

		recordList.add(record);
		recordsMap.put(new TopicPartition(singleTopic, 0), recordList);
		ConsumerRecords<String, ObservationData> records 
		= new ConsumerRecords<String, ObservationData>(recordsMap);

		return this.send(records);
	}

	@Override
	public void close() {
		som.closeSocket();
	}
	
}