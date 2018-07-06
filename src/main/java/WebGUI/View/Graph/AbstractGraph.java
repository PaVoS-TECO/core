package webGUI.View.graph;

import java.util.*;

/**
 * A graph that visualizes the data in its dataset.
 */
public class AbstractGraph extends ViewComponent implements GraphOptionPanelObserver, MapObserver, SensorOptionPanelObserver, TimeOptionPanelObserver {

    /**
     * Default constructor
     */
    public AbstractGraph() {
    }

    /**
     * 
     */
    protected RequestCommand dataset;

    /**
     * 
     */
    protected TimeFrame timeFrame;

    /**
     * 
     */
    protected Date timeStamp;

    /**
     * Update the dataset of this AbstractGraph by giving it a new RequestCommand.
     * @param dataset
     */
    public void updateDataset(RequestCommand dataset) {
        // TODO implement here
    }

    /**
     * Set the starting and end time point of the displayed dataset segment.
     * @param timeFrame
     */
    public void setTimeFrame(TimeFrame timeFrame) {
        // TODO implement here
    }

    /**
     * Set a time stamp.
     * @param timeStamp
     */
    public void setTimeStamp(Date timeStamp) {
        // TODO implement here
    }

    /**
     * Update the observer  with the current GraphOptionPanel state.
     */
    public void graphOptionUpdate() {
        // TODO implement here
    }

    /**
     * 
     */
    public void mapUpdate() {
        // TODO implement here
    }

    /**
     * Update the observer  with the current SensorOptionPanel state.
     */
    public void sensorOptionUpdate() {
        // TODO implement here
    }

    /**
     * Update the observer  with the current TimeOptionPanel state.
     */
    public void timeOptionUpdate() {
        // TODO implement here
    }

}