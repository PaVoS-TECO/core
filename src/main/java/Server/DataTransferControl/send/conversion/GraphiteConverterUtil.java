package server.dataTransferControl.send.conversion;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.slf4j.Logger;

import DataTransferControl.SerializationDeserialization.KafkaObservationData;

/**
 * Provides the functionality to create python metrics and log the results
 */
public class GraphiteConverterUtil {
	
    /**
     * Transforms a property into a Graphite-readable format with python
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     * @param name The name of the observed property
     * @param value The value the sensor registrated to the specified property
     */
    protected static void addFloatMetric(ConsumerRecord<String, KafkaObservationData> record, PyList list, String name, String value, Logger logger) {
    	if (value == null) {
            // Some values are optional or not giving data due to broken sensors etc
            return;
        }
    	
        LocalDateTime dateTime = LocalDateTime.parse(record.value().observationDate);
        
        PyString metricName = new PyString(record.topic() + "." + name);
        PyInteger timestamp = new PyInteger((int) dateTime.toEpochSecond(ZoneOffset.UTC));
        PyFloat metricValue = new PyFloat(Double.parseDouble(value));
        PyTuple metric = new PyTuple(metricName, new PyTuple(timestamp, metricValue));
        logMetric(logger, metric);
        list.add(metric);
    }

    /**
     * Documents our result-matrics
     * @param metric The metric formed from our original data value of a certain property
     */
    private static void logMetric(Logger logger, PyTuple metric) {
    	if (logger != null) {
    		logger.info("Added metric: " + metric.toString());
    	}
    }
	
}