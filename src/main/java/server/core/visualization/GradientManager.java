package server.core.visualization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import server.core.properties.GradientPropertiesFileManager;
import server.core.visualization.gradients.MultiGradient;
import server.core.visualization.util.ColorUtil;

public final class GradientManager {
	
	private static GradientManager instance;
	private List<MultiGradient> gradients = new ArrayList<>();
	GradientPropertiesFileManager manager;
	
	private GradientManager() {
		this.manager = GradientPropertiesFileManager.getInstance();
		String colorsHex = manager.getProperty("temperature");
		String rangeCelsius = manager.getProperty("temperature.range.celsius");
		String rangeFahrenheit = manager.getProperty("temperature.range.fahrenheit");
		addGradient(getGradient("temperature", colorsHex), getRange("celsius", rangeCelsius), getRange("Fahrenheit", rangeFahrenheit));
	}
	
	public static GradientManager getInstance() {
		if (instance == null) {
			instance = new GradientManager();
		}
		return instance;
	}
	
	public List<MultiGradient> getAllGradients() {
		List<MultiGradient> result = new ArrayList<>();
		gradients.forEach((gradient) -> result.add(gradient));
		return result;
	}
	
	public MultiGradient getGradient(String name) {
		MultiGradient grad = new MultiGradient(name);
		return gradients.get(gradients.indexOf(grad));
	}
	
	private void addGradient(MultiGradient gradient) {
		if (gradient != null) gradients.add(gradient);
	}
	
	private void addGradient(MultiGradient gradient, GradientRange...ranges) {
		if (gradient == null) return;
		
		for (int i = 0; i < ranges.length; i++) {
			gradient.addRange(ranges[i]);
		}
		
		addGradient(gradient);
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
