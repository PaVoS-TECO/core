package server.transfer.sender;

import java.util.Collection;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;

import server.transfer.converter.GraphiteConverter;
import server.transfer.data.ObservationData;


/**
 * Sends data to the console in a readable format.
 */
public class ConsoleSender extends Sender {

	@Override
	public boolean send(ConsumerRecords<String, ObservationData> records) {
		PyList list = new PyList();

		records.forEach(record -> {
			GraphiteConverter.addObservations(record, list);
		});

		PyString payload = cPickle.dumps(list);
		System.out.println(payload.asString());
		return true;
	}
	
	@Override
	public boolean send(Collection<ObservationData> records) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void close() {
		
	}

}
