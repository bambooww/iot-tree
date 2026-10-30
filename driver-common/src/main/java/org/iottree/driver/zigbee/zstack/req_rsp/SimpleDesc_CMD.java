package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_zdo.SimpleDescRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.SimpleDescSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.SimpleDescSRSP;
import org.json.JSONObject;

public class SimpleDesc_CMD extends MsgReqRsp
{
	SimpleDescSRSP respRSP = null ;
	
	SimpleDescRspAREQ_CB respFlwRSP = null ;
	
	public SimpleDesc_CMD(int dst_addr,int nwk_addr_of_int,int endpoint)
	{
		super();
		SimpleDescSREQ req = new SimpleDescSREQ(dst_addr,nwk_addr_of_int,endpoint) ;
		this.asReq(req, true, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof SimpleDescSRSP))
		{
			failedr.append("not SimpleDescSRSP response") ;
			return false;
		}
		respRSP = (SimpleDescSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof SimpleDescRspAREQ_CB))
		{
			failedr.append("not SimpleDescRspAREQ_CB response") ;
			return false;
		}
		respFlwRSP = (SimpleDescRspAREQ_CB)m_recv;
		return true ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public SimpleDescRspAREQ_CB getSimpleDescRsp()
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
