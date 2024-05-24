package com.xxx.app.mn;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

/**
 * query quantity of material
 * @author jason.zhu
 *
 */
public class N_QueryNum extends MNNodeMid
{
	@Override
	public String getTP()
	{
		return "que_num";
	}

	@Override
	public String getTPTitle()
	{
		return "Query Number";
	}
	
	@Override
	public String getColor()
	{
		
		return "#59e285";
	}

	@Override
	public String getIcon()
	{
		return "\\uf002";
	}

	@Override
	public JSONTemp getInJT()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		return null;
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
