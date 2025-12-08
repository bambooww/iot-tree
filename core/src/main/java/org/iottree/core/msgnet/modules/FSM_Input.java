package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

public class FSM_Input extends MNNodeMid
{
	public static final String TP = "fsm_input" ;
	
	
	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return g(TP);
	}

	@Override
	public String getColor()
	{
		return "#ea95a6";
	}

	@Override
	public String getIcon()
	{
		return "PK_flw";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#333333" ;
	}


	@Override
	public int getOutNum()
	{
		return 1;
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
