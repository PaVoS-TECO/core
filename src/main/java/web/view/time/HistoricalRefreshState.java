package web.view.time;

import java.util.*;

/**
 * In this state the refresh function simulates the historical data mode. The timeStamp parameter isn't altered and the currently selected dataset entries stay the same.
 */
public class HistoricalRefreshState extends RefreshState {

    /**
     * Default constructor
     */
    public HistoricalRefreshState() {
    }

    /**
     * Switch to live data mode.
     * @param context
     */
    public void liveDataMode(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Switch to loop mode.
     * @param context
     */
    public void continueRoutine(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Returns the submitted time stamp without any change.
     * @param timeStamp 
     * @return the submitted time stamp without any change.
     */
    public Date refresh(Date timeStamp) {
        // TODO implement here
        return null;
    }

}