package server.transfer.data.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import server.core.grid.config.Seperators;
import server.core.grid.polygon.GeoPolygon;

public class GridTopicTranslator {
	
	public static final String p = ".";
	
	private GridTopicTranslator() {
		
	}
	
	public static Map<String, String> getTopic(Collection<String> sensorIDs, GeoPolygon polygon) {
		StringBuilder topicBuilder = new StringBuilder();
		String start = polygon.id;
		String[] args = start.split(Seperators.GRID_CLUSTER_SEPERATOR);
		String gridID = args[0];
		String[] clusters = args[1].split(Seperators.CLUSTER_SEPERATOR);
		
		topicBuilder.append(gridID + p);
		for (int i = 0; i < clusters.length; i++) {
			topicBuilder.append(clusters[i]);
			topicBuilder.append(p);
		}
		String base = topicBuilder.toString();
		Map<String, String> sensorTopicMap = new HashMap<>();
		for (String sensorID : sensorIDs) {
			sensorTopicMap.put(sensorID, base + sensorID);
		}
		return sensorTopicMap;
	}
	
	public static String getTopic(String sensorID, String gridClusterCombinationID) {
		StringBuilder topicBuilder = new StringBuilder();
		String[] args = gridClusterCombinationID.split(Seperators.GRID_CLUSTER_SEPERATOR);
		String gridID = args[0];
		String[] clusters = args[1].split(Seperators.CLUSTER_SEPERATOR);
		
		topicBuilder.append(gridID + p);
		for (int i = 0; i < clusters.length; i++) {
			topicBuilder.append(clusters[i]);
			topicBuilder.append(p);
		}
		String base = topicBuilder.toString();
		return base + sensorID;
	}
	
}
