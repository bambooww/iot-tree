package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_zdo.NwkAddrRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.NwkAddrSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.NwkAddrSRSP;
import org.json.JSONObject;

/**
 * 通过IEEE Addr查找设备或关联设备信息
 * 
 * @author jason.zhu
 *
 */
public class NwkAddr_CMD extends MsgReqRsp
{
	NwkAddrSRSP respRSP = null ;
	
	NwkAddrRspAREQ_CB respFlwRSP = null ;
	
	protected byte[] respNVData = null ;

	public NwkAddr_CMD(String ieee_addr,boolean que_assoc_dev,int start_idx)
	{
		super();
		NwkAddrSREQ req = new NwkAddrSREQ(ieee_addr,que_assoc_dev,start_idx) ;
		this.asReq(req, true, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof NwkAddrSRSP))
		{
			failedr.append("not NwkAddrSRSP response") ;
			return false;
		}
		respRSP = (NwkAddrSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof NwkAddrRspAREQ_CB))
		{
			failedr.append("not NwkAddrRspAREQ_CB response") ;
			return false;
		}
		respFlwRSP = (NwkAddrRspAREQ_CB)m_recv;
		return true ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public NwkAddrRspAREQ_CB getNwkAddrRsp()
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
