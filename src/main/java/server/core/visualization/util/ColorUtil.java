package server.core.visualization.util;

import java.awt.Color;

public final class ColorUtil {
	
	private ColorUtil() {
		
	}
	
	public static String getHexFromColor(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static Color getColorFromHex(String hexColor) {
		return Color.decode(hexColor);
	}
	
	public static String getRGBAFromColor(Color color) {
		return "rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha();
	}
	
}
