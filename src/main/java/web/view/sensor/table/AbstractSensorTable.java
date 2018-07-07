package web.view.sensor.table;

import java.util.Date;

import server.core.command.RequestCommand;
import view.ViewComponent;
import web.view.map.MapObserver;
import web.view.sensor.SensorOptionPanelObserver;
import web.view.time.TimeOptionPanelObserver;
import web.view.util.SensorID;

/**
 * A table that visualizes the data in its dataset and enables the selection of a Sensor by using its SensorID.
 */
public class AbstractSensorTable extends ViewComponent implements SensorOptionPanelObserver, TimeOptionPanelObserver, MapObserver {

    /**
     * Default constructor
     */
    public AbstractSensorTable() {
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
     * Update the dataset of this AbstractSensorTable by giving it a new RequestCommand.
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
     * Select a sensor in the dataset by using its SensorID.
     * @param sensorId
     */
    public void selectSensor(SensorID sensorId) {
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

    /**
     * 
     */
    public void mapUpdate() {
        // TODO implement here
    }

}