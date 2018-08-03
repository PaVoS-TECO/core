package server.transfer.converter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.python.core.PyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.transfer.converter.util.PythonMetricUtil;
import server.transfer.data.ObservationData;

/**
 * Converts different observed properties to python metrics
 */
public final class GraphiteConverter {
	
	private static final Logger logger = LoggerFactory.getLogger(GraphiteConverter.class);
	
	private GraphiteConverter() {
		
	}
	
	/**
     * Adds the sensor-observed properties to the collection of properties that will be sent
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     * @param logger Documents the metrics created by the {@link PythonMetricUtil}
     */
    public static void addObservations(ConsumerRecord<String, ObservationData> record, PyList list) {
    	if (!isDataReadable(record, list)) return;
    	PythonMetricUtil.addFloatMetric(record, list, record.value().observations);
    }
    
    private static boolean isDataReadable(Object...objects) {
    	if (isAnyNull(objects)) {
    		logger.error("parameters must not be null" + new NullPointerException());
    		return false;
    	}
    	return true;
    }
    
    private static boolean isAnyNull(Object...objects) {
    	for (Object object:objects) {
    		if (object == null) return true;
    	}
    	return false;
    }
    
}
