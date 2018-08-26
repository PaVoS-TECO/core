package server.core.grid.exceptions;

/**
 * This {@link Exception} is thrown when a cluster is currently not registrated.
 * This normally means, that the specified cluster does not exist.
 */
public class ClusterNotFoundException extends Exception {

	private static final long serialVersionUID = -461574811099025580L;
	private String clusterID;
	
	public ClusterNotFoundException(String clusterID) {
		this.clusterID = clusterID;
	}
	
	public String getCluster() {
		return this.clusterID;
	}
	
}
