package server.core.grid;

import java.awt.geom.Rectangle2D;

import server.core.grid.config.Seperators;
import server.core.grid.polygon.GeoPolygon;
import server.core.grid.polygon.GeoRectangle;

public class GeoRecRectangleGrid extends GeoGrid {
	
	public static final String NAME = "recursiveRectangleGrid";
	
	public GeoRecRectangleGrid(Rectangle2D.Double mapBounds, int rows, int columns, int maxLevel) {
		super(mapBounds, rows, columns, maxLevel, getGridID(rows, columns, maxLevel));
		generateGeoPolygons();
	}

	private static String getGridID(int rows, int columns, int maxLevel) {
		return NAME + Seperators.GRIDID_GRIDPROPERTIES_SEPERATOR + rows + Seperators.GRIDPROPERTIES_SEPERATOR 
				+ columns + Seperators.GRIDPROPERTIES_SEPERATOR + maxLevel;
	}
	
	@Override
	protected void generateGeoPolygons() {
		double width = MAP_BOUNDS.getWidth() / (double) COLUMNS;
		double height = MAP_BOUNDS.getHeight() / (double) ROWS;
		double baseXOffset = MAP_BOUNDS.getX();
		double baseYOffset = MAP_BOUNDS.getY();
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				double xOffset = baseXOffset + (double) col * width;
				double yOffset = baseYOffset + (double) row * height;
				String id = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoPolygon polygon = new GeoRectangle(xOffset, yOffset, width, height
						, ROWS, COLUMNS, (MAX_LEVEL - 1), GRID_ID + Seperators.GRID_CLUSTER_SEPERATOR + id);
				polygons.add(polygon);
			}
		}
	}
	
}
