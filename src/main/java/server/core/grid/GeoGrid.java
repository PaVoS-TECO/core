package server.core.grid;

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

import server.core.grid.config.Seperators;
import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.exceptions.SensorNotFoundException;
import server.core.grid.polygon.GeoPolygon;
import server.database.Facade;
import server.transfer.Destination;
import server.transfer.DirectUploadManager;
import server.transfer.TransferManager;
import server.transfer.data.ObservationData;
import server.transfer.producer.GraphiteProducer;

/**
 * A geographically oriented approach to a polygon-tiled map.<br>
 * Uses {@link GeoPolygon}s to tile the map.
 */
public abstract class GeoGrid {
	
	public final Rectangle2D.Double MAP_BOUNDS;
	public final int ROWS;
	public final int COLUMNS;
	public final int MAX_LEVEL;
	public final String GRID_ID;
	protected List<GeoPolygon> polygons = new ArrayList<>();
	protected Map<String, Point2D.Double> sensorsAndLocations = new HashMap<>();
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private GeoGridManager manager = GeoGridManager.getInstance();
	private final int CYCLES_UNTIL_RESET = 1;
	private int cyclesDone = 0;
	
	public GeoGrid(Rectangle2D.Double mapBounds, int rows, int columns, int maxLevel, String gridID) {
		this.MAP_BOUNDS = mapBounds;
		this.ROWS = rows;
		this.COLUMNS = columns;
		this.MAX_LEVEL = maxLevel;
		this.GRID_ID = gridID;
		
		this.manager.addGeoGrid(this);
	}
	
	/**
	 * Adds a single observation to the {@link GeoGrid}.
	 * Searches for the smallest cluster to put the values into.
	 * @param sensorID The id of the sensor
	 * @param location The {@link Point2D.Double} point on the map
	 * @param data The {@link ObservationData} to be added
	 */
	public void addObservation(Point2D.Double location, ObservationData data) {
		GeoPolygon targetPolygon = null;
		try {
			targetPolygon = getPolygonContaining(location, MAX_LEVEL);
			targetPolygon.addObservation(data);
			this.sensorsAndLocations.put(data.sensorID, location);
		} catch (PointNotOnMapException e) {
			logger.warn("Could not add Observation to map. Point '" + location 
					+ "' not in map boundaries! SensorID: " + data.sensorID + " " + e);
		}
	}
	
