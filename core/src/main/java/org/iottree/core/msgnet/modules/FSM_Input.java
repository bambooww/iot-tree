package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

/**
 * Unified input for FSM,msg will out in current FSM_State
 * @author jason.zhu
 *
 */
public class FSM_Input extends MNNodeEnd
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
		return "\\uf30b";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#333333" ;
	}


//	@Override
//	public int getOutNum()
//	{
//		return 1;
//	}
	

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
		FSM_M owner = (FSM_M)this.getOwnRelatedModule() ;
		FSM_State cur_st = owner.RT_getCurrentState() ;
		if(cur_st==null)
			return null ;
		cur_st.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(3, msg));
		return null;
	}

}
