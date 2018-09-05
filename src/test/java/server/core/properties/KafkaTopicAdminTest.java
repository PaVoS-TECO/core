package server.core.properties;

import static org.junit.Assert.*;

import org.junit.Test;

public class KafkaTopicAdminTest {

	@Test
	public void testExistsTopic() {
		KafkaTopicAdmin admin = KafkaTopicAdmin.getInstance();
		assertFalse(admin.existsTopic("NonExistingTopic"));
	}
	
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
