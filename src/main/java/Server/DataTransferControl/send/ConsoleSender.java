package server.dataTransferControl.send;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;

import DataTransferControl.SerializationDeserialization.KafkaObservationData;
import DataTransferControl.send.conversion.GraphiteConverter;


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
