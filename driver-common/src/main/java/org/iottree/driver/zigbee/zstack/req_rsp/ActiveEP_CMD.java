package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_zdo.ActiveEPRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.ActiveEPSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.ActiveEPSRSP;
import org.json.JSONObject;

public class ActiveEP_CMD extends MsgReqRsp
{
	ActiveEPSRSP respRSP = null ;
	
	ActiveEPRspAREQ_CB respFlwRSP = null ;
	
	protected byte[] respNVData = null ;

	public ActiveEP_CMD(int dst_addr)
	{
		super();
		ActiveEPSREQ req = new ActiveEPSREQ(dst_addr) ;
		this.asReq(req, true, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof ActiveEPSRSP))
		{
			failedr.append("not ActiveEPSRSP response") ;
			return false;
		}
		respRSP = (ActiveEPSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof ActiveEPRspAREQ_CB))
		{
			failedr.append("not ActiveEPRspAREQ_CB response") ;
			return false;
		}
		respFlwRSP = (ActiveEPRspAREQ_CB)m_recv;
		return true ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public ActiveEPRspAREQ_CB getActiveEPRsp()
	{
		return this.respFlwRSP ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return new JSONObject().put("status", this.getStatus());
	}

}
