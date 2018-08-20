package server.core.serializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;

/**
 * User: Bill Bejeck Date: 2/14/16 Time: 3:26 PM
 */

public class JsonDeserializer<T> implements Deserializer<T> {

	private Gson gson = new Gson();
	private Class<T> deserializedClass;

	public JsonDeserializer(Class<T> deserializedClass) {
		this.deserializedClass = deserializedClass;
	}

	public JsonDeserializer() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void configure(Map<String, ?> map, boolean b) {
		if (deserializedClass == null) {
			deserializedClass = (Class<T>) map.get("serializedClass");
		}
	}

	@Override
	public T deserialize(String s, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		return gson.fromJson(new String(bytes), deserializedClass);

	}

	@Override
	public void close() {

	}
}
