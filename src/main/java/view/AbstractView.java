package view;

import java.util.AbstractMap;

import web.view.export.AbstractExportOptionPanel;
import web.view.graph.AbstractGraph;
import web.view.graph.AbstractGraphOptionPanel;
import web.view.map.AbstractMapOptionPanel;
import web.view.sensor.AbstractSensorOptionPanel;
import web.view.sensor.table.AbstractSensorTable;
import web.view.time.AbstractTimeOptionPanel;

/**
 * Encapsulates all ViewComponents created by the AbstractViewFactory into a single object.
 */
public class AbstractView {

    /**
     * Default constructor
     */
    public AbstractView() {
    }


    /**
     * Get the AbstractMap.
     * @return the AbstractMap.
     */
    public AbstractMap getMap() {
        // TODO implement here
        return null;
    }

    /**
     * Get the AbstractMapOptionPanel.
     * @return the AbstractMapOptionPanel.
     */
    public AbstractMapOptionPanel getMapOptionPanel() {
        // TODO implement here
        return null;
    }

    /**
     * Get the AbstractGraph.
     * @return the AbstractGraph.
     */
    public AbstractGraph getGraph() {
        // TODO implement here
        return null;
    }

    /**
     * Get the AbstractGraphOptionPanel.
     * @return the AbstractGraphOptionPanel.
     */
    public AbstractGraphOptionPanel getGraphOptionPanel() {
        // TODO implement here
        return null;
    }

    /**
     * Get the AbstractSensorOptionPanel.
     * @return the AbstractSensorOptionPanel.
     */
    public AbstractSensorOptionPanel getSensorOptionPanel() {
        // TODO implement here
        return null;
    }

    /**
     * Get the AbstractSensorTable.
     * @return the AbstractSensorTable.
     */
    public AbstractSensorTable getSensorTable() {
        // TODO implement here
        return null;
    }

    /**
     * Get the AbstractTimeOptionPanel.
     * @return the AbstractTimeOptionPanel.
     */
    public AbstractTimeOptionPanel getTimeOptionPanel() {
        // TODO implement here
        return null;
    }

    /**
     * Get the AbstractExportOptionPanel.
     * @return the AbstractExportOptionPanel.
     */
    public AbstractExportOptionPanel getExportOptionPanel() {
        // TODO implement here
        return null;
    }

}