package server.core.visualization.gradients;

import java.awt.Color;

import server.core.visualization.GradientRange;

/**
 * {@link SimpleGradient}s are used in combination with {@link GradientRange}s
 * in order to map colors to a spectrum of values.
 * The colors are defined here.
 */
public class SimpleGradient {
	
	public final Color cStart;
	public final Color cEnd;
	
	/**
	 * Creates a new {@link SimpleGradient}
	 * @param cStart {@link Color} of position 0.0
	 * @param cEnd {@link Color} of position 1.0
	 */
	public SimpleGradient(Color cStart, Color cEnd) {
		this.cStart = cStart;
		this.cEnd = cEnd;
	}
	
	/**
	 * Returns the color mapped to the specified position.
	 * @param position {@link Double}
	 * @return color {@link Color}
	 */
	public Color getColorAt(double position) {
		if (position < 0.0) position = 0.0;
		if (position > 1.0) position = 1.0;
		float r = getValueInBetween(cStart.getRed(), cEnd.getRed(), position);
		float g = getValueInBetween(cStart.getGreen(), cEnd.getGreen(), position);
		float b = getValueInBetween(cStart.getBlue(), cEnd.getBlue(), position);
		float a = getValueInBetween(cStart.getAlpha(), cEnd.getAlpha(), position);
		return new Color(r, g, b, a);
	}
	
	private float getValueInBetween(float val1, float val2, double position) {
		double relVal1 = val1 / 255.0;
		double relVal2 = val2 / 255.0;
		return (float) ((relVal1 * (1.0 - position)) + (relVal2 * position));
	}
	
}
