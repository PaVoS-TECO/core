package gui.importer;

import java.util.*;

/**
 * Is like a chooser for the right FileReaderStrategy. If a new Strategy is added, this class needs some changes to use the new Strategy.
 */
public class ReaderType {

    /**
     * Default constructor
     */
    public ReaderType() {
    }



    /**
     * Gives a new Instance of a FileReaderStrategy for the specified FileExtension.
     * @param extension is the FileExtension for which a FileReaderStrategy has to be generated.
     * @return An instance of an implementation of the FileReaderStrategy interface.
     */
    public static FileReaderStrategy getFileReaderForFileExtension(FileExtension extension) {
        // TODO implement here
        return null;
    }

}