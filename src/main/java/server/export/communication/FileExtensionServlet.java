package server.export.communication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet, to let the WebGUI ask for the available FileExtensions for the Export.
 */
public class FileExtensionServlet extends HttpServlet {

    /**
     * Default constructor
     */
    public FileExtensionServlet() {
    }


    /**
     * Handles a GET request by sending Information about the available FileExtensions.
     * @param req Is the HttpServletRequest.
     * @param res Is the HttpServletResponse.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}