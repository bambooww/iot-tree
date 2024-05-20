package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NSSwitch extends MNNodeMid implements ILang
{
	@Override
	public String getColor()
	{
		return "#e6d970";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf074";
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
		return 3;
	}

	@Override
	public String getNodeTP()
	{
		return "switch";
	}

	@Override
	public String getNodeTPTitle()
	{
		return g("switch");
	}

	@Override
	public boolean needParam()
	{
		return false;
	}

	@Override
	public boolean isParamReady()
	{
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		
	}


}
