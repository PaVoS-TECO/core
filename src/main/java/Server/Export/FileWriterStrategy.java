package Export;

import java.util.*;

/**
 * Interface for the FileWriterStrategy classes. Realization of a Strategy to be able to swap out the way a File has to be saved.
 */
public interface FileWriterStrategy {


    /**
     * Creates a File as specified by the FilePath and saves the Data from the provided KafkaStream into it.
     * @param stream is the KStream, that should be exported to a File.
     * @param path Is the FilePath, where the new File should be created.
     */
    public void saveToFile(KStream stream, FilePath path);

}