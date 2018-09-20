package edu.teco.pavos.core.grid;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.teco.pavos.core.grid.config.Seperators;
import edu.teco.pavos.core.grid.exceptions.ClusterNotFoundException;
import edu.teco.pavos.core.grid.exceptions.PointNotOnMapException;
import edu.teco.pavos.core.grid.exceptions.SensorNotFoundException;
import edu.teco.pavos.core.grid.geojson.data.ObservationGeoJson;
import edu.teco.pavos.core.grid.polygon.GeoPolygon;
import edu.teco.pavos.database.Facade;
import edu.teco.pavos.transfer.Destination;
import edu.teco.pavos.transfer.DirectUploadManager;
import edu.teco.pavos.transfer.data.ObservationData;

/**
 * A geographically oriented approach to a polygon-tiled map.<br>
 * Uses {@link GeoPolygon}s to tile the map.
 */
public abstract class GeoGrid {
	
	private final Rectangle2D.Double mapBounds;
	private final int rows;
	private final int columns;
	private final int maxLevel;
	private final String id;
	protected volatile List<GeoPolygon> polygons = new ArrayList<>();
	protected volatile Map<String, Point2D.Double> sensorsAndLocations = new HashMap<>();
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int CYCLES_UNTIL_RESET = 0;
	private static volatile GeoGridManager manager = GeoGridManager.getInstance();
	private volatile int cyclesDone = 0;
	
	/**
	 * Creates a new {@link GeoGrid} and supplies the {@link GeoGridManager} with this grid.
	 * @param mapBounds {@link Rectangle2D.Double}
	 * @param rows {@link Integer}
	 * @param columns {@link Integer}
	 * @param maxLevel {@link Integer}
	 * @param gridID {@link String}
	 */
	public GeoGrid(Rectangle2D.Double mapBounds, int rows, int columns, int maxLevel, String gridID) {
		if (mapBounds == null || gridID == null) throw new NullPointerException("mapBounds and gridID cannot be null");
		this.mapBounds = mapBounds;
		this.rows = rows;
		this.columns = columns;
		this.maxLevel = maxLevel;
		this.id = gridID;
		
		manager.addGeoGrid(this);
	}
	
	/**
	 * Adds a single observation to the {@link GeoGrid}.
	 * Searches for the deepest cluster to put the values into.
	 * @param location The {@link Point2D.Double} point on the map
	 * @param observation {@link ObservationData}
	 */
	public synchronized void addObservation(Point2D.Double location, ObservationData observation) {
		GeoPolygon targetPolygon = null;
		try {
			targetPolygon = getPolygonContaining(location, maxLevel);
			targetPolygon.addObservation(observation);
			this.sensorsAndLocations.put(observation.getSensorID(), location);
		} catch (PointNotOnMapException e) {
			logger.warn("Could not add Observation to map. Point '" + location 
					+ "' not in map boundaries! SensorID: " + observation.getSensorID() + " ", e);
		}
	}
	
	/**
	 * Returns the specified {@link GeoPolygon} in GeoJson format.
	 * Uses live data to do so.
	 * The GeoJson recieved from this must be part of an {@link ObservationGeoJson},
	 * since it is only a single feature and no feature-collection.
	 * @param clusterID {@link String}
	 * @param observationType {@link String}
	 * @return geoJson {@link String}
	 * @throws ClusterNotFoundException The cluster is not recognized by the grid
	 */
	public String getLiveClusterGeoJson(String clusterID, String observationType) throws ClusterNotFoundException {
		
			GeoPolygon geoPolygon = null;
			try {
				geoPolygon = getPolygon(clusterID);
			} catch (ClusterNotFoundException e) {
				logger.warn("Could not find cluster: " + e.getCluster() 
				+ ". Decided to skip the cluster and continue the json-building process.", e);
			}
			if (geoPolygon == null) throw new ClusterNotFoundException(clusterID); 
			return geoPolygon.getLiveClusterGeoJson(observationType);
	}
	
