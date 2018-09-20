package edu.teco.pavos.transfer.converter.util;


import java.util.Map;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.teco.pavos.transfer.converter.GraphiteConverter;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.data.util.GridTopicTranslator;

/**
 * Provides the functionality to create python metrics and log the results
 */
public final class PythonMetricUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphiteConverter.class);
	
	private PythonMetricUtil() {
		
	}
    
    /**
     * Transforms a property into a Graphite-readable format with python
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     * @param observations Maps each set observed property to a value
     */
    public static void addFloatMetric(ObservationData record, 
    		PyList list, Map<String, String> observations) {
		for (Map.Entry<String, String> entry : observations.entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				LocalDateTime ldc = LocalDateTime.parse(
						record.getObservationDate(), DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
				PyString metricName = new PyString(GridTopicTranslator.getTopic(
						record.getSensorID(), record.getClusterID()) + "." + entry.getKey());
				PyInteger timestamp = new PyInteger((int) (ldc.toDateTime(DateTimeZone.UTC).getMillis() / 1000));
				PyFloat metricValue = new PyFloat(Double.parseDouble(value));
				PyTuple metric = new PyTuple(metricName, new PyTuple(timestamp, metricValue));
				list.append(metric);
				logMetric(metric);
			}
		}
    }

    private static void logMetric(PyTuple metric) {
    	if (LOGGER != null) {
    		LOGGER.debug("Added metric: {}", metric);
    	}
    }
	
}