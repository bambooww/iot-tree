package org.iottree.core.msgnet.modules;

import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class DBSql extends MNModule
{
	@Override
	public String getTP()
	{
		return "db_sql";
	}

	@Override
	public String getTPTitle()
	{
		return g("db_sql");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf1c0";
	}

	
	@Override
	public boolean isParamReady(StringBuilder failedr)
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
	protected void setParamJO(JSONObject jo)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<MNNode> getSupportedNodes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
}
