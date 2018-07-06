package View.SensorOption;

import java.util.*;

/**
 * An observer that is meant to observe changes in the SensorOptionPanel.
 */
public interface SensorOptionPanelObserver {

    /**
     * Update the observer  with the current SensorOptionPanel state.
     */
    public void sensorOptionUpdate();

}