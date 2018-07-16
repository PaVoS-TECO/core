package server.download;

/**
 * Verifies for the State of a Download.
 */
public class DownloadState {

    /**
     * Is an Identifier for a specific Download.
     */
    private DownloadID downloadID;
    private File filePath;
    private boolean ready;

    /**
     * Default constructor
     */
    public DownloadState(DownloadID id) {
        this.downloadID = id;
        // Set filePath and ready
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
    public boolean isFileReadyForDownload() {
        return this.ready;
    }

}
