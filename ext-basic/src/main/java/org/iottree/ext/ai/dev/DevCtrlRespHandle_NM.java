package org.iottree.ext.ai.dev;

import org.iottree.core.util.*;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONArray;
import org.json.JSONObject;

public class DevCtrlRespHandle_NM extends MNNodeMid
{
	
	@Override
	public String getTP()
	{
		return "ai_devctrl_resp_h";
	}

	@Override
	public String getTPTitle()
	{
		return "Response Handle";
	}

	@Override
	public String getColor()
	{
		return "#af40a0";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a0";
	}
	
	
	@Override
	public int getOutNum()
	{
		return 0;
	}


	@Override
	public boolean isParamReady(StringBuilder failedr)
	{

//		if(this.maxTokens<=0)
//		{
//			failedr.append("max tokens must >0") ;
//			return false;
//		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		
//		jo.putOpt("sys_msg",this.systemMsg) ;
		
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
//		this.systemMsg = jo.optString("sys_msg") ;
		
		
	}
	
	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		return "LLM Req" ;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		return "#a349a4";
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Object pld = msg.getPayload() ;
		if(pld==null||"".equals(pld))
			return null ;
		JSONObject jo = null;
		JSONArray jarr = null;
		if(pld instanceof JSONObject)
		{
			jo = (JSONObject)pld ;
		}
		else if(pld instanceof JSONArray)
		{
			jarr = (JSONArray)pld ;
		}
		else if(pld instanceof String)
		{
			String ss = (String)pld ;
			ss = ss.trim() ;
			try
			{
				if(ss.startsWith("{"))
					jo = new JSONObject(ss) ;
				else if(ss.startsWith("["))
					jarr = new JSONArray(ss) ;
			}
			catch(Exception ee)
			{
				RT_DEBUG_WARN.fire("err", ss);
			}
		}
		
		if(jo==null && jarr==null)
		{
			RT_DEBUG_WARN.fire("err", "unknown input");
			return null ;
		}
		if(jo!=null && jo.has("error"))
		{
			RT_DEBUG_WARN.fire("err", jo.optString("error"));
			return null ;
		}
		
		DevCtrl_M owner = (DevCtrl_M)this.getOwnRelatedModule() ;
		
		if(jarr!=null)
		{//[   {     "device_id": "kt.light",     "parameter": "on_off",     "value": "1"   },  
			// {     "device_id": "kt.airc",     "parameter": "on_off",     "value": "on"   } ]
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				if(!tmpjo.has("device_id") || !tmpjo.has("parameter") || !tmpjo.has("value"))
					continue ;
				String dev_id = tmpjo.optString("device_id") ;
				String param = tmpjo.optString("parameter") ;
				String strv =  tmpjo.optString("value") ;
				DevCtrlDevItem_NS devitem = owner.getRelatedDevItem(dev_id) ;
				if(devitem==null)
					continue ;
				devitem.RT_setParamValueOut(param, strv) ;
			}
		}
		
		
		
		return null ;
	}
}
