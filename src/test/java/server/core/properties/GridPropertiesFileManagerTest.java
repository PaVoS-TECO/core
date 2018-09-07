package server.core.properties;

import org.junit.Test;

/**
 * Tests {@link GridPropertiesFileManager}
 */
public class GridPropertiesFileManagerTest {

	/**
	 * Tests the initial loading phase.
	 */
	@Test
	public void initLoad() {
		GridPropertiesFileManager.getInstance();
	}

}
