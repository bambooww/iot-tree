package org.iottree.core.msgnet.modules;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class MemMultiQueue extends MNModule implements IMNRunner
{
	static List<MNNode> SUP_NS = Arrays.asList(new MemQueue_NM()) ;
	@Override
	protected List<MNNode> getSupportedNodes()
	{
		return SUP_NS;
	}

	@Override
	public String getTP()
	{
		return "mem_multi_que";
	}

	@Override
	public String getTPTitle()
	{
		return g("mem_multi_que");
	}

	@Override
	public String getColor()
	{
		return "#f0a566";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf141";
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

	@Override
	public boolean RT_start(StringBuilder failedr)
	{
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
		return false;
	}

	@Override
	public boolean RT_runnerEnabled()
	{
		return true;
	}

	@Override
	public boolean RT_runnerStartInner()
	{
		return false;
	}

}
