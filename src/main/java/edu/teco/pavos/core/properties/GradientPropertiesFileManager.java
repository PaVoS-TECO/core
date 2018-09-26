package edu.teco.pavos.core.properties;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Properties;

import edu.teco.pavos.core.visualization.gradients.MultiGradient;

/**
 * The {@link GradientPropertiesFileManager} manages different properties
 * that are needed to handle {@link MultiGradient}s
 * and stores them in a {@link Properties} object.
 */
public final class GradientPropertiesFileManager extends PropertiesFileManager {
	
	/**
	 * The path to the {@link Properties} file.
	 */
	private static final String GRADIENT_PROPERTIES_FILE_PATH = "src/main/resources/defaultGradients.properties";
	private static GradientPropertiesFileManager instance;
	
	private GradientPropertiesFileManager() {
		loadGradientProperties();
	}
	
	/**
	 * Returns the instance of this {@link GradientPropertiesFileManager} or generates a new one if it does not exists.
	 * @return {@link GradientPropertiesFileManager}
	 */
	public static GradientPropertiesFileManager getInstance() {
		if (instance == null) {
			instance = new GradientPropertiesFileManager();
		}
		return instance;
	}
	
	/**
	 * Load {@link Properties} from the specified {@link Path}.
	 */
	private void loadGradientProperties() {
		try {
			properties = PropertyFileReader.readPropertyFile(GRADIENT_PROPERTIES_FILE_PATH);

			// check if properties file is missing keys

			if (!properties.containsKey("temperature")
					|| !properties.containsKey("temperature.range.celsius")
					|| !properties.containsKey("temperature.range.fahrenheit")) {
				throw new InvalidParameterException();
			}
		}  catch (InvalidParameterException e) {
			logger.error("The configuration file is missing at least one of the following required arguments:\n"
					+ "\t- temperature\n" + "\t- temperature.range.celsius\n"
					+ "\t- temperature.range.fahrenheit", e);
			System.exit(-1);
		}
	}
	
}
