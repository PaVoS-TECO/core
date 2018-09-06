package server.database;

import java.util.HashSet;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import server.transfer.data.ObservationData;

/**
 * Tests the {@link Facade} class.
 * @author Oliver
 *
 */
public class FacadeTest {

	private static Facade facade;
	
	/**
	 * Set up the Facade.
	 * @throws Exception
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
