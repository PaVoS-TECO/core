package server.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.controller.process.ExportMergeProcess;
import server.core.controller.process.GridProcess;
import server.core.controller.process.MergeObsToFoiProcess;
import server.core.properties.GridPropertiesFileManager;
import server.core.web.WebServer;

public class Main {
	
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	
	public static void main(String[] args) throws InterruptedException {
		
		//load existing grid setup from properties file
		GridPropertiesFileManager.getInstance();
		
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
		Thread.sleep(5000);
		
		//WebServer
		new Thread(new WebServer()).start();
		
		logger.info("Finished starting routines.");
	}
}
