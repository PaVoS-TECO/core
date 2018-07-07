package server.transfer.send;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;

import server.transfer.send.conversion.GraphiteConverter;
import server.transfer.serialization.KafkaObservationData;


/**
 * Reformats the data and outputs it into the console
 */
public class ConsoleSender extends Sender {

	@Override
	public void send(ConsumerRecords<String, KafkaObservationData> records) {
		PyList list = new PyList();

		records.forEach(record -> {
			GraphiteConverter.addPM(record, list, logger);
		});

		PyString payload = cPickle.dumps(list);
		System.out.println(payload.asString());
	}

}
