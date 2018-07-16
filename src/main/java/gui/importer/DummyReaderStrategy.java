package gui.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the FileReaderStrategy interface to generate continuous data
 */
public class DummyReaderStrategy implements FileReaderStrategy {

	/**
     * Reads from a File as specified by the FilePath and sends the information in
     * it to the FROST-Server using the FrostSender that was provided.
     * @param file Is the File to Import.
     */
	public void sendFileData(File file) {
		int counter = 0;
		ArrayList<Integer> data = new ArrayList<Integer>();
		//Initialize data
		while (100000 > counter++) {
			// send data to FROST
			//FrostSender.sendToFrostServer("", "");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
