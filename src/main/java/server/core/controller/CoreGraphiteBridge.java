package server.core.controller;

import server.transfer.Destination;
import server.transfer.TransferManager;

public final class CoreGraphiteBridge {
	
	private static final String KAFKA_TOPIC = "KafkaToGraphite";
	private static final String GRAPHITE_TOPIC = "ValuesFromKafka";
	
	public static void run() {
		
		TransferManager tm = new TransferManager();
		tm.startDataTransfer(KAFKA_TOPIC, GRAPHITE_TOPIC, Destination.GRAPHITE);
		
		//TODO Shutdown of TransferManager before System.exit
		
		tm.stopDataTransfer();
		
	}
	
}
