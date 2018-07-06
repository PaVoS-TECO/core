package server.transfer.config;

/**
 * A utility class with all sorts of useful features for Config classes in this package.
 */
public class ConfigUtil {
	
	/**
     * Returns an environment-variable
     * @param name The name of the environment-variable
     * @param defaultValue The default-value of the environment-variable
     * @return value The currently set value of the environment-variable
     */
    protected static String getEnvironmentVariable(String name, String defaultValue) {
    	String value = System.getenv(name);
        if (value == null || "".equals(value)) {
            return defaultValue;
        }

        return value;
    }
	
}
