package server.transfer.connector;

import java.util.Collection;
import java.util.HashSet;

import server.transfer.data.ObservationData;
import server.transfer.sender.Sender;

public class DirectGraphiteConnector extends Connector {

	Collection<ObservationData> records = new HashSet<>();
	
	public DirectGraphiteConnector(Collection<ObservationData> records) {
		this.records = records;
	}
	
	@Override
	public boolean run(Sender sender) {
		this.sender = sender;
		
		if (!records.isEmpty()) {
			return sender.send(records);
		}
		
		return false;
	}

	@Override
	public void stop() {
		this.sender.close();
	}

}
