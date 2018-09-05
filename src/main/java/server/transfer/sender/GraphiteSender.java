package server.transfer.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

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
		if (som == null) return false;
		if (som.isConnectionClosed()) som.reconnect();
		if (som.isConnectionClosed()) return false;
		
		PyList list = new PyList();
		
		for (ObservationData record : records) {
			GraphiteConverter.addObservations(record, list);
		}
		
		PyString payload = cPickle.dumps(list);
		byte[] header = ByteBuffer.allocate(4).putInt(payload.__len__()).array();
		
		if (som.isConnectionClosed()) {
			som.reconnect();
		}
		
		return writeToGraphite(header, payload);
	}

	@Override
	public void close() {
		som.closeSocket();
	}
	
	private boolean writeToGraphite(byte[] header, PyString payload) {
		if (som == null || som.isConnectionClosed()) return false;
		try {
			OutputStream outputStream = som.getOutputStream();
			if (outputStream == null) {
				logger.error("Could not send data to Graphite. OutputStream not connected.");
				return false;
			}
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
	
}