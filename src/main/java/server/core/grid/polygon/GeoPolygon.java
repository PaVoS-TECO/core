package server.core.grid.polygon;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;

import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.geojson.GeoJsonConverter;
import server.core.grid.polygon.math.Tuple2D;
import server.core.grid.polygon.math.Tuple3D;
import server.transfer.data.ObservationData;
import server.transfer.data.util.GridTopicTranslator;
import server.transfer.producer.GraphiteProducer;
import server.transfer.sender.util.TimeUtil;

/**
 * A geographically oriented approach to polygons with double precision.<p>
 * Uses {@link Path2D.Double} for the polygon base.
 * Can contain sub-{@link GeoPolygon}s.
 * Provides a method to convert this polygon to a JSON-{@link String}.
 */
public abstract class GeoPolygon {
	
	public final Rectangle2D.Double bounds;
	public final int rows;
	public final int columns;
	public final double scale;
	public final String id;
	public final int levelsAfterThis;
	protected Path2D.Double path;
	protected List<GeoPolygon> subPolygons;
	protected Map<String, ObservationData> sensorValues;
	protected ObservationData observationData;
	
	/**
	 * Creates a {@link GeoPolygon} with the given offsets, width, height and id.<p>
	 * Sets {@code USE_SCALE} to {@code false}!
	 * @param bounds {@link Rectangle2D.Double} a bounding-box around our {@link GeoPolygon}
	 * @param rows How many times the {@link GeoPolygon} will be subdivided horizontally
	 * @param columns How many times the {@link GeoPolygon} will be subdivided vertically
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoPolygon}
	 */
	public GeoPolygon(Rectangle2D.Double bounds, int rows, int columns, int levelsAfterThis, String id) {
		this.bounds = bounds;
		this.rows = rows;
		this.columns = columns;
		this.id = id;
		this.scale = 0;
		this.levelsAfterThis = Math.max(levelsAfterThis, 0);
		
		commonConstructor();
	}
	
	private void commonConstructor() {
		this.path = new Path2D.Double();
		this.subPolygons = new ArrayList<>();
		this.sensorValues = new HashMap<>();
		this.observationData = new ObservationData();
		setupObservationData();
	}
	
	/**
	 * Creates or overrides a map-entry with the new value in double-precision.
	 * @param sensorID The {@link String} ID of the Sensor. Not a cluster.
	 * @param data The {@link ObservationData} with the value.
	 */
	public void addObservation(ObservationData data) {
		data.clusterID = this.id;
		this.sensorValues.put(data.sensorID, data);
	}
	
