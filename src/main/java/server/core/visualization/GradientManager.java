package server.core.visualization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.core.properties.GradientPropertiesFileManager;
import server.core.visualization.gradients.MultiGradient;
import server.core.visualization.util.ColorUtil;

public final class GradientManager {
	
	private static GradientManager instance;
	private Map<MultiGradient, List<GradientRange>> gradients = new HashMap<>();
	GradientPropertiesFileManager manager;
	
	private GradientManager() {
		this.manager = GradientPropertiesFileManager.getInstance();
		String colorsHex = manager.getProperty("temperature");
		String rangeCelsius = manager.getProperty("temperature.range.celsius");
		String rangeFahrenheit = manager.getProperty("temperature.range.celsius");
		addGradient(getGradient("temperature", colorsHex), getRange("celsius", rangeCelsius), getRange("Fahrenheit", rangeFahrenheit));
	}
	
	public static GradientManager getInstance() {
		if (instance == null) {
			instance = new GradientManager();
		}
		return instance;
	}
	
	public GradientRange getRangeFromName(String gradientName, String rangeName) {
		return getRangeFromGradient(getGradient(gradientName), rangeName);
	}
	
	public GradientRange getRangeFromGradient(MultiGradient gradient, String rangeName) {
		List<GradientRange> ranges = this.gradients.get(gradient);
		GradientRange range = new GradientRange(rangeName, 0, 0);
		if (!ranges.contains(range)) return null;
		return ranges.get(ranges.indexOf(range));
	}
	
	public MultiGradient getGradient(String name) {
		MultiGradient grad = new MultiGradient(name);
		for (MultiGradient key : this.gradients.keySet()) {
			if (key.equals(grad)) {
				return key;
			}
		}
		return null;
	}
	
	private void addGradient(MultiGradient gradient, GradientRange...ranges) {
		List<GradientRange> gradRanges = new ArrayList<>();
		if (ranges.length < 1 || gradient == null) return;
		for (int i = 0; i < ranges.length; i++) {
			gradRanges.add(ranges[i]);
		}
		this.gradients.put(gradient, gradRanges);
	}
	
	private GradientRange getRange(String name, String range) {
		String[] values = range.split(",");
		return new GradientRange(name, Double.valueOf(values[0]), Double.valueOf(values[1]));
	}
	
	private MultiGradient getGradient(String name, String colorsHex) {
		String[] colorsString = colorsHex.split(",");
		Color[] colors = new Color[colorsString.length];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = ColorUtil.getColorFromHex(colorsString[i]);
		}
		return new MultiGradient(name, colors);
	}
	
}
