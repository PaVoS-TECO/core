package edu.teco.pavos.core.visualization.gradients;

import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Test;

import edu.teco.pavos.core.visualization.GradientRange;

/**
 * Tests {@link MultiGradient}
 */
public class MultiGradientTest {
	
	/**
	 * Tests creating a Gradient with bad arguments.
	 */
	@Test
	public void testNullColorsAndNullName() {
		Color[] colors = null;
		MultiGradient grad = new MultiGradient(null, colors);
		assertTrue(grad.getID() != null);
		assertTrue(grad.getColors().length != 0);
	}
	
	/**
	 * Tests creating a Gradient with a single {@link Color}.
	 */
	@Test
	public void testSingleColor() {
		Color[] colors = new Color[] {new Color(0, 50, 200)};
		new MultiGradient("test", colors);
	}

	/**
	 * Tests creating a Gradient with multiple {@link Color}s.
	 */
	@Test
	public void testMultipleColors() {
		Color[] colors = new Color[] {new Color(0, 50, 200), new Color(47, 238, 20)};
		new MultiGradient("test", colors);
	}
	

	/**
	 * Tests converting the {@link Color}s of the gradient to {@link String}.
	 */
	@Test
	public void testColorsToString() {
		Color[] colors = new Color[] {new Color(0, 50, 200), new Color(47, 238, 20)};
		MultiGradient grad = new MultiGradient("test", colors);
		assertTrue(grad.toString().equals("\"test\": { \"gradient\": [\"#0032c8\", \"#2fee14\"] }"));
	}

	/**
	 * Tests the management of {@link GradientRange}s.
	 */
	@Test
	public void testRangeManagement() {
		Color[] colors = new Color[] {new Color(0, 50, 200), new Color(47, 238, 20)};
		String gradName = "test";
		MultiGradient grad = new MultiGradient(gradName, colors);
		String rangeName = "tmp";
		GradientRange range = new GradientRange(rangeName, -10.0, 28.0);
		grad.addRange(range);
		assertTrue(grad.getRanges().contains(range));
		assertTrue(grad.getRange(rangeName).equals(range));
		assertTrue(grad.removeRange(0).equals(range));
	}
	
	/**
	 * Tests if two objects are equal.
	 */
	@Test
	public void testEquals() {
		Color[] colors = new Color[] {new Color(0, 50, 200), new Color(47, 238, 20)};
		String gradName = "test";
		MultiGradient grad = new MultiGradient(gradName, colors);
		Color[] checkColors = null;
		MultiGradient checkGrad = new MultiGradient(gradName, checkColors);
		assertTrue(grad.equals(checkGrad));
	}
	
	/**
	 * Tests the management of {@link Color}s and {@link SimpleGradient}s.
	 */
	@Test
	public void testColorManagement() {
		Color[] colors = new Color[] {new Color(0, 50, 200), new Color(47, 238, 20), new Color(0, 50, 200)};
		String gradName = "test";
		MultiGradient grad = new MultiGradient(gradName, colors);
		assertTrue(grad.getColorAt(0.25).equals(new Color(24, 144, 110)));
		grad.addGradient(new SimpleGradient(new Color(0, 0, 0), new Color(200, 200, 200)), 1);
		assertTrue(grad.getColorAt(0.25).equals(new Color(0, 0, 0)));
		grad.substituteGradient(new SimpleGradient(new Color(41, 75, 233), new Color(7, 93, 20)), 1);
		assertTrue(grad.getColorAt(0.25).equals(new Color(41, 75, 233)));
	}

}
