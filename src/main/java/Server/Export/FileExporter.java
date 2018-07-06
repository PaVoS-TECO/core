package Export;

import java.util.*;

/**
 * Exporter of Data from Kafka to a File.
 */
public class FileExporter extends AbstractExporter {

    /**
     * Default constructor
     */
    public FileExporter() {
    }




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