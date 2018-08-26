package server.core.grid.exceptions;

/**
 * This {@link Exception} is thrown when a cluster is currently not registrated.
 * This normally means, that the specified cluster does not exist.
 */
public class GridNotFoundException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private String gridID;
	
	public GridNotFoundException(String gridID) {
		this.gridID = gridID;
	}
	
	public String getGrid() {
		return this.gridID;
	}
	
}
