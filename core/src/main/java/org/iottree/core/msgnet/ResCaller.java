package org.iottree.core.msgnet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.annotion.res_caller;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;

import org.iottree.core.util.*;

/**
 * Res Caller interface
 * 
 * @author jason.zhu
 *
 */
public abstract class ResCaller
{
	ResCat cat = null;

	public ResCaller(ResCat cat)
	{
		this.cat = cat;
	}

	public ResCaller()
	{
	}

	public ResCat getBelongTo()
	{
		return this.cat;
	}

	public String getUID()
	{
		if (cat == null)
			return this.getCallerName();
		return this.cat.getName() + "." + this.getCallerName();
	}

	public abstract String getCallerName();

	public abstract String getCallerTitle();
	
	public abstract JSONObject getParamJsonSchema();

	public abstract JSONObject RT_onResCall(JSONObject input) throws Exception;

	@FunctionalInterface
	public static interface CallOp
	{
		public JSONObject RT_onResCall(JSONObject input) throws Exception;
	}

	public static List<ResCaller> parseResCaller(Object obj)
	{
		ArrayList<ResCaller> rets = new ArrayList<>();
		Method[] ms = obj.getClass().getDeclaredMethods();
		if (ms == null)
			return rets;
		for (Method m : ms)
		{
			res_caller rc = m.getAnnotation(res_caller.class);
			if (rc == null)
				continue;
			ResCallerCom rcc = new ResCallerCom(obj, m, rc);
			rets.add(rcc);
		}
		return rets;
	}

	protected static JSONObject genJsonSchema(String json_str,List<String> required) throws Exception 
	{
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode jsonNode = mapper.readTree(json_str);
//		SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12,
//				OptionPreset.PLAIN_JSON);
//
//		configBuilder.with(Option.EXTRA_OPEN_API_FORMAT_VALUES);
//		configBuilder.with(Option.ENUM_KEYWORD_FOR_SINGLE_VALUES);
//		//configBuilder.with(Option.SCHEMA_VERSION_INDICATOR);
//
//		SchemaGeneratorConfig config = configBuilder.build();
//		SchemaGenerator generator = new SchemaGenerator(config);
		
		String ss = JsonSchemaGenerator.generateSchema(json_str,required);
		//String ss = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jn);
		return new JSONObject(ss) ;
	}

}
