package server.core.controller;



import org.python.modules.thread.thread;

import server.core.controller.Process.ExportMergeProcess;
import server.core.controller.Process.GridProcess;
import server.core.controller.Process.MergeObsToFoiProcess;
import server.core.grid.GeoGrid;

import server.core.properties.KafkaTopicAdmin;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		//init of the Topics
		Initialisation initialisation = new Initialisation();
		initialisation.createPavosTopics();
		Thread.sleep(1000);
		//Merge process
		MergeObsToFoiProcess foiProcess = new MergeObsToFoiProcess();
		foiProcess.kafkaStreamStart();
		Thread.sleep(5000);
		//Grid Process
		GridProcess gridProcess = new GridProcess();
		gridProcess.kafkaStreamStart();
		Thread.sleep(10000);
		//ExportProcess
		ExportMergeProcess exportMergeProcess = new ExportMergeProcess(false);
		exportMergeProcess.kafkaStreamStart();

		
		
		System.out.println("[Main] finished successfully!");
	}
}
