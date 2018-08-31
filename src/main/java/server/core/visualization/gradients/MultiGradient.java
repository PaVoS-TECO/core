package server.core.visualization.gradients;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import server.core.visualization.util.ColorUtil;

public class MultiGradient {
	
	private List<SimpleGradient> gradients = new ArrayList<>();
	public final String NAME;
	
	public MultiGradient(String name, Color... colors) {
		this.NAME = name;
	    if (colors.length < 1) {
	    	gradients.add(new SimpleGradient(new Color(0, 0, 0), new Color(0, 0, 0)));
	    } else if (colors.length == 1) {
			gradients.add(new SimpleGradient(colors[0], colors[0]));
		} else {
			for (int i = 0; i < colors.length - 1; i++) {
				gradients.add(new SimpleGradient(colors[i], colors[i + 1]));
			}
		}
	}
	
	@Override
	public String toString() {
		Color[] colors = getColors();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < colors.length - 1; i++) {
			builder.append(ColorUtil.getHexFromColor(colors[i]) + ",");
		}
		builder.append(ColorUtil.getHexFromColor(colors[colors.length - 1]));
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) return false;
		MultiGradient oGrad = (MultiGradient) o;
		return oGrad.NAME.equals(this.NAME);
	}
	
	@Override
	public int hashCode() {
		return this.NAME.hashCode();
	}
	
	public Color[] getColors() {
		Color[] colors = new Color[gradients.size() + 1];
		colors[0] = gradients.get(0).cStart;
		for (int i = 0; i < gradients.size(); i++) {
			colors[i + 1] = gradients.get(i).cEnd;
		}
		return colors;
	}
	
	public Color getColorAt(double position) {
		if (position < 0.0) position = 0.0;
		if (position > 1.0) position = 1.0;
		int amount = gradients.size();
		double gradLength = 1.0 / (double) amount;
		double ratio = (double) amount;
		double subPosition = 0.0;
		int gradIndex = 0;
		for (int i = 1; i < amount; i++) {
			if (position <= ((double) i) * gradLength) {
				gradIndex = i - 1;
				double subMin = gradLength * (double) gradIndex;
				subPosition = position - subMin;
			}
		}
		subPosition = subPosition * ratio;
		return gradients.get(gradIndex).getColorAt(subPosition);
	}
	
	public SimpleGradient substituteGradient(SimpleGradient grad, int index) {
		SimpleGradient result = removeGradient(index);
		addGradient(grad, index);
		return result;
	}
	
	public void addGradient(SimpleGradient grad, int index) {
		int beforeIndex = index - 1;
		SimpleGradient sg1 = this.gradients.remove(beforeIndex);
		SimpleGradient sg2 = this.gradients.remove(beforeIndex);
		sg1 = new SimpleGradient(sg1.cStart, grad.cStart);
		sg2 = new SimpleGradient(grad.cEnd, sg2.cEnd);
		this.gradients.add(beforeIndex, sg2);
		this.gradients.add(beforeIndex, sg1);
		this.gradients.add(index, grad);
	}
	
	public SimpleGradient removeGradient(int index) {
		int beforeIndex = index - 1;
		int afterIndex = index + 1;
		SimpleGradient sg2 = this.gradients.get(afterIndex);
		SimpleGradient sg1 = this.gradients.remove(beforeIndex);
		sg1 = new SimpleGradient(sg1.cStart, sg2.cStart);
		this.gradients.add(beforeIndex, sg1);
		return this.gradients.remove(index);
	}
	
}
