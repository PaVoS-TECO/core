package edu.teco.pavos.importer;

/**
 * Main Class of the Importer
 */
public final class ImportGUI {
	
	private ImportGUI() { }
	
	/**
	 * Main method
	 * @param args of main
	 */
	public static void main(String[] args) {
		DataImporter importer = new DataImporter();
        importer.startImportingFileData();
	}

}
