package edu.teco.pavos.core.grid.polygon;

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

import org.joda.time.DateTime;

import edu.teco.pavos.core.grid.exceptions.ClusterNotFoundException;
import edu.teco.pavos.core.grid.geojson.GeoJsonConverter;
import edu.teco.pavos.core.grid.geojson.data.ClusterGeoJson;
import edu.teco.pavos.core.grid.geojson.data.ObservationGeoJson;
import edu.teco.pavos.core.grid.polygon.math.ArrayListCalc;
import edu.teco.pavos.core.grid.polygon.math.Tuple2D;
import edu.teco.pavos.core.grid.polygon.math.Tuple3D;
import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;

/**
 * A geographically oriented approach to polygons with double precision.<p>
 * Uses {@link Path2D.Double} for the polygon base.
 * Can contain sub-{@link GeoPolygon}s.
 * Can contain {@link ObservationData} from sensors.
 */
public abstract class GeoPolygon {
	
	private final Object sensorValuesLock = new Object();
	private final Object observationDataLock = new Object();
	private final Rectangle2D.Double bounds;
	private final int rows;
	private final int columns;
	private final double scale;
	private final String id;
	private final int levelsAfterThis;
	
	protected volatile Path2D.Double path;
	protected volatile List<GeoPolygon> subPolygons;
	protected volatile Map<String, ObservationData> sensorValues;
	protected volatile ObservationData observationData;
	protected volatile ClusterGeoJson clusterGeoJson;
	private final Object clusterGeoJsonLock = new Object();
	
	/**
	 * Creates a {@link GeoPolygon} with the given bounds, rows, columns, levels and id.<p>
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
	 * Returns this {@link GeoPolygon} in GeoJson format.
	 * Uses the specified value as data.
	 * The GeoJson recieved from this must be part of an {@link ObservationGeoJson},
	 * since it is only a single feature and no feature-collection.
	 * @param observationType {@link String}
	 * @return geoJson {@link String}
	 */
	public String getLiveClusterGeoJson(String observationType) {
		ArrayList<Double> valueArray = this.observationData.getAnonObservation(observationType);
		synchronized (clusterGeoJsonLock) {
			if (clusterGeoJson == null) {
				this.clusterGeoJson = new ClusterGeoJson(valueArray, this.id, subPolygons, getPoints());
			}
			clusterGeoJson.setValue(valueArray);
		}
		return clusterGeoJson.getGeoJson();
	}
	
	/**
	 * Returns this {@link GeoPolygon} in GeoJson format.
	 * Uses the specified value as data.
	 * The GeoJson recieved from this must be part of an {@link ObservationGeoJson},
	 * since it is only a single feature and no feature-collection.
	 * @param observationType The {@link String} key / property of the observation. Like 'temperature_celsius'
	 * @param valueArray {@link ArrayList} of type {@link Double}
	 * @return geoJson {@link String}
	 */
	public String getArchivedClusterGeoJson(String observationType, ArrayList<Double> valueArray) {
		synchronized (clusterGeoJsonLock) {
			if (clusterGeoJson == null) {
				this.clusterGeoJson = new ClusterGeoJson(
						this.observationData.getAnonObservation(observationType),
						this.id, subPolygons, getPoints());
			}
		}
		return clusterGeoJson.getArchivedGeoJson(valueArray);
	}
	
	@Override
	public String toString() {
		return this.id;
	}
	
	/**
	 * Creates or overrides a map-entry with the new value in double-precision.
	 * SensorID has to be set in the {@link ObservationData}.
	 * @param data The {@link ObservationData} with the value.
	 */
	public void addObservation(ObservationData data) {
		data.setClusterID(this.id);
		synchronized (sensorValuesLock) {
			this.sensorValues.put(data.getSensorID(), data);
		}
	}
	
	/**
	 * Returns a cloned version of this {@link GeoPolygon}s {@link ObservationData} data.
	 * @return observationData {@link ObservationData}
	 */
	public ObservationData cloneObservation() {
		return this.observationData.clone();
	}
	
