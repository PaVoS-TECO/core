package edu.teco.pavos.importer;

import java.io.File;

/**
 * Interface for the FileReaderStrategy classes. Realization of a Strategy to be
 * able to swap out the way a File has to be read.
 */
public interface FileReaderStrategy {


    /**
     * Reads from a File as specified by the FilePath and sends the information in
     * it to the FROST-Server using the FrostSender that was provided.
     * @param file Is the File to Import.
     */
    void sendFileData(File file);

}
