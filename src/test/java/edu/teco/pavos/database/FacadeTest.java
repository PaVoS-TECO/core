package edu.teco.pavos.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.teco.pavos.transfer.data.ObservationData;

/**
 * Tests the {@link Facade} class.
 * @author Oliver
 *
 */
public class FacadeTest {

	private static Facade facade;
	
	/**
	 * Set up the Facade.
	 * @throws Exception The Facade could not be initialized
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		facade = Facade.getInstance();
	}

	/**
	 * Test all methods of the Facade.
	 */
	@Test
	public void test() {
		facade = Facade.getInstance();
		facade.isConnected();
		facade.addObservationData("invalid".getBytes());
		facade.addObservationData((ObservationData) null);
		assertNull(facade.getObservationData("P", "V", "S"));
		facade.addMemcachedServer(null, 0);
		assertEquals(new HashSet<String>(), facade.getObservedProperties("PaVoS"));
	}

}
