package com.xxx.app.mn;

import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class M_TestModule extends MNModule
{
	
	@Override
	public String getTP()
	{
		return null;
	}

	@Override
	public String getTPTitle()
	{
		return null;
	}

	@Override
	public String getColor()
	{
		return null;
	}

	@Override
	public String getIcon()
	{
		return null;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return false;
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
	
}
