package edu.teco.pavos.core.visualization.gradients;

import java.awt.Color;

import edu.teco.pavos.core.visualization.GradientRange;

/**
 * {@link SimpleGradient}s are used in combination with {@link GradientRange}s
 * in order to map colors to a spectrum of values.
 * The colors are defined here.
 */
public class SimpleGradient {
	
	private final Color cStart;
	private final Color cEnd;
	
	/**
	 * Creates a new {@link SimpleGradient}
	 * @param cStart {@link Color} of position 0.0
	 * @param cEnd {@link Color} of position 1.0
	 */
	public SimpleGradient(Color cStart, Color cEnd) {
		if (cStart == null || cEnd == null) throw new IllegalArgumentException();
		this.cStart = cStart;
		this.cEnd = cEnd;
	}
	
	/**
	 * Returns the color mapped to the specified position.
	 * @param position {@link Double}
	 * @return color {@link Color}
	 */
	public Color getColorAt(double position) {
		double newPosition = position;
		if (position < 0.0) newPosition = 0.0;
		else if (position > 1.0) newPosition = 1.0;
		float r = getValueInBetween(cStart.getRed(), cEnd.getRed(), newPosition);
		float g = getValueInBetween(cStart.getGreen(), cEnd.getGreen(), newPosition);
		float b = getValueInBetween(cStart.getBlue(), cEnd.getBlue(), newPosition);
		float a = getValueInBetween(cStart.getAlpha(), cEnd.getAlpha(), newPosition);
		return new Color(r, g, b, a);
	}
	
	private float getValueInBetween(float val1, float val2, double position) {
		double relVal1 = val1 / 255.0;
		double relVal2 = val2 / 255.0;
		return (float) ((relVal1 * (1.0 - position)) + (relVal2 * position));
	}
	
	/**
	 * @return the color at the beginning
	 */
	public Color getcStart() {
		return cStart;
	}

	/**
	 * @return the color at the end
	 */
	public Color getcEnd() {
		return cEnd;
	}
	
}
