package com.realestate.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created on 02/08/2015
 * Description:
 * static thread safe jackson object ObjectMapper,
 * http://stackoverflow.com/a/3909846
 * DeserializationFeature
 * http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/DeserializationFeature.html
 */
public class JacksonObjectMapper {
	private static ObjectMapper objectMapper = null;
	public static synchronized ObjectMapper getInstance() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		//DeserializationFeatures
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		}
		return objectMapper;
	}
}
