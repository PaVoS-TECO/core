package server.core.grid.polygon;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.geojson.GeoJsonConverter;
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
	
	public final boolean USE_SCALE;
	public final double X_OFFSET;
	public final double Y_OFFSET;
	public final double WIDTH;
	public final double HEIGHT;
	public final int ROWS;
	public final int COLUMNS;
	public final double SCALE;
	public final String ID;
	public final int LEVELS_AFTER_THIS;
	protected Path2D.Double path;
	protected List<GeoPolygon> subPolygons;
	protected Map<String, ObservationData> sensorValues;
	protected ObservationData observationData;
	
	/**
	 * Creates a {@link GeoPolygon} with the given offsets, width, height and id.<p>
	 * Sets {@code USE_SCALE} to {@code false}!
	 * @param xOffset The horizontal offset
	 * @param yOffset The vertical offset
	 * @param width
	 * @param height
	 * @param rows How many times the {@link GeoPolygon} will be subdivided horizontally
	 * @param columns How many times the {@link GeoPolygon} will be subdivided vertically
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoPolygon}
	 */
	public GeoPolygon(double xOffset, double yOffset, double width, double height, int rows, int columns, int levelsAfterThis, String id) {
		this.USE_SCALE = false;
		this.X_OFFSET = xOffset;
		this.Y_OFFSET = yOffset;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.ROWS = rows;
		this.COLUMNS = columns;
		this.ID = id;
		this.SCALE = 0;
		this.LEVELS_AFTER_THIS = Math.max(levelsAfterThis, 0);;
		
		commonConstructor();
	}
	
	/**
	 * Creates a {@link GeoPolygon} with the given offsets, scale and id.<p>
	 * Sets {@code USE_SCALE} to {@code true}!
	 * @param xOffset The horizontal offset
	 * @param yOffset The vertical offset
	 * @param scale
	 * @param subdivisions How many times the {@link GeoPolygon} will be subdivided horizontally and vertically
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoPolygon}
	 */
	public GeoPolygon(double xOffset, double yOffset, double scale, int subdivisions, int levelsAfterThis, String id) {
		this.USE_SCALE = true;
		this.X_OFFSET = xOffset;
		this.Y_OFFSET = yOffset;
		this.WIDTH = 0;
		this.HEIGHT = 0;
		this.ROWS = subdivisions;
		this.COLUMNS = subdivisions;
		this.ID = id;
		this.SCALE = scale;
		this.LEVELS_AFTER_THIS = Math.max(levelsAfterThis, 0);
		
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
		data.clusterID = this.ID;
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
		if (checkBoundsFirst) {
			if (!path.getBounds2D().contains(point)) {
				return false;
			}
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
			if (polygon.ID == clusterID) {
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
			Map<String, String> obsTemp = entry.observationData.observations;
			for (String property : obsTemp.keySet()) {
				properties.add(property);
			}
		}
		for (ObservationData entry : this.sensorValues.values()) {
			Map<String, String> obsTemp = entry.observations;
			for (String property : obsTemp.keySet()) {
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
		
		//create entries for sub-polygons & sensors
		for (GeoPolygon entry : this.subPolygons) {
			entry.updateObservations();
		}
		
		boolean anyEntry = false;
		ObservationData obs = new ObservationData();
		obs.observationDate = TimeUtil.getUTCDateTimeNowString();
		Set<Tuple3D<String, Integer, Double>> values = new HashSet<>();
		Set<String> properties = new HashSet<>();
		DateTime dt = null;
		// save properties found in sub-GeoPolygons and sensors
		for (GeoPolygon entry : this.subPolygons) {
			Map<String, String> obsTemp = entry.observationData.observations;
			for (String property : obsTemp.keySet()) {
				values.add(new Tuple3D<String, Integer, Double>(property
						, Integer.valueOf(entry.getNumberOfSensors(property)), Double.valueOf(obsTemp.get(property))));
				properties.add(property);
			}
			DateTime dtCheck = TimeUtil.getUTCDateTime(entry.observationData.observationDate).toDateTime();
			if (dt == null || dt.isBefore(dtCheck)) {
				dt = dtCheck;
			}
		}
		for (ObservationData entry : this.sensorValues.values()) {
			Map<String, String> obsTemp = entry.observations;
			for (String property : obsTemp.keySet()) {
				values.add(new Tuple3D<String, Integer, Double>(property
						, Integer.valueOf(1), Double.valueOf(obsTemp.get(property))));
				properties.add(property);
			}
			DateTime dtCheck = TimeUtil.getUTCDateTime(entry.observationDate).toDateTime();
			if (dt == null || dt.isBefore(dtCheck)) {
				dt = dtCheck;
			}
		}
		
		// save data in ObservationData obs after calculation
		for (String property : properties) {
			double value = 0;
			int totalSensors = 0;
			
			for (Tuple3D<String, Integer, Double> tuple : values) {
				if (tuple.getFirstValue().equals(property)) {
					if (!anyEntry) anyEntry = true;
					value += tuple.getThirdValue().doubleValue() * tuple.getSecondValue().doubleValue();
					totalSensors += tuple.getSecondValue().intValue();
				}
			}
			
			value = value / (double) totalSensors;
			if (anyEntry) {
				obs.observations.put(property, String.valueOf(value));
			} else {
				obs.observations.put(property, null);
			}
		}
		if (anyEntry) {
			obs.clusterID = this.ID;
			this.observationData = obs;
			obs.observationDate = TimeUtil.getUTCDateTimeString(dt.toLocalDateTime());
		}
	}
	
	public void resetObservations() {
		for (GeoPolygon subPolygon : subPolygons) {
			subPolygon.resetObservations();
		}
		this.sensorValues.clear();
	}
	
	private void setupObservationData() {
		this.observationData.observationDate = TimeUtil.getUTCDateTimeNowString();
		this.observationData.clusterID = this.ID;
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
