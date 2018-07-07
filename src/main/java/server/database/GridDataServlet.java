package server.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An HTTPServlet for requesting Grid data.
 */
public class GridDataServlet extends HttpServlet {

    /**
     * Default constructor
     */
    public GridDataServlet() {
    }


    /**
     * This method calls the getGrid method of the Facade to get a Grid of clusters at a certain ZoomLevel and Time . This saves the Grid into res.
     * @param req An HttpServletRequest object that contains the request the client has made of the servlet.
     * @param res An HttpServletResponse object that contains the response the servlet sends to the client.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}