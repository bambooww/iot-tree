package org.iottree.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsonSchemaGenerator
{

	private static final ObjectMapper mapper = new ObjectMapper();

	public static String generateSchema(String jsonString,List<String> required) throws Exception
	{
		JsonNode jsonNode = mapper.readTree(jsonString);

		ObjectNode schema = mapper.createObjectNode();
		//chema.put("$schema", "http://json-schema.org/draft-07/schema#");
		//schema.put("$id", "https://example.com/schema.json");
		//schema.put("title", "Generated Schema");
		//schema.put("description", "Auto-generated from JSON");

		// 根据JSON类型生成Schema
		if (jsonNode.isObject())
		{
			generateObjectSchema(schema, jsonNode,required);
		}
		else if (jsonNode.isArray())
		{
			generateArraySchema(schema, jsonNode);
		}
		else
		{
			generatePrimitiveSchema(schema, jsonNode);
		}

		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
	}

	private static void generateObjectSchema(ObjectNode schema, JsonNode objectNode,List<String> required_ns)
	{
		schema.put("type", "object");

		ObjectNode properties = mapper.createObjectNode();
		ArrayNode required = mapper.createArrayNode();

		for(String n:required_ns)
			required.add(n) ;
		
		objectNode.fields().forEachRemaining(entry -> {
			String key = entry.getKey();
			JsonNode value = entry.getValue();

			// 每个属性生成对应的Schema
			properties.set(key, generatePropertySchema(value));
			// 假设所有属性都是必需的
			//required.add(key);
		});

		schema.set("properties", properties);
		schema.set("required", required);

		//schema.put("additionalProperties", false);
	}

	private static JsonNode generatePropertySchema(JsonNode value)
	{
		ObjectNode propertySchema = mapper.createObjectNode();

		if (value.isObject())
		{
			// 嵌套对象
			propertySchema.put("type", "object");
			ObjectNode nestedProperties = mapper.createObjectNode();

			value.fields().forEachRemaining(entry -> {
				nestedProperties.set(entry.getKey(), generatePropertySchema(entry.getValue()));
			});

			propertySchema.set("properties", nestedProperties);

		}
		else if (value.isArray())
		{
			// 数组
			propertySchema.put("type", "array");

			if (value.size() > 0)
			{
				// 分析数组元素的类型
				Set<String> itemTypes = new HashSet<>();
				for (JsonNode item : value)
				{
					itemTypes.add(getJsonType(item));
				}

				if (itemTypes.size() == 1)
				{
					// 单一类型
					String type = itemTypes.iterator().next();
					if (!"null".equals(type))
					{
						propertySchema.set("items", createTypeSchema(type));
					}
				}
				else
				{
					// 多种类型，使用anyOf
					ArrayNode anyOf = mapper.createArrayNode();
					itemTypes.forEach(type -> anyOf.add(createTypeSchema(type)));
					propertySchema.set("items", mapper.createObjectNode().set("anyOf", anyOf));
				}
			}

		}
		else
		{
			// 基本类型
			String type = getJsonType(value);
			propertySchema.put("type", type);

			// 添加格式约束
			if ("string".equals(type) && value.asText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
			{
				propertySchema.put("format", "email");
			}
			else if ("string".equals(type) && value.asText().matches("^\\d{4}-\\d{2}-\\d{2}$"))
			{
				propertySchema.put("format", "date");
			}
		}

		return propertySchema;
	}

	private static void generateArraySchema(ObjectNode schema, JsonNode arrayNode)
	{
		schema.put("type", "array");

		if (arrayNode.size() > 0)
		{
			schema.set("items", generatePropertySchema(arrayNode.get(0)));
		}

		schema.put("minItems", arrayNode.size());
	}

	private static void generatePrimitiveSchema(ObjectNode schema, JsonNode node)
	{
		String type = getJsonType(node);
		schema.put("type", type);
	}

	private static String getJsonType(JsonNode node)
	{
		if (node.isTextual())
			return "string";
		if (node.isNumber())
		{
			return (node.isInt() || node.isLong()) ? "integer" : "number";
		}
		if (node.isBoolean())
			return "boolean";
		if (node.isArray())
			return "array";
		if (node.isObject())
			return "object";
		if (node.isNull())
			return "null";
		return "string";
	}

	private static ObjectNode createTypeSchema(String type)
	{
		ObjectNode typeSchema = mapper.createObjectNode();
		typeSchema.put("type", type);
		return typeSchema;
	}
}