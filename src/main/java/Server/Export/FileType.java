package server.export;

import java.util.*;

/**
 * Is used to store a FileExtension information and give  the right  FileWriter for this FileExtension.
 */
public class FileType {

    /**
     * Default constructor
     */
    public FileType() {
    }

    /**
     * The FileExtension is defining the FileType.
     */
    public FileExtension extension;






    /**
     * Gives an instance of the implemented FileWriter that is associated with this FileType, thus this FileExtension. To do so it uses the static method getFileWriterForFileExtension from the FileTypesUtility class.
     * @return Is a new instance of an implementation of a FilWriterStrategy.
     */
    public FileWriterStrategy getFileWriter() {
        // TODO implement here
        return null;
    }

}