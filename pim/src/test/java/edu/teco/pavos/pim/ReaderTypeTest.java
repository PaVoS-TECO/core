package edu.teco.pavos.pim;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test of the DataTable
 * @author Jean Baumgarten
 */
public class ReaderTypeTest {

	@Test
	public void test() {

		try {
			FileReaderStrategy reader = ReaderType.getFileReaderForFileExtension("csv", "");
			assertTrue(true);
		} catch (IllegalFileExtensionException e) {
			assertTrue(false);
		}
		
		try {
			FileReaderStrategy reader = ReaderType.getFileReaderForFileExtension("pdf", "");
			assertTrue(false);
		} catch (IllegalFileExtensionException e) {
			assertTrue(true);
		}
		
	}

}
