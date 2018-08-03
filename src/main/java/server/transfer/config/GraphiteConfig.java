package server.transfer.config;

import server.transfer.config.util.EnvironmentUtil;

/**
 * The specified configuration-object that stores all needed configurations for the connection from Kafka to Graphite
 */
public final class GraphiteConfig {
	
	private GraphiteConfig() {
		
	}
	
    /**
     * Returns whether a start from the beginning is required
     * @return startFromBeginning Tells us whether a start from the beginning is required
     */
    public static boolean getStartFromBeginning() {
    	return "true".equals(EnvironmentUtil.getEnvironmentVariable("WM_GRAPHITE_FROM_BEGINNING", "false"));
    }
    
    /**
     * Returns the host-name of Graphite
     * @return name The Graphite-host-name
     */
    public static String getGraphiteHostName() {
    	return EnvironmentUtil.getEnvironmentVariable("WM_GRAPHITE_HOST_NAME", "localhost");
    }

    /**
     * Returns the port of the Graphite-connection
     * @return port The port of the Graphite-connection
     */
    public static Integer getGraphitePort() {
    	String portString = EnvironmentUtil.getEnvironmentVariable("WM_GRAPHITE_PORT", "2004");
    	return Integer.parseInt(portString);
    }

}