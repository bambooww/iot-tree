package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_zdo.MgmtPermitJoinRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.MgmtPermitJoinSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.MgmtPermitJoinSRSP;
import org.json.JSONObject;

public class PermitJoinCMD extends MsgReqRsp
{
	MgmtPermitJoinSREQ req = null ;
	MgmtPermitJoinSRSP respSRSP = null ;
	MgmtPermitJoinRspAREQ_CB flwRsp = null ;
	
	public PermitJoinCMD(int addr_mode,int dst_addr,int duration,int tc_significance)
	{
		super();
		req = new MgmtPermitJoinSREQ(addr_mode,dst_addr,duration,tc_significance) ;
		this.asReq(req, true, 5000) ;
	}
	
	public PermitJoinCMD(boolean permit_or_not)
	{
		super();
		req = new MgmtPermitJoinSREQ(permit_or_not) ;
		this.asReq(req, true, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof MgmtPermitJoinSRSP))
		{
			failedr.append("not MgmtPermitJoinSRSP response") ;
			return false;
		}
		respSRSP = (MgmtPermitJoinSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof MgmtPermitJoinRspAREQ_CB))
		{
			failedr.append("not MgmtPermitJoinRspAREQ_CB response") ;
			return false;
		}
		flwRsp = (MgmtPermitJoinRspAREQ_CB)m_recv;
		return true ;
	}
	
	public int getReqPermitDuration()
	{
		return this.req.getDuration() ;
	}
	
	public boolean isRspStatusOk()
	{
		if(respSRSP==null)
			return false ;
		return respSRSP.getStatus() ;
	}
	
	public boolean isFlwOk()
	{
		if(flwRsp==null)
			return false;
		return this.flwRsp.getStatus() ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return new JSONObject().put("status", this.isRspStatusOk())
				.putOpt("duration", this.getReqPermitDuration()).put("flw_ok",this.isFlwOk());
	}

}
