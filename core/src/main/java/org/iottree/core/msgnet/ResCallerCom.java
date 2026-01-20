package org.iottree.core.msgnet;

import org.iottree.core.msgnet.annotion.res_caller;
import org.iottree.core.msgnet.annotion.res_caller_pm;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.SchemaGenerator;

import org.iottree.core.util.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class ResCallerCom extends ResCaller
{
	String name ;
	String title ;
	
	//CallOp callOp ;
	
	Object ownerOb ;
	Method method ;
	res_caller res_c ;
	
	JSONObject paramJsonSchema = null ;
	
	ResCallerCom(Object owner_ob,Method m,res_caller rc)
	{
		this.name = rc.name() ;
		this.title = rc.title() ;
		this.res_c = rc;
		this.ownerOb = owner_ob ;
		this.method = m ;
		try
		{
			this.paramJsonSchema = calcParamJsonSchema() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	private JSONObject calcParamJsonSchema() throws Exception
	{
		Parameter[] ps = this.method.getParameters() ;
		JSONObject sample_pms = new JSONObject() ;
		ArrayList<String> reqs = new ArrayList<>() ;
		if(ps!=null&&ps.length>0)
		{
			for(Parameter p:ps)
			{
				String pn = p.getName() ;
				Class<?> c = p.getType() ;
				
				String pnn = pn ;
				res_caller_pm rnr = p.getAnnotation(res_caller_pm.class) ;
				if(rnr!=null)
				{
					pnn = rnr.name() ;
					if(rnr.required())
						reqs.add(pnn) ;
				}
				Object samplev = genSampleVal(c) ;
				if(samplev!=null)
					sample_pms.put(pnn,samplev) ;
			}
		}
		String jstr = sample_pms.toString() ;
		
		JSONObject schema = genJsonSchema(jstr,reqs) ;
		// System.out.println(" sample ---\r\n"+jstr+" \r\n--schema ==\r\n"+schema.toString(4)) ;
		return schema ;
	}
	
	@Override
	public String getCallerName()
	{
		return this.name;
	}

	@Override
	public String getCallerTitle()
	{
		return this.title;
	}
	
	@Override
	public JSONObject getParamJsonSchema()
	{
		return this.paramJsonSchema ;
	}
	
	@Override
	public JSONObject RT_onResCall(JSONObject input) throws Exception
	{
		Parameter[] ps = this.method.getParameters() ;
		ArrayList<Object> args = new ArrayList<>() ;
		JSONObject sample_pms = new JSONObject() ;
		if(ps!=null&&ps.length>0)
		{
			for(Parameter p:ps)
			{
				String pn = p.getName() ;
				Class<?> c = p.getType() ;
				res_caller_pm rnr = p.getAnnotation(res_caller_pm.class) ;
				if(rnr!=null)
					pn = rnr.name() ;
				Object ob = input.opt(pn) ;
				if(rnr!=null && rnr.required())
				{//required
					if(ob==null)
						throw new Exception("call ["+this.name+"] no param input with name="+pn) ;
				}
				args.add(ob) ;
				Object samplev = genSampleVal(c) ;
				if(samplev!=null)
					sample_pms.put("pn",samplev) ;
			}
		}
		Object retv = this.method.invoke(this.ownerOb, args.toArray()) ;
		if(retv instanceof JSONObject)
			return (JSONObject)retv ;
		String retn = this.res_c.return_name();
		if(Convert.isNotNullEmpty(retn))
			return new JSONObject().put(retn,retv) ;
		return null ;
	}
	
	private static Object genSampleVal(Class<?> c)
	{
		if (c == null) return null;

        if (c == byte.class || c == Byte.class) return (byte) 1;
        if (c == short.class || c == Short.class) return (short) 2;
        if (c == int.class || c == Integer.class) return 42;
        if (c == long.class || c == Long.class) return 42L;
        if (c == float.class || c == Float.class) return 3.14f;
        if (c == double.class || c == Double.class) return 3.14159;
        if (c == BigDecimal.class) return new BigDecimal("123.45");
        if (c == BigInteger.class) return BigInteger.valueOf(999);
        if (c == boolean.class || c == Boolean.class) return true;
        if (c == char.class || c == Character.class) return 'A';

        if (c == String.class) return "sample";

        if (c == Date.class) return new Date();
        if (c == LocalDate.class) return LocalDate.now();
        if (c == LocalDateTime.class) return LocalDateTime.now();

        return null;
	}
}
