package org.iottree.core.msgnet.modules;

import java.util.List;

import org.iottree.core.msgnet.*;
import org.json.JSONObject;

public class MsgMTO_IN extends MNNodeEnd
{
	@Override
	public String getTP()
	{
		return "mto_in";
	}

	@Override
	public String getTPTitle()
	{
		return g("mto_in");
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

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		MsgMTO_M m = (MsgMTO_M)this.getOwnRelatedModule() ;
		List<MsgMTO_OUT> outs = m.listRelatedOut() ;
		if(outs==null)
			return null ;
		for(MsgMTO_OUT out:outs)
		{
			RTOut rto = RTOut.createOutIdx().asIdxMsg(0, msg) ;
			out.RT_sendMsgOut(rto);
		}
		return null;
	}
}
