package edu.teco.pavos.core.properties;

import org.junit.Test;

/**
 * Tests {@link GradientPropertiesFileManager}
 */
public class GradientPropertiesFileManagerTest {

	/**
	 * Tests the initial loading phase.
	 */
	@Test
	public void initLoad() {
		GradientPropertiesFileManager.getInstance();
	}

}
