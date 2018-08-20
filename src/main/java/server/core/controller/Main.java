package server.core.controller;

import server.core.properties.KafkaTopicAdmin;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		KafkaTopicAdmin.getInstance();
		GraphitePClass pc = new GraphitePClass("test");
		pc.start();
		System.out.println("[Main] finished successfully!");
	}
}
