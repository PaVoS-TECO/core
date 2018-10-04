/**
 * 
 */
package edu.teco.pavos.core.controller.process;

import static org.junit.Assert.fail;

import org.junit.Test;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * Tests {@link MergeObsToFoiProcess}.
 */
public class MergeObsToFoiProcessTest {
	
	private static final String TOPIC_OBSERVATIONS = "ObservationsTest";
	private static final String TOPIC_FEATURE_OF_INTEREST = "FeaturesOfInterestTest";
	private static final String TOPIC_MERGE = "ObservationsMergeGenericTest";
	
	/**
	 * Tests the core functionality.
	 */
	@Test
	public void test() {
		MergeObsToFoiProcess process = new MergeObsToFoiProcess(
				TOPIC_OBSERVATIONS, TOPIC_FEATURE_OF_INTEREST, TOPIC_MERGE, "FeatureOfInterest");
		process.start();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		process.stop();
	}

}
