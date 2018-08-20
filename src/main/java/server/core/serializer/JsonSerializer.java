package server.core.serializer;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;

public class JsonSerializer<T> implements Serializer<T> {

	private Gson gson = new Gson();

	@Override
	public void configure(Map<String, ?> map, boolean b) {

	}

	@Override
	public byte[] serialize(String topic, T t) {
		return gson.toJson(t).getBytes(Charset.forName("UTF-8"));
	}

	@Override
	public void close() {

	}
}
