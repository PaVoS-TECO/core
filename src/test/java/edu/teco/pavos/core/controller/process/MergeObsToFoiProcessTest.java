/**
 * 
 */
package edu.teco.pavos.core.controller.process;

import static org.junit.Assert.fail;

import org.junit.Test;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * Tests {@link GridProcess}.
 */
public class MergeObsToFoiProcessTest {
	
	/**
	 * Tests the core functionality.
	 */
	@Test
	public void test() {
		MergeObsToFoiProcess process = new MergeObsToFoiProcess(
				"ObservationsTest", "FeaturesOfInterestTest", "ObservationsMergeGenericTest", "FeatureOfInterest");
		process.kafkaStreamStart();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		process.kafkaStreamClose();
	}

}
