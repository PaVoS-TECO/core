package server.download;

/**
 * Verifies for the State of a Download.
 */
public class DownloadState {

    /**
     * Default constructor
     */
    public DownloadState() {
    }

    /**
     * Is an Identifier for a specific Download.
     */
    public DownloadID downloadID;




    /**
     * Gives the FilePath associated with this DownloadID.
     * @return The FilePath of the File for the Download.
     */
    public FilePath getFilePath() {
        // TODO implement here
        return null;
    }

    /**
     * Checks if a File is Ready to be downloaded.
     * @return A boolean whether the file is downloadable or not.
     */
    public boolean isFileReadyForDownload() {
        // TODO implement here
        return false;
    }

}