package edu.teco.pavos.core.visualization;

import edu.teco.pavos.core.visualization.gradients.SimpleGradient;

/**
 * {@link GradientRange}s are used in combination with {@link SimpleGradient}s
 * in order to map colors to a spectrum of values.
 * The values are defined here.
 */
public class GradientRange {
	
	private final String id;
	private final double valueStart;
	private final double valueEnd;
	
	/**
	 * Creates a new {@link GradientRange}.
	 * @param id {@link String}
	 * @param valueStart {@link Double}
	 * @param valueEnd {@link Double}
	 */
	public GradientRange(String id, double valueStart, double valueEnd) {
		if (id == null) throw new IllegalArgumentException();
		this.id = id;
		this.valueStart = valueStart;
		this.valueEnd = valueEnd;
	}
	
	@Override
	public String toString() {
		return String.format("\"_%s\": [%s, %s]", id, valueStart, valueEnd);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) return false;
		GradientRange oRange = (GradientRange) o;
		return oRange.hashCode() == this.hashCode();
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/**
	 * @return the identifier of this object
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * @return the first value of this range
	 */
	public double getValueStart() {
		return valueStart;
	}
	
	/**
	 * @return the second value of this range
	 */
	public double getValueEnd() {
		return valueEnd;
	}
	
}
