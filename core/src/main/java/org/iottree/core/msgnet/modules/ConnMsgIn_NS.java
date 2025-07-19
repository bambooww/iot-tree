package org.iottree.core.msgnet.modules;

import org.iottree.core.conn.ConnPtMsg;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnMsgIn_NS   extends MNNodeStart 
{
	public static final String TP = "conn_in_msg_in";
	
	
	//String connPtMsgId = null ;
	
	boolean bTransStr = false;
	
	boolean bJSON = false;
	
	String transStrEnc = "utf-8" ;
	
	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return g(TP);
	}

	@Override
	public String getColor()
	{
		return "#007cb7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#dddddd" ;
	}

	@Override
	public String getIcon()
	{
		return "\\uf0c1";
	}
	

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("trans_str",bTransStr) ;
		jo.put("b_json",this.bJSON) ;
		jo.putOpt("trans_str_enc", this.transStrEnc) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.bTransStr = jo.optBoolean("trans_str",false) ;
		this.bJSON  = jo.optBoolean("b_json",false) ;
		this.transStrEnc = jo.optString("trans_str_enc") ;
	}
	
//	@Override
//	public String RT_getOutColor(int idx)
//	{
//		// TODO Auto-generated method stub
//		return super.RT_getOutColor(idx);
//	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "Raw Data";
		//case 1:
		//	return "Bind Tag Data" ;
		default:
			return null ;
		}
	}
	
	public boolean RT_fireByConnInMsg(ConnPtMsg cpt_msg,String topic,Object msgob)// throws UnsupportedEncodingException
	{
		ConnInMsg_M cim = (ConnInMsg_M)this.getOwnRelatedModule() ;
		
		if(!cpt_msg.getId().equals(cim.connPtMsgId))
			return false;
		
		//String pldstr = null ;
		Object pld = msgob ;
		if(msgob instanceof byte[])
		{
			if(bTransStr)
			{
				try
				{
				if(Convert.isNotNullEmpty(this.transStrEnc))
					pld = new String((byte[])msgob,this.transStrEnc) ;
				else
					pld = new String((byte[])msgob,"UTF-8") ;
				}
				catch(Exception ee)
				{
					RT_DEBUG_ERR.fire("conn_msg_in", ee.getMessage(),ee);
					return false;
				}
			}
		}
		
		if(bJSON)
		{
			if(msgob instanceof byte[])
			{
				try
				{
					pld = new String((byte[])msgob,"UTF-8") ;
				}
				catch(Exception ee)
				{
					RT_DEBUG_WARN.fire("conn_msg_in", ee.getMessage());
					return false;
				}
			}
			
			if(pld instanceof String)
			{
				String tmps = ((String)pld).trim() ;
				if(tmps.indexOf("{")==0)
					pld = new JSONObject(tmps) ;
				else if(tmps.indexOf("[")==0)
					pld = new JSONArray(tmps) ;
				else
				{
					String ss = tmps ;
					if(ss.length()>50)
						ss = ss.substring(0,50)+"..." ;
					RT_DEBUG_WARN.fire("conn_msg_in", "recv string is not JSON format: "+ss);
					return false;
				}
			}
			
			if(pld instanceof JSONObject || pld instanceof JSONArray)
			{
				RT_DEBUG_WARN.clear("conn_msg_in") ;
			}
			else
			{
				RT_DEBUG_WARN.fire("conn_msg_in", "recv object is not JSON format: "+pld.getClass().getCanonicalName());
				return false;
			}
		}
		
		MNMsg msg = new MNMsg();
		msg.asPayload(pld) ;
		RT_sendMsgOut(RTOut.createOutAll(msg));
		return true ;
	}
}
