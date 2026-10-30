package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zspec.util.DeviceState;
import org.iottree.driver.zigbee.zspec.util.StartupStatus;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_zdo.ActiveEPRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.StartupFromAppSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.StartupFromAppSRSP;
import org.iottree.driver.zigbee.zstack.msg_zdo.StateChangeIndAREQ_CB;
import org.json.JSONObject;

public class Startup_CMD extends MsgReqRsp
{
	StartupFromAppSRSP respRSP = null ;
	
	StateChangeIndAREQ_CB respFlw = null ;
	
	public Startup_CMD()
	{
		super();
		StartupFromAppSREQ req = new StartupFromAppSREQ() ;
		this.asReq(req, true, 8000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof StartupFromAppSRSP))
		{
			failedr.append("not StartupFromAppSRSP response") ;
			return false;
		}
		respRSP = (StartupFromAppSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof StateChangeIndAREQ_CB))
		{
			failedr.append("not StateChangeIndAREQ_CB response") ;
			return false;
		}
		respFlw = (StateChangeIndAREQ_CB)m_recv;
		return true ;
	}
	
	public StateChangeIndAREQ_CB getStateChgRsp()
	{
		return this.respFlw ;
	}
	
	public DeviceState getDeviceState()
	{
		if(this.respFlw==null)
			return null ;
		return this.respFlw.getDeviceState() ;
	}
	
	public StartupStatus getStartupStatus()
	{
		if(this.respRSP==null)
			return null ;
		return this.respRSP.getStatus() ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject();
		if(respRSP!=null)
		{
			StartupStatus ss = respRSP.getStatus() ;
			ret.put("status",ss.getStatus()) ;
			ret.put("status_t",ss.getStatusT()) ;
		}
		return ret ;
	}

}
