package org.iottree.core.msgnet.modules;

import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.json.JSONObject;

/**
 * message Many To One forwarding
 * 
 * *-1 transmit
 * 
 * @author jason.zhu
 *
 */
public class MsgMTO_M extends MNModule 
{
	@Override
	public String getTP()
	{
		return "mto";
	}

	@Override
	public String getTPTitle()
	{
		return g("mto");
	}

	@Override
	public String getColor()
	{
		return "#aaaaaa";
	}

	@Override
	public String getIcon()
	{
		return "PK_mto";
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

	public List<MsgMTO_OUT> listRelatedOut()
	{
		return this.listRelatedNodes(MsgMTO_OUT.class) ;
	}
}
