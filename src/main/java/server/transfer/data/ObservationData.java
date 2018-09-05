package server.transfer.data;

import java.util.HashMap;
import java.util.Map;

/**
 * A serializable object that contains the observed data
 */
public class ObservationData implements java.io.Serializable {

	/**
	 * The unique identifier of this object
	 */
	private static final long serialVersionUID = 5410581483969835624L;

	/**
	 * The identificator of the sensor that created this observation
	 */
    public String sensorID;
    
    /**
     * The identificator of the cluster that contains the sensor
     */
    public String clusterID;
    
    /**
     * The date of the observation
     */
    public String observationDate;

    // Attributes listed below here are observed properties //
    
    /**
     *  The different observed properties (keys) and their corresponding values
     */
    public Map<String, String> observations = new HashMap<>();
    
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("{");
    	builder.append("clusterID=" + clusterID + ", ");
    	builder.append("sensorID=" + sensorID + ", ");
    	builder.append("observationDate=" + observationDate + ", ");
    	builder.append("observations=" + observations.toString());
    	builder.append("}");
    	return builder.toString();
    }
    
}