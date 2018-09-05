package server.transfer.sender;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;

import server.transfer.converter.GraphiteConverter;
import server.transfer.data.ObservationData;


/**
 * Sends data to the console in a readable format.
 */
public class ConsoleSender extends Sender {
	
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
		
		logger.debug("Sender-Header: " + header.toString());
		logger.debug("Sender-Payload: " + payload.toString());
		return true;
	}
	
	@Override
	public void close() {
		
	}

}
