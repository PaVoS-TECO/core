package server.core.grid.exceptions;

import java.awt.geom.Point2D;

/**
 * This {@link Exception} is thrown when a {@link Point2D} is not located within previously set boundaries.
 */
public class PointNotOnMapException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private Point2D.Double point;
	
	public PointNotOnMapException(Point2D.Double point) {
		this.point = point;
	}
	
	public Point2D.Double getPoint() {
		return this.point;
	}
	
}
