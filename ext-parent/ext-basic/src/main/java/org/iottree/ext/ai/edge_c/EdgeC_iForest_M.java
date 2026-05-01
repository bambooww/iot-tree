package org.iottree.ext.ai.edge_c;

import org.iottree.core.msgnet.MNModule;
import org.json.JSONObject;

public class EdgeC_iForest_M extends MNModule 
{

	@Override
	public String getTP()
	{
		return "iforest";
	}

	@Override
	public String getTPTitle()
	{
		return "Isolation Forest";
	}

	@Override
	public String getColor()
	{
		return "#a349a4";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a0";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		// TODO Auto-generated method stub
		
	}

}
