package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.*;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

public class MsgMTO_OUT extends MNNodeStart
{
	@Override
	public String getTP()
	{
		return "mto_out";
	}

	@Override
	public String getTPTitle()
	{
		return g("mto_out");
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
	public int getOutNum()
	{
		return 1;
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
