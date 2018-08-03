package server.transfer.config.util;

/**
 * A utility class, providing information about the system environment.
 */
public final class EnvironmentUtil {
	
	private EnvironmentUtil() {
		
	}
	
	/**
     * Returns an environment-variable
     * @param name The name of the environment-variable
     * @param defaultValue The default-value of the environment-variable
     * @return value The currently set value of the environment-variable
     */
    public static String getEnvironmentVariable(String name, String defaultValue) {
    	String value = System.getenv(name);
        if (value == null || "".equals(value)) {
            return defaultValue;
        }

        return value;
    }
	
}
