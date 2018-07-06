package web.view.map;

import java.util.*;

/**
 * An observer that is meant to observe changes in the MapOptionPanel.
 */
public interface MapOptionPanelObserver {

    /**
     * Update the observer  with the current MapOptionPanel state.
     */
    public void mapOptionUpdate();

}