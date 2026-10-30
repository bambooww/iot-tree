package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_zdo.PowerDescRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.PowerDescSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.PowerDescSRSP;
import org.json.JSONObject;

public class PowerDesc_CMD extends MsgReqRsp
{
	PowerDescSRSP respRSP = null ;
	
	PowerDescRspAREQ_CB respFlwRSP = null ;
	
	protected byte[] respNVData = null ;

	public PowerDesc_CMD(int dst_addr,int nwk_addr_of_int)
	{
		super();
		PowerDescSREQ req = new PowerDescSREQ(dst_addr,nwk_addr_of_int) ;
		this.asReq(req, true, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof PowerDescSRSP))
		{
			failedr.append("not PowerDescSRSP response") ;
			return false;
		}
		respRSP = (PowerDescSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof PowerDescRspAREQ_CB))
		{
			failedr.append("not PowerDescRspAREQ_CB response") ;
			return false;
		}
		respFlwRSP = (PowerDescRspAREQ_CB)m_recv;
		return true ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public PowerDescRspAREQ_CB getPowerDescRsp()
	{
		return this.respFlwRSP ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject().put("status", this.getStatus());
		if(this.respFlwRSP!=null)
			ret.putOpt("flw_resp", respFlwRSP.toString());
		return ret ;
	}

}
