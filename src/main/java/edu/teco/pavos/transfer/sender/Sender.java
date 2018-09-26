package edu.teco.pavos.transfer.sender;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.teco.pavos.transfer.data.ObservationData;

/**
 * Sends data to the specified component
 */
public abstract class Sender {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * Sends the recorded data.
     * @param records {@link Collection} of {@link ObservationData}
     * @return operationSuccessful {@link Boolean}
     */
    public abstract boolean send(Collection<ObservationData> records);
    
    /**
     * Closes the sender and the connection
     */
    public abstract void close();

}