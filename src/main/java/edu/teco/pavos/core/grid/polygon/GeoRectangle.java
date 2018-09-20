package edu.teco.pavos.core.grid.polygon;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import edu.teco.pavos.core.grid.config.Seperators;
import edu.teco.pavos.transfer.data.ObservationData;

/**
 * A geographically oriented approach to polygons with double precision.<p>
 * Uses {@link Path2D.Double} for the polygon base.
 * Can contain sub-{@link GeoRectangle}s.
 * Can contain {@link ObservationData} from sensors.
 */
public class GeoRectangle extends GeoPolygon {
	
	/**
	 * Creates a {@link GeoRectangle} with the given bounds, rows, columns and levels.<p>
	 * @param bounds {@link Rectangle2D.Double} a bounding-box around our {@link GeoRectangle}
	 * @param rows How many times the {@link GeoRectangle} will be subdivided horizontally
	 * @param columns How many times the {@link GeoRectangle} will be subdivided vertically
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoRectangle}
	 */
	public GeoRectangle(Rectangle2D.Double bounds, int rows, int columns, int levelsAfterThis, String id) {
		super(bounds, rows, columns, levelsAfterThis, id);
		generatePath();
		if (this.getLevelsAfterThis() > 0) {
			generateSubPolygons(this.getRows(), this.getColumns());
		}
	}

	@Override
	protected void generatePath() {
		path.moveTo(getBounds().getX(), getBounds().getY());
		path.lineTo(getBounds().getX() + getBounds().getWidth(), getBounds().getY());
		path.lineTo(getBounds().getX() + getBounds().getWidth(), getBounds().getY() + getBounds().getHeight());
		path.lineTo(getBounds().getX(), getBounds().getY() + getBounds().getHeight());
		path.closePath();
	}
	
	@Override
	protected void generateSubPolygons(int subdivisions) {
		generateSubPolygons(subdivisions, subdivisions);
	}

	@Override
	protected void generateSubPolygons(int xSubdivisions, int ySubdivisions) {
		double subWidth = getBounds().getWidth() / (double) ySubdivisions;
		double subHeight = getBounds().getHeight() / (double) xSubdivisions;
		
		for (int row = 0; row < xSubdivisions; row++) {
			for (int col = 0; col < ySubdivisions; col++) {
				double subXOffset = getBounds().getX() + (double) col * subWidth;
				double subYOffset = getBounds().getY() + (double) row * subHeight;
				Rectangle2D.Double subBounds = new Rectangle2D.Double(subXOffset, subYOffset, subWidth, subHeight);
				String subID = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoRectangle subPolygon = new GeoRectangle(subBounds, this.getRows(),
						this.getColumns(), (getLevelsAfterThis() - 1),
						getID() + Seperators.CLUSTER_SEPERATOR + subID);
				subPolygons.add(subPolygon);
			}
		}
	}
	
}
