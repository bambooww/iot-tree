package org.iottree.core.msgnet.modules;

import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class TagRuntime extends MNModule
{

	@Override
	protected List<MNNode> getSupportedNodes()
	{
		return null;
	}

	@Override
	public String getTP()
	{
		return "tag_rt";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_rt");
	}

	@Override
	public String getColor()
	{
		return "#a1cbde";
	}

	@Override
	public String getIcon()
	{
		return "\\uf02c" ;
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

}
