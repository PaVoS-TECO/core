package server.core.grid.polygon;

import server.core.grid.config.Seperators;

/**
 * A geographically oriented rectangle-polygon.
 * For more information see {@link GeoPolygon}.
 */
public class GeoRectangle extends GeoPolygon {
	
	/**
	 * Creates a {@link GeoRectangle} with the given offsets, width, height and id.<p>
	 * @param xOffset The horizontal offset
	 * @param yOffset The vertical offset
	 * @param width
	 * @param height
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoPolygon}
	 */
	public GeoRectangle(double xOffset, double yOffset, double width, double height, int rows, int columns, int levelsAfterThis, String id) {
		super(xOffset, yOffset, width, height, rows, columns, levelsAfterThis, id);
		generatePath();
		if (LEVELS_AFTER_THIS > 0) {
			generateSubPolygons(ROWS, COLUMNS);
		}
	}

	@Override
	protected void generatePath() {
		path.moveTo(X_OFFSET, Y_OFFSET);
		path.lineTo(X_OFFSET + WIDTH, Y_OFFSET);
		path.lineTo(X_OFFSET + WIDTH, Y_OFFSET + HEIGHT);
		path.lineTo(X_OFFSET, Y_OFFSET + HEIGHT);
		path.closePath();
	}
	
	@Override
	protected void generateSubPolygons(int subdivisions) {
		generateSubPolygons(subdivisions, subdivisions);
	}

	@Override
	protected void generateSubPolygons(int xSubdivisions, int ySubdivisions) {
		double subWidth = WIDTH / (double) ySubdivisions;
		double subHeight = HEIGHT / (double) xSubdivisions;
		
		for (int row = 0; row < xSubdivisions; row++) {
			for (int col = 0; col < ySubdivisions; col++) {
				double subXOffset = (double) col * subWidth;
				double subYOffset = (double) row * subHeight;
				String subID = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoRectangle subPolygon = new GeoRectangle(subXOffset, subYOffset, subWidth, subHeight
						, ROWS, COLUMNS, (LEVELS_AFTER_THIS - 1)
						, ID + Seperators.CLUSTER_SEPERATOR + subID);
				subPolygons.add(subPolygon);
			}
		}
	}
	
}
