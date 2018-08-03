package server.transfer.serialization;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;

import org.apache.kafka.common.serialization.Deserializer;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationDataDeserializer;
import server.transfer.data.ObservationType;

public class ObservationDataDeserializerTests {

	private static boolean print = false;
	
	@Test
	public void deserialize_serializedObjectCheck_returnKafkaObservationData() {
		if (print) System.out.println("Running test: 'deserialization of a serializable KafkaObservationData object'");
		ObservationData data = new ObservationData();
		setupCorrectData(data);
		
		ObjectMapper mapper = new ObjectMapper();
		boolean canSerialize = mapper.canSerialize(ObservationData.class);
		if (print) System.out.println("Mapper can serialize object: " + canSerialize);
		assert(canSerialize == true);
		
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
		System.out.println("locationElevation: " + result.locationElevation);
		System.out.println("locationID: " + result.locationID);
		System.out.println("locationName: " + result.locationName);
		System.out.println("observationDate: " + result.observationDate);
		System.out.println("particulateMatter: " + result.observations.get(
				ObservationType.PARTICULATE_MATTER_PM10.toString()));
		}
		
		assert(result.locationElevation.equals(data.locationElevation)
				&& result.locationID.equals(data.locationID)
				&& result.locationName.equals(data.locationName)
				&& result.observationDate.equals(data.observationDate)
				&& result.observations.get(ObservationType.PARTICULATE_MATTER_PM10.toString())
				.equals(data.observations.get(ObservationType.PARTICULATE_MATTER_PM10.toString())));
	}
	
	private ObservationData setupCorrectData(ObservationData data) {
		return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest", new Date().toString(), "0");
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation, String locationID, String locationName, String date, String pM10) {
		data.locationElevation = locationElevation;
		data.locationID = locationID;
		data.locationName = locationName;
		data.observationDate = date;
		data.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), pM10);
		return data;
	}

}
