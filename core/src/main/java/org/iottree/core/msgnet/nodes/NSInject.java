package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NSInject extends MNNodeStart implements ILang
{

	@Override
	public String getColor()
	{
		return "#9fbccf";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf04b";
	}
	
	@Override
	public boolean supportInOnOff()
	{
		return true;
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
		return 1;
	}

	@Override
	public String getNodeTP()
	{
		return "inject";
	}

	@Override
	public String getNodeTPTitle()
	{
		return g("inject");
	}


	@Override
	public boolean needParam()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isParamReady()
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

	public boolean RT_trigger(MNMsg msg,StringBuilder failedr)
	{
		failedr.append("TODO") ;
		return false;
	}


}
