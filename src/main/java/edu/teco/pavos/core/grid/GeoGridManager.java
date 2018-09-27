package edu.teco.pavos.core.grid;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.teco.pavos.core.grid.exceptions.GridNotFoundException;
import edu.teco.pavos.core.grid.exceptions.PointNotOnMapException;
import edu.teco.pavos.core.grid.exceptions.SensorNotFoundException;
import edu.teco.pavos.transfer.data.ObservationData;

/**
 * The {@link GeoGridManager} manages every living instance of {@link GeoGrid}
 * and schedules updates to connected components.
 */
public final class GeoGridManager {
	
	private static final Object INSTANCE_LOCK = new Object();
	private static volatile GeoGridManager instance;
	private static volatile List<GeoGrid> grids = Collections.synchronizedList(new ArrayList<>());
	private ScheduledExecutorService execUpdate = Executors.newSingleThreadScheduledExecutor();
	
	private GeoGridManager() {
		new Thread(() -> 
			execUpdate.scheduleAtFixedRate(() -> {
				for (GeoGrid grid : grids) {
					grid.updateObservations();
					grid.transferSensorDataDirectly();
					grid.updateDatabase();
					grid.resetObservations();
				}
			}, 10, 10, TimeUnit.SECONDS)
		).start();
	}
	
	/**
	 * Returns the instance of this {@link GeoGridManager} or generates a new one if it does not exists.
	 * @return {@link GeoGridManager}
	 */
	public static GeoGridManager getInstance() {
		GeoGridManager result = instance;
		if (result == null) {
			synchronized (INSTANCE_LOCK) {
				result = instance;
				if (result == null) {
					result = new GeoGridManager();
					instance = result;
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the most recently added {@link GeoGrid}.
	 * @return grid {@link GeoGrid}
	 */
	public GeoGrid getNewestGrid() {
		synchronized (grids) {
			return grids.get(0);
		}
	}
	
	/**
	 * Adds the specified {@link GeoGrid} to this {@link GeoGridManager}.
	 * @param grid {@link GeoGrid}
	 */
	public void addGeoGrid(GeoGrid grid) {
		synchronized (grids) {
			grids.add(0, grid);
		}
	}
	
	/**
	 * Returns the {@link GeoGrid} specified by ID.
	 * @param gridID {@link String}
	 * @return grid {@link GeoGrid}
	 */
	public GeoGrid getGrid(String gridID) {
		for (GeoGrid entry : grids) {
			if (entry.getID().equals(gridID)) {
				return entry;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if this {@link GeoGridManager} has knowledge about the specified {@link GeoGrid}.
	 * @param grid {@link GeoGrid}
	 * @return isActive {@link Boolean}
	 */
	public boolean isGridActive(GeoGrid grid) {
		synchronized (grids) {
			return grids.contains(grid);
		}
	}
	
	/**
	 * Returns true if this {@link GeoGridManager} has knowledge about the specified {@link GeoGrid}.
	 * @param gridID {@link String}
	 * @return isActive {@link Boolean}
	 */
	public boolean isGridActive(String gridID) {
		synchronized (grids) {
			for (GeoGrid entry : grids) {
				if (entry.getID().equals(gridID)) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Removes the specified {@link GeoGrid} from this {@link GeoGridManager}.
	 * @param grid {@link GeoGrid}
	 * @return operationSuccessful {@link Boolean}
	 */
	public boolean removeGeoGrid(GeoGrid grid) {
		synchronized (grids) {
			return grids.remove(grid);
		}
	}
	
	/**
	 * Returns a {@link Collection} of all observationTypes currently managed by grids
	 * that this {@link GeoGridManager} knows about.
	 * @return allObservationTypes {@link Collection} of {@link String}
	 */
	public Collection<String> getAllObservationTypes() {
		synchronized (grids) {
			Collection<String> properties = new HashSet<>();
			for (GeoGrid grid : grids) {
				synchronized (grid) {
					properties.addAll(grid.getGridObservationTypes());
				}
			}
			return properties;
		}
	}
	
	/**
	 * Returns a {@link Collection} of all {@link ObservationData} from sensors currently managed
	 * by grids that this {@link GeoGridManager} knows about.
	 * @return allSensorObservations {@link Collection} of {@link ObservationData}
	 */
	public Collection<ObservationData> getAllSensorObservations() {
		synchronized (grids) {
			Collection<ObservationData> observations = new HashSet<>();
			for (GeoGrid grid : grids) {
				synchronized (grid) {
					observations.addAll(grid.getGridSensorObservations());
				}
			}
			return observations;
		}
	}
	
	/**
	 * Returns the {@link ObservationData} of a sensor currently managed by a grid
	 * that this {@link GeoGridManager} knows about.
	 * @param sensorID {@link String}
	 * @param gridID {@link String}
	 * @return observation {@link ObservationData}
	 * @throws GridNotFoundException The grid is not recognized.
	 * @throws SensorNotFoundException The sensorID is not recognized.
	 * @throws PointNotOnMapException The location is not inside the map-boundaries.
	 */
	public ObservationData getSensorObservation(String sensorID, String gridID) 
			throws GridNotFoundException, SensorNotFoundException, PointNotOnMapException {
		synchronized (grids) {
			for (GeoGrid grid : grids) {
				synchronized (grid) {
					if (grid.getID().equals(gridID)) {
						Point2D.Double point = grid.getSensorLocation(sensorID);
						return grid.getSensorObservation(sensorID, point);
					}
				}
			}
			throw new GridNotFoundException(gridID);
		}
	}
	
}
