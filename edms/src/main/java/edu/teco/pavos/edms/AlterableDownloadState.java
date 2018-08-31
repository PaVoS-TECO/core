package edu.teco.pavos.edms;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Verifies for the State of a Download. Can also change it.
 */
public class AlterableDownloadState extends DownloadState {

    /**
     * Default constructor
     * @param id is the ID of that download
     */
    public AlterableDownloadState(String id) {
    	
        super(id);
        
    }

    /**
     * Defines the FilePath for the DownloadID.
     * @param path Is the FilePath to be set.
     */
    public void setFilePath(File path) {
    	
        super.filePath = path;
        
    }

    /**
     * Validate, that the File is ready to be downloaded.
     */
    public void setFileReadyForDownload() {
    	
        super.ready = "true";
        
    }

    /**
     * Validate, that the File is ready to be downloaded.
     */
    public void setFilePreparingForDownload() {
    	
        super.ready = "false";
        
    }
    
    /**
     * Validate, that the File had an error and can not be downloaded.
     */
    public void setFileHadError() {
    	
    	super.ready = "error";
    	
    }

    /**
     * Save the changed Data persistently.
     */
    public void savePersistent() {
    	
    	Preferences prefs = Preferences.userRoot().node(super.save);
    	prefs.putBoolean(super.downloadID, true);
    	prefs.put(super.downloadID + "/Path", super.filePath.getAbsolutePath());
    	prefs.put(super.downloadID + "/Ready", super.ready);

    }
    

}
