package server.core.visualization.gradients;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import server.core.visualization.GradientRange;
import server.core.visualization.util.ColorUtil;

/**
 * {@link MultiGradient}s combine different {@link SimpleGradient}s
 */
public class MultiGradient {
	
	private List<SimpleGradient> gradients = new ArrayList<>();
	private List<GradientRange> ranges = new ArrayList<>();
	public final String ID;
	
	/**
	 * Creates a new {@link MultiGradient}
	 * @param id {@link String}
	 * @param colors {@link Array} of {@link Color}s
	 */
	public MultiGradient(String id, Color... colors) {
		if (id != null) {
			this.ID = id;
		} else {
			this.ID = String.valueOf(new Random().nextInt());
		}
	    if (colors == null || colors.length < 1) {
	    	gradients.add(new SimpleGradient(new Color(0, 0, 0), new Color(0, 0, 0)));
	    } else if (colors.length == 1) {
			gradients.add(new SimpleGradient(colors[0], colors[0]));
		} else {
			for (int i = 0; i < colors.length - 1; i++) {
				gradients.add(new SimpleGradient(colors[i], colors[i + 1]));
			}
		}
	}
	
	/**
	 * Returns all colors from position 0.0 to 1.0.
	 * Steps are the endpoints of the internal {@link SimpleGradient}s.
	 * @return colors {@link Array} of {@link Color}s
	 */
	public Color[] getColors() {
		if (gradients.size() == 0) return new Color[0];
		Color[] colors = new Color[gradients.size() + 1];
		colors[0] = gradients.get(0).getColorAt(0);
		for (int i = 0; i < gradients.size(); i++) {
			colors[i + 1] = gradients.get(i).getColorAt(1);
		}
		return colors;
	}
	
	private String colorsToString(Color[] colors) {
		if (colors.length == 0) return "";
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < colors.length; i++) {
			builder.append(String.format("\"%s\"", ColorUtil.getHexFromColor(colors[i])));
			if (i < colors.length - 1) builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("\"%s\": { ", this.ID));
		builder.append(gradientToString());
		ranges.forEach((range) -> builder.append(String.format(", %s", range.toString())));
		builder.append(" }");
		return builder.toString();
	}
	
	/**
	 * Returns the current {@link MultiGradient} without {@link GradientRange}s as {@link String}.
	 * @return gradient {@link String}
	 */
	public String gradientToString() {
		return "\"gradient\": " + colorsToString(getColors());
	}
	
	/**
	 * Returns all {@link GradientRange}s of this {@link MultiGradient}.
	 * @return ranges {@link Collection} of {@link GradientRange}s
	 */
	public Collection<GradientRange> getRanges() {
		Collection<GradientRange> result = new ArrayList<>();
		ranges.forEach((range) -> result.add(range));
		return result;
	}
	
	/**
	 * Returns the {@link GradientRange} that matches with the specified id.
	 * @param rangeId {@link String}
	 * @return range {@link GradientRange}
	 */
	public GradientRange getRange(String rangeId) {
		GradientRange check = new GradientRange(rangeId, 0, 0);
		return ranges.get(ranges.indexOf(check));
	}
	
	/**
	 * Adds a new {@link GradientRange} to this {@link MultiGradient}.
	 * @param range {@link GradientRange}
	 */
	public void addRange(GradientRange range) {
		if (range != null) ranges.add(range);
	}
	
	/**
	 * Removes a {@link GradientRange} from this {@link MultiGradient}s ranges.
	 * @param index {@link Integer}
	 * @return range The removed {@link GradientRange}
	 */
	public GradientRange removeRange(int index) {
		if (index >= 0 && index < ranges.size()) return ranges.remove(index);
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) return false;
		MultiGradient oGrad = (MultiGradient) o;
		return oGrad.hashCode() == this.hashCode();
	}
	
	@Override
	public int hashCode() {
		return this.ID.hashCode();
	}
	
	/**
	 * Returns the color mapped to the specified position.
	 * @param position {@link Double}
	 * @return color {@link Color}
	 */
	public Color getColorAt(double position) {
		if (position < 0.0) position = 0.0;
		if (position > 1.0) position = 1.0;
		int amount = gradients.size();
		double gradLength = 1.0 / (double) amount;
		double ratio = (double) amount;
		double subPosition = 0.0;
		int gradIndex = 0;
		if (amount > 1) {
			for (int i = 1; i < amount; i++) {
				if (position <= ((double) i) * gradLength) {
					gradIndex = i - 1;
					double subMin = gradLength * (double) gradIndex;
					subPosition = position - subMin;
				}
			}
		} else {
			subPosition = position;
		}
		subPosition = subPosition * ratio;
		return gradients.get(gradIndex).getColorAt(subPosition);
	}
	
	/**
	 * Swaps a {@link SimpleGradient} with another {@link SimpleGradient}.
	 * @param grad {@link SimpleGradient} to be placed
	 * @param index {@link Integer}
	 * @return gradient The swapped {@link SimpleGradient}
	 */
	public SimpleGradient substituteGradient(SimpleGradient grad, int index) {
		SimpleGradient result = removeGradient(index);
		addGradient(grad, index);
		return result;
	}
	
	/**
	 * Adds a new {@link SimpleGradient} to this {@link MultiGradient}.
	 * @param grad {@link SimpleGradient} to be added
	 * @param index {@link Integer}
	 */
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
	
	/**
	 * Removes an existing {@link SimpleGradient} from this {@link MultiGradient}.
	 * @param index {@link Integer}
	 * @return gradient The removed {@link SimpleGradient}
	 */
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
