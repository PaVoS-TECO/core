package server.core.grid;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	
	private static final Object lock = new Object();
	private static volatile GeoGridManager instance;
	private static volatile List<GeoGrid> grids = Collections.synchronizedList(new ArrayList<>());
	private ScheduledExecutorService execUpdate = Executors.newSingleThreadScheduledExecutor();
	
	private GeoGridManager() {
		new Thread(() -> {
			execUpdate.scheduleAtFixedRate(() -> {
				synchronized (grids) {
					for (GeoGrid grid : grids) {
						grid.updateObservations();
						grid.transferSensorDataDirectly();
						grid.updateDatabase();
						grid.resetObservations();
					}
				}
			}, 0, 10, TimeUnit.SECONDS);
		}).start();
	}
	
	public static GeoGridManager getInstance() {
		GeoGridManager result = instance;
		if (result == null) {
			synchronized (lock) {
				result = instance;
				if (result == null) {
					result = new GeoGridManager();
					instance = result;
				}
			}
		}
		return result;
	}
	
	public GeoGrid getNewestGrid() {
		synchronized (grids) {
			return grids.get(0);
		}
	}
	
	public void addGeoGrid(GeoGrid grid) {
		synchronized (grids) {
			grids.add(0, grid);
		}
	}
	
	public GeoGrid getGrid(String gridID) {
		synchronized (grids) {
			for (GeoGrid entry : grids) {
				if (entry.id.equals(gridID)) {
					return entry;
				}
			}
			return null;
		}
	}
	
	public boolean isGridActive(GeoGrid grid) {
		synchronized (grids) {
			return grids.contains(grid);
		}
	}
	
	public boolean isGridActive(String gridID) {
		synchronized (grids) {
			for (GeoGrid entry : grids) {
				if (entry.id.equals(gridID)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public void removeGeoGrid(GeoGrid grid) {
		synchronized (grids) {
			grids.remove(grid);
		}
	}
	
	public Collection<String> getAllProperties() {
		synchronized (grids) {
			Collection<String> properties = new HashSet<>();
			for (GeoGrid grid : grids) {
				properties.addAll(grid.getGridProperties());
			}
			return properties;
		}
	}
	
	public Collection<ObservationData> getAllSensorObservations() {
		synchronized (grids) {
			Collection<ObservationData> observations = new HashSet<>();
			for (GeoGrid grid : grids) {
				observations.addAll(grid.getGridSensorObservations());
			}
			return observations;
		}
	}
	
	public ObservationData getSensorObservation(String sensorID, String gridID) 
			throws GridNotFoundException, SensorNotFoundException, PointNotOnMapException {
		synchronized (grids) {
			for (GeoGrid grid : grids) {
				if (grid.id.equals(gridID)) {
					Point2D.Double point = grid.getSensorLocation(sensorID);
					return grid.getSensorObservation(sensorID, point);
				}
			}
			throw new GridNotFoundException(gridID);
		}
	}
	
}
