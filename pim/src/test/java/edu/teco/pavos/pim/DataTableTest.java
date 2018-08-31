package edu.teco.pavos.pim;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.swing.JButton;

import org.junit.Test;

/**
 * Test of the DataTable
 * @author Jean Baumgarten
 */
public class DataTableTest {

	@Test
	public void test() {
		
		JButton a = new JButton();
		JButton b = new JButton();
		a.setEnabled(false);
		b.setEnabled(false);
		DataTable table = new DataTable(a, b);
		
		assertTrue(!a.isEnabled());
		assertTrue(!b.isEnabled());
		
		ArrayList<String> files = new ArrayList<String>();
		files.add("1");
		files.add("2");
		table.setFiles(files);
		table.setProgress("2", 50);
		

		assertTrue(table.getRowCount() == 2);
		assertTrue(((String) table.getValueAt(0, 0)).equals("1"));
		assertTrue(((String) table.getValueAt(0, 1)).equals("0 %"));
		assertTrue(((String) table.getValueAt(1, 0)).equals("2"));
		assertTrue(((String) table.getValueAt(1, 1)).equals("50 %"));

		table.setProgress("1", 100);
		table.setProgress("2", 100);
		table.sendFinished();
		
		assertTrue(!a.isEnabled());
		assertTrue(!b.isEnabled());
		
		table.sendFinished();
		
		assertTrue(a.isEnabled());
		assertTrue(b.isEnabled());
		
	}

}
