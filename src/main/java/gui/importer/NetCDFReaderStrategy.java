package gui.importer;

import java.io.File;

/**
 * Implementation of the FileReaderStrategy interface for NetCDF files.
 */
public class NetCDFReaderStrategy implements FileReaderStrategy {

    /**
     * Default constructor
     */
    public NetCDFReaderStrategy() {
    }

    /**
     * Reades from a File as specified by the FilePath and sends the information
     * in it to the FROST-Server using the FrostSender that was provided.
     * @param file Is the File to Import.
     */
    public void sendFileData(File file) {
        // TODO implement here
    }

}
