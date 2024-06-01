package org.iottree.core.msgnet;

import java.util.Date;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public enum MNCxtValTP
{
	vt_int("Integer"),
	vt_float("Float"),
	vt_bool("Bool"),
	vt_str("String"),
	vt_date("Date"),
	vt_jo("JSONObject"),
	vt_jarr("JSONArray");
	
	private final String tt;

	MNCxtValTP(String t)
	{
		tt =t ;
	}
	
	public String getName()
	{
		return name().substring(3) ;
	}
	
	public String getTitle()
	{
		return tt ;
	}
	
	public static MNCxtValTP parseFrom(String nn)
	{
		if(Convert.isNullOrEmpty(nn))
			return null ;
		
		if(nn.startsWith("vt_"))
			return MNCxtValTP.valueOf(nn) ;
		return  MNCxtValTP.valueOf("vt_"+nn) ;
	}
	
	public Object transStrToObj(String strv)
	{
		if(strv==null)
			return null ;
		switch(this)
		{
		case vt_str:
			return strv ;
		case vt_int:
			if(strv.isEmpty())
				return null ;
			return Long.parseLong(strv) ;
		case vt_float:
			if(strv.isEmpty())
				return null ;
			return Double.parseDouble(strv) ;
		case vt_bool:
			return "true".equalsIgnoreCase(strv) ;
		case vt_date:
			if(strv.isEmpty())
				return null ;
			return new Date(Long.parseLong(strv)) ;
		case vt_jo:
			if(strv.isEmpty())
				return null ;
			return new JSONObject(strv) ;
		case vt_jarr:
			if(strv.isEmpty())
				return null ;
			return new JSONArray(strv) ;
		default:
			return null ;
		}
	}
	
	public Object fitObjToVal(Object obj,StringBuilder failedr)
	{
		if(obj instanceof String)
			return transStrToObj((String)obj) ;
		
		switch(this)
		{
		case vt_int:
			if(!(obj instanceof Number))
			{
				failedr.append("no number input") ;
				return null ;
			}
			return ((Number)obj).longValue() ;
		case vt_float:
			if(!(obj instanceof Number))
			{
				failedr.append("no number input") ;
				return null ;
			}
			return ((Number)obj).doubleValue() ;
		case vt_date:
			if(!(obj instanceof Long))
			{
				failedr.append("no time ms long") ;
				return null ;
			}
			return obj;
		case vt_jo:
			if(!(obj instanceof JSONObject))
			{
				failedr.append("no JSONObject input") ;
				return null ;
			}
			return (JSONObject)obj;
		case vt_jarr:
			if(!(obj instanceof JSONArray))
			{
				failedr.append("no JSONArray input") ;
				return null ;
			}
			return (JSONArray)obj;
		default:
			return null ;
		}
	}
}
