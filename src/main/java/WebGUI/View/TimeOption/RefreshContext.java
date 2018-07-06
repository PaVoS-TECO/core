package View.TimeOption;

import java.util.*;

/**
 * Encapsulates the logic of switching between historical and live data mode and starting and stopping the loop mode. Through LiveRefreshConfiguration it also encapsulates whether live data is fetched automatically or manually and in which interval.
 */
public class RefreshContext implements TimeOptionPanelObserver {

    /**
     * Default constructor
     */
    public RefreshContext() {
    }

    /**
     * 
     */
    private TimeFrame loopTimeFrame;





    /**
     * Set the current refresh state.
     * @param refreshState
     */
    public void setRefreshState(RefreshState refreshState) {
        // TODO implement here
    }

    /**
     * Get the RefreshConfiguration.
     * @return the RefreshConfiguration.
     */
    public RefreshConfiguration getRefreshConfig() {
        // TODO implement here
        return null;
    }

    /**
     * Set the start and end time point of the loop.
     * @param loopTimeFrame
     */
    public void setLoopTimeFrame(TimeFrame loopTimeFrame) {
        // TODO implement here
    }

    /**
     * Get the loop time frame.
     * @return the loop time frame.
     */
    public TimeFrame getLoopTimeFrame() {
        // TODO implement here
        return null;
    }

    /**
     * Switch to live data mode.
     */
    public void liveDataMode() {
        // TODO implement here
    }

    /**
     * Switch to historical data mode.
     */
    public void historicalDataMode() {
        // TODO implement here
    }

    /**
     * Continue the current routine.
     */
    public void continueRoutine() {
        // TODO implement here
    }

    /**
     * Stop the current routine.
     */
    public void stopRoutine() {
        // TODO implement here
    }

    /**
     * Refresh the submitted time stamp depending on the TimeStampState by returning a new time stamp.
     * @param timeStamp 
     * @return the submitted time stamp altered depending on the TimeStampState.
     */
    public Date refresh(Date timeStamp) {
        // TODO implement here
        return null;
    }

    /**
     * Update the observer  with the current TimeOptionPanel state.
     */
    public void timeOptionUpdate() {
        // TODO implement here
    }

}