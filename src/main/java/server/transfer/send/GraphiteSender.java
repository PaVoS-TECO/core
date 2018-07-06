package server.transfer.send;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;
import DataTransferControl.SerializationDeserialization.KafkaObservationData;
import DataTransferControl.config.GraphiteConfig;
import DataTransferControl.send.conversion.GraphiteConverter;

/**
 * Reformats the data and sends it to Graphite
 */
public class GraphiteSender extends Sender {

    /**
     * Default constructor
     */
    public GraphiteSender() {
    }

    @Override
    public void send(ConsumerRecords<String, KafkaObservationData> records) {
        try (Socket socket = new Socket(GraphiteConfig.getGraphiteHostName(), GraphiteConfig.getGraphitePort()))  {
            PyList list = new PyList();

            records.forEach(record -> {
                GraphiteConverter.addPM(record, list, logger);
            });

            PyString payload = cPickle.dumps(list);
            byte[] header = ByteBuffer.allocate(4).putInt(payload.__len__()).array();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(header);
            outputStream.write(payload.toBytes());
            outputStream.flush();

        } catch (IOException e) {
            logger.error("Exception thrown writing data to graphite: " + e);
        }
    }

}