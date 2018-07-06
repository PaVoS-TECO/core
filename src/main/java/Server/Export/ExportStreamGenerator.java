package Export;

import java.util.*;

/**
 * Generates a Stream for the Export by asking for one at the PaVoS Core and Subscribing to it.
 */
public class ExportStreamGenerator {

    /**
     * Default constructor
     */
    public ExportStreamGenerator() {
    }

    /**
     * Contains the Properties of an Export Request.
     */
    public ExportProperties properties;


    /**
     * Asks for a KafkaStream and subscribes to it. Then gives it through to the needed part for the export.
     * @return Is a KStream of the Data that should be exported.
     */
    public KStream createExportStream() {
        // TODO implement here
        return null;
    }

}