	/**
	 * Returns the specified {@link GeoPolygon} in GeoJson format.
	 * Uses the specified value as data.
	 * The GeoJson recieved from this must be part of an {@link ObservationGeoJson},
	 * since it is only a single feature and no feature-collection.
	 * @param clusterID {@link String}
	 * @param observationType {@link String}
	 * @param value {@link String}
	 * @return geoJson {@link String}
	 * @throws ClusterNotFoundException The cluster is not recognized by the grid
	 */
	public String getArchivedClusterGeoJson(String clusterID, String observationType, String value)
			throws ClusterNotFoundException {
		
			GeoPolygon geoPolygon = null;
			try {
				geoPolygon = getPolygon(clusterID);
			} catch (ClusterNotFoundException e) {
				logger.warn("Could not find cluster: " + e.getCluster() 
				+ ". Decided to skip the cluster and continue the json-building process.", e);
			}
			if (geoPolygon == null) throw new ClusterNotFoundException(clusterID);
			return geoPolygon.getArchivedClusterGeoJson(observationType, value);
	}
	
	/**
	 * Closes this grid, deleting it from the {@link GeoGridManager}.
	 */
	public void close() {
		manager.removeGeoGrid(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) return false;
		GeoGrid oGrid = (GeoGrid) o;
		return oGrid.hashCode() == this.hashCode();
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/**
	 * Returns the ID for the Cluster which contains the {@link Point2D.Double} point.
	 * If the point is not on the mapped area, returns {@link null}!<p>
	 * The ID is also specified by the {@link int} level of grid-scaling.
	 * The level ranges from 0 to {@code MAX_LEVEL}.
	 * A higher level means more {@link GeoPolygon}s to check but gives a better
	 * representation for the selected {@link Point2D.Double} location.
	 * @param location The {@link Point2D.Double} that is contained by the cluster
	 * @param level The {@link int} level controls how deep we search
	 * @return clusterID {@link String}
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	public String getClusterID(Point2D.Double location, int level) throws PointNotOnMapException {
		return getPolygonContaining(location, level).getID();
	}
	
	/**
	 * Returns the {@link ObservationData} of a single sensor at a given {@link Point2D.Double} location.
	 * @param sensorID {@link String}
	 * @param location {@link Point2D.Double}
	 * @return observation {@link ObservationData}
	 * @throws PointNotOnMapException The location is outside the map boundaries.
	 * @throws SensorNotFoundException The sensorID is not recognized.
	 */
	public ObservationData getSensorObservation(String sensorID, Point2D.Double location) 
			throws PointNotOnMapException, SensorNotFoundException {
		GeoPolygon polygon = getPolygonContaining(location, maxLevel);
		for (ObservationData observation : polygon.getSensorDataList()) {
			if (observation.getSensorID().equals(sensorID)) {
				return observation;
			}
		}
		throw new SensorNotFoundException(sensorID);
	}
	
	/**
	 * Returns all {@link ObservationData} objects of clusters in this {@link GeoGrid}.
	 * @return clusterObservations {@link Collection} of {@link ObservationData}
	 */
	public Collection<ObservationData> getGridObservations() {
		Collection<ObservationData> observations = new ArrayList<>();
		for (GeoPolygon polygon : polygons) {
			observations.addAll(polygon.getSubObservations());
			observations.add(polygon.cloneObservation());
		}
		return observations;
	}
	
	/**
	 * Returns all observed data types like temperature of this {@link GeoGrid}.
	 * @return observedTypes {@link Collection} of {@link String}
	 */
	public Collection<String> getGridObservationTypes() {
		Collection<String> observedTypes = new HashSet<>();
		Collection<ObservationData> observations = getGridObservations();
		for (ObservationData observation : observations) {
			// TODO - Add Vector support
			for (String key : observation.getDoubleObservations().keySet()) {
				observedTypes.add(key);
			}
		}
		return observedTypes;
	}
	
	/**
	 * Returns all {@link ObservationData} objects of sensors in this {@link GeoGrid}.
	 * @return sensorObservations {@link Collection} of {@link ObservationData}
	 */
	public Collection<ObservationData> getGridSensorObservations() {
		synchronized (polygons) {
			Collection<ObservationData> observations = new ArrayList<>();
			for (GeoPolygon polygon : polygons) {
				synchronized (polygon) {
					observations.addAll(polygon.getSubSensorObservations());
					observations.addAll(polygon.getSensorDataList());
				}
			}
			return observations;
		}
	}
	
	/**
	 * Returns the {@link String} topic for kafka, in which all values should be written.
	 * @return topic {@link String}
	 */
	public String getOutputTopic() {
		return this.id + ".out";
	}
	
	/**
	 * Returns the location of a given {@link String} sensorID.
	 * @param sensorID {@link String}
	 * @return location {@link Point2D.Double}
	 * @throws SensorNotFoundException The sensorID is not recognized.
	 */
	public Point2D.Double getSensorLocation(String sensorID) throws SensorNotFoundException {
		Point2D.Double result = this.sensorsAndLocations.get(sensorID);
		if (result == null) throw new SensorNotFoundException(sensorID);
		return result;
	}
	
	/**
	 * Returns the {@link GeoPolygon} that is associated with the specified {@link String} clusterID.
	 * @param clusterID {@link String}
	 * @return polygon {@link GeoPolygon}
	 * @throws ClusterNotFoundException The clusterID is not recognized.
	 */
	public GeoPolygon getPolygon(String clusterID) throws ClusterNotFoundException {
		GeoPolygon result = null;
		try {
			String[] splitGridClusters = clusterID.split(Seperators.GRID_CLUSTER_SEPERATOR);
			String[] clusters = splitGridClusters[1].split(Seperators.CLUSTER_SEPERATOR);
			int levels = clusters.length;

			StringBuilder currentID = new StringBuilder();
			currentID.append(id + Seperators.GRID_CLUSTER_SEPERATOR);
			
			currentID.append(clusters[0]);
			result = updatePolygonIfClusterMatches(currentID.toString(), result, this.polygons);
			
			for (int i = 1; i < levels; i++) {
				if (result == null) throw new NullPointerException();
				currentID.append(Seperators.CLUSTER_SEPERATOR + clusters[i]);
				result = updatePolygonIfClusterMatches(currentID.toString(), result, result.getSubPolygons());
			}
			
			return result;
			
		} catch (NullPointerException e) {
			throw new ClusterNotFoundException(clusterID);
		}
	}
	
	private GeoPolygon updatePolygonIfClusterMatches(String currentID, GeoPolygon input, List<GeoPolygon> polygons) {
		GeoPolygon result = input;
		for (GeoPolygon polygon : polygons) {
			if (polygon.getID().equals(currentID)) {
				result = polygon;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns the ID for the Cluster which contains the {@link Point2D.Double} point.
	 * If the point is not on the mapped area, returns {@link null}!<p>
	 * The ID is also specified by the {@link int} level of grid-scaling.
	 * The level ranges from 0 to {@code MAX_LEVEL}.
	 * A higher level means more {@link GeoPolygon}s to check but gives
	 * a better representation for the selected {@link Point2D.Double}.
	 * @param location The {@link Point2D.Double} that is contained by the cluster
	 * @param level The {@link int} level controls how deep we search
	 * @return clusterID {@link String}
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	public GeoPolygon getPolygonContaining(Point2D.Double location, int level) throws PointNotOnMapException {
		GeoPolygon targetPolygon = getPolygonContainingPointFromCollection(location, polygons);
		
		// t2Polygon, meaning: tier-2-polygon
		GeoPolygon t2Polygon = targetPolygon;
		int levelBounds = Math.min(level, maxLevel);
		for (int currentLevel = 1; currentLevel < levelBounds; currentLevel++) {
			try {
				t2Polygon = getPolygonContainingPointFromCollection(location, t2Polygon.getSubPolygons());
				targetPolygon = t2Polygon;
			} catch (PointNotOnMapException e) {
				break;
			}
		}
		return targetPolygon;
	}
	
	/**
	 * Transfers all {@link ObservationData} from sensors to Graphite using a {@link DirectUploadManager}.
	 */
	public void transferSensorDataDirectly() {
		Set<ObservationData> observations = new HashSet<>();
		for (GeoPolygon polygon : polygons) {
			observations.addAll(polygon.getSensorObservations());
		}
		DirectUploadManager dum = new DirectUploadManager();
		dum.uploadData(observations, Destination.GRAPHITE);
	}
	
	/**
	 * Updates all values of this {@link GeoGrid} and it's {@link GeoPolygon}s.<p>
	 * The process takes into account that {@link GeoPolygon} may have more or less data
	 * about a certain property.
	 * It sums up all values (that were factored by the amount of data) and finally divides it
	 * by the total amount of data.
	 * This way, we achieve the most realistic representation of our data.
	 */
	public void updateObservations() {
		for (GeoPolygon polygon : polygons) {
			polygon.updateObservations();
		}
	}
	
	/**
	 * Resets all {@link ObservationData} of sensors in this {@link GeoGrid}.
	 * Is used to reduce heavy loads.
	 * Should be called at any point after the invocation of {@code updateObservations()}
	 * but before the next invocation to eliminate phantom-data.
	 * Waits until enough invocation-cycles have been completed, before removing the data.
	 */
	public void resetObservations() {
		if (cyclesDone == CYCLES_UNTIL_RESET) {
			resetObservationsNow();
			this.sensorsAndLocations.clear();
			cyclesDone = 0;
		} else {
			cyclesDone++;
		}
	}
	
	/**
	 * Resets all {@link ObservationData} of sensors in this {@link GeoGrid}.
	 * Is used to reduce heavy loads.
	 * Should be called at any point after the invocation of {@code updateObservations()}
	 * but before the next invocation to eliminate phantom-data.
	 * Removes the data instantly.
	 */
	public void resetObservationsNow() {
		for (GeoPolygon polygon : polygons) {
			polygon.resetObservations();
		}
	}
	
	/**
	 * Updates the Database.
	 * Saves {@link ObservationData} values of {@link GeoPolygon} clusters only.
	 */
	public void updateDatabase() {
		Facade database = Facade.getInstance();
		if (!database.isConnected()) return;
		Collection<ObservationData> observations = getGridObservations();
		for (ObservationData entry : observations) {
			database.addObservationData(entry);
		}
	}
	
	/**
	 * Generates the {@link GeoPolygon}s that make up all clusters of the grid.
	 */
	protected abstract void generateGeoPolygons();

	/**
	 * Returns the first {@link GeoPolygon} from the {@link Collection} of {@link GeoPolygon}s
	 * that contains the specified {@link Point2D.Double}.
	 * @param point {@link Point2D.Double}
	 * @param collection {@link Collection} of {@link GeoPolygon}s
	 * @return polygonContainingPoint {@link GeoPolygon}
	 * @throws PointNotOnMapException The location is not inside the map-boundaries.
	 */
	protected GeoPolygon getPolygonContainingPointFromCollection(Double point, Collection<GeoPolygon> collection)
			throws PointNotOnMapException {
		for (GeoPolygon entry : collection) {
			if (entry.contains(point, false)) {
				return entry;
			}
		}
		throw new PointNotOnMapException(point);
	}

	/**
	 * @return the bounds of the {@link GeoGrid} / map
	 */
	public Rectangle2D.Double getMapBounds() {
		return new Rectangle2D.Double(mapBounds.getX(), mapBounds.getY(), mapBounds.getWidth(), mapBounds.getHeight());
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}
	
	/**
	 * @return the max level
	 */
	public int getMaxLevel() {
		return maxLevel;
	}
	
	/**
	 * @return the max level
	 */
	public String getID() {
		return id;
	}
	
}
