/**
 * 
 */
package edu.teco.pavos.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.teco.pavos.transfer.data.ObservationData;
import edu.teco.pavos.transfer.sender.util.TimeUtil;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * Tests the {@link ObservationDataToStorageProcessor} class.
 * @author Oliver
 *
 */
public class ObservationDataToStorageProcessorTest {
	
	@Autowired
	private ObservationDataToStorageProcessor processor;
	
	private MemcachedClient memcachedClient;
	
	/**
	 * Set up a direct client to the database and an {@link ObservationDataToStorageProcessor} object.
	 * @throws Exception The {@link XMemcachedClient} could not be established
	 */
	@Before
	public void setUpBeforeTest() throws Exception {
		memcachedClient = mock(MemcachedClient.class);
		
		processor = new ObservationDataToStorageProcessor(memcachedClient);
	}
	
	/**
	 * Test the add() method.
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testAdd() throws TimeoutException, InterruptedException, MemcachedException {
		when(memcachedClient.get("testGrid-1_1_1:0_0")).thenReturn(0L);
		
		ObservationData od = new ObservationData();
		od.setClusterID("testGrid-1_1_1:0_0");
		od.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		od.setSensorID("testSensor");
		od.addSingleObservation("temperature_celsius", 17.2);
		od.addSingleObservation("pM_10", 2.5);
		processor.add(od);
	}
	
	/**
	 * Test the get() method.
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testGet() throws TimeoutException, InterruptedException, MemcachedException {
		ObservationData od = new ObservationData();
		od.setClusterID("testGrid-1_1_1:0_0");
		od.setObservationDate(TimeUtil.getUTCDateTimeNowString());
		od.setSensorID("testSensor");
		String observationType = "temperature_celsius";
		od.addSingleObservation("temperature_celsius", 17.2);
		
		// No Exception
		when(memcachedClient.get(od.getClusterID())).thenReturn(0L);
		when(memcachedClient.get(od.getClusterID() + "|0")).thenReturn(od);
		processor.get(od.getClusterID(), od.getObservationDate(), observationType);
		
		// ClassCastException
		when(memcachedClient.get(od.getClusterID() + "|0")).thenThrow(ClassCastException.class);
		processor.get(od.getClusterID(), od.getObservationDate(), observationType);
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
		od.addSingleObservation("test", 1.0);
		od.addSingleObservation("marco", 2.0);
		processor.add(od);
		assertNull(memcachedClient.get("a|b"));
		assertNull(memcachedClient.get("a|b|0"));
		memcachedClient.delete("a|b");
		memcachedClient.delete("a|b|0");
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
		
		LocalDateTime validDateTimeResult = (LocalDateTime) method.invoke(processor, validDateTime);
		LocalDateTime before1970DateTimeResult = (LocalDateTime) method.invoke(processor, before1970DateTime);
		LocalDateTime missingZDateTimeResult = (LocalDateTime) method.invoke(processor, missingZDateTime);
		LocalDateTime missingTDateTimeResult = (LocalDateTime) method.invoke(processor, missingTDateTime);
		LocalDateTime monthOutOfBoundsDateTimeLowResult
		= (LocalDateTime) method.invoke(processor, monthOutOfBoundsDateTimeLow);
		LocalDateTime monthOutOfBoundsDateTimeHighResult
		= (LocalDateTime) method.invoke(processor, monthOutOfBoundsDateTimeHigh);
		LocalDateTime dayOutOfBoundsDateTimeLowResult
		= (LocalDateTime) method.invoke(processor, dayOutOfBoundsDateTimeLow);
		LocalDateTime dayOutOfBoundsDateTimeHighResult
		= (LocalDateTime) method.invoke(processor, dayOutOfBoundsDateTimeHigh);
		LocalDateTime hourOutOfBoundsDateTimeResult = (LocalDateTime) method.invoke(processor, hourOutOfBoundsDateTime);
		LocalDateTime minuteOutOfBoundsDateTimeResult =
				(LocalDateTime) method.invoke(processor, minuteOutOfBoundsDateTime);
		LocalDateTime secondOutOfBoundsDateTimeResult =
				(LocalDateTime) method.invoke(processor, secondOutOfBoundsDateTime);
		LocalDateTime notADateTimeResult = (LocalDateTime) method.invoke(processor, notADateTime);
		LocalDateTime notADateTimeEitherResult = (LocalDateTime) method.invoke(processor, notADateTimeEither);
		LocalDateTime dawnOfDateTimeResult = (LocalDateTime) method.invoke(processor, dawnOfDateTime);

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
		String gridID = "testGrid";
		String clusterID = gridID + ":1_0-0_1";
		od.setClusterID(clusterID);
		od.setObservationDate("2012-03-04T05:06:07Z");
		od.setSensorID(null);
		od.addSingleObservation("test", 1.0);
		od.addSingleObservation("marco", 2.0);
		
		when(memcachedClient.get(od.getClusterID())).thenReturn(0L);
		processor.add(od);
		
		when(memcachedClient.get(od.getClusterID() + "|0")).thenReturn(od);
		String result1 = processor.get(od.getClusterID(), od.getObservationDate(), "test");
		String result2 = processor.get(od.getClusterID(), od.getObservationDate(), "marco");
		
		HashSet<String> expectedProperties = new HashSet<>();
		expectedProperties.add("test");
		expectedProperties.add("marco");
		
		when(memcachedClient.get(gridID)).thenReturn(expectedProperties);
		Set<String> actualProperties = processor.getObservedProperties(od.getClusterID().split(":")[0]);
		
		assertEquals("[1.0]", result1);
		assertEquals("[2.0]", result2);
		assertEquals(expectedProperties, actualProperties);
		
		assertEquals((Long) 0L, (Long) memcachedClient.get(clusterID));
		
		processor.add(od);
		
		when(memcachedClient.get(od.getClusterID())).thenReturn(1L);
		assertEquals((Long) 1L, (Long) memcachedClient.get(clusterID));
	}
	
	/**
	 * Test the get method with various invalid parameters.
	 * @throws TimeoutException The client timed out while trying to connect to memcached
	 * @throws InterruptedException The client was interrupted while trying to connect to memcached
	 * @throws MemcachedException Internal memcached exception
	 */
	@Test
	public void testGetInvalidParameters() throws TimeoutException, InterruptedException, MemcachedException {
		processor.get(null, null, null);
		processor.get(String.valueOf(new Random().nextLong()), "2012-12-12T12:12:12Z", "something");
		memcachedClient.set("testGetInvalidParameters", 1000, 0L);
		processor.get("testGetInvalidParameters", "invalidTimestamp", "something");
		processor.get("testGetInvalidParameters", "2012-12-12T12:12:12Z", "something");
		memcachedClient.delete("testGetInvalidParameters");
	}

	/**
	 * Test the reconnect method.
	 */
	@Test
	public void testReconnect() {
		processor.reconnect();
	}
	
	/**
	 * Test the addServer method.
	 */
	@Test
	public void testAddServer() {
		processor.addServer("192.168.56.101", 11211);
	}

}
