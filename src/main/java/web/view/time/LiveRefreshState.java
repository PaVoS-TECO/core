package web.view.time;

import java.util.Date;

/**
 * In this state the refresh function simulates the live data mode. Depending on the RefreshConfiguration the refresh function fetches live data. The timeStamp parameter isn't altered.
 */
public class LiveRefreshState extends RefreshState {

    /**
     * Default constructor
     */
    public LiveRefreshState() {
    }

    /**
     * Switch to historical data mode.
     * @param context
     */
    public void historicalDataMode(RefreshContext context) {
        // TODO implement here
    }

    /**
     * Fetch live data and return the most up-to-date time stamp.
     * @param timeStamp 
     * @return the most up-to-date time stamp.
     */
    public Date refresh(Date timeStamp) {
        // TODO implement here
        return null;
    }

}