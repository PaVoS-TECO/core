package webGUI.View.sensorOption;

import java.util.*;

/**
 * A panel for handling user input, that deals with setting a new ObservedProperty and notifying its observers about changes.
 */
public class AbstractSensorOptionPanel extends ViewComponent {

    /**
     * Default constructor
     */
    public AbstractSensorOptionPanel() {
    }





    /**
     * Subscribe a SensorOptionPanelObserver to this AbstractSensorOptionPanel.
     * @param observer
     */
    public void subscribeObserver(SensorOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Unsubscribe a SensorOptionPanelObserver from this AbstractSensorOptionPanel.
     * @param observer
     */
    public void unsubscribeObserver(SensorOptionPanelObserver observer) {
        // TODO implement here
    }

    /**
     * Notify all subscribed SensorOptionPanelObservers about a change in this AbstractSensorOptionPanel.
     */
    public void notify() {
        // TODO implement here
    }

    /**
     * Set the sensor types.
     * @param observedProperties
     */
    public void setObservedProperties(Set<ObservedProperty> observedProperties) {
        // TODO implement here
    }

    /**
     * Get the sensor types.
     * @return the sensor types.
     */
    public Set<ObservedProperty> getObservedProperties() {
        // TODO implement here
        return null;
    }

}