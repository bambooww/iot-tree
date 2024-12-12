package org.iottree.ext.msg_net;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class TCPClient_NM extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(TCPClient_NM.class) ;
	
	static Lan lan = Lan.getLangInPk(TCPClient_NM.class) ;
	
	public static enum RetMode
	{
		buf, //byte[]
		str; //
		
		public String getTitle()
		{
			return lan.g(this.name()) ;
		}
	}
	
	public static enum CloseMode
	{
		never(0),
		aft_fixed_to(1),
		recv_char(2),
		recv_char_num(3),
		immediately(4);
		
		private final int val ;
		
		CloseMode(int v)
		{
			val = v ;
		}
		
		public int getVal()
		{
			return this.val ;
		}
		
		public String getTitle()
		{
			return lan.g(this.name()) ;
		}
	}
	
	String serverAddr ;
	
	int serverPort ;
	
	int localPort = -1;
	
	RetMode retMode = RetMode.buf ;
	
	CloseMode closeMode = CloseMode.never ;
	
	@Override
	public int getOutNum()
	{
		return 3;
	}

	@Override
	public String getTP()
	{
		return "tcp_client";
	}

	@Override
	public String getTPTitle()
	{
		return "TCP Client";
	}

	@Override
	public String getColor()
	{
		return "#c0c0c0";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0ec";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.serverAddr))
		{
			failedr.append("no server set") ;
			return false;
		}
		if(serverPort<=0)
		{
			failedr.append("no server port set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("server", this.serverAddr) ;
		jo.putOpt("port", this.serverPort) ;
		jo.putOpt("loc_port", this.serverAddr) ;
		jo.putOpt("server", this.serverAddr) ;
		jo.putOpt("server", this.serverAddr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		return null;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		if(idx==1)
			return "#25b541";
		else if(idx==2)
			return "#e13c2f" ;
		return null;
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "Received Data in payload" ;
		case 1:
			return "Connect server ready msg";
		case 2:
			return "Connection is broken msg";
		}
		return null ;
	}
}