	/**
	 * Returns a cloned object of this {@link GeoPolygon}s {@link ObservationData} data.
	 * @return observationData {@link ObservationData}
	 */
	public ObservationData cloneObservation() {
		ObservationData result = new ObservationData();
		result.observationDate = this.observationData.observationDate;
		result.sensorID = this.observationData.sensorID;
		result.clusterID = this.observationData.clusterID;
		for (Map.Entry<String, String> entry : this.observationData.observations.entrySet()) {
			result.observations.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	/**
	 * Returns true if the current {@link GeoPolygon} contains the specified {@link Point2D.Double}.
	 * If {@code checkBoundsFirst} is set to {@code true}, the method will check if the object is inside the boundaries first,
	 * in order to reduce overhead on {@link GeoPolygon}s with a high amount of vertices.
	 * @param point {@link Point2D.Double}
	 * @param checkBoundsFirst {@link boolean}
	 * @return containsPoint {@link boolean}
	 */
	public boolean contains(Point2D.Double point, boolean checkBoundsFirst) {
		if (checkBoundsFirst && !path.getBounds2D().contains(point)) {
				return false;
		}
		return path.contains(point);
	}
	
	/**
	 * Returns the current {@link String} sensorIDs that are inside this cluster as a {@link Collection} over all sensors.
	 * This includes sensors from all sub-{@link GeoPolygon}s until the last level.
	 * @return sensorDataSet {@code Collection<String>}
	 */
	public Collection<String> getAllSensorIDs() {
		Collection<String> result = new HashSet<>();
		result.addAll(getDirectSensorIDs());
		for (GeoPolygon subPolygon : this.subPolygons) {
			result.addAll(subPolygon.getAllSensorIDs());
		}
		return result;
	}
	
	/**
	 * Returns the current {@link String} sensorIDs that are inside this cluster as a {@link Collection} over all sensors.
	 * This does not include sensors from sub-{@link GeoPolygon}s.
	 * @return sensorDataSet {@code Collection<String>}
	 */
	public Collection<String> getDirectSensorIDs() {
		return this.sensorValues.keySet();
	}
	
	/**
	 * Returns the current {@link GeoPolygon} as JSON-String
	 * @param property The {@link String} representing an observation-type.
	 * @return json {@link String}
	 */
	public String getJson(String property) {
		return GeoJsonConverter.convert(this, property);
	}
	
	/**
	 * Returns the number of sensors in this {@link GeoPolygon} in total.
	 * @return numberOfSensors {@link int}
	 */
	public int getNumberOfSensors() {
		int sum = 0;
		
		for (GeoPolygon entry : this.subPolygons) {
			sum += entry.getNumberOfSensors();
		}
		sum += this.sensorValues.size();
		
		return sum;
	}
	
	/**
	 * Returns the number of sensors in this {@link GeoPolygon} that send data about a specific {@link Collection} of properties.
	 * @param properties The {@link Collection} of {@link String}s representing different observation-types.
	 * @return numberOfSensors {@link int}
	 */
	public int getNumberOfSensors(Collection<String> properties) {
		int sum = 0;
		
		for (GeoPolygon entry : this.subPolygons) {
			sum += entry.getNumberOfSensors(properties);
		}
		for (ObservationData data : this.sensorValues.values()) {
			boolean containsAll = true;
			for (String property : properties) {
				if (!data.observations.containsKey(property)) {
					containsAll = false;
				}
			}
			if (containsAll) sum++;
		}
		
		return sum;
	}
	
	/**
	 * Returns the number of sensors in this {@link GeoPolygon} that send data about a specific property.
	 * @param property The {@link String} representing an observation-type.
	 * @return numberOfSensors {@link int}
	 */
	public int getNumberOfSensors(String property) {
		int sum = 0;
		
		for (GeoPolygon entry : this.subPolygons) {
			sum += entry.getNumberOfSensors(property);
		}
		for (ObservationData data : this.sensorValues.values()) {
			if (data.observations.containsKey(property)) {
				sum++;
			}
		}
		
		return sum;
	}
	
	/**
	 * Returns a {@link Collection} of {@link Point2D.Double}s that make up the current {@link GeoPolygon}
	 * @return points {@code Collection<Point2D.Double>}
	 */
	public List<Point2D.Double> getPoints() {
		PathIterator pi = path.getPathIterator(null);
		double[] values = new double[6];
		List<Point2D.Double> points = new ArrayList<>();
		
		while (!pi.isDone()) {
		    int type = pi.currentSegment(values);
		    if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {
		    	points.add(new Point2D.Double(values[0], values[1]));
		    }
		    else {
		        // SEG_MOVETO, SEG_QUADTO, SEG_CUBICTO
		    }
		    pi.next();
		}
		return points;
	}
	
	/**
	 * Returns the current {@link ObservationData} data as a {@link Collection} over all sensors.
	 * The new sensorID will consist of the {@link GeoPolygon}.ID and the original sensorID.
	 * @return sensorDataSet {@code Collection<ObservationData>}
	 */
	public Collection<ObservationData> getSensorDataList() {
		return this.sensorValues.values();
	}
	
	public Collection<ObservationData> getSubObservations() {
		Collection<ObservationData> observations = new ArrayList<>();
		for (GeoPolygon polygon : subPolygons) {
			observations.addAll(polygon.getSubObservations());
			observations.add(polygon.cloneObservation());
		}
		return observations;
	}
	
	/**
	 * Returns the sub-{@link GeoPolygon} that is associated with the specified {@link String} clusterID.
	 * @param clusterID {@link String}
	 * @return polygon {@link GeoPolygon}
	 * @throws ClusterNotFoundException 
	 */
	public GeoPolygon getSubPolygon(String clusterID) throws ClusterNotFoundException {
		for (GeoPolygon polygon : this.subPolygons) {
			if (polygon.id == clusterID) {
				return polygon;
			}
		}
		throw new ClusterNotFoundException(clusterID);
	}
	
	/**
	 * Returns a {@link Collection} of all {@link GeoPolygon}s inside this {@link GeoPolygon}.
	 * @return subPolygons {@code Set<Entry<String, GeoPolygon>>}
	 */
	public List<GeoPolygon> getSubPolygons() {
		return subPolygons;
	}
	
	/**
	 * Produces messages for the output kafka-topic.
	 * This method Produces recursively and starts with the smallest clusters.
	 * @param topic {@link String}
	 * @param producer {@link GraphiteProducer}
	 * @return topics {@link List} of {@link String}s that define KafkaTopics
	 */
	public List<String> produceSensorDataMessages(GraphiteProducer producer) {
		List<String> topics = new ArrayList<>();
		
		for (GeoPolygon subPolygon : subPolygons) {
			topics.addAll(subPolygon.produceSensorDataMessages(producer));
		}
		
		if (!sensorValues.isEmpty()) {
			Collection<String> sensorIDs = sensorValues.keySet();
			Map<String, String> sensorTopicMap = GridTopicTranslator.getTopic(sensorIDs, this);
			for (String sensorID : sensorIDs) {
				String topic = sensorTopicMap.get(sensorID);
				producer.produceMessage(topic, sensorValues.get(sensorID));
				topics.add(topic);
			}
		}
		return topics;
	}
	
	public Collection<ObservationData> transferSensorDataDirectly() {
		Collection<ObservationData> observations = new HashSet<>();
		for (GeoPolygon subPolygon : subPolygons) {
			observations.addAll(subPolygon.transferSensorDataDirectly());
		}
		
		if (!sensorValues.isEmpty()) {
			observations.addAll(sensorValues.values());
		}
		return observations;
	}
	
	public Collection<String> getAllProperties() {
		Collection<String> properties = new HashSet<>();
		for (GeoPolygon entry : this.subPolygons) {
			for (String property : entry.observationData.observations.keySet()) {
				properties.add(property);
			}
		}
		for (ObservationData entry : this.sensorValues.values()) {
			for (String property : entry.observations.keySet()) {
				properties.add(property);
			}
		}
		return properties;
	}
	
	/**
	 * Updates all values of this {@link GeoPolygon} and it's sub-{@link GeoPolygon}s.<p>
	 * The process takes into account that sub-{@link GeoPolygon} may have more or less data
	 * about a certain property.
	 * It sums up all values (that were factored by the amount of data) and finally divides it
	 * by the total amount of data.
	 * This way, we achieve the most realistic representation of our data.
	 */
	public void updateObservations() {
		for (GeoPolygon entry : this.subPolygons) {
			entry.updateObservations();
		}
		
		Collection<Tuple3D<String, Integer, Double>> values = getPropertyWeight();
		Collection<String> properties = getProperties();
		
		setWeightedObservationForCluster(values, properties);
	}
	
	private void setWeightedObservationForCluster(Collection<Tuple3D<String, Integer, Double>> values, Collection<String> properties) {
		boolean anyEntry = false;
		ObservationData obs = new ObservationData();
		DateTime dt = selectDateTimeFromAllSources();
		obs.observationDate = TimeUtil.getUTCDateTimeNowString();
		for (String property : properties) {
			anyEntry = weightProperty(property, values, obs);
		}
		if (anyEntry) {
			obs.clusterID = this.id;
			obs.observationDate = TimeUtil.getUTCDateTimeString(dt.toLocalDateTime());
			this.observationData = obs;
		}
	}
	
	private boolean weightProperty(String property, Collection<Tuple3D<String, Integer, Double>> values, ObservationData output) {
		double value = 0;
		int totalSensors = 0;
		boolean anyEntry = false;
		
		for (Tuple3D<String, Integer, Double> tuple : values) {
			if (tuple.getFirstValue().equals(property)) {
				Tuple2D<Double, Integer> weight = addWeightedData(tuple, value, totalSensors);
				if (weight != null && weight.getFirstValue() != null && weight.getSecondValue() != null) {
					value = weight.getFirstValue();
					totalSensors = weight.getSecondValue();
					anyEntry = true;
				}
			}
		}
		value = (totalSensors != 0) ? value / (double) totalSensors : value;
		output.observations.put(property, anyEntry ? String.valueOf(value) : null);
		return anyEntry;
	}
	
	private Tuple2D<Double, Integer> addWeightedData(Tuple3D<String, Integer, Double> tuple, double value, int totalSensors) {
		if (tuple.getFirstValue() == null || tuple.getSecondValue() == null || tuple.getThirdValue() == null) return null; 
		return new Tuple2D<>(value + tuple.getThirdValue().doubleValue() * tuple.getSecondValue().doubleValue(), 
				totalSensors + tuple.getSecondValue().intValue());
	}
	
	public Collection<String> getProperties() {
		Collection<String> properties = new HashSet<>();
		for (GeoPolygon polygon : this.subPolygons) {
			for (Entry<String, String> entry : polygon.observationData.observations.entrySet()) {
				properties.add(entry.getKey());
			}
		}
		for (ObservationData observation : this.sensorValues.values()) {
			for (Entry<String, String> entry : observation.observations.entrySet()) {
				properties.add(entry.getKey());
			}
		}
		return properties;
	}
	
	private DateTime selectDateTimeFromAllSources() {
		DateTime dt = null;
		for (GeoPolygon polygon : this.subPolygons) {
			dt = earliestDateTime(dt, TimeUtil.getUTCDateTime(polygon.observationData.observationDate).toDateTime());
		}
		for (ObservationData observation : this.sensorValues.values()) {
			dt = earliestDateTime(dt, TimeUtil.getUTCDateTime(observation.observationDate).toDateTime());
		}
		if (dt == null) dt = TimeUtil.getUTCDateTimeNow().toDateTime();
		return dt;
	}
	
	private DateTime earliestDateTime(DateTime dt1, DateTime dt2) {
		if ((dt1 == null && dt2 == null)) return null;
		if (dt1 == null) return dt2;
		if (dt2 == null) return dt1;
		DateTime result = new DateTime(dt1.getMillis());
		if (dt1.isBefore(dt2)) result = dt2;
		return result;
	}
	
	/**
	 * Returns multiple weightings for each property.
	 * @return weightings A {@link Set} of {@link Tuple3D} with the settings:
	 * {@link String} property, {@link Integer} number of sensors and {@link Double} value.
	 */
	private Collection<Tuple3D<String, Integer, Double>> getPropertyWeight() {
		Collection<Tuple3D<String, Integer, Double>> values = new HashSet<>();
		for (GeoPolygon polygon : this.subPolygons) {
			for (Entry<String, String> entry : polygon.observationData.observations.entrySet()) {
				values.add(new Tuple3D<String, Integer, Double>(entry.getKey()
						, Integer.valueOf(polygon.getNumberOfSensors(entry.getKey())), 
						Double.valueOf(polygon.observationData.observations.get(entry.getKey()))));
			}
		}
		for (ObservationData observation : this.sensorValues.values()) {
			for (Entry<String, String> entry : observation.observations.entrySet()) {
				values.add(new Tuple3D<String, Integer, Double>(entry.getKey()
						, Integer.valueOf(1), 
						Double.valueOf(observation.observations.get(entry.getKey()))));
			}
		}
		return values;
	}
	
	public void resetObservations() {
		for (GeoPolygon subPolygon : subPolygons) {
			subPolygon.resetObservations();
		}
		this.sensorValues.clear();
	}
	
	private void setupObservationData() {
		this.observationData.observationDate = TimeUtil.getUTCDateTimeNowString();
		this.observationData.clusterID = this.id;
	}
	
	/**
	 * Generates and sets the {@link Path2D.Double} of this {@link GeoPolygon}
	 */
	protected abstract void generatePath();
	
	/**
	 * Generates {@link GeoPolygon}s inside of this {@link GeoPolygon}.<p>
	 * Uses {@code SCALE}.
	 * @param subdivisions The amount of subdivisions
	 */
	protected abstract void generateSubPolygons(int subdivisions);
	
	/**
	 * Generates {@link GeoPolygon}s inside of this {@link GeoPolygon}.<p>
	 * Uses {@code WIDTH} and  {@code HEIGHT}
	 * @param xSubdivisions The amount of horizontal subdivisions
	 * @param ySubdivisions The amount of vertical subdivisions
	 */
	protected abstract void generateSubPolygons(int xSubdivisions, int ySubdivisions);
	
	/**
	 * Produces messages for the output kafka-topic.
	 * This method Produces recursively and starts with the smallest clusters.
	 * Returns a {@link Collection} of {@link ObservationData}.
	 * @param topic {@link String}
	 * @param recursive {@link boolean}
	 */
	protected Collection<ObservationData> getClusterObservations() {
		Collection<ObservationData> result = new HashSet<>();
		for (GeoPolygon polygon : subPolygons) {
			result.addAll(polygon.getClusterObservations());
		}
		result.add(cloneObservation());
		return result;
	}
	
}
