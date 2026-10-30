package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zspec.util.Capabilities;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_sys.PingSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.PingSRSP;
import org.json.JSONObject;

public class Ping_CMD extends MsgReqRsp
{
	PingSRSP respRSP = null ;
	
	public Ping_CMD()
	{
		super();
		PingSREQ req = new PingSREQ() ;
		this.asReq(req, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof PingSRSP))
		{
			failedr.append("not PingSRSP response") ;
			return false;
		}
		respRSP = (PingSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public PingSRSP getPingRsp()
	{
		return this.respRSP ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject();
		if(respRSP!=null)
		{
			Capabilities cap = respRSP.getCapability() ;
			ret.put("cap", cap.getCapVal());
			ret.put("caps", cap.getOptionsList());
		}
		return ret ;
	}

}
