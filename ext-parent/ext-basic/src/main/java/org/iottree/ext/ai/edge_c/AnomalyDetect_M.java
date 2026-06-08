package org.iottree.ext.ai.edge_c;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.util.ILang;
import org.json.JSONObject;

public class AnomalyDetect_M extends MNModule implements ILang
{

	@Override
	public String getTP()
	{
		return "anomaly_detect";
	}

	@Override
	public String getTPTitle()
	{
		return g("ano_det");
	}

	@Override
	public String getColor()
	{
		return "#ff7f27";
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
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}
}
