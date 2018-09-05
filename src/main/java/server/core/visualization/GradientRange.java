package server.core.visualization;

import server.core.visualization.gradients.SimpleGradient;

/**
 * {@link GradientRange}s are used in combination with {@link SimpleGradient}s
 * in order to map colors to a spectrum of values.
 * The values are defined here.
 */
public class GradientRange {
	
	public final String ID;
	public final double VALUE_START;
	public final double VALUE_END;
	
	/**
	 * Creates a new {@link GradientRange}.
	 * @param id {@link String}
	 * @param valueStart {@link Double}
	 * @param valueEnd {@link Double}
	 */
	public GradientRange(String id, double valueStart, double valueEnd) {
		this.ID = id;
		this.VALUE_START = valueStart;
		this.VALUE_END = valueEnd;
	}
	
	@Override
	public String toString() {
		return String.format("\"_%s\": [%s, %s]", ID, VALUE_START, VALUE_END);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o ==null || !o.getClass().equals(this.getClass())) return false;
		GradientRange oRange = (GradientRange) o;
		return oRange.hashCode() == this.hashCode();
	}
	
	@Override
	public int hashCode() {
		return this.ID.hashCode();
	}
	
}
