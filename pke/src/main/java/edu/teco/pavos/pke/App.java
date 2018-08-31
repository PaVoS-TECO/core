package edu.teco.pavos.pke;

import java.util.concurrent.TimeUnit;

/**
 * Main class of the Pavos Kafka Exporter
 * @author Jean Baumgarten
 */
public final class App {
	
	private App() { }
	
	/**
	 * Main method
	 * @param args of main
	 */
    public static void main(String[] args) {
    	
    	String ext = args[0];
		String tf = args[1];
		String ops = args[2];
		String cIDs = args[3];
		String dID = args[4];
		
		ExportProperties props = new ExportProperties(ext, tf, ops, cIDs);
		FileExporter exporter = new FileExporter(props, dID);
		exporter.createFile();
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		System.exit(0);
        
    }
    
}
