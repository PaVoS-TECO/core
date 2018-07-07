package server.export.communication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides an abstract class to be subclassed to create an HTTP servlet suitable for a Web site. (javax.servlet.http.HttpServlet)
 */
public class HttpServlet {

    /**
     * Default constructor
     */
    public HttpServlet() {
    }

    /**
     * Called by the server (via the service method) to allow a servlet to handle a GET request.
     * @param req Is the HttpServletRequest.
     * @param res Is the HttpServletResponse.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}