package server.core.properties;

import java.awt.geom.Rectangle2D;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoRecRectangleGrid;
import server.transfer.config.util.EnvironmentUtil;

public final class GridPropertiesFileManager {
	
	private static final String DEFAULT_GRID_NAME = "PAVOS_DEFAULT_GRID_NAME";
	private static final String DEFAULT_GRID_ROWS = "PAVOS_DEFAULT_GRID_ROWS";
	private static final String DEFAULT_GRID_COLUMNS = "PAVOS_DEFAULT_GRID_COLUMNS";
	private static final String DEFAULT_GRID_LEVELS = "PAVOS_DEFAULT_GRID_LEVELS";
	private static final String DEFAULT_GRID_X_MIN = "PAVOS_DEFAULT_GRID_X_MIN";
	private static final String DEFAULT_GRID_X_MAX = "PAVOS_DEFAULT_GRID_X_MAX";
	private static final String DEFAULT_GRID_Y_MIN = "PAVOS_DEFAULT_GRID_Y_MIN";
	private static final String DEFAULT_GRID_Y_MAX = "PAVOS_DEFAULT_GRID_Y_MAX";
	private Properties properties = new Properties();
	private static GridPropertiesFileManager instance;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private void loadGridProperties() {
		properties.put(DEFAULT_GRID_NAME, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_NAME", "recursiveRectangleGrid"));
		properties.put(DEFAULT_GRID_ROWS, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_ROWS", "2"));
		properties.put(DEFAULT_GRID_COLUMNS, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_COLUMNS", "2"));
		properties.put(DEFAULT_GRID_LEVELS, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_LEVELS", "3"));
		properties.put(DEFAULT_GRID_X_MIN, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_X_MIN", "-180.0"));
		properties.put(DEFAULT_GRID_X_MAX, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_X_MAX", "180.0"));
		properties.put(DEFAULT_GRID_Y_MIN, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_Y_MIN", "-85.0"));
		properties.put(DEFAULT_GRID_Y_MAX, EnvironmentUtil.getEnvironmentVariable("PAVOS_DEFAULT_GRID_Y_MAX", "85.0"));
	}
	
	/**
	 * Default Constructor
	 */
	private GridPropertiesFileManager() {
		loadGridProperties();
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
		
		Thread t = new Thread(() ->  {
			switch(name) {
			case GeoRecRectangleGrid.NAME:
				new GeoRecRectangleGrid(new Rectangle2D.Double(xMin, yMin, xMax * 2, yMax * 2), rows, columns, levels);
				break;
			default:
				throw new IllegalArgumentException("Grid not recognized. Exiting with Errors.");
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			t.interrupt();
			logger.error("Thread was interrupted.");
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
	
}
