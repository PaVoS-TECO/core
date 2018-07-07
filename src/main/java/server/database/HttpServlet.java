package server.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An abstract HTTPServlet.
 */
public class HttpServlet {

    /**
     * Default constructor
     */
    public HttpServlet() {
    }

    /**
     * Called by the server (via the service method) to allow a servlet to handle a GET request.
     * @param req An HttpServletRequest object that contains the request the client has made of the servlet.
     * @param res An HttpServletResponse object that contains the response the servlet sends to the client.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}