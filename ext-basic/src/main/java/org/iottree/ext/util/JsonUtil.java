package org.iottree.ext.util;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil
{
	private JsonUtil() throws InstantiationException
	{
		throw new InstantiationException("Can't instantiate this utility class.");
	}

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(INDENT_OUTPUT);

	private static final ObjectMapper OBJECT_MAPPER_WITHOUT_IDENT = new ObjectMapper().disable(INDENT_OUTPUT);

	public static String toJson(Object object)
	{
		try
		{
			return OBJECT_MAPPER.writeValueAsString(object);
		}
		catch ( JsonProcessingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String toJsonWithoutIdent(Object object)
	{
		try
		{
			return OBJECT_MAPPER_WITHOUT_IDENT.writeValueAsString(object);
		}
		catch ( JsonProcessingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String jsonStr, Class<T> clazz)
	{
		try
		{
			return OBJECT_MAPPER.readValue(jsonStr, clazz);
		}
		catch ( JsonProcessingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String jsonStr, TypeReference<T> typeReference)
	{
		try
		{
			return OBJECT_MAPPER.readValue(jsonStr, typeReference);
		}
		catch ( JsonProcessingException e)
		{
			throw new RuntimeException(e);
		}
	}
}
