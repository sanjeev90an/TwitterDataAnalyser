package mmds.twitter.analyser.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

	private static ObjectMapper objectMapper = new ObjectMapper();

	{
		objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
	}

	public static String getJsonStringForObject(Object object) throws Exception {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			throw new Exception(e);
		}
	}

	public static <T> T parseJSON(String jsonString, Class<T> clazz) throws Exception {
		try {
			return objectMapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			throw new Exception(e);
		}

	}

}