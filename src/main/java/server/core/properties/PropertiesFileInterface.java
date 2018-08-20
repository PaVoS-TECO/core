package server.core.properties;

/**
 * The PropertyFileInterface is a special form of associative memory in which
 * key-value pairs are always of type string. Since the entries can be stored in
 * a file and read out again, hardwired character strings can be externalized
 * from the program text so that the values ​​can be easily changed without
 * retranslation.
 */
public interface PropertiesFileInterface {

	/**
	 * This method returns the requested property value
	 * 
	 * @param propertyName propertyName is the name of the requested property
	 *
	 * @return Return the value to the requested property
	 */
	public String getValue(String propertyName);

	/**
	 * The Method adds a key-value pair to the Properties object. To get back to the
	 * value later, is called with the key and then return
	 * 
	 * @param propertyName  propertyName is the Name of the Property which you want
	 *                      to edit
	 * @param propertyValue propertyValue is the Value of the Property which you
	 *                      want to edit
	 * @return true wenn the property got set false otherwise
	 */
	public boolean putProperty(String propertyName, String propertyValue);

	/**
	 * This Method saves the PropertiesFile with the Option to do a Backup of the
	 * File
	 * 
	 * @param makeBackup true if you want to make a Bachup
	 * @return true when the file got saved, false otherwise
	 */
	public boolean save(boolean makeBackup);

}
