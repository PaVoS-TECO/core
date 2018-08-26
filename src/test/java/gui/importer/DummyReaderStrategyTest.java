package gui.importer;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class DummyReaderStrategyTest {

	@Test
	public void test() {
		DummyReaderStrategy dummy = new DummyReaderStrategy();
		File file = new File("");
		dummy.sendFileData(file);
		assert (true);
	}

}
