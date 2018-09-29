package edu.teco.pavos.transfer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * A serializable object that contains the observed data.
 */
public class ObservationData implements Serializable, Cloneable {
	
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
     *  The different observed properties (keys) and their corresponding values.
     *  Single values.
     */
    private Map<String, Double> singleObservations = new HashMap<>();
    
    /**
     *  The different observed properties (keys) and their corresponding values.
     *  Vector values.
     */
    private Map<String, ArrayList<Double>> vectorObservations = new HashMap<>();
    
    @Override
    public ObservationData clone() {
    	ObservationData result = new ObservationData();
		result.setObservationDate(this.getObservationDate());
		result.setSensorID(this.getSensorID());
		result.setClusterID(this.getClusterID());
		this.getSingleObservations().entrySet().forEach(
				entry -> result.addSingleObservation(entry.getKey(), entry.getValue()));
		this.getVectorObservations().entrySet().forEach(
				entry -> result.addVectorObservation(entry.getKey(), entry.getValue()));
		return result;
    }
    
    /**
     * Returns all observations of type {@link Double}.
     * @return observations The map of observations of type {@link Double}
     */
    public Map<String, Double> getSingleObservations() {
    	return singleObservations;
    }
    
    /**
     * Sets all observations of type {@link Double}.
     * CAUTION! This method overrides any existing values!
     * This method does not check wether the format is correct!
     * @param observationsToSet The {@link Map} contains all observationsTypes and observations
     */
    public void setSingleObservations(Map<String, ? extends Number> observationsToSet) {
    	this.singleObservations.clear();
    	Map<String, Double> convertedMap = new HashMap<>();
    	for (Map.Entry<String, ? extends Number> entry : observationsToSet.entrySet()) {
    		convertedMap.put(entry.getKey(), entry.getValue().doubleValue());
    	}
    	this.singleObservations.putAll(convertedMap);
    }
    
    /**
     * Returns all observations of type {@link Vector} with entries of type {@link Double}.
     * @return observations The map of observations of type {@link Vector}
     */
    public Map<String, ArrayList<Double>> getVectorObservations() {
    	return vectorObservations;
    }
    
    /**
     * Sets all observations of type {@link Vector} with entries of type {@link Double}.
     * CAUTION! This method overrides any existing values!
     * This method does not check wether the format is correct!
     * @param observationsToSet The {@link Map} contains all observationsTypes and observations
     */
    public void setVectorObservations(Map<String, ArrayList<? extends Number>> observationsToSet) {
    	vectorObservations.clear();
    	Map<String, ArrayList<Double>> convertedMap = new HashMap<>();
    	for (Entry<String, ArrayList<? extends Number>> entry : observationsToSet.entrySet()) {
    		ArrayList<Double> convertedList = new ArrayList<>();
    		entry.getValue().forEach(number -> number.doubleValue());
    		convertedMap.put(entry.getKey(), convertedList);
    	}
    	vectorObservations.putAll(convertedMap);
    }
    
    /**
     * Adds a new Observation of type {@link Double} to the existing observations.
     * @param observationType The type of the observation to add
     * @param observationValue The observation to add
     */
    public void addSingleObservation(String observationType, Number observationValue) {
    	if (observationType != null && (Object) observationValue != null) {
    		singleObservations.put(observationType, observationValue.doubleValue());
    	}
    }
    
    /**
     * Adds a {@link Collection} of Observations of type {@link Double} to the existing observations.
     * @param observationsToAdd Contains the type of the observation and the observation itself to add
     */
    public void addSingleObservations(Map<String, ? extends Number> observationsToAdd) {
    	for (Entry<String, ? extends Number> entry : observationsToAdd.entrySet()) {
    		addSingleObservation(entry.getKey(), entry.getValue());
    	}
    }
    
    /**
     * Adds a new Observation of type {@link Vector} with entries of type
     * {@link Double} to the existing observations.
     * @param observationType The type of the observation to add
     * @param observationValue The observation to add
     */
    public void addVectorObservation(String observationType, ArrayList<? extends Number> observationValue) {
    	if (observationType != null && observationValue != null) {
    		ArrayList<Double> convertedList = new ArrayList<>();
    		observationValue.forEach(entry -> convertedList.add(entry.doubleValue()));
    		vectorObservations.put(observationType, convertedList);
    	}
    }
    
    /**
     * Adds a {@link Collection} of Observations of type {@link Vector}
     * with entries of type {@link Double} to the existing observations.
     * @param observationsToAdd Contains the type of the observation and the observation itself to add
     */
    public void addVectorObservations(Map<String, ArrayList<? extends Number>> observationsToAdd) {
    	for (Entry<String, ArrayList<? extends Number>> entry : observationsToAdd.entrySet()) {
    		addVectorObservation(entry.getKey(), entry.getValue());
    	}
    }
    
    /**
     * Returns an {@link ArrayList} of type {@link Double}.
     * If the observation is a single observation, it will be converted to
     * an {@link ArrayList} with a single entry.
     * If the observation is a vector observation, it will be the exact 
     * {@link ArrayList} entry.
     * @param observationType The {@link String} key / property of the observation. Like 'temperature_celsius'
     * @return valueArray {@link ArrayList} of type {@link Double}
     */
    public ArrayList<Double> getAnonObservation(String observationType) {
    	Double value = getSingleObservations().get(observationType);
		ArrayList<Double> valueArray = new ArrayList<>();
		if (value == null) {
			valueArray.addAll(getVectorObservations().get(observationType));
		} else {
			valueArray.add(value);
		}
		return valueArray;
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
    	builder.append("singleObservations=" + singleObservations.toString() + ", ");
    	builder.append("vectorObservations=" + vectorObservations.toString());
    	builder.append("}");
    	return builder.toString();
    }
    
}