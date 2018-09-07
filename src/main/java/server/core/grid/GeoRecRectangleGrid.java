package server.core.grid;

import java.awt.geom.Rectangle2D;

import server.core.grid.config.Seperators;
import server.core.grid.polygon.GeoPolygon;
import server.core.grid.polygon.GeoRectangle;

/**
 * A geographically oriented approach to a polygon-tiled map.<br>
 * Uses {@link GeoRectangle}s to tile the map.
 */
public class GeoRecRectangleGrid extends GeoGrid {
	
	/**
	 * The name used to identify this class.
	 */
	public static final String NAME = "recursiveRectangleGrid";
	
	/**
	 * Creates a new {@link GeoRecRectangleGrid} and supplies the {@link GeoGridManager} with this grid.
	 * @param mapBounds {@link Rectangle2D.Double}
	 * @param rows {@link Integer}
	 * @param columns {@link Integer}
	 * @param maxLevel {@link Integer}
	 */
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
		double width = getMapBounds().getWidth() / (double) getColumns();
		double height = getMapBounds().getHeight() / (double) getRows();
		double baseXOffset = getMapBounds().getX();
		double baseYOffset = getMapBounds().getY();
		
		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getColumns(); col++) {
				double xOffset = baseXOffset + (double) col * width;
				double yOffset = baseYOffset + (double) row * height;
				Rectangle2D.Double bounds = new Rectangle2D.Double(xOffset, yOffset, width, height);
				String newId = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoPolygon polygon = new GeoRectangle(bounds, getRows(), getColumns(), (getMaxLevel() - 1), 
						getID() + Seperators.GRID_CLUSTER_SEPERATOR + newId);
				polygons.add(polygon);
			}
		}
	}
	
}
