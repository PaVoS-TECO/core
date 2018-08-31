package server.core.grid.polygon;

import java.awt.geom.Rectangle2D;

import server.core.grid.config.Seperators;

/**
 * A geographically oriented rectangle-polygon.
 * For more information see {@link GeoPolygon}.
 */
public class GeoRectangle extends GeoPolygon {
	
	/**
	 * Creates a {@link GeoRectangle} with the given offsets, width, height and id.<p>
	 * @param bounds {@link Rectangle2D.Double} a bounding-box around our {@link GeoRectangle}
	 * @param rows 
	 * @param columns 
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoRectangle}
	 */
	public GeoRectangle(Rectangle2D.Double bounds, int rows, int columns, int levelsAfterThis, String id) {
		super(bounds, rows, columns, levelsAfterThis, id);
		generatePath();
		if (this.levelsAfterThis > 0) {
			generateSubPolygons(this.rows, this.columns);
		}
	}

	@Override
	protected void generatePath() {
		path.moveTo(bounds.getX(), bounds.getY());
		path.lineTo(bounds.getX() + bounds.getWidth(), bounds.getY());
		path.lineTo(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());
		path.lineTo(bounds.getX(), bounds.getY() + bounds.getHeight());
		path.closePath();
	}
	
	@Override
	protected void generateSubPolygons(int subdivisions) {
		generateSubPolygons(subdivisions, subdivisions);
	}

	@Override
	protected void generateSubPolygons(int xSubdivisions, int ySubdivisions) {
		double subWidth = bounds.getWidth() / (double) ySubdivisions;
		double subHeight = bounds.getHeight() / (double) xSubdivisions;
		
		for (int row = 0; row < xSubdivisions; row++) {
			for (int col = 0; col < ySubdivisions; col++) {
				double subXOffset = bounds.getX() + (double) col * subWidth;
				double subYOffset = bounds.getY() + (double) row * subHeight;
				Rectangle2D.Double subBounds = new Rectangle2D.Double(subXOffset, subYOffset, subWidth, subHeight);
				String subID = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoRectangle subPolygon = new GeoRectangle(subBounds, this.rows, this.columns, (levelsAfterThis - 1)
						, id + Seperators.CLUSTER_SEPERATOR + subID);
				subPolygons.add(subPolygon);
			}
		}
	}
	
}
