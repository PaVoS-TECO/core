package edu.teco.pavos.pim;

import java.io.File;

/**
 * Implementation of the FileReaderStrategy interface for NetCDF files.
 */
public class NetCDFReaderStrategy implements FileReaderStrategy {

    /**
     * Default constructor
     * @param url is the destination server for the data.
     */
    public NetCDFReaderStrategy(String url) {
    }

    /**
     * Reads from a File as specified by the FilePath and sends the information
     * in it to the FROST-Server using the FrostSender that was provided.
     * @param file Is the File to Import.
     */
    public void sendFileData(File file) {
        // TODO implement here
    }

    /**
     * Set the prefix for the iot id of imported data
     * @param prefix for imported data
     */
	@Override
	public void setIotIdPrefix(String prefix) {
		// TODO Auto-generated method stub
		
	}

	/**
     * Set the dataTable of the Import
     * @param dataTable of the Import
     */
	@Override
	public void setDataTable(DataTable dataTable) {
		// TODO Auto-generated method stub
		
	}

}
