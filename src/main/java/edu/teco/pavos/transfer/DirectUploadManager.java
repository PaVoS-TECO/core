package edu.teco.pavos.transfer;

import java.util.Collection;

import edu.teco.pavos.transfer.connector.Connector;
import edu.teco.pavos.transfer.connector.GraphiteConnector;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.GraphiteSender;

/**
 * The {@link DirectUploadManager} manages uploading {@link ObservationData} to any {@link Destination}.
 */
public class DirectUploadManager {
	
	/**
	 * Upload a {@link Collection} of {@link ObservationData} to the specified {@link Destination}
	 * @param records {@link Collection} of {@link ObservationData}
	 * @param dest {@link Destination}
	 * @return operationSuccessful {@link Boolean}
	 */
	public boolean uploadData(Collection<ObservationData> records, Destination dest) {
		if (dest.equals(Destination.GRAPHITE)) {
			return uploadDataToGraphite(records);
		}
		return false;
	}

	private boolean uploadDataToGraphite(Collection<ObservationData> records) {
		Connector connector = new GraphiteConnector(records);
    	boolean result = connector.run(new GraphiteSender());
    	connector.stop();
    	return result;
	}
	
}
