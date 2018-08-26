package gui.importer;

import java.io.File;

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
     * Adds the Data of a File at a specified FilePath to the FROST-Server. To do so, the FileExtension
     * of the File is determined.With help of the readerTypeClass the matching implementation of the
     * FileReaderStrategy interface for the FileExtension is generated and can be used to get the Data
     * from then File.
     * @param file Is the File to Import.
     */
    public void addFileData(File file) {
    	String extension = this.getFileExtension(file.getAbsolutePath());
		try {
			FileReaderStrategy reader = ReaderType.getFileReaderForFileExtension(extension);
			reader.sendFileData(file);
		} catch (IllegalFileExtensionException e) {
        	System.out.println(e.getLocalizedMessage());
		}
    }

    /**
     * generates a file extension from a Path.
     * @param path to get the extension from.
     * @return String of the extension.
     */
    private String getFileExtension(String path) {
        try {
        	int i = path.lastIndexOf('.');
        	int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        	if (i > p) {
        	    return path.substring(i + 1);
        	}
        	return "";
        } catch (IndexOutOfBoundsException e) {
        	System.out.println(e.getLocalizedMessage());
        	return "";
        }
    }

}
