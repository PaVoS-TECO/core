package ExportDownloadCommunication;

import java.util.*;

/**
 * HttpServlet to  get a Dataexport request from the WebGUI.
 */
public class ExportServlet extends HttpServlet {

    /**
     * Default constructor
     */
    public ExportServlet() {
    }

    /**
     * Contains the Properties of an Export Request.
     */
    public ExportProperties properties;



    /**
     * Handles a GET request by starting the export of the desired Data. At the same time a DownloadID is sent back to the WebGUI, so that it can check for the File.
     * @param req Is the HttpServletRequest.
     * @param res Is the HttpServletResponse.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}