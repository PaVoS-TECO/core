package server.transfer.converter.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.transfer.converter.GraphiteConverter;
import server.transfer.data.ObservationData;

/**
 * Provides the functionality to create python metrics and log the results
 */
public final class PythonMetricUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(GraphiteConverter.class);
	
	private PythonMetricUtil() {
		
	}
	
    /**
     * Transforms a property into a Graphite-readable format with python
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     * @param observations Maps each set observed property to a value
     * @param logger The logger documents 
     */
    public static void addFloatMetric(ConsumerRecord<String, ObservationData> record, 
    		PyList list, Map<String, String> observations) {
		for (Map.Entry<String, String> entry : observations.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key != null && value != null) {
				
				LocalDateTime dateTime = LocalDateTime.parse(record.value().observationDate);

				PyString metricName = new PyString(record.topic() + "." + key);
				PyInteger timestamp = new PyInteger((int) dateTime.toEpochSecond(ZoneOffset.UTC));
				PyFloat metricValue = new PyFloat(Double.parseDouble(value));
				PyTuple metric = new PyTuple(metricName, new PyTuple(timestamp, metricValue));
				list.append(metric);
				logMetric(metric);
			}
		}
    }

    private static void logMetric(PyTuple metric) {
    	if (logger != null) {
    		logger.info("Added metric: " + metric.toString());
    	}
    }
	
}