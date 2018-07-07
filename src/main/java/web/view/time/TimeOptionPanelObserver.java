package web.view.time;

/**
 * An observer that is meant to observe changes in the TimeOptionPanel.
 */
public interface TimeOptionPanelObserver {

    /**
     * Update the observer  with the current TimeOptionPanel state.
     */
    public void timeOptionUpdate();

}