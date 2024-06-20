package com.xxx.app.mn;

import java.util.Map;

import org.json.JSONObject;

public class N_TestSimple
{
	public int MN_getOutNum()
	{
		return 2 ;
	}
	
	public String MN_getOutColor(int idx)
	{
		return null ;
	}
	
	public boolean MN_isParamReady(StringBuilder sb)
	{
		return true ;
	}
	
	public JSONObject MN_getParamJO()
	{
		return null ;
	}
	
	public void MN_setParamJO(JSONObject jo)
	{
		
	}
	
	
	public void MN_RT_processMsgIn(String topic,Map<String,Object> heads,Object payload)
	{
		
	}
	
	public Object MN_RT_getMsgOutPayload(int idx)
	{
		return null ;
	}
	
	public Map<String,Object> MN_RT_getMsgOutHeads(int idx)
	{
		return null;
	}
	
	public String MN_RT_getMsgOutTopic(int idx)
	{
		return null ;
	}
}
