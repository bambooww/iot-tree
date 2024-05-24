package org.iottree.core.msgnet;

import java.util.HashMap;

import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public class MNMsg
{
	String _msgId ;
	
	HashMap<String,Object> heads = new HashMap<>() ;
	
	Object payload = null ; 
	
	public MNMsg()
	{
		_msgId = IdCreator.newSeqId() ;
	}
	
	public MNMsg asPayload(Object payload)
	{
		this.payload = payload ;
		return this ;
	}
	
	public String getMsgId()
	{
		return _msgId ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("_msgid", _msgId) ;
		jo.putOpt("payload", payload) ;
		return jo ;
	}
}
