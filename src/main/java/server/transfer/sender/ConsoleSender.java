package server.transfer.sender;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

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
	 * Sends the recorded data to the console.
	 * Uses a record of multiple data objects.
	 * <p>
	 * @param records {@link Collection} of {@link ObservationData}
	 * @return sendingSuccessful {@link Boolean}
	 */
	public boolean send(Collection<ObservationData> records) {
		
		PyList list = new PyList();
		
		for (ObservationData record : records) {
			GraphiteConverter.addObservations(record, list);
		}
		
		PyString payload = cPickle.dumps(list);
		byte[] header = ByteBuffer.allocate(4).putInt(payload.__len__()).array();
		
		logger.debug("Sender-Header: " + Arrays.toString(header));
		logger.debug("Sender-Payload: " + payload.toString());
		return true;
	}
	
	@Override
	public void close() {
		
	}

}
