package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeState;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

/**
 * FSM State node
 * @author jason.zhu
 *
 */
public class FSM_State extends MNNodeState
{
	public static final String TP = "fsm_state" ;
	
	boolean bStart = false;
	
	long activeRunIntv = 1000 ;
	
	
	
	
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
		return "#dddddd";
	}

	@Override
	public String getIcon()
	{
		if(bStart)
			return "\\uf35a" ;
		else
			return "\\uf101";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#333333" ;
	}

	@Override
	public final int getOutNum()
	{
		return 4;
	}
	

	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "in";
		case 1:
			return "run";
		case 2:
			return "out";
		case 3:
			return "⟶" ;
		default:
			return null ;
		}
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		switch(idx)
		{
		case 0:
			return "green";
		case 1:
			return "blue";
		case 2:
			return "red";
		default:
			return null ;
		}
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		return new JSONObject().put("start", this.bStart)
				.put("act_run_intv", this.activeRunIntv);
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.bStart = jo.optBoolean("start",false) ;
		this.activeRunIntv = jo.optLong("act_run_intv",1000);
	}
	

	@Override
	public boolean RT_isStateActive()
	{
		FSM_M m = (FSM_M)this.getOwnRelatedModule();
		return m.RT_getCurrentState()==this;
	}
	
	@Override
	public boolean RT_isStateRunning()
	{
		FSM_M m = (FSM_M)this.getOwnRelatedModule();
		return m.RT_isRunning() ;
	}

	@Override
	protected final RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		//input msg will set this node to current state
		FSM_M m = (FSM_M)this.getOwnRelatedModule();
		FSM_State old_act = m.RT_getCurrentState() ;
		if(old_act!=null)
			old_act.RT_onInactive();
		m.RT_setCurrentState(this);
		this.RT_onActive(msg);
		return null;
	}
	
	void RT_onActive(MNMsg msg)
	{
		MNMsg m = new MNMsg().asPayload(msg.getPayload()) ;
		RTOut out = RTOut.createOutIdx().asIdxMsg(0, m) ;
		this.RT_sendMsgOut(out);
	}
	
	void RT_onInactive()
	{
		MNMsg m = new MNMsg().asPayload("") ;
		RTOut out = RTOut.createOutIdx().asIdxMsg(2, m) ;
		this.RT_sendMsgOut(out);
	}
	
	void RT_onActiveRunning()
	{
		JSONObject pld = null ;
		MNMsg m = new MNMsg().asPayload(pld) ;
		RTOut out = RTOut.createOutIdx().asIdxMsg(1, m) ;
		this.RT_sendMsgOut(out);
	}
	
	private transient long RT_lastRunDT = -1 ;
	/**
	 * called by FSM_M
	 * 
	 */
	void RT_runInLoop()
	{
		if(System.currentTimeMillis()-RT_lastRunDT<activeRunIntv)
			return ;//
		
		try
		{
			RT_onActiveRunning();
		}
		finally
		{
			RT_lastRunDT = System.currentTimeMillis() ;
		}
	}

//	protected abstract void RT_onStateIn(MNConn in_conn) throws Exception;
//
//	@Override
//	protected void RT_onStateIn(MNConn in_conn) throws Exception
//	{
//		// TODO Auto-generated method stub
//		
//	}
}
