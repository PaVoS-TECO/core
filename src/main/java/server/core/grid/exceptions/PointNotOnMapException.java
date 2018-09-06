package server.core.grid.exceptions;

import java.awt.geom.Point2D;

/**
 * This {@link Exception} is thrown when a {@link Point2D} is not located within previously set boundaries.
 */
public class PointNotOnMapException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private Point2D.Double point;
	
	/**
	 * Creates a new {@link PointNotOnMapException}.
	 * @param point {@link Point2D.Double}
	 */
	public PointNotOnMapException(Point2D.Double point) {
		this.point = point;
	}
	
	/**
	 * Returns the {@link Point2D.Double} that caused this {@link Exception}.
	 * @return point {@link Point2D.Double}
	 */
	public Point2D.Double getPoint() {
		return this.point;
	}
	
}
