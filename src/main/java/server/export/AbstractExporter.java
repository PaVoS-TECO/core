package server.export;

import server.download.DownloadID;

/**
 * Abstract Exporter of Data to a File.
 */
public class AbstractExporter {

    /**
     * Default constructor
     */
    public AbstractExporter() {
    }

    /**
     * Contains the Properties of an Export Request.
     */
    public ExportProperties properties;




    /**
     * Creates Information for that Export. These Information will be used to identifie a File for the WebGUI, that gets the created DownloadID.
     * @return Is the DownloadID for the started Export.
     */
    public DownloadID createFileInformation() {
        // TODO implement here
        return null;
    }

    /**
     * Generates the File with the desired Data.
     */
    public void createFile() {
        // TODO implement here
    }

}