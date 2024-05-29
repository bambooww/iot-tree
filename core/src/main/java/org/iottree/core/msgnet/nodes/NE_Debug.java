package org.iottree.core.msgnet.nodes;

import java.util.Date;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NE_Debug extends MNNodeEnd implements ILang
{
	boolean bOutWin = true;
	boolean bOutConsole = false;
	
	@Override
	public String getIcon()
	{
		return "\\uf188";
	}
	
	@Override
	public String getColor()
	{
		return "#7caa82";
	}
	
	@Override
	public boolean supportOutOnOff()
	{
		return true;
	}
	
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

//	@Override
	public String getTP()
	{
		return "debug";
	}

	@Override
	public String getTPTitle()
	{
		return g("debug");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		//jo.put("b_delay", this.bDelayExec) ;
		jo.put("out_win", this.bOutWin) ;
		jo.put("out_console", this.bOutConsole) ;
		return jo ;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		this.bOutWin= jo.optBoolean("out_win", true) ;
		this.bOutConsole = jo.optBoolean("out_console",false) ;
	}

	
	@Override
	protected final RTOut RT_onMsgIn(MNConn in_conn,MNMsg msg)
	{
		if(!this.getOutOnOff())
			return null;//
		JSONObject jo = msg.toJO() ;
		if(bOutConsole)
		{
			System.out.println(Convert.toFullYMDHMS(new Date()) +" node "+this.getTitle()) ;
			System.out.println(jo) ;
		}
		if(bOutWin)
		{//TODO send to client win
			
		}
		return null;//RTOut.NONE;
	}
}
