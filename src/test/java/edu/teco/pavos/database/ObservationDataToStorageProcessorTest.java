/**
 * 
 */
package edu.teco.pavos.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.teco.pavos.transfer.data.ObservationData;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * Tests the {@link ObservationDataToStorageProcessor} class.
 * @author Oliver
 *
 */
public class ObservationDataToStorageProcessorTest {

	private static ObservationDataToStorageProcessor odtsp;
	private static MemcachedClient cli;
	
	/**
	 * Set up a direct client to the database and an {@link ObservationDataToStorageProcessor} object.
	 * @throws java.lang.Exception The {@link XMemcachedClient} could not be established
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cli = new XMemcachedClient("192.168.56.101", 11211);
		assumeTrue(!cli.isShutdown());
		odtsp = new ObservationDataToStorageProcessor("192.168.56.101", 11211);
		assumeTrue(odtsp.isConnected());
	}
	
	/**
	 * Test the establishment of a connection to an unreachable server.
	 * All methods called on this instance (except {@code reconnect()} and {@code addServer()})
	 * should return immediately in order to not cause delays.
	 */
	@Test
	public void testUnreachableServer() {
		ObservationDataToStorageProcessor unreachable = new ObservationDataToStorageProcessor("0.0.0.0", 11211);
		assertFalse(unreachable.reconnect());
		assertFalse(unreachable.isConnected());
		unreachable.add(null);
		unreachable.get("P", "V", "S");
		unreachable.addServer("0.0.0.0", 11211);
		unreachable.getObservedProperties("PaVoS");
		unreachable.shutdown();
	}
	
	/**
	 * Test adding an ObservationData object with an invalid ClusterID to the database.
	 * @throws TimeoutException The client timed out while trying to connect to memcached
	 * @throws InterruptedException The client was interrupted while trying to connect to memcached
	 * @throws MemcachedException Internal memcached exception
	 */
	@Test
	public void testInvalidClusterId() throws TimeoutException, InterruptedException, MemcachedException {
		ObservationData od = new ObservationData();
		od.setClusterID("a|b");
		od.setObservationDate("2012-03-04T05:06:07Z");
		od.setSensorID(null);
		od.addDoubleObservation("test", 1.0);
		od.addDoubleObservation("marco", 2.0);
		odtsp.add(od);
		assertNull(cli.get("a|b"));
		assertNull(cli.get("a|b|0"));
		cli.delete("a|b");
		cli.delete("a|b|0");
	}
	
	/**
	 * Test the Time parsing method.
	 * @throws NoSuchMethodException The method does not exist
	 * @throws SecurityException The necessary level of security is not provided
	 * @throws IllegalAccessException The access was denied
	 * @throws IllegalArgumentException The arguments do not match the necessary memcached parameters
	 * @throws InvocationTargetException An invoked method has thrown an exception
	 */
	@Test
	public void testGetTime() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		// set up private method for testing
		Method method = ObservationDataToStorageProcessor.class.getDeclaredMethod("getTime", String.class);
		method.setAccessible(true);
		
		String validDateTime = "2018-03-23T11:11:11Z";
		String before1970DateTime = "1969-01-01T00:00:00Z";
		String missingZDateTime = "2018-01-01T00:00:00";
		String missingTDateTime = "2018-01-0100:00:00Z";
		String monthOutOfBoundsDateTimeLow = "2018-00-01T00:00:00Z";
		String monthOutOfBoundsDateTimeHigh = "2018-13-01T00:00:00Z";
		String dayOutOfBoundsDateTimeLow = "2018-01-00T00:00:00Z";
		String dayOutOfBoundsDateTimeHigh = "2018-01-32T00:00:00Z";
		String hourOutOfBoundsDateTime = "2018-13-01T24:00:00Z";
		String minuteOutOfBoundsDateTime = "2018-13-01T00:60:00Z";
		String secondOutOfBoundsDateTime = "2018-13-01T00:00:60Z";
		String notADateTime = "This is not a date Time";
		String notADateTimeEither = null;
		String dawnOfDateTime = "0000-00-00T00:00:00Z";
		
