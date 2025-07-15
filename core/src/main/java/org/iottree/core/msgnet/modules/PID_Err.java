package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class PID_Err extends MNNodeStart
{
	public static final String TP = "pid_err" ;
	
	@Override
	public String getColor()
	{
		return "#e6d970";
	}

	@Override
	public String getIcon()
	{
		return "PK_pid";
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
	
	// @Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return g(TP);//"Output";
	}
	
	@Override
	public int getOutNum()
	{
		return 1;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		return "red";
	}
	
	@Override
	public int getMaxNumInModule()
	{
		return 1 ;// limit one
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