	/**
	 * Returns true if the current {@link GeoPolygon} contains the specified {@link Point2D.Double}.
	 * If {@code checkBoundsFirst} is set to {@code true},
	 * the method will check if the object is inside the boundaries first,
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
	 * Returns the current {@link String} sensorIDs that are
	 * inside this cluster as a {@link Collection} over all sensors.
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
	 * Returns the current {@link String} sensorIDs that are
	 * inside this cluster as a {@link Collection} over all sensors.
	 * This does not include sensors from sub-{@link GeoPolygon}s.
	 * @return sensorDataSet {@code Collection<String>}
	 */
	public Collection<String> getDirectSensorIDs() {
		return this.sensorValues.keySet();
	}
	
	/**
	 * Returns the current {@link GeoPolygon} as GeoJson-String
	 * @param observationType {@link String}.
	 * @return geoJson {@link String}
	 */
	public String getGeoJson(String observationType) {
		return GeoJsonConverter.convert(this, observationType);
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
	 * Returns the number of sensors in this {@link GeoPolygon}
	 * that send data about a specific {@link Collection} of properties.
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
				if (!data.getSingleObservations().containsKey(property)
						&& !data.getVectorObservations().containsKey(property)) {
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
			if (data.getSingleObservations().containsKey(property)
					|| data.getVectorObservations().containsKey(property)) {
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
		    // SEG_MOVETO, SEG_QUADTO, SEG_CUBICTO
		    pi.next();
		}
		return points;
	}
	
	/**
	 * Returns the current {@link ObservationData} data as a {@link Collection} over all sensors.
	 * The new clusterID will be the {@link GeoPolygon}.ID
	 * @return sensorDataSet {@code Collection<ObservationData>}
	 */
	public Collection<ObservationData> getSensorDataList() {
		synchronized (sensorValues) {
			return this.sensorValues.values();
		}
	}
	
	/**
	 * Returns the current sensor-{@link ObservationData} of sub-{@link GeoPolygon}s.
	 * @return subObservations {@link Collection} of {@link ObservationData}
	 */
	public Collection<ObservationData> getSubSensorObservations() {
		Collection<ObservationData> observations = new ArrayList<>();
		for (GeoPolygon polygon : subPolygons) {
			observations.addAll(polygon.getSubSensorObservations());
		}
		observations.addAll(getSensorDataList());
		return observations;
	}
	
	/**
	 * Returns the current cluster-{@link ObservationData} of sub-{@link GeoPolygon}s.
	 * @return subObservations {@link Collection} of {@link ObservationData}
	 */
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
			if (polygon.id.equals(clusterID)) {
				return polygon;
			}
		}
		throw new ClusterNotFoundException(clusterID);
	}
	
	/**
	 * Returns a {@link Collection} of all {@link GeoPolygon}s inside this {@link GeoPolygon}.
	 * @return subPolygons {@link List} of {@link GeoPolygon}s
	 */
	public List<GeoPolygon> getSubPolygons() {
		return subPolygons;
	}
	
	/**
	 * Returns this {@link GeoPolygon}s and all sub-{@link GeoPolygon}s sensor-{@link ObservationData}.
	 * @return observations {@link Collection} of {@link ObservationData}
	 */
	public Collection<ObservationData> getSensorObservations() {
		Collection<ObservationData> observations = new HashSet<>();
		for (GeoPolygon subPolygon : subPolygons) {
			observations.addAll(subPolygon.getSensorObservations());
		}
		
		if (!sensorValues.isEmpty()) {
			observations.addAll(sensorValues.values());
		}
		return observations;
	}
	
	/**
	 * Returns all observation-types that this {@link GeoPolygon} and it's sub-{@link GeoPolygon}s have observed
	 * since the last invocation of {@code resetObservations()}.
	 * @return observationTypes {@link Collection} of {@link String}
	 */
	public Collection<String> getAllObservationTypes() {
		Collection<String> properties = new HashSet<>();
		for (GeoPolygon entry : this.subPolygons) {
			for (String property : entry.observationData.getSingleObservations().keySet()) {
				properties.add(property);
			}
			for (String property : entry.observationData.getVectorObservations().keySet()) {
				if (!properties.contains(property)) properties.add(property);
			}
		}
		for (ObservationData entry : this.sensorValues.values()) {
			for (String property : entry.getSingleObservations().keySet()) {
				properties.add(property);
			}
			for (String property : entry.getVectorObservations().keySet()) {
				if (!properties.contains(property)) properties.add(property);
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
		
		Collection<Tuple3D<String, Integer, ArrayList<Double>>> values = getPropertyWeight();
		Collection<String> properties = getProperties();
		
		setWeightedObservationForCluster(values, properties);
	}
	
	private void setWeightedObservationForCluster(Collection<Tuple3D<String, Integer, ArrayList<Double>>> values,
			Collection<String> properties) {
		synchronized (observationDataLock) {
			boolean anyEntry = false;
			ObservationData obs = new ObservationData();
			DateTime dt = selectDateTimeFromAllSources();
			obs.setObservationDate(TimeUtil.getUTCDateTimeNowString());
			for (String property : properties) {
				anyEntry = weightProperty(property, values, obs);
			}
			if (anyEntry) {
				obs.setClusterID(this.id);
				obs.setObservationDate(TimeUtil.getUTCDateTimeString(dt.toLocalDateTime()));
				this.observationData = obs;
			}
		}
	}
	
	private boolean weightProperty(String property, Collection<Tuple3D<String, Integer, ArrayList<Double>>> values,
			ObservationData output) {
		ArrayList<Double> value = new ArrayList<>();
		int totalSensors = 0;
		boolean anyEntry = false;
		
		for (Tuple3D<String, Integer, ArrayList<Double>> tuple : values) {
			if (tuple.getFirstValue().equals(property)) {
				Tuple2D<ArrayList<Double>, Integer> weight = addWeightedData(tuple, value, totalSensors);
				if (weight != null && weight.getFirstValue() != null && weight.getSecondValue() != null) {
					value = weight.getFirstValue();
					totalSensors = weight.getSecondValue();
					anyEntry = true;
				}
			}
		}
		value = (totalSensors != 0) ? ArrayListCalc.divideArrayValue(value, (double) totalSensors) : value;
		if (value.size() == 1) {
			output.addSingleObservation(property, anyEntry ? value.get(0) : null);
		} else {
			output.addVectorObservation(property, anyEntry ? value : null);
		}
		return anyEntry;
	}
	
	private Tuple2D<ArrayList<Double>, Integer> addWeightedData(Tuple3D<String, Integer, ArrayList<Double>> tuple,
			ArrayList<Double> value, int totalSensors) {
		if (tuple.getFirstValue() == null || tuple.getSecondValue() == null 
				|| tuple.getThirdValue() == null) return null;
		return new Tuple2D<>(ArrayListCalc.sumArrayArray(
				value, ArrayListCalc.multiplyArrayValue(tuple.getThirdValue(), tuple.getSecondValue())),
				totalSensors + tuple.getSecondValue().intValue());
	}
	
	/**
	 * Returns all observation-types that this {@link GeoPolygon} has observed
	 * since the last invocation of {@code resetObservations()}.
	 * @return observationTypes {@link Collection} of {@link String}
	 */
	public Collection<String> getProperties() {
		Collection<String> properties = new HashSet<>();
		for (GeoPolygon polygon : this.subPolygons) {
			for (Entry<String, Double> entry : polygon.observationData.getSingleObservations().entrySet()) {
				properties.add(entry.getKey());
			}
			for (Entry<String, ArrayList<Double>> entry : polygon.observationData.getVectorObservations().entrySet()) {
				String property = entry.getKey();
				if (!properties.contains(property)) properties.add(property);
			}
		}
		for (ObservationData observation : this.sensorValues.values()) {
			for (String property : observation.getSingleObservations().keySet()) {
				properties.add(property);
			}
			for (String property : observation.getVectorObservations().keySet()) {
				if (!properties.contains(property)) properties.add(property);
			}
		}
		return properties;
	}
	
	private DateTime selectDateTimeFromAllSources() {
		DateTime dt = null;
		for (GeoPolygon polygon : this.subPolygons) {
			dt = latestDateTime(dt, TimeUtil.getUTCDateTime(polygon.observationData.getObservationDate()).toDateTime());
		}
		for (ObservationData observation : this.sensorValues.values()) {
			dt = latestDateTime(dt, TimeUtil.getUTCDateTime(observation.getObservationDate()).toDateTime());
		}
		if (dt == null) dt = TimeUtil.getUTCDateTimeNow().toDateTime();
		return dt;
	}
	
	private DateTime latestDateTime(DateTime dt1, DateTime dt2) {
		if ((dt1 == null && dt2 == null)) return null;
		if (dt1 == null) return dt2;
		if (dt2 == null) return dt1;
		DateTime result = new DateTime(dt1.getMillis());
		if (dt1.isAfter(dt2)) result = dt2;
		return result;
	}
	
	private Collection<Tuple3D<String, Integer, ArrayList<Double>>> getPropertyWeight() {
		Collection<Tuple3D<String, Integer, ArrayList<Double>>> values = new HashSet<>();
		for (GeoPolygon polygon : this.subPolygons) {
			for (Entry<String, Double> entry : polygon.observationData.getSingleObservations().entrySet()) {
				ArrayList<Double> valueArray = new ArrayList<>();
				valueArray.add(entry.getValue());
				values.add(new Tuple3D<String, Integer, ArrayList<Double>>(entry.getKey(),
						Integer.valueOf(polygon.getNumberOfSensors(entry.getKey())), 
						valueArray));
			}
			for (Entry<String, ArrayList<Double>> entry : polygon.observationData.getVectorObservations().entrySet()) {
				values.add(new Tuple3D<String, Integer, ArrayList<Double>>(entry.getKey(),
						Integer.valueOf(polygon.getNumberOfSensors(entry.getKey())),
						entry.getValue()));
			}
		}
		for (ObservationData observation : this.sensorValues.values()) {
			for (Entry<String, Double> entry : observation.getSingleObservations().entrySet()) {
				ArrayList<Double> valueArray = new ArrayList<>();
				valueArray.add(entry.getValue());
				values.add(new Tuple3D<String, Integer, ArrayList<Double>>(entry.getKey(),
						Integer.valueOf(1),
						valueArray));
			}
			for (Entry<String, ArrayList<Double>> entry : observation.getVectorObservations().entrySet()) {
				values.add(new Tuple3D<String, Integer, ArrayList<Double>>(entry.getKey(),
						Integer.valueOf(1),
						entry.getValue()));
			}
		}
		return values;
	}
	
	/**
	 * Resets all sensor-{@link ObservationData} of this {@link GeoPolygon} and its sub-{@link GeoPolygon}s.
	 */
	public void resetObservations() {
		for (GeoPolygon subPolygon : subPolygons) {
			subPolygon.resetObservations();
		}
		synchronized (sensorValuesLock) {
			this.sensorValues.clear();
		}
	}
	
	private void setupObservationData() {
		this.observationData.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		this.observationData.setClusterID(this.id);
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
	 * @return clusterObservations {@link Collection} of {@link ObservationData}
	 */
	protected Collection<ObservationData> getClusterObservations() {
		Collection<ObservationData> result = new HashSet<>();
		for (GeoPolygon polygon : subPolygons) {
			result.addAll(polygon.getClusterObservations());
		}
		result.add(cloneObservation());
		return result;
	}

	/**
	 * @return the bounds
	 */
	public Rectangle2D.Double getBounds() {
		return new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
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
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}
	
	/**
	 * @return the identifier
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return the levelsAfterThis
	 */
	public int getLevelsAfterThis() {
		return levelsAfterThis;
	}
	
}
