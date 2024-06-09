package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

/**
 * 内存队列，
 * @author jason.zhu
 *
 */
public class MemQueue_NM extends MNNodeMid
{
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "mem_que";
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
	public String getTPTitle()
	{
		return g("mem_que");
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
	protected void setParamJO(JSONObject jo)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		return null;
	}

}
