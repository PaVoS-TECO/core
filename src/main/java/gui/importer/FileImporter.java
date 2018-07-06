package gui.importer;

import java.util.*;

/**
 * Importer for the Data contained in a File. Takes the Data and sends them to the FROST-Server.
 */
public class FileImporter {

    /**
     * Default constructor
     */
    public FileImporter() {
    }






    /**
     * Adds the Data of a File at a specified FilePath to the FROST-Server. To do so, the FileExtension of the File is determined.With help of the readerTypeClass the matching implementation of the FileReaderStrategy interface for the FileExtension is generated and can be used to get the Data from then File.
     * @param path Is the FilePath of the File to Import.
     * @param froster Is the FrostSender instance that will be used to send the files data to the Frost-Server.
     */
    public void addFileData(FilePath path, FrostSender froster) {
        // TODO implement here
    }

}