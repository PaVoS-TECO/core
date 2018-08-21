package server.core.grid;

import java.awt.geom.Point2D;

import server.core.grid.config.Seperators;
import server.core.grid.polygon.GeoPolygon;
import server.core.grid.polygon.GeoRectangle;

public class GeoRectangleGrid extends GeoGrid {
	
	public GeoRectangleGrid(Point2D.Double mapBounds, int rows, int columns, int maxLevel, String gridID) {
		super(mapBounds, rows, columns, maxLevel, gridID);
		generateGeoPolygons();
	}

	@Override
	protected void generateGeoPolygons() {
		double width = MAP_BOUNDS.getX() / (double) COLUMNS;
		double height = MAP_BOUNDS.getY() / (double) ROWS;
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				double xOffset = (double) col * width;
				double yOffset = (double) row * height;
				String id = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoPolygon subPolygon = new GeoRectangle(xOffset, yOffset, width, height
						, ROWS, COLUMNS, (MAX_LEVEL - 1), GRID_ID + Seperators.GRID_CLUSTER_SEPERATOR + id);
				polygons.add(subPolygon);
			}
		}
	}
	
}
