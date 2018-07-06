package Export;

import java.util.*;

/**
 * Contains the Properties of an Export Request.
 */
public class ExportProperties {

    /**
     * Default constructor
     */
    public ExportProperties() {
    }





    /**
     * Get the FileExtension for the Export File.
     * @return The FileExtension for the File to export.
     */
    public FileExtension getFileExtension() {
        // TODO implement here
        return null;
    }

    /**
     * Get the TimeFrame of the Data that should be exported.
     * @return The TimeFrame of the Data to be exported.
     */
    public TimeFrame getTimeFrame() {
        // TODO implement here
        return null;
    }

    /**
     * Get the ObsorvedProperties that should be exported.
     * @return The ObservedProperties that should be used for the export.
     */
    public Set<ObservedProperty> getObservedProperties() {
        // TODO implement here
        return null;
    }

    /**
     * Get the ClusterIDs that should be exported. Always only exports a Groupd of Sensors or a Group of Clusters. The other Option is Empty.
     * @return The Clusters that should be taken in the Export.
     */
    public Set<ClusterID> getClusters() {
        // TODO implement here
        return null;
    }

    /**
     * Get the SensorIDs that should be exported. Always only exports a Groupd of Sensors or a Group of Clusters. The other Option is Empty.
     * @return The SensorIDs of the Data that should be exported.
     */
    public Set<SensorID> getSensorIDs() {
        // TODO implement here
        return null;
    }

}