package server.core.properties;

import java.awt.geom.Rectangle2D;
import java.security.InvalidParameterException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoRecRectangleGrid;

public final class GridPropertiesFileManager {
	
	private static final String DEFAULT_GRID_NAME = "default.grid.name";
	private static final String DEFAULT_GRID_ROWS = "default.grid.rows";
	private static final String DEFAULT_GRID_COLUMNS = "default.grid.columns";
	private static final String DEFAULT_GRID_LEVELS = "default.grid.levels";
	private static final String DEFAULT_GRID_X_MIN = "default.grid.x.min";
	private static final String DEFAULT_GRID_X_MAX = "default.grid.x.max";
	private static final String DEFAULT_GRID_Y_MIN = "default.grid.y.min";
	private static final String DEFAULT_GRID_Y_MAX = "default.grid.y.max";
	private Properties properties;
	public final String gradientPropertyFilePath = "src/main/resources/GridProperties.properties";
	private static GridPropertiesFileManager instance;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Default Constructor
	 */
	private GridPropertiesFileManager() {
		loadGradientProperties();
		loadGrid();
	}
	
	private void loadGrid() {
		String name = getProperty(DEFAULT_GRID_NAME);
		int rows = Integer.parseInt(getProperty(DEFAULT_GRID_ROWS));
		int columns = Integer.parseInt(getProperty(DEFAULT_GRID_COLUMNS));
		int levels = Integer.parseInt(getProperty(DEFAULT_GRID_LEVELS));
		double xMin = Double.parseDouble(getProperty(DEFAULT_GRID_X_MIN));
		double xMax = Double.parseDouble(getProperty(DEFAULT_GRID_X_MAX));
		double yMin = Double.parseDouble(getProperty(DEFAULT_GRID_Y_MIN));
		double yMax = Double.parseDouble(getProperty(DEFAULT_GRID_Y_MAX));
		
		switch(name) {
		case GeoRecRectangleGrid.NAME:
			System.out.println("--- grid recRecGrid created ---");
			System.out.println(new GeoRecRectangleGrid(new Rectangle2D.Double(xMin, yMin, xMax, yMax), rows, columns, levels).getGridObservations());
			break;
		default:
			throw new IllegalArgumentException("Grid not recognized. Exiting with Errors.");
		}
	}
	
	/**
	 * 
	 * @return it Self
	 */
	public static GridPropertiesFileManager getInstance() {
		if (instance == null) {
			instance = new GridPropertiesFileManager();
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

			if (!properties.containsKey(DEFAULT_GRID_NAME)
					|| !properties.containsKey(DEFAULT_GRID_ROWS)
					|| !properties.containsKey(DEFAULT_GRID_COLUMNS)
					|| !properties.containsKey(DEFAULT_GRID_LEVELS)
					|| !properties.containsKey(DEFAULT_GRID_X_MIN)
					|| !properties.containsKey(DEFAULT_GRID_X_MAX)
					|| !properties.containsKey(DEFAULT_GRID_Y_MIN)
					|| !properties.containsKey(DEFAULT_GRID_Y_MAX)) {
				throw new InvalidParameterException();
			}
		}  catch (InvalidParameterException e) {
			logger.error("The configuration file is missing at least one of the following required arguments:\n"
					+ "\t- " + DEFAULT_GRID_NAME + "\n" + "\t- " + DEFAULT_GRID_ROWS + "\n" + "\t- " + DEFAULT_GRID_COLUMNS + "\n"
					+ "\t- " + DEFAULT_GRID_LEVELS + "\n" + "\t- " + DEFAULT_GRID_X_MIN + "\n" + "\t- " + DEFAULT_GRID_X_MAX + "\n"
					+ "\t- " + DEFAULT_GRID_Y_MIN + "\n" + "\t- " + DEFAULT_GRID_Y_MAX, e);
			System.exit(-1);
		}
	}
	
	
}
