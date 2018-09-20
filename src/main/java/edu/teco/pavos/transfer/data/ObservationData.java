package edu.teco.pavos.transfer.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * A serializable object that contains the observed data
 */
public class ObservationData implements java.io.Serializable {
	private static final String TYPE_DOUBLE = "single-double";
	private static final String TYPE_VECTOR_DOUBLE = "vector-double";
	
	/**
	 * The unique identifier of this object
	 */
	private static final long serialVersionUID = 5410581483969835624L;

	/**
	 * The identificator of the sensor that created this observation
	 */
    private String sensorID;
    
    /**
     * The identificator of the cluster that contains the sensor
     */
    private String clusterID;
    
    /**
     * The date of the observation
     */
    private String observationDate;

    // Attributes listed below here are observed properties //
    
    /**
     *  The different observed properties (keys) and their corresponding values
     */
    private Map<String, Map<String, String>> observations = new HashMap<>();
    
    /**
     * Returns all observations of type {@link Double}.
     * @return observations The map of observations of type {@link Double}
     */
    public Map<String, String> getDoubleObservations() {
    	createType(TYPE_DOUBLE);
    	return observations.get(TYPE_DOUBLE);
    }
    
    /**
     * Sets all observations of type {@link Double}.
     * CAUTION! This method overrides any existing values!
     * This method does not check wether the format is correct!
     * @param observationsToSet The {@link Map} contains all observationsTypes and observations
     */
    public void setDoubleObservations(Map<String, String> observationsToSet) {
    	createType(TYPE_DOUBLE);
    	this.observations.get(TYPE_DOUBLE).clear();
    	this.observations.get(TYPE_DOUBLE).putAll(observationsToSet);
    }
    
    /**
     * Returns all observations of type {@link Vector} with entries of type {@link Double}.
     * @return observations The map of observations of type {@link Vector}
     */
    public Map<String, String> getVectorDoubleObservations() {
    	createType(TYPE_VECTOR_DOUBLE);
    	return observations.get(TYPE_VECTOR_DOUBLE);
    }
    
    /**
     * Sets all observations of type {@link Vector} with entries of type {@link Double}.
     * CAUTION! This method overrides any existing values!
     * This method does not check wether the format is correct!
     * @param observationsToSet The {@link Map} contains all observationsTypes and observations
     */
    public void setVectorDoubleObservations(Map<String, String> observationsToSet) {
    	createType(TYPE_VECTOR_DOUBLE);
    	this.observations.get(TYPE_VECTOR_DOUBLE).clear();
    	this.observations.get(TYPE_VECTOR_DOUBLE).putAll(observationsToSet);
    }
    
    /**
     * Adds a new Observation of type {@link Double} to the existing observations.
     * @param observationType The type of the observation to add
     * @param observationValue The observation to add
     */
    public void addDoubleObservation(String observationType, double observationValue) {
    	if (observationType != null && (Object) observationValue != null) {
    		createType(TYPE_DOUBLE);
    		this.observations.get(TYPE_DOUBLE).put(observationType, String.valueOf(observationValue));
    	}
    }
    
    /**
     * Adds a {@link Collection} of Observations of type {@link Double} to the existing observations.
     * @param observationsToAdd Contains the type of the observation and the observation itself to add
     */
    public void addDoubleObservations(Map<String, Double> observationsToAdd) {
    	for (Entry<String, Double> entry : observationsToAdd.entrySet()) {
    		addDoubleObservation(entry.getKey(), entry.getValue());
    	}
    }
    
    /**
     * Adds a new Observation of type {@link Vector} with entries of type
     * {@link Double} to the existing observations.
     * @param observationType The type of the observation to add
     * @param observationValue The observation to add
     */
    public void addVectorDoubleObservation(String observationType, ArrayList<Double> observationValue) {
    	if (observationType != null && observationValue != null) {
    		createType(TYPE_VECTOR_DOUBLE);
    		this.observations.get(TYPE_VECTOR_DOUBLE).put(observationType, observationValue.toString());
    	}
    }
    
    /**
     * Adds a {@link Collection} of Observations of type {@link Vector}
     * with entries of type {@link Double} to the existing observations.
     * @param observationsToAdd Contains the type of the observation and the observation itself to add
     */
    public void addVectorDoubleObservations(Map<String, ArrayList<Double>> observationsToAdd) {
    	for (Entry<String, ArrayList<Double>> entry : observationsToAdd.entrySet()) {
    		addVectorDoubleObservation(entry.getKey(), entry.getValue());
    	}
    }
    
    private void createType(String type) {
    	if (!this.observations.containsKey(type)) {
    		this.observations.put(type, new HashMap<String, String>());
    	}
    }
    
	/**
	 * @return the sensorID
	 */
	public String getSensorID() {
		return sensorID;
	}

	/**
	 * @param sensorID the sensorID to set
	 */
	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}

	/**
	 * @return the clusterID
	 */
	public String getClusterID() {
		return clusterID;
	}

	/**
	 * @param clusterID the clusterID to set
	 */
	public void setClusterID(String clusterID) {
		this.clusterID = clusterID;
	}

	/**
	 * @return the observationDate
	 */
	public String getObservationDate() {
		return observationDate;
	}

	/**
	 * @param observationDate the observationDate to set
	 */
	public void setObservationDate(String observationDate) {
		this.observationDate = observationDate;
	}
    
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