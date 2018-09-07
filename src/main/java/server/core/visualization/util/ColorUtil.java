package server.core.visualization.util;

import java.awt.Color;

/**
 * A utility class for managing {@link Color}s
 */
public final class ColorUtil {
	
	private ColorUtil() {
		
	}
	
	/**
	 * Converts the {@link Color} to a {@link String} in hex format.
	 * @param color {@link Color}
	 * @return hexColor {@link String}
	 */
	public static String getHexFromColor(Color color) {
		if (color == null) return "";
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
	
	/**
	 * Converts a {@link String} in hex format to a {@link Color}
	 * @param hexColor {@link String}
	 * @return color {@link Color}
	 */
	public static Color getColorFromHex(String hexColor) {
		if (hexColor == null) throw new IllegalArgumentException();
		return Color.decode(hexColor);
	}
	
	/**
	 * Returns a String with all RGBA values in it.
	 * @param color {@link Color}
	 * @return rbgaColor {@link String}
	 */
	public static String getRGBAFromColor(Color color) {
		if (color == null) throw new IllegalArgumentException();
		return "rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha();
	}
	
}
