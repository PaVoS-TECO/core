package server.export.communication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.download.DownloadID;

/**
 * Servlet to let the WebGUI download a finished Export.
 */
public class DownloadServlet extends HttpServlet {

    /**
     * Default constructor
     */
    public DownloadServlet() {
    }

    /**
     * Is an Identifier for a specific Download.
     */
    public DownloadID downloadID;



    /**
     * Handles a GET request by sending the desired File to the WebGUI.
     * @param req Is the HttpServletRequest.
     * @param res Is the HttpServletResponse.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}