package Bridge;

import java.util.*;

/**
 * Convenience class which provides methods for interacting with the schema registry.
 */
public class SchemaRegistryConnector {

    /**
     * Default constructor
     */
    public SchemaRegistryConnector() {
    }

    /**
     * The URI of the schema registry.
     */
    private String schemaRegistryURI;



    /**
     * Private method performing the actual HTTP request to the schema registry and filtering out the schema.
     * Returns the schema if successful, null if not.
     * @param url The URL to which a HTTP GET request is to be sent.
     * @return A String representation of the schema acquired from the HTTP request. This value is null if there was an error performing the HTTP request or filtering out the schema from the response.
     */
    private String getSchema(URL url) {
        // TODO implement here
        return "";
    }

    /**
     * Requests the schema associated with the schema ID from the schema registry.
     * Returns the schema if successful, null if not.
     * @param id The schema id.
     * @return The schema if successful, null if not.
     */
    public String getSchemaById(int id) {
        // TODO implement here
        return "";
    }

    /**
     * Requests the latest version of the schema associated with the given subject from the schema registry.
     * Returns the schema if successful, null if not.
     * @param subject The subject of the schema.
     * @return The schema if successful, null if not.
     */
    public String getSchemaBySubject(String subject) {
        // TODO implement here
        return "";
    }

    /**
     * Requests the given version of the schema associated with the given subject from the schema registry.
     * Returns the schema if successful, null if not.
     * @param subject The subject of the schema.
     * @param version The schema version.
     * @return the schema if successful, null if not.
     */
    public String getSchemaBySubject(String subject, int version) {
        // TODO implement here
        return "";
    }

}