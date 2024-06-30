package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

public class NS_OnFlowEvt extends MNNodeStart
{

	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public String getTP()
	{
		return "on_flow_evt";
	}

	@Override
	public String getTPTitle()
	{
		return g("on_flow_evt");
	}

	@Override
	public String getColor()
	{
		return "#c5c5c3";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0e7";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
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

	
	public void RT_fireFlowStart()
	{
		MNMsg m = new MNMsg().asPayload(System.currentTimeMillis());
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, m));
	}
	
	public void RT_fireFlowStop()
	{
		MNMsg m = new MNMsg().asPayload(System.currentTimeMillis());
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(1, m));
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return "On Flow Start" ;
		if(idx==1)
			return "On Flow Stop";
		return null ;
	}
}
