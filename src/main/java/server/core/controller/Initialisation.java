package server.core.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.core.properties.KafkaTopicAdmin;

/**
 * @author Patrick
 *This is a Initalasation Class for the Core
 */
public class Initialisation {
	final String[] topics = {"Things"
							,"Datastreams"
							,"Locations"
							,"HistoricalLocations"
							,"Sensors"
							,"ObservedProperties"
							,"FeaturesOfInterest"
							,"Observations"};
	
	/**
	 * Create all necesaary topics for the Kafka Prozesses
	 */
	public void createPavosTopics() {
		
		List<String> listTopics = new ArrayList<String>(Arrays.asList(topics));
		
		KafkaTopicAdmin admin = KafkaTopicAdmin.getInstance();
		if(admin.existsTopic(listTopics)) {
			System.out.println("All necessary topics exist!");
		}else {
			for (int i = 0; i < topics.length; i++) {
				if(!admin.existsTopic(topics[i])) {
					if(!admin.createTopic(topics[i])) {
						System.out.println("successfully created" + topics[i]);
					}else {
						System.out.println("Error while creating "+ topics[i]);
					}
				}
			}
			
		}
		
		
		
		
	}

}
