package org.iottree.core.msgnet;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public class MNMsg implements IMNCxtPk
{
	private String _id ;
	
	private HashMap<String,Object> heads = null;//new HashMap<>() ;
	
	private String topic = null ;
	
	private Object payload = null ; 
	
	public MNMsg()
	{
		_id = IdCreator.newSeqId() ;
	}
	
	private MNMsg(String id)
	{
		this._id = id ;
	}
	
	public String getMsgId()
	{
		return this._id ;
	}
	
	public long getMsgDT()
	{
		return IdCreator.extractTimeInMillInSeqId(_id);
	}
	
	public MNMsg asTopic(String topic)
	{
		this.topic = topic ;
		return this ;
	}
	
	public String getTopic()
	{
		return this.topic ;
	}
	
	synchronized public MNMsg asHead(String name,Object val)
	{
		if(val==null)
		{
			if(heads==null) return this ;
			heads.remove(name) ;
			return this ;
		}
		
		if(!val.getClass().isPrimitive() && !(val instanceof String))
			throw new IllegalArgumentException("head value must primitive and string") ;
		
		if(heads==null)
		{
			heads = new HashMap<>() ;
		}
		heads.put(name, val) ;
		return this;
	}
	
	public MNMsg asHeads(Map<?,?> heads)
	{
		if(heads==null)
			return this ;
		
		for(Map.Entry<?, ?> n2v:heads.entrySet())
		{
			Object k = n2v.getKey() ;
			if(!(k instanceof String))
				continue ;
			Object v = n2v.getValue() ;
			if(!v.getClass().isPrimitive() && !(v instanceof String))
				continue ;
			asHead((String)k,v) ;
		}
		return this ;
	}
	
	public Object getHeadVal(String name)
	{
		if(heads==null)
			return null ;
		return heads.get(name) ;
	}
	
	public Map<String,Object> getHeadsMap()
	{
		if(heads==null)
		{
			heads = new HashMap<>() ;
		}
		return heads ;
	}
	
	public MNMsg asPayload(Object payload)
	{
		if(payload==null)
		{
			this.payload = null ;
			return this;
		}
		
//		if(!payload.getClass().isPrimitive() && !(payload instanceof String)
//				&& !(payload instanceof JSONObject) && !(payload instanceof JSONArray))
//			throw new IllegalArgumentException("invalid payload type,it must be primitive string,JSONObject,JSONArray") ;
		
		this.payload = payload ;
		return this ;
	}
	
	public Object getPayload()
	{
		return payload ;
	}
	
	public Number getPayloadNumber()
	{
		return (Number)payload ;
	}
	
	public boolean getPayloadBool(boolean def_v) {
        if(payload==null)
        	return def_v;
        if (payload instanceof Boolean){
            return ((Boolean) payload).booleanValue();
        }
//        try {
//            // we'll use the get anyway because it does string conversion.
//            return this.getBoolean(key);
//        } catch (Exception e) {
//            return def_v;
//        }
        
        return def_v ;
    }
	
	
	public JSONObject getPayloadJO(JSONObject def_v)
	{
		 if(payload==null)
	        	return def_v;
		 if(payload instanceof JSONObject)
			 return (JSONObject)payload ;
		 
		 if(payload instanceof String)
		 {
			 String pstr = (String)payload ;
			 JSONObject tmpjo = new JSONObject(pstr) ;
			 return tmpjo ;
		 }
		 
		 throw new RuntimeException("not JSONObject payload") ;
	}
	
	public int getPayloadInt32()
	{
		if(!(payload instanceof Number))
			throw new RuntimeException("payload is not number") ;
		return ((Number)payload).intValue() ;
	}
	
	public String getPayloadStr()
	{
		if(this.payload==null)
			return "" ;
		return this.payload.toString() ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", _id) ;
		jo.put("time_ms", this.getMsgDT()) ;
		jo.putOpt("topic", this.topic) ;
		if(heads!=null)
		{
			jo.put("heads", new JSONObject(this.heads)) ;
		}
		
		jo.putOpt("payload", payload) ;
		return jo ;
	}
	
	public static MNMsg fromJO(JSONObject jo)
	{
		MNMsg ret = new MNMsg(jo.getString("id")) ;
		ret.topic = jo.optString("topic") ;
		JSONObject hdjo = jo.optJSONObject("heads") ;
		if(hdjo!=null)
		{
			ret.heads = new HashMap<>() ;
			for(String n:hdjo.keySet())
			{
				ret.heads.put(n, hdjo.get(n)) ;
			}
		}
		ret.payload = jo.opt("payload") ;
		return ret;
	}

	// CXT PK
	static List<String> PK_SubNList = Arrays.asList("id","time_ms","topic","payload") ; //,"heads"
	static List<String> PK_SubNListW = Arrays.asList("topic","payload") ; //,"heads"
	static HashSet<String> PK_subNames = new HashSet<>() ;
	static
	{
		PK_subNames.addAll(PK_SubNList) ;
	}
	
	@Override
	public List<String> CXT_PK_getSubNames()
	{
		return PK_SubNList;
	}

	@Override
	public List<String> CXT_PK_getSubNamesW()
	{
		return PK_SubNListW;
	}

	@Override
	public List<MNCxtValTP> CXT_PK_getSubLimit(String subname)
	{
		switch(subname)
		{
		case "topic":
			return Arrays.asList(MNCxtValTP.vt_str) ;
		default:
			return null ;
		}
	}

	@Override
	public Object CXT_PK_getSubVal(String subname)
	{
		switch(subname)
		{
		case "_id":
			return this.getMsgId() ;
		case "_time_ms":
			return this.getMsgDT();
		case "topic":
			return this.getTopic() ;
		case "heads":
			break ;
		case "payload":
			return this.getPayload() ;
		}
		if(subname.startsWith("payload."))
		{
			Object ob = this.getPayload() ;
			if(ob==null)
				return null ;
			if(ob instanceof JSONObject)
				return JsonUtil.getValByPath((JSONObject)ob, subname.substring(8)) ;
		}
		//may JSONPath
		return null;
	}

	@Override
	public boolean CXT_PK_setSubVal(String subname,Object subv,StringBuilder failedr) 
	{
		switch(subname)
		{
		case "topic":
			this.asTopic((String)subv) ;
			return true ;
		
		case "payload":
			this.asPayload(subv) ;
			return true ;
		case "heads":
		default:
			this.asHead(subname, subv) ;
			return true ;
		}

		//failedr.append("sub "+subname+" is not supported") ;
		//return false;
	}
	
	/**
	 * trans to var to map
	 * it will be used by Mustache {{ }} templete out
	 * @return
	 */
	public Map<String,Object> CXT_PK_toMap()
	{
		HashMap<String,Object> rets = new HashMap<>() ;
		rets.put("_id",this.getMsgId()) ;
		rets.put("_time_ms",this.getMsgDT());
		rets.put("topic",this.getTopic()) ;
		rets.put("heads",this.getHeadsMap()) ;
		rets.put("payload",CXT_PK_getPayload()) ;
		return rets ;
	}
	
	public Object CXT_PK_getPayload()
	{
		if(this.payload==null)
			return "" ;
		
		if(this.payload instanceof JSONObject)
			return ((JSONObject)this.payload).toMap() ;
		if(this.payload instanceof JSONArray)
			return ((JSONArray)this.payload).toList() ;
		return this.payload ;
	}
	
	public void CXT_PK__renderTree(Writer w)  throws IOException
	{
		CXT_PK_renderMapTree(w,CXT_PK_toMap()) ; 
	}
	
	public void CXT_PK__renderPayloadTree(Writer w)  throws IOException
	{
		CXT_PK_renderObjTree(w,CXT_PK_getPayload()) ; 
	}

	public static void CXT_PK_renderMapTree(Writer w,Map<String,Object> map) throws IOException
	{
		w.write("<ul>");
		for(Map.Entry<String, Object> n2o:map.entrySet())
		{
			String n = Convert.plainToHtml(n2o.getKey()) ;
			Object o = n2o.getValue() ;
			w.write("<li >"+n) ;
			CXT_PK_renderObjTree(w, o) ;
			w.write("</li>") ;
		}
        w.write("</ul>");
	}
	
	
	public static void CXT_PK_renderObjTree(Writer w,Object o) throws IOException
	{
		if(o instanceof List)
		{
			w.write(":[]") ;
			List<?> ll = (List<?>)o ;
			if(ll.size()>=0)
			{
				Object ob = ll.get(0) ;
				if(ob instanceof Map)
					CXT_PK_renderMapTree(w,(Map<String,Object>)ob) ;
				//else if(ob instanceof List)
				//	CXT_PK_renderTreeObj(w,Object o)
			}
		}
		else if(o instanceof Map)
		{
			CXT_PK_renderMapTree(w,(Map)o) ;
		}
		else
		{
			if(o!=null)
			{
				if(o instanceof Number)
					w.write(":number") ;
				else if(o instanceof String)
					w.write(":string");
				else if(o instanceof Boolean)
					w.write(":boolean");
				else
					w.write(":obj");
			}
		}
	}
	
	public static Object transStrJoJArr(String txt)
	{
		if(txt==null)
			return null ;
		String ss = txt.trim() ;
		if(ss.startsWith("{"))
		{
			try
			{
				JSONObject tmpjo = new JSONObject(ss) ;
				return tmpjo;
			}
			catch(Exception ee)
			{
				return txt ;
			}
		}
		else if(ss.startsWith("["))
		{
			try
			{
				JSONArray tmpjarr = new JSONArray(ss) ;
				return tmpjarr ;
			}
			catch(Exception ee)
			{
				return txt ;
			}
		}
		
		return txt;
	}
}
