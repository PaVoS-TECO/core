package edu.teco.pavos.pke;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Verifies for the State of a Download.
 * @author Jean Baumgarten
 */
public class DownloadState {

	protected String dirPath;
	protected String infoPath;
    protected String downloadID;
    protected File filePath;
    protected String ready;
    protected String save = "pavos-export-files";

    /**
     * Default constructor
     * @param id of the Download
     */
    public DownloadState(String id) {
    	
    	this.downloadID = id;
    	
    	Preferences prefs = Preferences.userRoot().node(this.save);
    	boolean exists = prefs.getBoolean(this.downloadID, false);
    	
    	if (exists) {
    		
    		String path = prefs.get(this.downloadID + "/Path", "");
    		String ready = prefs.get(this.downloadID + "/Ready", "false");
    		this.filePath = new File(path);
    		this.ready = ready;
    		
    	} else {
    		
    		this.ready = "noID";
    		this.filePath = new File("/usr/pke/ext.txt");
    		
    	}
    	
    }
    
    /**
     * Gives the String ID associated with this DownloadID.
     * @return The String ID of the File for the Download.
     */
    public String getID() {
    	
    	return this.downloadID;
    	
    }

    /**
     * Gives the FilePath associated with this DownloadID.
     * @return The FilePath of the File for the Download.
     */
    public File getFilePath() {
    	
        return this.filePath;
        
    }

    /**
     * Checks if a File is Ready to be downloaded.
     * @return A boolean whether the file is downloadable or not.
     */
    public String getDownloadState() {
    	
        return this.ready;
        
    }

}