		LocalDateTime validDateTimeResult = (LocalDateTime) method.invoke(odtsp, validDateTime);
		LocalDateTime before1970DateTimeResult = (LocalDateTime) method.invoke(odtsp, before1970DateTime);
		LocalDateTime missingZDateTimeResult = (LocalDateTime) method.invoke(odtsp, missingZDateTime);
		LocalDateTime missingTDateTimeResult = (LocalDateTime) method.invoke(odtsp, missingTDateTime);
		LocalDateTime monthOutOfBoundsDateTimeLowResult
		= (LocalDateTime) method.invoke(odtsp, monthOutOfBoundsDateTimeLow);
		LocalDateTime monthOutOfBoundsDateTimeHighResult
		= (LocalDateTime) method.invoke(odtsp, monthOutOfBoundsDateTimeHigh);
		LocalDateTime dayOutOfBoundsDateTimeLowResult
		= (LocalDateTime) method.invoke(odtsp, dayOutOfBoundsDateTimeLow);
		LocalDateTime dayOutOfBoundsDateTimeHighResult
		= (LocalDateTime) method.invoke(odtsp, dayOutOfBoundsDateTimeHigh);
		LocalDateTime hourOutOfBoundsDateTimeResult = (LocalDateTime) method.invoke(odtsp, hourOutOfBoundsDateTime);
		LocalDateTime minuteOutOfBoundsDateTimeResult = (LocalDateTime) method.invoke(odtsp, minuteOutOfBoundsDateTime);
		LocalDateTime secondOutOfBoundsDateTimeResult = (LocalDateTime) method.invoke(odtsp, secondOutOfBoundsDateTime);
		LocalDateTime notADateTimeResult = (LocalDateTime) method.invoke(odtsp, notADateTime);
		LocalDateTime notADateTimeEitherResult = (LocalDateTime) method.invoke(odtsp, notADateTimeEither);
		LocalDateTime dawnOfDateTimeResult = (LocalDateTime) method.invoke(odtsp, dawnOfDateTime);

		assertEquals(LocalDateTime.parse("2018-03-23T11:11:11"), validDateTimeResult);
		assertEquals(LocalDateTime.parse("1969-01-01T00:00:00"), before1970DateTimeResult);
		assertNull(missingZDateTimeResult);
		assertNull(missingTDateTimeResult);
		assertNull(monthOutOfBoundsDateTimeLowResult);
		assertNull(monthOutOfBoundsDateTimeHighResult);
		assertNull(dayOutOfBoundsDateTimeLowResult);
		assertNull(dayOutOfBoundsDateTimeHighResult);
		assertNull(hourOutOfBoundsDateTimeResult);
		assertNull(minuteOutOfBoundsDateTimeResult);
		assertNull(secondOutOfBoundsDateTimeResult);
		assertNull(notADateTimeResult);
		assertNull(notADateTimeEitherResult);
		assertNull(dawnOfDateTimeResult);
	}

	/**
	 * Test adding an ObservationData object twice.
	 * This should add both objects to the database, with two different counters.
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testAddValidObservationDataTwice() throws TimeoutException, InterruptedException, MemcachedException {
		ObservationData od = new ObservationData();
		od.setClusterID("testCluster:1_0-0_1");
		od.setObservationDate("2012-03-04T05:06:07Z");
		od.setSensorID(null);
		od.addDoubleObservation("test", 1.0);
		od.addDoubleObservation("marco", 2.0);
		odtsp.add(od);
		String result1 = odtsp.get(od.getClusterID(), od.getObservationDate(), "test");
		String result2 = odtsp.get(od.getClusterID(), od.getObservationDate(), "marco");
		Set<String> actualProperties = odtsp.getObservedProperties(od.getClusterID().split(":")[0]);
		HashSet<String> expectedProperties = new HashSet<>();
		expectedProperties.add("test");
		expectedProperties.add("marco");
		assertEquals("1.0", result1);
		assertEquals("2.0", result2);
		assertEquals(expectedProperties, actualProperties);
		assertEquals((Long) 0L, (Long) cli.get("testCluster:1_0-0_1"));
		odtsp.add(od);
		assertEquals((Long) 1L, (Long) cli.get("testCluster:1_0-0_1"));
		cli.delete("testCluster:1_0-0_1");
		cli.delete("testCluster:1_0-0_1|0");
		cli.delete("testCluster:1_0-0_1|1");
		cli.delete(od.getClusterID().split(":")[0]);
	}
	
	/**
	 * Test the get method with various invalid parameters.
	 * @throws TimeoutException The client timed out while trying to connect to memcached
	 * @throws InterruptedException The client was interrupted while trying to connect to memcached
	 * @throws MemcachedException Internal memcached exception
	 */
	@Test
	public void testGetInvalidParameters() throws TimeoutException, InterruptedException, MemcachedException {
		odtsp.get(null, null, null);
		odtsp.get(String.valueOf(new Random().nextLong()), "2012-12-12T12:12:12Z", "something");
		cli.set("testGetInvalidParameters", 1000, 0L);
		odtsp.get("testGetInvalidParameters", "invalidTimestamp", "something");
		odtsp.get("testGetInvalidParameters", "2012-12-12T12:12:12Z", "something");
		cli.delete("testGetInvalidParameters");
	}

	/**
	 * Test the reconnect method.
	 */
	@Test
	public void testReconnect() {
		odtsp.reconnect();
	}
	
	/**
	 * Test the addServer method.
	 */
	@Test
	public void testAddServer() {
		odtsp.addServer("192.168.56.101", 11211);
	}

}
