package edu.teco.pavos.exporter;

import java.io.File;

/**
 * Implementation of the FileWriterStrategy interface for NetCDF files.
 */
public class NetCDFWriterStrategy implements FileWriterStrategy {

    /**
     * Default constructor
     * @param props are the properties of the data, that should be exported to a File.
     */
    public NetCDFWriterStrategy(ExportProperties props) {
    }

    /**
     * Creates a File as specified by the FilePath and saves the Data from the provided KafkaStream into it.
     * @param file Is the FilePath, where the new File should be created.
     */
	public void saveToFile(File file) {
		// TODO Auto-generated method stub
		
	}

}
