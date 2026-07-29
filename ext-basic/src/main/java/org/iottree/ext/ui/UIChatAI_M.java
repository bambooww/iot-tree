package org.iottree.ext.ui;

import org.iottree.core.msgnet.MNModule;
import org.json.JSONObject;

public class UIChatAI_M extends MNModule 
{

	@Override
	public String getTP()
	{
		return "ui_chatai";
	}

	@Override
	public String getTPTitle()
	{
		return "UI By AI Chat";
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
