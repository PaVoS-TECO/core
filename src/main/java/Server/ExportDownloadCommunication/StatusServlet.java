package server.exportDownloadCommunication;

import java.util.*;

/**
 * Servlet to let the WebGUI check if a Download is ready.
 */
public class StatusServlet extends HttpServlet {

    /**
     * Default constructor
     */
    public StatusServlet() {
    }

    /**
     * Is an Identifier for a specific Download.
     */
    public DownloadID downloadID;



    /**
     * Handles a GET request by checking the availability of the desired download.
     * @param req Is the HttpServletRequest.
     * @param res Is the HttpServletResponse.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}