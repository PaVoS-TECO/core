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
		double width = mapBounds.getWidth() / (double) columns;
		double height = mapBounds.getHeight() / (double) rows;
		double baseXOffset = mapBounds.getX();
		double baseYOffset = mapBounds.getY();
		
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				double xOffset = baseXOffset + (double) col * width;
				double yOffset = baseYOffset + (double) row * height;
				Rectangle2D.Double bounds = new Rectangle2D.Double(xOffset, yOffset, width, height);
				String newId = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoPolygon polygon = new GeoRectangle(bounds, rows, columns, (maxLevel - 1), 
						this.id + Seperators.GRID_CLUSTER_SEPERATOR + newId);
				polygons.add(polygon);
			}
		}
	}
	
}
