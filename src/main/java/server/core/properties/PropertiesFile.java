package server.core.properties;

public class PropertiesFile implements PropertiesFileInterface {

	public PropertiesFile() {
	}

	private String propertiesFileName;

	@Override
	public String getValue(String propertyName) {
		// TODO implement here
		return "";
	}

	@Override
	public boolean putProperty(String propertyName, String propertyValue) {
		// TODO implement here
		return false;
	}

	@Override
	public boolean save(boolean makeBackup) {
		// TODO implement here
		return false;
	}

}
