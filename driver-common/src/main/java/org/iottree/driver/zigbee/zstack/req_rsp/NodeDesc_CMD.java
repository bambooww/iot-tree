package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_zdo.NodeDescRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.NodeDescSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.NodeDescSRSP;

import org.json.JSONObject;

public class NodeDesc_CMD extends MsgReqRsp
{
	NodeDescSRSP respRSP = null ;
	
	NodeDescRspAREQ_CB respFlwRSP = null ;
	
	protected byte[] respNVData = null ;

	public NodeDesc_CMD(int dst_addr,int nwk_addr_of_int)
	{
		super();
		NodeDescSREQ req = new NodeDescSREQ(dst_addr,nwk_addr_of_int) ;
		this.asReq(req, true, 1000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof NodeDescSRSP))
		{
			failedr.append("not NodeDescSRSP response") ;
			return false;
		}
		respRSP = (NodeDescSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof NodeDescRspAREQ_CB))
		{
			failedr.append("not SimpleDescRspAREQ_CB response") ;
			return false;
		}
		respFlwRSP = (NodeDescRspAREQ_CB)m_recv;
		return true ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public NodeDescRspAREQ_CB getNodeDescRsp()
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
