package DataTransferControl.send.conversion;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.python.core.PyList;
import org.slf4j.Logger;

import DataTransferControl.SerializationDeserialization.KafkaObservationData;

/**
 * Converts different observed properties to python metrics
 */
public class GraphiteConverter {
	
	/**
     * Adds the sensor-observed property 'particulate matter' to the collection of properties that will be sent
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     */
    public static void addPM(ConsumerRecord<String, KafkaObservationData> record, PyList list, Logger logger) {
    	GraphiteConverterUtil.addFloatMetric(record, list, "particulateMatter", record.value().particulateMatter, logger);
    }
	
}
