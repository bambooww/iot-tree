package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NSDebug extends MNNodeEnd implements ILang
{
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

	@Override
	public int getOutNum()
	{
		return 0;
	}

	@Override
	public String getNodeTP()
	{
		return "debug";
	}

	@Override
	public String getNodeTPTitle()
	{
		// TODO Auto-generated method stub
		return g("debug");
	}

	@Override
	public boolean needParam()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isParamReady()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		// TODO Auto-generated method stub
		
	}

	

}
