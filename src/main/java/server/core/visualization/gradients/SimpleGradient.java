package server.core.visualization.gradients;

import java.awt.Color;

public class SimpleGradient {
	
	public final Color cStart;
	public final Color cEnd;
	
	public SimpleGradient(Color cStart, Color cEnd) {
		this.cStart = cStart;
		this.cEnd = cEnd;
	}
	
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
		return (float) ((val1 * (1.0 - position)) + (val2 * position));
	}
	
}
