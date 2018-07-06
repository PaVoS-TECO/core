package web.view.time;

import java.util.*;

/**
 * A state
 */
public class RefreshState {

    /**
     * Default constructor
     */
    public RefreshState() {
    }

    /**
     * 
     */
    private RefreshContext refreshState;

    /**
     * Switch to live data mode.
     * @param context
     */
    public void liveDataMode(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Switch to historical data mode.
     * @param context
     */
    public void historicalDataMode(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Continue the current routine.
     * @param context
     */
    public void continueRoutine(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Stop the current routine.
     * @param context
     */
    public void stopRoutine(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Refresh the the submitted time stamp depending on the TimeStampState by returning a new time stamp.
     * @param timeStamp 
     * @return the most up-to-date time stamp.
     */
    public Date refresh(Date timeStamp) {
        // TODO implement here
        return null;
    }

}