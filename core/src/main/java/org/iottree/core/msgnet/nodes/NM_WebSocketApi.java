package org.iottree.core.msgnet.nodes;

import java.nio.ByteBuffer;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.ws.WSServerMsgNet;
import org.json.JSONObject;

public class NM_WebSocketApi extends MNNodeMid //implements
{
	public static final String TP = "ws_api" ;
	
	String apiName = null ;
	
	private transient Object outputObj = null ;
	
	public String getApiName()
	{
		return this.apiName ;
	}
	
	public String getAccessPath()
	{
		MNNet net = this.getBelongTo() ;
		UAPrj prj = net.getBelongTo().getBelongToPrj() ;
		return "/_ws/api/"+prj.getName()+"/"+net.getName()+"/"+this.apiName ;
	}
	
	
	public Object getOutputObj()
	{
		return this.outputObj ;
	}
	
	/**
	 * when url received post data,it will output
	 */
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
		return "WebSocket Api";
	}

	@Override
	public String getTitleColor()
	{
		return "#eeeeee" ;
	}
	
	@Override
	public String getColor()
	{
		return "#1488f5";
	}

	@Override
	public String getIcon()
	{
		return "\\uf090";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.apiName))
		{
			failedr.append("no api name set") ;
			return false;
		}
		return true ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("api_n",this.apiName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.apiName = jo.optString("api_n") ;
	}
	
	@Override
	public String RT_getInTitle()
	{
		return "Send data to WebSocket client";//this.apiName;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "client in";
		}
		return null ;
	}
	
	public void RT_onClientInTxt(String txt)
	{
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asPayload(txt)));
	}
	
	
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Object pld = msg.getPayload() ;
		if(pld!=null)
		{
			WSServerMsgNet.sendTxtOut(this,pld.toString()) ;
			return null ;
		}
		
		byte[] bs = msg.getBytesArray() ;
		if(bs!=null)
		{
			WSServerMsgNet.sendBinOut(this,ByteBuffer.wrap(bs)) ;
			return null ;
		}
		
		//send to client
		return null;
	}
}
