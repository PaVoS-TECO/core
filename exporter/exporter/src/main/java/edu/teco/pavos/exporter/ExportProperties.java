package edu.teco.pavos.exporter;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains the Properties of an Export Request.
 */
public class ExportProperties {
	
	private String extension;
	private Set<String> observedProperties;
	private Set<String> clusters;
	private TimeIntervall time;

    /**
     * Default constructor
     * @param ext is the extension
     * @param tf is the time frame
     * @param ops is the list of observed properties
     * @param  cIDs is the list of cluster ids
     */
    public ExportProperties(String ext, String tf, String ops, String cIDs) {
    	this.extension = ext;
    	String[] obprops = ops.split(",");
    	this.observedProperties = new HashSet<String>();
    	for (String op : obprops) {
    		this.observedProperties.add(op);
    	}
    	String[] cluIDs = cIDs.split(",");
    	this.clusters = new HashSet<String>();
    	for (String cluster : cluIDs) {
    		this.clusters.add(cluster);
    	}
    	this.time = new TimeIntervall(tf);
    }

    /**
     * Get the FileExtension for the Export File.
     * @return The FileExtension for the File to export.
     */
    public String getFileExtension() {
        return this.extension;
    }

    /**
     * Get the TimeFrame of the Data that should be exported.
     * @return The TimeIntervall of the Data to be exported.
     */
    public TimeIntervall getTimeFrame() {
        return this.time;
    }

    /**
     * Get the ObsorvedProperties that should be exported.
     * @return The ObservedProperties that should be used for the export.
     */
    public Set<String> getObservedProperties() {
        return this.observedProperties;
    }

    /**
     * Get the ClusterIDs that should be exported. Always only exports a Groupd of Sensors
     * or a Group of Clusters. The other Option is Empty.
     * @return The Clusters that should be taken in the Export.
     */
    public Set<String> getClusters() {
        return this.clusters;
    }

}
