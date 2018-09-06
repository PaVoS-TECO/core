package server.core.grid.exceptions;

import server.core.grid.GeoGrid;

/**
 * This {@link Exception} is thrown when a cluster is currently not registrated.
 * This normally means, that the specified cluster does not exist.
 */
public class GridNotFoundException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private String gridID;
	
	/**
	 * Creates a new {@link GridNotFoundException}.
	 * @param gridID {@link String}
	 */
	public GridNotFoundException(String gridID) {
		this.gridID = gridID;
	}
	
	/**
	 * Returns the identifier of the {@link GeoGrid} that caused this {@link Exception}.
	 * @return gridID {@link String}
	 */
	public String getGrid() {
		return this.gridID;
	}
	
}
