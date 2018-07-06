package View.TimeOption;

import java.util.*;

/**
 * In this state the refresh function simulates the loop data mode. Depending on the loopTimeFrame value and the RefreshConfiguration, the refresh method modifies the submitted timeStamp which can be submitted to other ViewComponents to iterate to the next dataset entries.
 */
public class LoopRefreshState extends RefreshState {

    /**
     * Default constructor
     */
    public LoopRefreshState() {
    }

    /**
     * Switch to live data mode.
     * @param context
     */
    public void liveDataMode(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Switch to historical mode.
     * @param context
     */
    public void stopRoutine(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Returns the submitted time stamp modified according to the RefreshConfiguration.
     * @param timeStamp 
     * @return the submitted time stamp modified according to the RefreshConfiguration.
     */
    public Date refresh(Date timeStamp) {
        // TODO implement here
        return null;
    }

}