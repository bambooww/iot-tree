package org.iottree.core.msgnet.modules;

import org.iottree.core.conn.ConnPtMsg;
import org.iottree.core.msgnet.*;
import org.json.JSONObject;

public class ConnMsgOut_NE  extends MNNodeEnd
{
	public static final String TP = "conn_in_msg_out";
	
	//String connPtMsgId = null ;
	
	String transEncoding = "UTF-8" ;
	
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
		ConnInMsg_M m = (ConnInMsg_M)this.getOwnRelatedModule();
		ConnPtMsg connpt = m.getConnPt() ;
		if(connpt==null)
		{
			failedr.append("no conn pt msg found") ;
			return false;
		}
		if(!connpt.RT_supportSendMsgOut())
		{
			failedr.append("Conn Point is not support send msg") ;
			return false;
		}
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		ConnInMsg_M m = (ConnInMsg_M)this.getOwnRelatedModule();
		ConnPtMsg connpt = m.getConnPt() ;
		if(connpt==null)
		{
			return null;
		}
		if(!connpt.RT_supportSendMsgOut())
		{
			return null;
		}
		
		Object obj = msg.getPayload() ;
		if(obj==null)
			return null ; ;
		byte[] bs = null ;
		if(obj instanceof byte[])
		{
			bs = (byte[])obj ;
		}
		else if(obj instanceof String)
		{
			bs = ((String)obj).getBytes(transEncoding);
		}
		else
		{
			bs = obj.toString().getBytes(transEncoding) ;
		}
		
		StringBuilder failedr = new StringBuilder() ;
		if(connpt.RT_sendMsgOut(msg.getTopic(),bs,failedr))
			RT_DEBUG_INF.fire("conn_msg_out", "send msg out len="+bs.length);
		else
			RT_DEBUG_WARN.fire("conn_msg_out", "send msg out err :"+failedr.toString());
		return null;
	}

}
