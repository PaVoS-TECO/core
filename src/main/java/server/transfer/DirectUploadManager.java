package server.transfer;

import java.util.Collection;

import server.transfer.connector.Connector;
import server.transfer.connector.DirectGraphiteConnector;
import server.transfer.data.ObservationData;
import server.transfer.sender.GraphiteSender;

public class DirectUploadManager {
	
	private Connector connector;
	
	public boolean uploadData(Collection<ObservationData> records, Destination dest) {
		if (dest.equals(Destination.GRAPHITE)) {
			return uploadDataToGraphite(records);
		}
		return false;
	}

	private boolean uploadDataToGraphite(Collection<ObservationData> records) {
		connector = new DirectGraphiteConnector(records);
    	boolean result = connector.run(new GraphiteSender());
    	connector.stop();
    	return result;
	}
	
}
