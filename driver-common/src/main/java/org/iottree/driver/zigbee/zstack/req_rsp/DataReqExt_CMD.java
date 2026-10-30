package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.ZStackManager;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestExtSREQ;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestExtSRSP;
import org.iottree.driver.zigbee.zstack.msg_af.IncomingMsgAREQ_CB;
import org.json.JSONObject;

public class DataReqExt_CMD extends MsgReqRsp
{
	
	//DataRequestSRSP respRSP = null ;
	DataRequestExtSRSP respRSP = null ;
	
	IncomingMsgAREQ_CB respFlwRSP = null ;
	
	protected byte[] respNVData = null ;

	public DataReqExt_CMD(ZStackManager zsm,int dst_addr,int dst_ep,int dst_pan_id,
			int src_ep,int cluster_id,ZCLFrame zcl_frm)
	{
		super();
		int trans_id = zsm.get_next_trans_id() ;
		int radius = 30 ;
		zcl_frm.asSeqNum(trans_id) ;
		byte[] zcl_data = zcl_frm.packFrame() ;
//		DataRequestSREQ req = new DataRequestSREQ(dst_addr,dst_ep,src_ep,
//				cluster_id,trans_id,0x00,radius,zcl_data) ;
		DataRequestExtSREQ req = new DataRequestExtSREQ(dst_addr,
				dst_ep,dst_pan_id,
				src_ep,cluster_id,trans_id,0x00,radius,zcl_data) ;
		this.asReq(req, true, 5000) ;
		
		System.out.println("**="+zcl_frm) ;
		
		System.out.println("$$="+req) ;
	}
	
	public DataReqExt_CMD(ZStackManager zsm,String dst_ieeeaddr,int dst_ep,int dst_pan_id,
			int src_ep,int cluster_id,ZCLFrame zcl_frm)
	{
		super();
		int trans_id = zsm.get_next_trans_id() ;
		int radius = 30 ;
		zcl_frm.asSeqNum(trans_id) ;
		byte[] zcl_data = zcl_frm.packFrame() ;
//		DataRequestSREQ req = new DataRequestSREQ(dst_addr,dst_ep,src_ep,
//				cluster_id,trans_id,0x00,radius,zcl_data) ;
		DataRequestExtSREQ req = new DataRequestExtSREQ(dst_ieeeaddr,
				dst_ep,dst_pan_id,
				src_ep,cluster_id,trans_id,0x00,radius,zcl_data) ;
		this.asReq(req, true, 5000) ;
		
		System.out.println("**="+zcl_frm) ;
		
		System.out.println("$$="+req) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof DataRequestExtSRSP))
		{
			failedr.append("not DataRequestExtSRSP response") ;
			return false;
		}
		respRSP = (DataRequestExtSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof IncomingMsgAREQ_CB))
		{
			failedr.append("not IncomingMsgAREQ_CB response") ;
			return false;
		}
		respFlwRSP = (IncomingMsgAREQ_CB)m_recv;
		return true ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public IncomingMsgAREQ_CB getIncomingMsg()
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