	public void close() {
		this.manager.removeGeoGrid(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		GeoGrid oGrid = (GeoGrid) o;
		return (this.GRID_ID.equals(oGrid.GRID_ID));
	}
	
	/**
	 * Returns the ID for the Cluster which contains the {@link Point2D.Double} point. If the point is not on the mapped area, returns {@link null}!<p>
	 * The ID is also specified by the {@link int} level of grid-scaling.
	 * The level ranges from 0 to {@code MAX_LEVEL}.
	 * A higher level means more {@link GeoPolygon}s to check but gives a better representation for the selected {@link Point2D.Double}.
	 * @param point The {@link Point2D.Double} that is contained by the cluster that we are searching for
	 * @param level The {@link int} that controls the level of detail
	 * @return id The cluster id
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	public String getClusterID(Point2D.Double point, int level) throws PointNotOnMapException {
		return getPolygonContaining(point, level).ID;
	}
	
	public ObservationData getSensorObservation(String sensorID, Point2D.Double point) throws PointNotOnMapException, SensorNotFoundException {
		GeoPolygon polygon = getPolygonContaining(point, MAX_LEVEL);
		for (ObservationData observation : polygon.getSensorDataList()) {
			if (observation.sensorID.equals(sensorID)) {
				return observation;
			}
		}
		throw new SensorNotFoundException(sensorID);
	}
	
	/**
	 * Returns all {@link ObservationData} objects of clusters in this {@link GeoGrid}.
	 * @return gridObservations {@link Collection} of {@link ObservationData}
	 */
	public Collection<ObservationData> getGridObservations() {
		Collection<ObservationData> observations = new ArrayList<>();
		for (GeoPolygon polygon : polygons) {
			observations.addAll(polygon.getSubObservations());
			observations.add(polygon.cloneObservation());
		}
		return observations;
	}
	
	public Collection<String> getGridProperties() {
		Collection<String> properties = new HashSet<>();
		Collection<ObservationData> observations = getGridObservations();
		for (ObservationData observation : observations) {
			for (String key : observation.observations.keySet()) {
				properties.add(key);
			}
		}
		return properties;
	}
	
	public Collection<ObservationData> getGridSensorObservations() {
		Collection<ObservationData> observations = new ArrayList<>();
		for (GeoPolygon polygon : polygons) {
			observations.addAll(polygon.getSubObservations());
			observations.addAll(polygon.getSensorDataList());
		}
		return observations;
	}
	
	/**
	 * Returns the {@link String} topic for kafka, in which all values should be written.
	 * @return topic {@link String}
	 */
	public String getOutputTopic() {
		return this.GRID_ID + ".out";
	}
	
	public Point2D.Double getSensorLocation(String sensorID) throws SensorNotFoundException {
		Point2D.Double result = this.sensorsAndLocations.get(sensorID);
		if (result == null) throw new SensorNotFoundException(sensorID);
		return result;
	}
	
	/**
	 * Returns the {@link GeoPolygon} that is associated with the specified {@link String} clusterID.
	 * @param clusterID {@link String}
	 * @return polygon {@link GeoPolygon}
	 * @throws ClusterNotFoundException 
	 */
	public GeoPolygon getPolygon(String clusterID) throws ClusterNotFoundException {
		GeoPolygon result = null;
		try {
			String[] splitGridClusters = clusterID.split(Seperators.GRID_CLUSTER_SEPERATOR);
			String[] clusters = splitGridClusters[1].split(Seperators.CLUSTER_SEPERATOR);
			int levels = clusters.length;

			StringBuilder currentID = new StringBuilder();
			currentID.append(GRID_ID + Seperators.GRID_CLUSTER_SEPERATOR);
			for (int i = 0; i < levels; i++) {
				if (i == 0) {
					currentID.append(clusters[i]);
					for (GeoPolygon polygon : this.polygons) {
						if (polygon.ID.equals(currentID.toString())) {
							result = polygon;
							break;
						}

					}
				} else {
					currentID.append(Seperators.CLUSTER_SEPERATOR + clusters[i]);
					for (GeoPolygon polygon : result.getSubPolygons()) {
						if (polygon.ID.equals(currentID.toString())) {
							result = polygon;
							break;
						}
					}
				}
			}
		} catch (NullPointerException e) {
			throw new ClusterNotFoundException(clusterID);
		}
		if (result == null) {
			throw new ClusterNotFoundException(clusterID);
		}
		return result;
	}
	
	/**
	 * Returns the ID for the Cluster which contains the {@link Point2D.Double} point. If the point is not on the mapped area, returns {@link null}!<p>
	 * The ID is also specified by the {@link int} level of grid-scaling.
	 * The level ranges from 0 to {@code MAX_LEVEL}.
	 * A higher level means more {@link GeoPolygon}s to check but gives a better representation for the selected {@link Point2D.Double}.
	 * @param point The {@link Point2D.Double} that is contained by the cluster that we are searching for
	 * @param level The {@link int} that controls the level of detail
	 * @return id The cluster id
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	public GeoPolygon getPolygonContaining(Point2D.Double point, int level) throws PointNotOnMapException {
		GeoPolygon targetPolygon = getPolygonContainingPointFromCollection(point, polygons);
		
		// t2Polygon, meaning: tier-2-polygon
		GeoPolygon t2Polygon = targetPolygon;
		int levelBounds = Math.min(level, MAX_LEVEL);
		for (int currentLevel = 1; currentLevel < levelBounds; currentLevel++) {
			try {
				t2Polygon = getPolygonContainingPointFromCollection(point, t2Polygon.getSubPolygons());
				targetPolygon = t2Polygon;
			} catch (PointNotOnMapException e) {
				break;
			}
		}
		return targetPolygon;
	}
	
	/**
	 * Produces messages for the output kafka-topic.
	 * Each message contains a single {@link ObservationData} object.
	 * This method Produces recursively and starts with the smallest clusters.
	 * @return 
	 */
	public List<String> produceSensorDataMessages() {
		GraphiteProducer producer = new GraphiteProducer();
		List<String> topics = new ArrayList<>();
		for (GeoPolygon polygon : polygons) {
			topics.addAll(polygon.produceSensorDataMessages(producer));
		}
		producer.close();
		return topics;
	}
	
	public void transferSensorDataDirectly() {
		Set<ObservationData> observations = new HashSet<>();
		for (GeoPolygon polygon : polygons) {
			observations.addAll(polygon.transferSensorDataDirectly());
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
		
		// Live data
		
//		List<String> topics = produceSensorDataMessages();
//		System.out.println("Topics: " + topics);
//		transferToGraphite(topics);
		
		transferSensorDataDirectly();
		
		// send to database
		
//		updateDatabase();
		
		
		// ONLY RESET AT THE END
		
		if (cyclesDone == CYCLES_UNTIL_RESET) {
			resetObservations();
			this.sensorsAndLocations.clear();
			cyclesDone = 0;
		} else {
			cyclesDone++;
		}
	}
	
	private void resetObservations() {
		for (GeoPolygon polygon : polygons) {
			polygon.resetObservations();
		}
	}
	
	@SuppressWarnings("unused")
	private void transferToGraphite(List<String> topics) {
		TransferManager tm = new TransferManager();
		tm.startDataTransfer(topics, Destination.GRAPHITE);
		tm.stopDataTransfer();
	}
	
	/**
	 * Updates the Database.
	 */
	public void updateDatabase() {
		Facade database = new Facade();
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
	 * Returns the first {@link GeoPolygon} from the {@link Collection} of {@link GeoPolygon}s that contains the specified {@link Point2D.Double}.
	 * @param point {@link Point2D.Double}
	 * @param collection {@link Collection} of {@link GeoPolygon}s
	 * @return polygonContainingPoint {@link GeoPolygon}
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	protected GeoPolygon getPolygonContainingPointFromCollection(Double point, Collection<GeoPolygon> collection) throws PointNotOnMapException {
		for (GeoPolygon entry : collection) {
			if (entry.contains(point, false)) {
				return entry;
			}
		}
		throw new PointNotOnMapException(point);
	}
	
}
