package edu.teco.pavos.pim;

/**
 * Main Class of the Importer
 * @author Jean Baumgarten
 */
public final class App {
	
	private App() { }
	
	/**
	 * Main method
	 * @param args of main
	 */
    public static void main(String[] args) {
    	
    	DataImporter importer = new DataImporter();
        importer.startImportingFileData();
        
    }
    
}
