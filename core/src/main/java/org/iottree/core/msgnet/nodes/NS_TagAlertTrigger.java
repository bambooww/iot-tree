package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NS_TagAlertTrigger  extends MNNodeStart 
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
		return "tag_alert_trigger";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_alert_trigger");
	}

	@Override
	public String getColor()
	{
		return "#ff8566";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a2";
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
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		
	}

}
