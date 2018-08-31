package edu.teco.pavos.pke;

/**
 * Is used to store a FileExtension information and give  the right  FileWriter for this FileExtension.
 * @author Jean Baumgarten
 */
public class FileType {

    /**
     * The FileExtension is defining the FileType.
     */
    private ExportProperties properties;

    /**
     * Default constructor
     * @param properties that defines this export
     */
    public FileType(ExportProperties properties) {
    	
    	this.properties = properties;
    	
    }
    
    /**
     * Gives an instance of the implemented FileWriter that is associated with this FileType, thus this
     * FileExtension. To do so it uses the static method getFileWriterForFileExtension from the FileTypesUtility class.
     * @return Is a new instance of an implementation of a FilWriterStrategy.
     * @throws IllegalFileExtensionException 
     */
    public FileWriterStrategy getFileWriter() throws IllegalFileExtensionException {
    	
        return FileTypesUtility.getFileWriterForFileExtension(this.properties);
        
    }

}
