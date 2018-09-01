package server.core.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerFactory;

/**
 * Reads the Property File to the System
 * 
 * @author Patrick
 *
 */
public final class PropertyFileReader {
	
	private static Logger logger = new Log4jLoggerFactory().getLogger(PropertyFileReader.class.toString());
	
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
			logger.error("There was an error reading the configuration file.\n"
					+ "Please make sure that the file '" + filePath + "' exists.");
			System.exit(-1);
		}

		return properties;
	}

}
