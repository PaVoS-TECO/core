package edu.teco.pavos.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.teco.pavos.core.controller.process.ExportMergeProcess;
import edu.teco.pavos.core.controller.process.GridProcess;
import edu.teco.pavos.core.controller.process.MergeObsToFoiProcess;
import edu.teco.pavos.core.web.WebServer;

/**
 * The main class controls the execution of processes in the given order.
 */
public final class Main {
	
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
	private Main() {
		
	}
	
	/**
	 * 
	 * @param args Arguments for special execution steps
	 * @throws InterruptedException The process timing could not be scheduled properly
	 */
	public static void main(String[] args) throws InterruptedException {
		mergeObsFoi();
		gridCreationAndInput();
		exportSetUp();
		
		webServerStart();
		
		logger.info("Finished starting routines.");
	}
	
	private static void mergeObsFoi() throws InterruptedException {
		MergeObsToFoiProcess foiProcess = new MergeObsToFoiProcess();
		foiProcess.kafkaStreamStart();
		Thread.sleep(5000);
	}
	
	private static void gridCreationAndInput() throws InterruptedException {
		GridProcess gridProcess = new GridProcess();
		gridProcess.kafkaStreamStart();
		Thread.sleep(5000);
	}
	
	private static void exportSetUp() throws InterruptedException {
		ExportMergeProcess exportMergeProcess = new ExportMergeProcess();
		exportMergeProcess.kafkaStreamStart();
		Thread.sleep(5000);
	}
	
	private static void webServerStart() {
		new Thread(new WebServer()).start();
	}
	
}
