package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NM_Exec extends MNNodeMid
{
	@Override
	public String getColor()
	{
		return "#fa9275";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf013";
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

//	@Override
	public String getTP()
	{
		return "exec";
	}

	@Override
	public String getTPTitle()
	{
		return g("exec");
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
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		return RTOut.createOutAll(msg) ;
	}
	
}
