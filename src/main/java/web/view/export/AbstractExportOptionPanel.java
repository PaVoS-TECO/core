package web.view.export;

import java.util.*;

/**
 * A panel for handling user input, that deals with exporting datasets. The user can select Clusters by their ClusterIDs, Sensors by their SensorIDs, a time frame, sensor types and a file format.
 */
public class AbstractExportOptionPanel extends ViewComponent implements MapObserver, SensorOptionPanelObserver, TimeOptionPanelObserver {

    /**
     * Default constructor
     */
    public AbstractExportOptionPanel() {
    }

    /**
     * 
     */
    protected Set<ClusterID> clusterIds;

    /**
     * 
     */
    protected Set<SensorID> sensorIds;

    /**
     * 
     */
    protected TimeFrame timeFrame;

    /**
     * 
     */
    protected Set<ObservedProperty> observedProperties;

    /**
     * 
     */
    protected FileExtension fileExtension;

    /**
     * Set the ClusterIDs.
     * @param clusterIds
     */
    public void setClusterIds(Set<ClusterID> clusterIds) {
        // TODO implement here
    }

    /**
     * Set the SensorIDs.
     * @param sensorIds
     */
    public void setSensorIds(Set<SensorID> sensorIds) {
        // TODO implement here
    }

    /**
     * Set the TimeFrame.
     * @param timeFrame
     */
    public void setTimeFrame(TimeFrame timeFrame) {
        // TODO implement here
    }

    /**
     * Set the SensorTypes.
     * @param observedProperties
     */
    public void setObservedProperties(Set<ObservedProperty> observedProperties) {
        // TODO implement here
    }

    /**
     * Set the ExportFormat.
     * @param fileExtension
     */
    public void setFileExtension(FileExtension fileExtension) {
        // TODO implement here
    }

    /**
     * Request an export with the given parameters.
     */
    public void export() {
        // TODO implement here
    }

    /**
     * 
     */
    public void mapUpdate() {
        // TODO implement here
    }

    /**
     * Update the observer  with the current SensorOptionPanel state.
     */
    public void sensorOptionUpdate() {
        // TODO implement here
    }

    /**
     * Update the observer  with the current TimeOptionPanel state.
     */
    public void timeOptionUpdate() {
        // TODO implement here
    }

}