package edu.teco.pavos.core.properties;

import java.lang.reflect.Array;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link KafkaTopicAdmin} manages existing topics in Kafka,
 * creating new ones and deleting existing ones on demand.
 */
public final class KafkaTopicAdmin {

	private AdminClient admin;
	private static KafkaTopicAdmin instance;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Collection<TopicListing> topicListings = new ArrayList<>();

	private KafkaTopicAdmin() {
		init();
	}
	
	/**
	 * Returns the instance of this {@link KafkaTopicAdmin} or generates a new one if it does not exists.
	 * @return {@link KafkaTopicAdmin}
	 */
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
				propManager.getProperty("PAVOS_BOOTSTRAP_SERVERS_CONFIG"));
		admin = AdminClient.create(adminp);
	}
	
	/**
	 * Returns true if the specified topic already exists.
	 * @param topicNames {@link Array} of {@link String}
	 * @return exists {@link Boolean}
	 */
	public boolean existsTopic(String... topicNames) {
		Collection<String> topicNamesColl = new ArrayList<>();
		for (int i = 0; i < topicNames.length; i++) {
			topicNamesColl.add(topicNames[i]);
		}

		return existsTopic(topicNamesColl);
	}

	/**
	 * Returns true if the specified topic already exists.
	 * @param topicNames {@link Collection} of {@link String}
	 * @return exists {@link Boolean}
	 */
	public boolean existsTopic(Collection<String> topicNames) {
		Collection<TopicListing> allListings = getExistingTopics();
		Collection<TopicListing> listingsToCheck = new ArrayList<>();

		for (String topicName : topicNames) {
			listingsToCheck.add(new TopicListing(topicName, false));
		}
		return containsAllTopicListings(allListings, listingsToCheck);
	}
	
	private boolean containsAllTopicListings(Collection<TopicListing> allListings, 
			Collection<TopicListing> listingsToCheck) {
		int num = 0;
		for (TopicListing a : allListings) {
			for (TopicListing b: listingsToCheck) {
				if (a.name().equals(b.name())) {
					num++;
				}
			}
		}
		return num == listingsToCheck.size();
	}
	
	private Collection<TopicListing> getExistingTopics() {
		Thread t = new Thread(() -> {
			try {
				topicListings = admin.listTopics().listings().get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Interrupted while trying to check Kafka topics.", e);
				Thread.currentThread().interrupt();
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error("Interrupted while joining threads.", e);
			Thread.currentThread().interrupt();
		}
		return topicListings;
	}
	
	/**
	 * Deletes an existing topic and returns true if the topic is no longer
	 * present after the operation.
	 * @param topic {@link String}
	 * @return operationSuccessful {@link Boolean}
	 */
	public boolean deleteTopic(String topic) {
		if (!existsTopic(topic)) return true;

		Collection<String> topicsToRemove = new ArrayList<>();
		topicsToRemove.add(topic);
		DeleteTopicsResult result = admin.deleteTopics(topicsToRemove);

		return result.all().isDone();
	}
	
	/**
	 * Creates a new topic with the specified name.
	 * @param topic {@link String}
	 * @return operationSuccessful {@link Boolean}
	 */
	public boolean createTopic(String topic) {
		return createTopic(topic, 1, (short) 1);
	}
	
	/**
	 * Creates a new topic with the specified name, amount of partitions
	 * and replicationFactor.
	 * @param topic {@link String}
	 * @param partitions {@link Integer}
	 * @param replicationFactor {@link Short}
	 * @return operationSuccessful {@link Boolean}
	 */
	public boolean createTopic(String topic, int partitions, short replicationFactor) {
		if (existsTopic(topic)) return true;
		
		NewTopic newTopic = new NewTopic(topic, partitions, replicationFactor);
		Collection<NewTopic> newTopics = new HashSet<>();
		newTopics.add(newTopic);
		CreateTopicsResult result = admin.createTopics(newTopics);
		
		return result.all().isDone();
	}
	
}
