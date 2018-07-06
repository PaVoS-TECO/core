package server.databaseConnection;

import java.util.*;

/**
 * An HTTPServlet for requesting a list of sensors.
 */
public class SensorListServlet extends HttpServlet {

    /**
     * Default constructor
     */
    public SensorListServlet() {
    }


    /**
     * This method calls the getSensors method of the Facade to get a list of Sensors that are in a certain cluster.
     * @param req An HttpServletRequest object that contains the request the client has made of the servlet.
     * @param res An HttpServletResponse object that contains the response the servlet sends to the client.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        // TODO implement here
    }

}