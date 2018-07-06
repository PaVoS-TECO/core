package webGUI.View.map;

import java.util.*;

/**
 * A panel for handling user input, that deals with setting a new TileType and notifying its observers about the change.
 */
public class AbstractMapOptionPanel extends ViewComponent {

    /**
     * Default constructor
     */
    public AbstractMapOptionPanel() {
    }


    /**
     * 
     */
    protected Set<MapOptionPanelObserver> observers;


    /**
     * Subscribe a MapOptionPanelObserver to this AbstractMapOptionPanel.
     * @param observer
     */
    public void subscribeObserver(MapOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Unsubscribe a MapOptionPanelObserver from this AbstractMapOptionPanel.
     * @param observer
     */
    public void unsubscribeObserver(MapOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Notify all subscribed MapOptionPanelObservers about a change in this AbstractMapOptionPanel.
     */
    public void notify() {
        // TODO implement here
    }

    /**
     * Set the TileTypes.
     * @param tileTypes
     */
    public void setTileTypes(Set<TileType> tileTypes) {
        // TODO implement here
    }

    /**
     * Get the TileTypes.
     * @return the TileTypes.
     */
    public Set<TileType> getTileTypes() {
        // TODO implement here
        return null;
    }

}