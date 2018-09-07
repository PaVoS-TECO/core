package server.core.properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link KafkaTopicAdmin}
 */
public class KafkaTopicAdminTest {

	/**
	 * Tests for nonexisting topics.
	 */
	@Test
	public void testExistsTopic() {
		KafkaTopicAdmin admin = KafkaTopicAdmin.getInstance();
		assertFalse(admin.existsTopic("NonExistingTopic"));
	}
	
	/**
	 * Tests creation and removal of topics.
	 */
	@Test
	public void testCreateAndDelete() {
		KafkaTopicAdmin admin = KafkaTopicAdmin.getInstance();
		String topic = "NewCreatedTopic";
		admin.deleteTopic(topic);
		assertFalse(admin.existsTopic(topic));
		admin.createTopic(topic);
		assertTrue(admin.existsTopic(topic));
		admin.deleteTopic(topic);
	}

}
