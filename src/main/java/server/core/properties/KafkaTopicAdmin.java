package server.core.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;

public final class KafkaTopicAdmin {

	private AdminClient admin;
	private static KafkaTopicAdmin instance;

	private KafkaTopicAdmin() {
		init();
	}
	
	public static KafkaTopicAdmin getInstance() {
		if (instance == null) {
			instance = new KafkaTopicAdmin();
		}
		return instance;
	}

	private void init() {
		Properties adminp = new Properties();
		KafkaPropertiesFileManager propManager = KafkaPropertiesFileManager.getInstance();
		
		adminp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
				propManager.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		admin = AdminClient.create(adminp);
	}

	public boolean existsTopic(String topicName) {
		Collection<String> topicNames = new ArrayList<>();
		topicNames.add(topicName);

		return existsTopic(topicNames);
	}

	public boolean existsTopic(String topicName1, String topicName2) {
		Collection<String> topicNames = new ArrayList<>();
		topicNames.add(topicName1);
		topicNames.add(topicName2);

		return existsTopic(topicNames);
	}

	public boolean existsTopic(Collection<String> topicNames) {
		Collection<TopicListing> allListings = getExistingTopics();
		Collection<TopicListing> listingsToCheck = new ArrayList<TopicListing>();

		for (String topicName : topicNames) {
			listingsToCheck.add(new TopicListing(topicName, false));
		}
		if (!containsAllTopicListings(allListings, listingsToCheck)) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean containsAllTopicListings(Collection<TopicListing> allListings, Collection<TopicListing> listingsToCheck) {
		int num = 0;
		for (TopicListing a : allListings) {
			for (TopicListing b: listingsToCheck) {
				if (a.name().equals(b.name())) {
					num++;
				}
			}
		}
		if (num == listingsToCheck.size()) return true;
		return false;
	}
	
	private Collection<TopicListing> getExistingTopics() {
		Collection<TopicListing> topicListings = new ArrayList<>();
		try {
			topicListings = admin.listTopics().listings().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return topicListings;
	}

	public boolean deleteTopic(String topic) {
		Collection<TopicListing> topicListings = getExistingTopics();
		TopicListing tl = new TopicListing(topic, false);
		if (!topicListings.contains(tl)) return true;

		Collection<String> topicsToRemove = new ArrayList<String>();
		topicsToRemove.add(topic);
		DeleteTopicsResult result = admin.deleteTopics(topicsToRemove);

		return result.all().isDone();
	}
	
	public boolean createTopic(String topic) {
		return createTopic(topic, 1, (short) 1);
	}
	
	public boolean createTopic(String topic, int partitions, short replicationFactor) {
		if (existsTopic(topic)) return true;
		
		NewTopic newTopic = new NewTopic(topic, partitions, replicationFactor);
		Collection<NewTopic> newTopics = new HashSet<>();
		newTopics.add(newTopic);
		CreateTopicsResult result = admin.createTopics(newTopics);
		
		return result.all().isDone();
	}
	
}
