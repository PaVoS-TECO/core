package server.transfer.serialization;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.apache.kafka.common.serialization.Deserializer;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationDataDeserializer;
import server.transfer.data.ObservationType;
import server.transfer.sender.util.TimeUtil;

/**
 * Tests {@link ObservationDataDeserializer}
 */
public class ObservationDataDeserializerTest {

	private static boolean print = true;
	
	/**
	 * Tests deserializing an {@link ObservationData} object in serialized form.
	 */
	@Test
	public void deserializeSerializedObjectCheckReturnKafkaObservationData() {
		if (print) System.out.println("Running test: 'deserialization of a serializable KafkaObservationData object'");
		ObservationData data = new ObservationData();
		setupCorrectData(data);
		
		ObjectMapper mapper = new ObjectMapper();
		boolean canSerialize = mapper.canSerialize(ObservationData.class);
		if (print) System.out.println("Mapper can serialize object: " + canSerialize);
		assertTrue(canSerialize);
		
		String sData = null;
		byte[] bData = null;
		try {
			sData = mapper.writeValueAsString(data);
			bData = mapper.writeValueAsBytes(data);
		} catch (JsonProcessingException e) {
			fail("JsonProcessingException thrown");
		}
		if (print) {
			System.out.println("Serialized data as String: " + sData);
			System.out.println("Serialized data as byte array: " + bData);
		}
		
		ObservationData result = null;
		Deserializer<ObservationData> des = new ObservationDataDeserializer();
		des.configure(new HashMap<String, ObservationData>(), false);
		result = des.deserialize("deserializationTest", bData);
		des.close();
		
		if (print) {
		System.out.println("\nThe mapping-result is shown below:");
		System.out.println("observationDate: " + result.observationDate);
		System.out.println("particulateMatter: " + result.observations.get(
				ObservationType.PARTICULATE_MATTER_PM10.toString()));
		}
		
		assert (result.observationDate.equals(data.observationDate)
				&& result.observations.get(ObservationType.PARTICULATE_MATTER_PM10.toString())
				.equals(data.observations.get(ObservationType.PARTICULATE_MATTER_PM10.toString())));
	}
	
	private ObservationData setupCorrectData(ObservationData data) {
		return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29",
				"Mt.Everest", TimeUtil.getUTCDateTimeNowString(), "0");
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation,
			String locationID, String locationName, String date, String pM10) {
		data.observationDate = date;
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		return data;
	}

}
