package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NS_ConnInMsgTrigger   extends MNNodeStart 
{

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
	public String getTP()
	{
		return "conn_in_msg_trigger";
	}

	@Override
	public String getTPTitle()
	{
		return g("conn_in_msg_trigger");
	}

	@Override
	public String getColor()
	{
		return "#007cb7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#dddddd" ;
	}

	@Override
	public String getIcon()
	{
		return "\\uf0c1";
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
