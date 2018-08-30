package server.core.properties;

import java.security.InvalidParameterException;
import java.util.Properties;

public class GradientPropertiesFileManager {
	
	private Properties properties;
	public final String gradientPropertyFilePath = "src/main/resources/defaultGradients.properties";
	private static GradientPropertiesFileManager instance;
	
	/**
	 * Default Constructor
	 */
	private GradientPropertiesFileManager() {
		loadGradientProperties();
	}
	
	/**
	 * 
	 * @return it Self
	 */
	public static GradientPropertiesFileManager getInstance() {
		if (instance == null) {
			instance = new GradientPropertiesFileManager();
		}
		return instance;
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	/**
	 * Load from File Properties
	 */
	
	private void loadGradientProperties() {
		try {
			properties = PropertyFileReader.readPropertyFile(gradientPropertyFilePath);

			// check if properties file is missing keys

			if (!properties.containsKey("temperature")
					|| !properties.containsKey("temperature.range.celsius")
					|| !properties.containsKey("temperature.range.fahrenheit")) {
				throw new InvalidParameterException();
			}
		}  catch (InvalidParameterException e) {
			e.printStackTrace();
			System.err.println("The configuration file is missing at least one of the following required arguments:\n"
					+ "\t- temperature\n" + "\t- temperature.range.celsius\n"
					+ "\t- temperature.range.fahrenheit");
			System.exit(-1);
		}
	}
	
}
