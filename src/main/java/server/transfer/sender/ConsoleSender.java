package server.transfer.sender;

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
	public void send(ConsumerRecords<String, ObservationData> records, String graphTopic) {
		PyList list = new PyList();

		records.forEach(record -> {
			GraphiteConverter.addObservations(record, list, graphTopic);
		});

		PyString payload = cPickle.dumps(list);
		System.out.println(payload.asString());
	}

	@Override
	public void close() {
		
	}

}
