package webGUI.View.timeOption;

import java.util.*;

/**
 * Encapsulates the preferences about the fetching of live data and the loop mode of historical data.
 */
public class RefreshConfiguration {

    /**
     * Default constructor
     */
    public RefreshConfiguration() {
    }

    /**
     * 
     */
    private boolean autoRefresh;

    /**
     * 
     */
    private float refreshInterval;



    /**
     * In live mode set whether data should be fetched automatically or manually. In historic mode set whether in loop mode the time stamp should be refreshed automatically or manually.
     * @param bool
     */
    public void setAutoRefresh(boolean bool) {
        // TODO implement here
    }

    /**
     * In live mode return whether data should be fetched automatically or manually. In historic mode return whether in loop mode the time stamp should be refreshed automatically or manually.
     * @return in live mode whether data should be fetched automatically or manually and In historic mode whether in loop mode the time stamp should be refreshed automatically or manually.
     */
    public boolean autoRefresh() {
        // TODO implement here
        return false;
    }

    /**
     * Set the interval in which automatic refreshes are made.
     * @param inv
     */
    public void setRefreshInterval(Interval inv) {
        // TODO implement here
    }

    /**
     * Returns the interval in which automatic refreshes are made.
     * @return the interval in which automatic refreshes are made.
     */
    public float refreshInterval() {
        // TODO implement here
        return 0.0f;
    }

}