package web.view.graph;

import java.util.*;

/**
 * A panel for handling user input, that deals with which time segment of the graphs dataset is being displayed, how that is done and notifying all observers about changes in its state.
 */
public class AbstractGraphOptionPanel extends ViewComponent {

    /**
     * Default constructor
     */
    public AbstractGraphOptionPanel() {
    }

    /**
     * 
     */
    protected TimeFrame timeframe;




    /**
     * Subscribe a GraphOptionPanelObserver to this AbstractGraphOptionPanel.
     * @param observer
     */
    public void subscribeObserver(GraphOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Unsubscribe a GraphOptionPanelObserver from this AbstractGraphOptionPanel.
     * @param observer
     */
    public void unsubscribeObserver(GraphOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Notify all subscribed GraphOptionPanelObservers about a change in this AbstractGraphOptionPanel.
     */
    public void notify() {
        // TODO implement here
    }

    /**
     * Set the GraphDisplayTypes.
     * @param displayTypes
     */
    public void setDisplayTypes(Set<GraphDisplayType> displayTypes) {
        // TODO implement here
    }

    /**
     * Get the GraphDisplayTypes.
     * @return the GraphDisplayTypes.
     */
    public Set<GraphDisplayType> getDisplayTypes() {
        // TODO implement here
        return null;
    }

    /**
     * Set the starting and end time point of the displayed dataset segment..
     * @param timeFrame
     */
    public void setTimeFrame(TimeFrame timeFrame) {
        // TODO implement here
    }

}