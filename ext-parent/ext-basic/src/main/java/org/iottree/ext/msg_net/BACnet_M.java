package org.iottree.ext.msg_net;

import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class BACnet_M extends MNModule implements IMNRunner
{

	@Override
	protected List<MNNode> getSupportedNodes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTP()
	{
		return "bacnet";
	}

	@Override
	public String getTPTitle()
	{
		return "BACnet";
	}

	@Override
	public String getColor()
	{
		return "#e5bdd6";//"#007dbf";
	}

	@Override
	public String getIcon()
	{
		return "PK_bacnet";//"\\uf1ad";
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
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		// TODO Auto-generated method stub
		
	}

	// rt
	

	@Override
	public boolean RT_start(StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void RT_stop()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean RT_isRunning()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_runnerEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_runnerStartInner()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
