package edu.teco.pavos.transfer.connector;

import java.util.Collection;
import java.util.HashSet;

import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.Sender;

/**
 * Preforms any necessary steps before establishing a connection to Graphite.
 */
public class GraphiteConnector extends Connector {

	Collection<ObservationData> records = new HashSet<>();
	
	/**
	 * Creates a {@link GraphiteConnector}
	 * @param records {@link Collection} of {@link ObservationData}
	 */
	public GraphiteConnector(Collection<ObservationData> records) {
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
