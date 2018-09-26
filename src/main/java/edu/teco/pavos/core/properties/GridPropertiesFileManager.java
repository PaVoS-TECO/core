package edu.teco.pavos.core.properties;

import java.awt.geom.Rectangle2D;
import java.util.Properties;

import edu.teco.pavos.core.grid.GeoGrid;
import edu.teco.pavos.core.grid.GeoRecRectangleGrid;
import edu.teco.pavos.transfer.config.util.EnvironmentUtil;

/**
 * The {@link GridPropertiesFileManager} manages different properties
 * that are needed to create a {@link GeoGrid}
 * and stores them in a {@link Properties} object.
 */
public final class GridPropertiesFileManager extends PropertiesFileManager {
	
	private static final String DEFAULT_GRID_NAME = "PAVOS_DEFAULT_GRID_NAME";
	private static final String DEFAULT_GRID_ROWS = "PAVOS_DEFAULT_GRID_ROWS";
	private static final String DEFAULT_GRID_COLUMNS = "PAVOS_DEFAULT_GRID_COLUMNS";
	private static final String DEFAULT_GRID_LEVELS = "PAVOS_DEFAULT_GRID_LEVELS";
	private static final String DEFAULT_GRID_X_MIN = "PAVOS_DEFAULT_GRID_X_MIN";
	private static final String DEFAULT_GRID_X_MAX = "PAVOS_DEFAULT_GRID_X_MAX";
	private static final String DEFAULT_GRID_Y_MIN = "PAVOS_DEFAULT_GRID_Y_MIN";
	private static final String DEFAULT_GRID_Y_MAX = "PAVOS_DEFAULT_GRID_Y_MAX";
	private static GridPropertiesFileManager instance;
	
	private void loadGridProperties() {
		load(DEFAULT_GRID_NAME, "recursiveRectangleGrid");
		load(DEFAULT_GRID_ROWS, "2");
		load(DEFAULT_GRID_COLUMNS, "2");
		load(DEFAULT_GRID_LEVELS, "3");
		load(DEFAULT_GRID_X_MIN, "-180.0");
		load(DEFAULT_GRID_X_MAX, "180.0");
		load(DEFAULT_GRID_Y_MIN, "-85.0");
		load(DEFAULT_GRID_Y_MAX, "85.0");
	}
	
	private void load(String property, String defaultValue) {
		properties.put(property, EnvironmentUtil.getEnvironmentVariable(property, defaultValue));
	}
	
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
			if (name.equals(GeoRecRectangleGrid.NAME)) {
				new GeoRecRectangleGrid(new Rectangle2D.Double(xMin, yMin, xMax * 2, yMax * 2), rows, columns, levels);
			} else {
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
	 * Returns the instance of this {@link GridPropertiesFileManager} or generates a new one if it does not exists.
	 * @return {@link GridPropertiesFileManager}
	 */
	public static GridPropertiesFileManager getInstance() {
		if (instance == null) {
			instance = new GridPropertiesFileManager();
		}
		return instance;
	}
	
}
