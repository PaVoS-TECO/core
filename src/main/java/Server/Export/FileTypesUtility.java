package Export;

import java.util.*;

/**
 * Utility class that provides static methods to get all supported FileExtensions and one to get a new Instance of the FileWriter associated with a given FileExtension. If a new FileWriter is added to PaVoS, this class needs some changed to be able to return the new FileWriter.
 */
public class FileTypesUtility {

    /**
     * Default constructor
     */
    public FileTypesUtility() {
    }



    /**
     * Gives all supported FileExtensions in an ArrayList.
     * @return Is an Array of the possible FileExtensions for an Export.
     */
    public static Set<FileExtension> getAllPossibleFileExtensions() {
        // TODO implement here
        return null;
    }

    /**
     * Gives a new Instance of the FileWriter associated witha given FileExtension.
     * @param extension Is the FileExtension for which a new instance of an Implementation of the FileWriterStrategy is wanted.
     * @return Is the instance of the implementation of a FileWriterStrategy.
     */
    public static FileWriterStrategy getFileWriterForFileExtension(FileExtension extension) {
        // TODO implement here
        return null;
    }

}