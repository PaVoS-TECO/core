package gui.importer;

import server.export.FileExtension;

/**
 * Is like a chooser for the right FileReaderStrategy. If a new Strategy is added,
 * this class needs some changes to use the new Strategy.
 */
public final class ReaderType {

    /**
     * Default constructor
     */
    private ReaderType() { }

    /**
     * Gives a new Instance of a FileReaderStrategy for the specified FileExtension.
     * @param extension is the FileExtension for which a FileReaderStrategy has to be generated.
     * @return An instance of an implementation of the FileReaderStrategy interface.
     * @throws IllegalFileExtensionException if file extension is unknown
     */
    public static FileReaderStrategy getFileReaderForFileExtension(String extension)
    		throws IllegalFileExtensionException {
        String ext = extension.toLowerCase();
        if (ext.equals("csv")) {
    		return new CSVReaderStrategy();
    	} else if (ext.equals("netcdf")) {
    		return new NetCDFReaderStrategy();
    	} else {
    		throw new IllegalFileExtensionException("This File Extension is not known to PaVoS Importer");
    	}
    }

}
