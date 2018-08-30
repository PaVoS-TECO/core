package server.core.grid;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.core.grid.exceptions.GridNotFoundException;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.exceptions.SensorNotFoundException;
import server.transfer.data.ObservationData;

public final class GeoGridManager {
	
	private static GeoGridManager instance;
	private List<GeoGrid> grids = new ArrayList<>();
	private ScheduledExecutorService execUpdate = Executors.newSingleThreadScheduledExecutor();
	
	private GeoGridManager() {
		execUpdate.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				for (GeoGrid grid : grids) {
					grid.updateObservations();
				}
			}
			
		}, 0, 10, TimeUnit.SECONDS);
		
//		new Thread(WebServer.getInstance()).start();
		
	}
	
	public static GeoGridManager getInstance() {
		if (instance == null) {
			instance = new GeoGridManager();
		}
		return instance;
	}
	
	public void addGeoGrid(GeoGrid grid) {
		this.grids.add(grid);
	}
	
	public GeoGrid getGrid(String gridID) {
		for (GeoGrid entry : this.grids) {
			if (entry.GRID_ID.equals(gridID)) {
				return entry;
			}
		}
		return null;
	}
	
	public boolean isGridActive(GeoGrid grid) {
		return this.grids.contains(grid);
	}
	
	public boolean isGridActive(String gridID) {
		for (GeoGrid entry : this.grids) {
			if (entry.GRID_ID.equals(gridID)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeGeoGrid(GeoGrid grid) {
		this.grids.remove(grid);
	}
	
	public Collection<String> getAllProperties() {
		Collection<String> properties = new HashSet<>();
		for (GeoGrid grid : this.grids) {
			properties.addAll(grid.getGridProperties());
		}
		return properties;
	}
	
	public Collection<ObservationData> getAllSensorObservations() {
		Collection<ObservationData> observations = new HashSet<>();
		for (GeoGrid grid : this.grids) {
			observations.addAll(grid.getGridSensorObservations());
		}
		return observations;
	}
	
	public ObservationData getSensorObservation(String sensorID, String gridID) 
			throws GridNotFoundException, SensorNotFoundException, PointNotOnMapException {
		for (GeoGrid grid : this.grids) {
			if (grid.GRID_ID.equals(gridID)) {
				Point2D.Double point = grid.getSensorLocation(sensorID);
				return grid.getSensorObservation(sensorID, point);
			}
		}
		throw new GridNotFoundException(gridID);
	}
	
}
