package edu.teco.pavos.pim;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * Test of the FileImporter
 * @author Jean Baumgarten
 */
public class FileImporterTest {

	@Test
	public void test() {
		
		JButton a = new JButton();
		JButton b = new JButton();
		FileImporter imp = new FileImporter("", new DataTable(a, b), "");
		String data1 = "somwthing.csv";
		String data2 = "somwthing/more/complex.csv";
		
		Method method;
		try {
			
			method = FileImporter.class.getDeclaredMethod("getFileExtension", String.class);
			method.setAccessible(true);
			String ext1 = (String) method.invoke(imp, new Object[] { data1 });
			String ext2 = (String) method.invoke(imp, new Object[] { data2 });
			
			assertTrue(ext1.equals("csv"));
			assertTrue(ext2.equals("csv"));
			
		} catch (NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (SecurityException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalAccessException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (InvocationTargetException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		}
		
	}

}
