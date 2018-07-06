package View.Map;

import java.util.*;

/**
 * A world map with displayable and hideable MapLayers, move and zoom function. It notifies its observers about changes in its state.
 */
public class AbstractMap extends ViewComponent implements MapOptionPanelObserver, SensorOptionPanelObserver, TimeOptionPanelObserver {

    /**
     * Default constructor
     */
    public AbstractMap() {
    }

    /**
     * 
     */
    protected RequestCommand dataset;

    /**
     * 
     */
    protected Date timeStamp;

    /**
     * 
     */
    protected Point position;

    /**
     * 
     */
    protected int zoom;



    /**
     * Subscribe a MapObserver to this AbstractMap.
     * @param observer
     */
    public void subscribeObserver(MapObserver observer) {
        // TODO implement here
    }

    /**
     * Unsubscribe a MapObserver from this AbstractMap.
     * @param observer
     */
    public void unsubscribeObserver(MapObserver observer) {
        // TODO implement here
    }

    /**
     * Notify all subscribed MapObservers about a change in this AbstractMap.
     */
    public void notify() {
        // TODO implement here
    }

    /**
     * Update the dataset of this AbstractMap by giving it a new RequestCommand.
     * @param dataset
     */
    public void updateDataset(RequestCommand dataset) {
        // TODO implement here
    }

    /**
     * Set a time stamp and display the data from the dataset at the specified point in time.
     * @param timeStamp
     */
    public void setTimeStamp(Date timeStamp) {
        // TODO implement here
    }

    /**
     * Add a MapLayer.
     * @param layer
     */
    public void addLayer(MapLayer layer) {
        // TODO implement here
    }

    /**
     * Remove a MapLayer.
     * @param layer
     */
    public void removeLayer(MapLayer layer) {
        // TODO implement here
    }

    /**
     * Display a MapLayer.
     * @param layer
     */
    public void displayLayer(MapLayer layer) {
        // TODO implement here
    }

    /**
     * Hide a MapLayer.
     * @param layer
     */
    public void hideLayer(MapLayer layer) {
        // TODO implement here
    }

    /**
     * Set the center position of the AbstractMap.
     * @param point
     */
    public void setPosition(Point point) {
        // TODO implement here
    }

    /**
     * Set the zoom level of this AbstractMap.
     * @param zoom
     */
    public void setZoom(int zoom) {
        // TODO implement here
    }

    /**
     * Update the observer  with the current MapOptionPanel state.
     */
    public void mapOptionUpdate() {
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