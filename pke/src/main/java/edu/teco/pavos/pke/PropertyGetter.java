package edu.teco.pavos.pke;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Getter cLASS FOR PORPERTIES STORED IN A PROPERTIES FILE
 * @author Jean Baumgarten
 */
public class PropertyGetter {
	
	private Properties properties;

	/**
	 * Default Constructor
	 */
	public PropertyGetter() {
		
		this.properties = new Properties();
		BufferedInputStream stream;
		try {
			
			stream = new BufferedInputStream(new FileInputStream("/usr/pke/export.properties"));
			properties.load(stream);
			stream.close();
			
		} catch (FileNotFoundException e) {
			
			System.out.println(e.getLocalizedMessage());
			
		} catch (IOException e) {

			System.out.println(e.getLocalizedMessage());
			
		}
		
	}
	
	/**
	 * Getter for the porperties stored in the file
	 * @param key of the property
	 * @return value of the requested property
	 */
	public String getProperty(String key) {
		
		return this.properties.getProperty(key);
		
	}

}
