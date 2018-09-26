package edu.teco.pavos.core.grid.exceptions;

import edu.teco.pavos.core.grid.polygon.GeoPolygon;

/**
 * This {@link Exception} is thrown when a cluster is currently not registrated.
 * This normally means, that the specified cluster does not exist.
 */
public class ClusterNotFoundException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private final String clusterID;
	
	/**
	 * Creates a new {@link ClusterNotFoundException}.
	 * @param clusterID {@link String}
	 */
	public ClusterNotFoundException(String clusterID) {
		this.clusterID = clusterID;
	}
	
	/**
	 * Returns the identifier of the {@link GeoPolygon} cluster that caused this {@link Exception}.
	 * @return clusterID {@link String}
	 */
	public String getCluster() {
		return this.clusterID;
	}
	
}
