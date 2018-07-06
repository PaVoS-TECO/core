package Import;

import java.util.*;

/**
 * Interface for the FileReaderStrategy classes. Realization of a Strategy to be able to swap out the way a File has to be read.
 */
public interface FileReaderStrategy {


    /**
     * Reades from a File as specified by the FilePath and sends the information in it to the FROST-Server using the FrostSender that was provided.
     * @param path Is the FilePath of the File to Import.
     * @param froster Is the FrostSender instance that will be used to send the files data to the Frost-Server.
     */
    public void sendFileData(FilePath path, FrostSender froster);

}