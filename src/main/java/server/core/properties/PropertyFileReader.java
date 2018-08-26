package server.core.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads the Property File to the System
 * 
 * @author Patrick
 *
 */
public final class PropertyFileReader {

	/**
	 * Default constructor
	 */
	private PropertyFileReader() {

	}

	/**
	 * Read the file form the Path
	 * @param filePath
	 * @return
	 */
	public static Properties readPropertyFile(String filePath) {
		Properties properties = new Properties();

		try {
			FileInputStream file = new FileInputStream(filePath);
			properties.load(file);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("There was an error reading the configuration file.\n"
					+ "Please make sure that the file '" + filePath + "' exists.");
			System.exit(-1);
		}

		return properties;
	}

}
