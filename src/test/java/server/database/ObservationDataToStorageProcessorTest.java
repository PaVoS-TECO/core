/**
 * 
 */
package server.database;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import server.transfer.data.ObservationData;

/**
 * @author Oliver
 *
 */
public class ObservationDataToStorageProcessorTest {

	public static ObservationDataToStorageProcessor odtsp;
	public static MemcachedClient cli;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cli = new XMemcachedClient("192.168.56.101", 11211);
		assumeTrue(!cli.isShutdown());
		odtsp = new ObservationDataToStorageProcessor("192.168.56.101", 11211);
		assumeTrue(odtsp.isConnected());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Test
	public void testUnreachableClient() {
		new ObservationDataToStorageProcessor("0.0.0.0", 11211).get("key", "", "");
	}
	
	@Test
	public void testInvalidClusterId() throws TimeoutException, InterruptedException, MemcachedException {
		ObservationData od = new ObservationData();
		od.clusterID = "a|b";
		od.observationDate = "2012-03-04T05:06:07Z";
		od.sensorID = null;
		od.observations = new HashMap<String, String>();
		od.observations.put("test", "good");
		od.observations.put("marco", "polo");
		odtsp.add(od);
		assertNull(cli.get("a|b"));
		assertNull(cli.get("a|b|0"));
		cli.delete("a|b");
		cli.delete("a|b|0");
	}
	
	@Test
	public void testGetTime() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
		LocalDateTime monthOutOfBoundsDateTimeLowResult = (LocalDateTime) method.invoke(odtsp, monthOutOfBoundsDateTimeLow);
		LocalDateTime monthOutOfBoundsDateTimeHighResult = (LocalDateTime) method.invoke(odtsp, monthOutOfBoundsDateTimeHigh);
		LocalDateTime dayOutOfBoundsDateTimeLowResult = (LocalDateTime) method.invoke(odtsp, dayOutOfBoundsDateTimeLow);
		LocalDateTime dayOutOfBoundsDateTimeHighResult = (LocalDateTime) method.invoke(odtsp, dayOutOfBoundsDateTimeHigh);
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
	 * Test method for {@link server.database.ObservationDataToStorageProcessor#add(server.transfer.data.ObservationData)}.
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testAddValidObservationDataTwice() throws TimeoutException, InterruptedException, MemcachedException {
		ObservationData od = new ObservationData();
		od.clusterID = "testCluster:1_0-0_1";
		od.observationDate = "2012-03-04T05:06:07Z";
		od.sensorID = null;
		od.observations = new HashMap<String, String>();
		od.observations.put("test", "good");
		od.observations.put("marco", "polo");
		odtsp.add(od);
		String result1 = odtsp.get(od.clusterID, od.observationDate, "test");
		String result2 = odtsp.get(od.clusterID, od.observationDate, "marco");
		Set<String> actualProperties = odtsp.getObservedProperties(od.clusterID.split(":")[0]);
		HashSet<String> expectedProperties = new HashSet<>();
		expectedProperties.add("test");
		expectedProperties.add("marco");
		assertEquals("good", result1);
		assertEquals("polo", result2);
		assertEquals(expectedProperties, actualProperties);
		assertEquals((Long) 0L, (Long) cli.get("testCluster:1_0-0_1"));
		odtsp.add(od);
		assertEquals((Long) 1L, (Long) cli.get("testCluster:1_0-0_1"));
		cli.delete("testCluster:1_0-0_1");
		cli.delete("testCluster:1_0-0_1|0");
		cli.delete("testCluster:1_0-0_1|1");
		cli.delete(od.clusterID.split(":")[0]);
	}
	
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
	 * Test method for {@link server.database.ObservationDataToStorageProcessor#get(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGet() {
		
	}

}
