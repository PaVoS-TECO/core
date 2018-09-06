package server.core.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes a specified {@link Path} and initializes a {@link Properties} object with the content of the file.
 */
public final class PropertyFileReader {
	
	private static Logger logger = LoggerFactory.getLogger(PropertyFileReader.class);
	
	private PropertyFileReader() {

	}

	/**
	 * Read the {@link Properties} form the file-{@link Path}.
	 * @param filePath {@link Path}
	 * @return properties {@link Properties}
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
