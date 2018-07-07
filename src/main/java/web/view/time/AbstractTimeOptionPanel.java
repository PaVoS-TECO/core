package web.view.time;

import java.util.Date;

import view.ViewComponent;
import web.view.util.TimeFrame;

/**
 * A panel for handling user input, that deals with timing options and notifying its observers about changes in its state.
 */
public class AbstractTimeOptionPanel extends ViewComponent {

    /**
     * Default constructor
     */
    public AbstractTimeOptionPanel() {
    }

    /**
     * 
     */
    protected TimeFrame loopTimeFrame;

    /**
     * 
     */
    protected Date timeStamp;

    /**
     * 
     */
    protected RefreshConfiguration refreshConfig;





    /**
     * Subscribe a TimeOptionPanelObserver to this AbstractTimeOptionPanel.
     * @param observer
     */
    public void subscribeObserver(TimeOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Unsubscribe a TimeOptionPanelObserver from this AbstractTimeOptionPanel.
     * @param observer
     */
    public void unsubscribeObserver(TimeOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Notify all subscribed TimeOptionPanelObservers about a change in this AbstractTimeOptionPanel.
     */
    public void notify() {
        // TODO implement here
    }

    /**
     * Get the RefreshContext.
     * @return the RefreshContext.
     */
    public RefreshContext getRefreshContext() {
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
    public TimeFrame getLoopTimeframe() {
        // TODO implement here
        return null;
    }

    /**
     * Set the time stamp.
     * @param timeStamp
     */
    public void setTimeStamp(Date timeStamp) {
        // TODO implement here
    }

    /**
     * Get the time stamp.
     * @return the time stamp.
     */
    public Date getTimeStamp() {
        // TODO implement here
        return null;
    }

}