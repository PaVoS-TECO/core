package server.core.properties;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link GridPropertiesFileManager} manages different properties
 * and stores them in a {@link Properties} object.
 */
public abstract class PropertiesFileManager {

	protected Properties properties = new Properties();
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected PropertiesFileManager() {
		
	}
	
	/**
	 * Returns the value of the given property from this 
	 * {@link PropertiesFileManager}s {@link Properties}.
	 * @param key {@link String}
	 * @return property {@link String}
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
}
