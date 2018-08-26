package edu.teco.pavos.importer;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

/**
 * Testcase for the FrostSender
 */
public class FrostSenderTest {
	
	@Test
	public void testDataTransfert() {
		String id = "pavos.teco.edu/ObservedProperties/TestProperty";
		String observedProperty = "{\"name\": \"TestProperty\", \"description\":  \"TestProperty"
				+ "\", \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#Acceleration\","
				+ "\"@iot.id\": \"" + id + "\"}";
		String url = "http://pavos-master.teco.edu/FROST-Server/v1.0/ObservedProperties";
		FrostSender.sendToFrostServer(url, observedProperty);
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		try {
			URL rurl = new URL(url);
			HttpURLConnection http = (HttpURLConnection) rurl.openConnection();
			http.setRequestMethod("GET");
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("Content-Encoding", "charset=UTF-8");
			http.setRequestProperty("Accept", "application/json");
			http.connect();
			try {
			    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
			    String inputLine;
			    boolean found = false;

			    while ((inputLine = in.readLine()) != null) {
			    	if (inputLine.trim().startsWith("\"@iot.id\"")) {
			    		String[] cont = inputLine.split("\"");
			    		if (cont.length >= 4) {
			    			if (cont[3].equals(id)) {
			    				found = true;
			    			}
			    		}
			    	}
			    }
			    in.close();
			    assertTrue(found);
			} catch (IOException e) {
				System.out.println(e.getLocalizedMessage());
				assertTrue(false);
			}
		} catch (MalformedURLException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (ProtocolException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		}
	}
	
	@After
	public void cleanFrost() {
		String id = "pavos.teco.edu/ObservedProperties/TestProperty";
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		try {
			String surl = "http://pavos-master.teco.edu/FROST-Server/v1.0/ObservedProperties('" + id + "')";
			URL url = new URL(surl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("DELETE");
            http.setDoInput(true);
            http.setDoOutput(true);
			http.connect();
			try {
                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();
                System.out.println(response.toString());
            } catch (IOException e) {
            	System.out.println(e.getLocalizedMessage());
            }
		} catch (MalformedURLException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (ProtocolException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
}
