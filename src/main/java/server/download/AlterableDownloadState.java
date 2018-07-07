package server.download;

/**
 * Verifies for the State of a Download. Can also change it.
 */
public class AlterableDownloadState extends DownloadState {

    /**
     * Default constructor
     */
    public AlterableDownloadState() {
    }



    /**
     * Gives the FilePath associated with this DownloadID.
     * @return The FilePath of the File for the Download.
     */
    public FilePath getFilePath() {
        // TODO implement here
        return null;
    }

    /**
     * Defines the FilePath for the DownloadID.
     * @param path Is the FilePath to be set.
     */
    public void setFilePath(FilePath path) {
        // TODO implement here
    }

    /**
     * Checks if a File is Ready to be downloaded.
     * @return A boolean whether the file is downloadable or not.
     */
    public boolean isFileReadyForDownload() {
        // TODO implement here
        return false;
    }

    /**
     * Validate, that the File is ready to be downloaded.
     */
    public void setFileReadyForDownload() {
        // TODO implement here
    }

    /**
     * Save the changed Data persistently.
     */
    public void savePersistent() {
        // TODO implement here
    }

}